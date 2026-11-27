
package hemahoa;

public class Vigenere {

    // Chuan hoa va kiem tra khoa
    private static String normalizeKey(String key) {

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException(
                "Khoa Vigenere khong duoc de trong!"
            );
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < key.length(); i++) {

            char c = key.charAt(i);

            if (c >= 'a' && c <= 'z') {
                result.append((char) (c - 'a' + 'A'));

            } else if (c >= 'A' && c <= 'Z') {
                result.append(c);

            } else {
                throw new IllegalArgumentException(
                    "Khoa chi duoc chua chu cai A-Z, khong co dau, "
                    + "so, khoang trang hoac ky tu dac biet!"
                );
            }
        }

        if (result.length() == 0) {
            throw new IllegalArgumentException(
                "Khoa phai co it nhat mot chu cai A-Z!"
            );
        }

        return result.toString();
    }

    // Ma hoa Vigenere
    public static String encrypt(String text, String key) {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(
                "Ban ro khong duoc de trong!"
            );
        }

        String cleanKey = normalizeKey(key);

        StringBuilder result = new StringBuilder();

        int keyIndex = 0;
        int keyLength = cleanKey.length();

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            // Chu hoa
            if (c >= 'A' && c <= 'Z') {

                int p = c - 'A';
                int k = cleanKey.charAt(keyIndex % keyLength) - 'A';

                char encrypted = (char) (
                    (p + k) % 26 + 'A'
                );

                result.append(encrypted);
                keyIndex++;

            // Chu thuong
            } else if (c >= 'a' && c <= 'z') {

                int p = c - 'a';
                int k = cleanKey.charAt(keyIndex % keyLength) - 'A';

                char encrypted = (char) (
                    (p + k) % 26 + 'a'
                );

                result.append(encrypted);
                keyIndex++;

            } else {
                // Giu nguyen tieng Viet co dau, so,
                // khoang trang, emoji va ky tu dac biet.
                result.append(c);
            }
        }

        return result.toString();
    }

    // Giai ma Vigenere
    public static String decrypt(String text, String key) {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(
                "Ban ma khong duoc de trong!"
            );
        }

        String cleanKey = normalizeKey(key);

        StringBuilder result = new StringBuilder();

        int keyIndex = 0;
        int keyLength = cleanKey.length();

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            // Chu hoa
            if (c >= 'A' && c <= 'Z') {

                int encrypted = c - 'A';
                int k = cleanKey.charAt(keyIndex % keyLength) - 'A';

                char decrypted = (char) (
                    (encrypted - k + 26) % 26 + 'A'
                );

                result.append(decrypted);
                keyIndex++;

            // Chu thuong
            } else if (c >= 'a' && c <= 'z') {

                int encrypted = c - 'a';
                int k = cleanKey.charAt(keyIndex % keyLength) - 'A';

                char decrypted = (char) (
                    (encrypted - k + 26) % 26 + 'a'
                );

                result.append(decrypted);
                keyIndex++;

            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
