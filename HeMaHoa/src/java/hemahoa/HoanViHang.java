
package hemahoa;

import java.util.Arrays;

public class HoanViHang {

    // Chuan hoa va kiem tra khoa
    private static String validateKey(String key) {

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException(
                "Khoa khong duoc de trong!"
            );
        }

        // Khong chap nhan khoang trang dau va cuoi
        if (!key.equals(key.trim())) {
            throw new IllegalArgumentException(
                "Khoa khong duoc chua khoang trang dau hoac cuoi!"
            );
        }

        key = key.toUpperCase();

        if (!key.matches("[A-Z0-9]+")) {
            throw new IllegalArgumentException(
                "Khoa chi duoc chua chu cai A-Z va chu so 0-9!"
            );
        }

        return key;
    }

    // Kiem tra noi dung
    private static void validateText(String text, String message) {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    // Xac dinh thu tu doc cot theo khoa
    // Neu ky tu khoa trung nhau thi giu nguyen thu tu ban dau
    private static int[] getKeyOrder(String key) {

        key = validateKey(key);

        int n = key.length();
        Integer[] positions = new Integer[n];

        for (int i = 0; i < n; i++) {
            positions[i] = i;
        }

        final String finalKey = key;

        Arrays.sort(positions, (a, b) -> {

            char c1 = finalKey.charAt(a);
            char c2 = finalKey.charAt(b);

            if (c1 != c2) {
                return Character.compare(c1, c2);
            }

            return Integer.compare(a, b);
        });

        int[] order = new int[n];

        for (int i = 0; i < n; i++) {
            order[i] = positions[i];
        }

        return order;
    }

    // Tao bang ky tu theo hang
    private static int[][] createTable(String text, int cols) {

        if (cols <= 0) {
            throw new IllegalArgumentException(
                "So cot phai lon hon 0!"
            );
        }

        int[] chars = text.codePoints().toArray();

        int rows = (chars.length + cols - 1) / cols;

        int[][] table = new int[rows][cols];

        int index = 0;

        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < cols; col++) {

                if (index < chars.length) {
                    table[row][col] = chars[index++];
                }
            }
        }

        return table;
    }

    // Ma hoa
    public static String encrypt(String text, String key) {

        validateText(
            text,
            "Ban ro khong duoc de trong!"
        );

        key = validateKey(key);

        int[] chars = text.codePoints().toArray();
        int[] order = getKeyOrder(key);

        int cols = order.length;
        int rows = (chars.length + cols - 1) / cols;

        int[][] table = createTable(text, cols);

        StringBuilder result = new StringBuilder();

        // Doc tung cot theo thu tu khoa
        for (int col : order) {

            for (int row = 0; row < rows; row++) {

                int index = row * cols + col;

                if (index < chars.length) {
                    result.appendCodePoint(table[row][col]);
                }
            }
        }

        return result.toString();
    }

    // Giai ma
    public static String decrypt(String text, String key) {

        validateText(
            text,
            "Ban ma khong duoc de trong!"
        );

        key = validateKey(key);

        int[] chars = text.codePoints().toArray();
        int[] order = getKeyOrder(key);

        int cols = order.length;
        int rows = (chars.length + cols - 1) / cols;
        int remainder = chars.length % cols;

        int[][] table = new int[rows][cols];

        int index = 0;

        // Phan phoi ban ma vao cac cot
        for (int col : order) {

            int columnLength = rows;

            if (remainder != 0 && col >= remainder) {
                columnLength--;
            }

            for (int row = 0; row < columnLength; row++) {

                if (index >= chars.length) {
                    throw new IllegalArgumentException(
                        "Du lieu ban ma khong hop le!"
                    );
                }

                table[row][col] = chars[index++];
            }
        }

        // Doc lai theo hang
        StringBuilder result = new StringBuilder();

        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < cols; col++) {

                int position = row * cols + col;

                if (position < chars.length) {
                    result.appendCodePoint(table[row][col]);
                }
            }
        }

        return result.toString();
    }

    // Hien thi bang dang van ban
    public static String getEncryptionTable(String text, String key) {

        validateText(
            text,
            "Ban ro khong duoc de trong!"
        );

        key = validateKey(key);

        int[] order = getKeyOrder(key);
        int cols = order.length;

        int[] chars = text.codePoints().toArray();
        int[][] table = createTable(text, cols);

        int rows = table.length;

        StringBuilder result = new StringBuilder();

        result.append("BANG HOAN VI HANG\n\n");
        result.append("Khoa: ").append(key).append("\n");

        result.append("Thu tu doc cot: ");

        for (int i = 0; i < order.length; i++) {

            result.append(order[i] + 1);

            if (i < order.length - 1) {
                result.append(" -> ");
            }
        }

        result.append("\n\n");
        result.append("Hang\\Cot\t");

        for (int col = 0; col < cols; col++) {
            result.append(col + 1).append("\t");
        }

        result.append("\n");

        for (int row = 0; row < rows; row++) {

            result.append(row + 1).append("\t\t");

            for (int col = 0; col < cols; col++) {

                if (row * cols + col < chars.length) {
                    result.appendCodePoint(table[row][col]);
                } else {
                    result.append("-");
                }

                result.append("\t");
            }

            result.append("\n");
        }

        return result.toString();
    }

    // Tao bang HTML de hien thi tren JSP
    public static String getEncryptionTableHtml(String text, String key) {

        validateText(
            text,
            "Ban ro khong duoc de trong!"
        );

        key = validateKey(key);

        int[] order = getKeyOrder(key);
        int cols = order.length;

        int[] chars = text.codePoints().toArray();
        int[][] table = createTable(text, cols);

        int rows = table.length;

        StringBuilder html = new StringBuilder();

        html.append("<div class='permutation-table-wrapper'>");
        html.append("<h3>Bang sap xep ky tu</h3>");

        html.append("<div class='table-responsive'>");
        html.append("<table class='permutation-table'>");

        // Tieu de cot
        html.append("<thead><tr>");
        html.append("<th>Hang</th>");

        for (int col = 0; col < cols; col++) {

            html.append("<th>Cot ")
                .append(col + 1)
                .append(" (")
                .append(escapeHtml(
                    String.valueOf(key.charAt(col))
                ))
                .append(")</th>");
        }

        html.append("</tr></thead>");

        // Noi dung bang
        html.append("<tbody>");

        for (int row = 0; row < rows; row++) {

            html.append("<tr>");
            html.append("<th>")
                .append(row + 1)
                .append("</th>");

            for (int col = 0; col < cols; col++) {

                html.append("<td>");

                if (row * cols + col < chars.length) {

                    String character = new String(
                        Character.toChars(table[row][col])
                    );

                    html.append(escapeHtml(character));

                } else {
                    html.append("-");
                }

                html.append("</td>");
            }

            html.append("</tr>");
        }

        html.append("</tbody>");
        html.append("</table>");
        html.append("</div>");

        // Thu tu doc cot
        html.append("<div class='column-order'>");
        html.append("<strong>Thu tu doc cot:</strong> ");

        for (int i = 0; i < order.length; i++) {

            html.append("Cot ")
                .append(order[i] + 1);

            if (i < order.length - 1) {
                html.append(" &rarr; ");
            }
        }

        html.append("</div>");
        html.append("</div>");

        return html.toString();
    }

    // Ma hoa an toan noi dung HTML
    private static String escapeHtml(String value) {

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}
