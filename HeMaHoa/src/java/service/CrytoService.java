package service;

import hemahoa.Affine;
import hemahoa.Caesar;
import hemahoa.DES;
import hemahoa.DiffieHellman;
import hemahoa.Hill;
import hemahoa.HoanViHang;
import hemahoa.Playfair;
import hemahoa.RSASignature;
import hemahoa.Vigenere;

import java.security.KeyPair;
import java.util.Map;

public class CrytoService {

    public String execute(
            String algorithm,
            String operation,
            Map<String, String> data) throws Exception {

        if (algorithm == null || algorithm.trim().isEmpty()) {
            throw new IllegalArgumentException("Bạn chưa chọn thuật toán!");
        }

        if (operation == null || operation.trim().isEmpty()) {
            throw new IllegalArgumentException("Bạn chưa chọn thao tác!");
        }

        switch (algorithm.toLowerCase()) {

            case "caesar":
                return processCaesar(operation, data);

            case "affine":
                return processAffine(operation, data);

            case "playfair":
                return processPlayfair(operation, data);

            case "hill":
                return processHill(operation, data);

            case "hoanvi":
                return processHoanVi(operation, data);

            case "des":
                return processDES(operation, data);

            case "vigenere":
                return processVigenere(operation, data);

            case "diffiehellman":
                return processDiffieHellman(operation, data);

            case "rsa":
                return processRSA(operation, data);

            default:
                throw new IllegalArgumentException(
                        "Thuật toán không tồn tại: " + algorithm
                );
        }
    }

    // ==================== CAESAR ====================

    private String processCaesar(
            String operation, Map<String, String> data) {

        String text = required(data, "text");
        int key = parseInteger(data, "key");

        if ("encrypt".equals(operation)) {
            return Caesar.encrypt(text, key);
        }

        if ("decrypt".equals(operation)) {
            return Caesar.decrypt(text, key);
        }

        throw invalidOperation();
    }

    // ==================== AFFINE ====================

    private String processAffine(
            String operation, Map<String, String> data) {

        String text = required(data, "text");
        int a = parseInteger(data, "a");
        int b = parseInteger(data, "b");

        if ("encrypt".equals(operation)) {
            return Affine.maHoa(text, a, b);
        }

        if ("decrypt".equals(operation)) {
            return Affine.giaiMa(text, a, b);
        }

        throw invalidOperation();
    }

    // ==================== PLAYFAIR ====================

    // ==================== PLAYFAIR ====================

private String processPlayfair(
        String operation, Map<String, String> data) {

    String text = required(data, "text");
    String key = required(data, "key");

    Playfair playfair = new Playfair();

    if ("encrypt".equals(operation)) {
        return playfair.maHoa(text, key);
    }

    if ("decrypt".equals(operation)) {
        return playfair.giaiMa(text, key);
    }

    throw invalidOperation();
}

    // ==================== HILL ====================

    private String processHill(
            String operation, Map<String, String> data) {

        String text = required(data, "text");
        int dimension = parseInteger(data, "dimension");
        String matrixText = required(data, "keyMatrix");

        if (dimension < 2 || dimension > 4) {
            throw new IllegalArgumentException(
                    "Kích thước ma trận chỉ được là 2, 3 hoặc 4!"
            );
        }

        int[][] matrix = parseMatrix(matrixText, dimension);

        // ================= MÃ HÓA =================

        if ("encrypt".equals(operation)) {

            int originalLength = Hill.getNormalizedLength(text);

            String ciphertext = Hill.encrypt(text, matrix);

            data.put("originalLength", String.valueOf(originalLength));

            return ciphertext;
        }

        // ================= GIẢI MÃ =================

        if ("decrypt".equals(operation)) {

            String originalLengthText = data.get("originalLength");

            if (originalLengthText != null
                    && !originalLengthText.trim().isEmpty()) {

                int originalLength;

                try {
                    originalLength = Integer.parseInt(
                            originalLengthText.trim()
                    );
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Độ dài bản rõ ban đầu không hợp lệ!"
                    );
                }

                if (originalLength <= 0) {
                    throw new IllegalArgumentException(
                            "Độ dài bản rõ phải lớn hơn 0!"

                    );
                }

                return Hill.decrypt(
                        text,
                        matrix,
                        originalLength
                );
            }

            return Hill.decrypt(text, matrix);
        }

