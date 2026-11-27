package controller;

import hemahoa.DiffieHellman;
import hemahoa.RSASignature;
import hemahoa.Vigenere;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import service.CrytoService;

@WebServlet("/crypto")
public class CrytoServlet extends HttpServlet {

    private final CrytoService service = new CrytoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect(request.getContextPath() + "/index.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if ("json".equals(request.getParameter("format"))) {
            handleJson(request, response);
        } else {
            handleForm(request, response);
        }
    }

    // ====================== CACH 1: JSON ======================

    private void handleJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");

        Map<String, Object> out = new LinkedHashMap<>();

        try {
            String algorithm = need(request, "algorithm").toLowerCase();
            String operation = need(request, "operation").toLowerCase();

            switch (algorithm) {
                case "vigenere":
                    vigenere(request, operation, out);
                    break;

                case "diffiehellman":
                    diffieHellman(request, operation, out);
                    break;

                case "rsa":
                    rsa(request, operation, out);
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Thuat toan khong hop le: " + algorithm);
            }

            out.put("ok", Boolean.TRUE);

        } catch (Exception e) {
            out.clear();
            out.put("ok", Boolean.FALSE);
            out.put("error",
                    e.getMessage() == null ? e.toString() : e.getMessage());
        }

        try (PrintWriter writer = response.getWriter()) {
            writer.write(toJson(out));
        }
    }

    // ====================== VIGENERE ======================

    private void vigenere(HttpServletRequest request, String operation,
            Map<String, Object> out) {

        String text = needRaw(request, "text");
        String key = need(request, "key");

        if ("encrypt".equals(operation)) {
            out.put("result", Vigenere.encrypt(text, key));
        } else if ("decrypt".equals(operation)) {
            out.put("result", Vigenere.decrypt(text, key));
        } else {
            throw new IllegalArgumentException(
                    "Thao tac Vigenere khong hop le: " + operation);
        }
    }

    // ====================== DIFFIE-HELLMAN ======================

    private void diffieHellman(HttpServletRequest request, String operation,
            Map<String, Object> out) {

        if (!"exchange".equals(operation)) {
            throw new IllegalArgumentException(
                    "Thao tac Diffie-Hellman khong hop le: " + operation);
        }

        String p = need(request, "p");
        String g = need(request, "g");
        String a = need(request, "privateA");
        String b = need(request, "privateB");

        BigInteger publicA =
                DiffieHellman.calculatePublicKey(p, g, a);

        BigInteger publicB =
                DiffieHellman.calculatePublicKey(p, g, b);

        BigInteger sharedA =
                DiffieHellman.calculateSharedKeyA(p, g, a, b);

        BigInteger sharedB =
                DiffieHellman.calculateSharedKeyB(p, g, a, b);

        out.put("publicA", publicA.toString());
        out.put("publicB", publicB.toString());
        out.put("sharedA", sharedA.toString());
        out.put("sharedB", sharedB.toString());
        out.put("match", sharedA.equals(sharedB));
    }

    // ====================== RSA ======================

    private void rsa(HttpServletRequest request, String operation,
            Map<String, Object> out) throws Exception {

        switch (operation) {

            case "generate": {
                int keySize = parseInt(
                        need(request, "keySize"), "Kich thuoc khoa");

                KeyPair keyPair = RSASignature.generateKeyPair(keySize);

                out.put("keySize", String.valueOf(keySize));
                out.put("privateKey",
                        RSASignature.getPrivateKey(keyPair));
                out.put("publicKey",
                        RSASignature.getPublicKey(keyPair));
                break;
            }

            case "sign": {
                String text = needRaw(request, "text");
                String privateKey = need(request, "privateKey");

                out.put("signature",
                        RSASignature.sign(text, privateKey));
                break;
            }

            case "verify": {
                String text = needRaw(request, "text");
                String signature = need(request, "signature");
                String publicKey = need(request, "publicKey");

                out.put("valid",
                        RSASignature.verify(text, signature, publicKey));

                out.put("message",
                        RSASignature.verifyResult(text, signature, publicKey));
                break;
            }

            default:
                throw new IllegalArgumentException(
                        "Thao tac RSA khong hop le: " + operation);
        }
    }

    // ====================== CACH 2: FORM JSP ======================

    private void handleForm(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String algorithm = request.getParameter("algorithm");
        String operation = request.getParameter("operation");

        Map<String, String> data = new HashMap<>();

        for (Map.Entry<String, String[]> entry
                : request.getParameterMap().entrySet()) {

            String[] values = entry.getValue();

            data.put(entry.getKey(),
                    values.length > 0 ? values[0] : "");
        }

        try {
            request.setAttribute(
                    "result",
                    service.execute(algorithm, operation, data));

        } catch (Exception e) {
            request.setAttribute(
                    "error",
                    e.getMessage() == null ? e.toString() : e.getMessage());
        }

        // Lay originalLength cho Hill
        if (data.get("originalLength") != null) {
            request.setAttribute(
                    "originalLength",
                    data.get("originalLength"));
        }

        request.getRequestDispatcher("/index.jsp")
                .forward(request, response);
    }

    // ====================== HAM HO TRO ======================

    private static String need(HttpServletRequest request, String name) {

        String value = request.getParameter(name);

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Thieu du lieu: " + name);
        }

        return value.trim();
    }

    private static String needRaw(HttpServletRequest request, String name) {

        String value = request.getParameter(name);

        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Thieu du lieu: " + name);
        }

        return value;
    }

    private static int parseInt(String value, String name) {

        try {
            return Integer.parseInt(value);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    name + " phai la so nguyen!");
        }
    }

    // ====================== TAO JSON ======================

    private static String toJson(Map<String, Object> map) {

        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Object> e : map.entrySet()) {

            if (!first) {
                sb.append(',');
            }

            first = false;

            sb.append('"')
                    .append(escape(e.getKey()))
                    .append("\":");

            Object v = e.getValue();

            if (v instanceof Boolean) {
                sb.append(v);
            } else {
                sb.append('"')
                        .append(escape(String.valueOf(v)))
                        .append('"');
            }
        }

        return sb.append('}').toString();
    }

    // ====================== ESCAPE JSON ======================

    private static String escape(String s) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {

            char c = s.charAt(i);

            switch (c) {

                case '"':
                    sb.append("\\\"");
                    break;

                case '\\':
                    sb.append("\\\\");
                    break;

                case '\n':
                    sb.append("\\n");
                    break;

                case '\r':
                    sb.append("\\r");
                    break;

                case '\t':
                    sb.append("\\t");
                    break;

                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }

        return sb.toString();
    }
}