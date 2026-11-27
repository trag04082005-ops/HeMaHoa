package hemahoa;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(urlPatterns = {"/PlayfairServlet"})
@MultipartConfig
public class Playfair extends HttpServlet {

    // Tao bang Playfair 5x5
    private char[][] taoBang(String key) {

        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Khoa khong duoc de trong!"
            );
        }

        if (!key.matches("[a-zA-Z\\s]+")) {
            throw new IllegalArgumentException(
                "Khoa chi duoc chua chu cai va khoang trang!"
            );
        }

        key = key.toUpperCase().replace("J", "I");

        StringBuilder s = new StringBuilder();

        for (char c : key.toCharArray()) {
            if (c >= 'A' && c <= 'Z'
                    && s.indexOf(String.valueOf(c)) == -1) {
                s.append(c);
            }
        }

        for (char c = 'A'; c <= 'Z'; c++) {
            if (c != 'J'
                    && s.indexOf(String.valueOf(c)) == -1) {
                s.append(c);
            }
        }

        char[][] bang = new char[5][5];
        int k = 0;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                bang[i][j] = s.charAt(k++);
            }
        }

        return bang;
    }

    // Tim vi tri ky tu trong bang
    private int[] tim(char[][] bang, char c) {

        if (c == 'J') {
            c = 'I';
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (bang[i][j] == c) {
                    return new int[]{i, j};
                }
            }
        }

        throw new IllegalArgumentException(
            "Ky tu khong ton tai trong bang!"
        );
    }

    // Chon ky tu chen
    private char kyTuChen(char c) {
        return c == 'X' ? 'Q' : 'X';
    }

    // Chuan bi van ban truoc khi ma hoa
    private String chuanBi(String text) {

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ban ro khong duoc de trong!"
            );
        }

        if (!text.matches("[a-zA-Z\\s]+")) {
            throw new IllegalArgumentException(
                "Ban ro chi duoc chua chu cai va khoang trang!"
            );
        }

        StringBuilder s = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (c >= 'A' && c <= 'Z'
                    || c >= 'a' && c <= 'z') {
                s.append(Character.toUpperCase(c));
            }
        }

        String input = s.toString().replace("J", "I");

        StringBuilder result = new StringBuilder();

        int i = 0;

        while (i < input.length()) {

            char a = input.charAt(i);

            if (i + 1 >= input.length()) {
                result.append(a);
                result.append(kyTuChen(a));
                i++;

            } else {

                char b = input.charAt(i + 1);

                if (a == b) {
                    result.append(a);
                    result.append(kyTuChen(a));
                    i++;

                } else {
                    result.append(a);
                    result.append(b);
                    i += 2;
                }
            }
        }

        return result.toString();
    }

    // Xu ly ma hoa va giai ma
    private String xuLyChu(
            String text,
            char[][] bang,
            boolean maHoa) {

        if (text == null || text.length() % 2 != 0) {
            throw new IllegalArgumentException(
                "Van ban xu ly phai co so ky tu chan!"
            );
        }

        StringBuilder kq = new StringBuilder();

        for (int i = 0; i < text.length(); i += 2) {

            char x = text.charAt(i);
            char y = text.charAt(i + 1);

            int[] a = tim(bang, x);
            int[] b = tim(bang, y);

            // Cung hang
            if (a[0] == b[0]) {

                int huong = maHoa ? 1 : 4;

                kq.append(bang[a[0]][(a[1] + huong) % 5]);
                kq.append(bang[b[0]][(b[1] + huong) % 5]);

            // Cung cot
            } else if (a[1] == b[1]) {

                int huong = maHoa ? 1 : 4;

                kq.append(bang[(a[0] + huong) % 5][a[1]]);
                kq.append(bang[(b[0] + huong) % 5][b[1]]);

            // Hinh chu nhat
            } else {

                kq.append(bang[a[0]][b[1]]);
                kq.append(bang[b[0]][a[1]]);
            }
        }

        return kq.toString();
    }

    // Thu loai bo ky tu dem khi giai ma
    private String loaiKyTuDem(String text) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            if (i > 0 && i + 1 < text.length()) {

                char truoc = text.charAt(i - 1);
                char sau = text.charAt(i + 1);

                if (c == 'X' && truoc == sau) {
                    continue;
                }

                if (c == 'Q' && truoc == 'X' && sau == 'X') {
                    continue;
                }
            }

            result.append(c);
        }

        // Loai bo ky tu dem cuoi
        if (result.length() > 0) {

            char cuoi = result.charAt(result.length() - 1);

            if (cuoi == 'X') {
                result.deleteCharAt(result.length() - 1);

            } else if (cuoi == 'Q'
                    && result.length() >= 2
                    && result.charAt(result.length() - 2) == 'X') {
                result.deleteCharAt(result.length() - 1);
            }
        }

        return result.toString();
    }

    // Hàm mã hóa gọi từ CrytoService
    public String maHoa(String text, String key) {
        return Playfair(text, key, true);
    }

    // Hàm giải mã gọi từ CrytoService
    public String giaiMa(String text, String key) {
        return Playfair(text, key, false);
    }

    // Thuat toan Playfair
    private String Playfair(
            String text,
            String key,
            boolean maHoa) {

        char[][] bang = taoBang(key);

        if (maHoa) {

            String s = chuanBi(text);

            return xuLyChu(s, bang, true);

        } else {

            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException(
                    "Ban ma khong duoc de trong!"
                );
            }

            if (!text.matches("[a-zA-Z\\s]+")) {
                throw new IllegalArgumentException(
                    "Ban ma chi duoc chua chu cai va khoang trang!"
            );
            }

            String s = text.toUpperCase()
                       .replaceAll("\\s+", "")
                       .replace("J", "I");

            if (s.isEmpty()) {
                throw new IllegalArgumentException(
                    "Ban ma khong co chu cai!"
                );
            }

            if (s.length() % 2 != 0) {
                throw new IllegalArgumentException(
                    "Ban ma phai co so ky tu chan!"
                );
            }

            String result = xuLyChu(s, bang, false);

            return loaiKyTuDem(result);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String text = request.getParameter("text");
        String key = request.getParameter("key");
        String action = request.getParameter("action");

        Part file = request.getPart("file");

        // Doc noi dung tu file
        if (file != null && file.getSize() > 0) {

            try (InputStream input = file.getInputStream()) {
                text = new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8
                );
            }
        }

        String result;

        try {

            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException(
                    "Vui long nhap van ban hoac chon file!"
                );
            }

            if (key == null || key.trim().isEmpty()) {
                throw new IllegalArgumentException(
                    "Vui long nhap khoa!"
                );
            }

            if (!key.matches("[a-zA-Z\\s]+")) {
                throw new IllegalArgumentException(
                    "Khoa chi duoc chua chu cai va khoang trang!"
                );
            }

            if (!"mahoa".equals(action)
                    && !"giaima".equals(action)) {
                throw new IllegalArgumentException(
                    "Vui long chon chuc nang hop le!"
                );
            }

            result = Playfair(
                text,
                key,
                "mahoa".equals(action)
            );

        } catch (IllegalArgumentException e) {
            result = e.getMessage();
        }

        request.setAttribute("result", result);
        request.setAttribute("inputText", text);
        request.setAttribute("inputKey", key);

        // Chi hien thi ma tran khi khoa hop le
        try {
            if (key != null && !key.trim().isEmpty()) {
                char[][] matrix = taoBang(key);
                request.setAttribute("matrix", matrix);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("matrix", null);
        }

        request.getRequestDispatcher("index.jsp")
                .forward(request, response);
    }
}