        throw invalidOperation();
    }

    // ==================== KIỂM TRA MA TRẬN HILL ====================

    private int[][] parseMatrix(
            String matrixText, int dimension) {

        String[] rows = matrixText.trim().split("\\R");

        if (rows.length != dimension) {
            throw new IllegalArgumentException(
                    "Ma trận phải có đúng " + dimension + " hàng!"
            );
        }

        int[][] matrix = new int[dimension][dimension];

        for (int i = 0; i < dimension; i++) {

            String row = rows[i].trim();

            if (row.isEmpty()) {
                throw new IllegalArgumentException(
                        "Hàng " + (i + 1) + " của ma trận đang trống!"
                );
            }

            String[] values = row.split("\\s+");

            if (values.length != dimension) {
                throw new IllegalArgumentException(
                        "Hàng " + (i + 1) + " phải có đúng "
                        + dimension + " phần tử!"
                );
            }

            for (int j = 0; j < dimension; j++) {

                try {
                    matrix[i][j] = Integer.parseInt(values[j]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Phần tử ma trận phải là số nguyên!"
                    );
                }

                if (matrix[i][j] < 0 || matrix[i][j] > 25) {
                    throw new IllegalArgumentException(
                            "Các phần tử ma trận phải nằm trong khoảng 0 đến 25!"
                    );
                }
            }
        }

        return matrix;
    }

    // ==================== HOÁN VỊ HÀNG ====================

    private String processHoanVi(
            String operation, Map<String, String> data) {

        String text = required(data, "text");
        String key = required(data, "key");

        if ("encrypt".equals(operation)) {
            return HoanViHang.encrypt(text, key);
        }

        if ("decrypt".equals(operation)) {
            return HoanViHang.decrypt(text, key);
        }

        throw invalidOperation();
    }

    // ==================== DES ====================

    private String processDES(
            String operation, Map<String, String> data) throws Exception {

        String text = required(data, "text");
        String key = required(data, "key");

        String mode = data.get("mode");
        String format = data.get("format");
        String padding = data.get("padding");

        // Giá trị mặc định

        if (mode == null || mode.trim().isEmpty()) {
            mode = "ECB";
        }

        if (format == null || format.trim().isEmpty()) {
            format = "Văn bản";
        }

        if (padding == null || padding.trim().isEmpty()) {
            padding = "PKCS5";
        }

        mode = mode.trim();
        format = format.trim();
        padding = padding.trim();

        boolean encrypt;

        if ("encrypt".equalsIgnoreCase(operation)) {
            encrypt = true;
        } else if ("decrypt".equalsIgnoreCase(operation)) {
            encrypt = false;
        } else {
            throw invalidOperation();
        }

        return DES.des(
                text,
                key,
                mode,
                encrypt,
                format,
                padding
        );
    }

    // ==================== VIGENERE ====================

    private String processVigenere(
            String operation, Map<String, String> data) {

        String text = required(data, "text");
        String key = required(data, "key");

        if ("encrypt".equals(operation)) {
            return Vigenere.encrypt(text, key);
        }

        if ("decrypt".equals(operation)) {
            return Vigenere.decrypt(text, key);
        }

        throw invalidOperation();
    }

    // ==================== DIFFIE-HELLMAN ====================

    private String processDiffieHellman(
            String operation, Map<String, String> data) {

        if (!"exchange".equals(operation)) {
            throw invalidOperation();
        }

        String p = required(data, "p");
        String g = required(data, "g");
        String privateA = required(data, "privateA");
        String privateB = required(data, "privateB");

        return DiffieHellman.exchangeKeys(
                p, g, privateA, privateB
        );
    }

    // ==================== RSA CHỮ KÝ SỐ ====================

    private String processRSA(
            String operation, Map<String, String> data) throws Exception {

        if ("generate".equals(operation)) {

            int keySize = parseInteger(data, "keySize");

            KeyPair keyPair = RSASignature.generateKeyPair(keySize);

            String privateKey = RSASignature.getPrivateKey(keyPair);

            String publicKey = RSASignature.getPublicKey(keyPair);

            return "===== CẶP KHÓA RSA =====\n\n"
                    + "Kích thước khóa: " + keySize + " bit\n\n"
                    + "PRIVATE KEY:\n"
                    + privateKey + "\n\n"
                    + "PUBLIC KEY:\n"
                    + publicKey;
        }

        if ("sign".equals(operation)) {

            String text = required(data, "text");

            String privateKey = required(data, "privateKey");

            String signature = RSASignature.sign(text, privateKey);

            return "===== KÝ SỐ THÀNH CÔNG =====\n\n"
                    + "Nội dung:\n" + text + "\n\n"
                    + "Chữ ký số (Base64):\n" + signature;
        }

        if ("verify".equals(operation)) {

            String text = required(data, "text");

            String signature = required(data, "signature");

            String publicKey = required(data, "publicKey");

            return RSASignature.verifyResult(
                    text, signature, publicKey
            );
        }

        throw invalidOperation();
    }

    // ==================== HÀM HỖ TRỢ ====================

    private String required(
            Map<String, String> data, String name) {

        String value = data.get(name);

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Trường dữ liệu '" + name + "' không được để trống!"
            );
        }

        return value;
    }

    private int parseInteger(
            Map<String, String> data, String name) {

        String value = required(data, name);

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Trường '" + name + "' phải là số nguyên hợp lệ!"
            );
        }
    }

    private IllegalArgumentException invalidOperation() {
        return new IllegalArgumentException(
                "Thao tác không hợp lệ!"
        );
    }
}