
package hemahoa;

public class Affine {

    // Tim UCLN
    private static int gcd(int a, int b) {

        a = Math.abs(a);

        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }

        return a;
    }

    // Kiem tra khoa a
    private static void checkKey(int a) {

        if (gcd(a, 26) != 1) {
            throw new IllegalArgumentException(
                "Khoa a khong hop le! a phai nguyen to cung nhau voi 26."
            );
        }
    }

    // Ma hoa Affine
    public static String maHoa(String text, int a, int b) {

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Van ban khong duoc de trong!"
            );
        }

        checkKey(a);

        a = Math.floorMod(a, 26);
        b = Math.floorMod(b, 26);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            if (c >= 'A' && c <= 'Z') {

                int x = c - 'A';
                int y = (a * x + b) % 26;

                result.append((char) ('A' + y));

            } else if (c >= 'a' && c <= 'z') {

                int x = c - 'a';
                int y = (a * x + b) % 26;

                result.append((char) ('a' + y));

            } else {

                result.append(c);
            }
        }

        return result.toString();
    }

    // Tim nghich dao modulo 26
    private static int nghichDao(int a) {

        a = Math.floorMod(a, 26);

        for (int i = 1; i < 26; i++) {

            if ((a * i) % 26 == 1) {
                return i;
            }
        }

        throw new IllegalArgumentException(
            "Khoa a khong co nghich dao modulo 26!"
        );
    }

    // Giai ma Affine
    public static String giaiMa(String text, int a, int b) {

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Van ban khong duoc de trong!"
            );
        }

        checkKey(a);

        int aInverse = nghichDao(a);
        b = Math.floorMod(b, 26);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            if (c >= 'A' && c <= 'Z') {

                int y = c - 'A';
                int x = Math.floorMod(aInverse * (y - b), 26);

                result.append((char) ('A' + x));

            } else if (c >= 'a' && c <= 'z') {

                int y = c - 'a';
                int x = Math.floorMod(aInverse * (y - b), 26);

                result.append((char) ('a' + x));

            } else {

                result.append(c);
            }
        }

        return result.toString();
    }

    // Test ma hoa
    private static void testEncrypt(String text, int a, int b) {

        try {

            String result = maHoa(text, a, b);

            System.out.println(
                "Encrypt: [" + text + "] | a = " + a
                + ", b = " + b + " => " + result
            );

        } catch (Exception e) {

            System.out.println(
                "Encrypt: [" + text + "] | a = " + a
                + ", b = " + b + " => LOI: " + e.getMessage()
            );
        }
    }

    // Test giai ma
    private static void testDecrypt(String text, int a, int b) {

        try {

            String result = giaiMa(text, a, b);

            System.out.println(
                "Decrypt: [" + text + "] | a = " + a
                + ", b = " + b + " => " + result
            );

        } catch (Exception e) {

            System.out.println(
                "Decrypt: [" + text + "] | a = " + a
                + ", b = " + b + " => LOI: " + e.getMessage()
            );
        }
    }

    // Test ma hoa va giai ma
    private static void testRoundTrip(
            String text, int a, int b) {

        try {

            String encrypted = maHoa(text, a, b);
            String decrypted = giaiMa(encrypted, a, b);

            boolean passed = text.equals(decrypted);

            System.out.println(
                "Ban ro: " + text
                + " | Ma hoa: " + encrypted
                + " | Giai ma: " + decrypted
                + " | " + (passed ? "PASS" : "FAIL")
            );

        } catch (Exception e) {

            System.out.println(
                "RoundTrip: " + text
                + " => LOI: " + e.getMessage()
            );
        }
    }

    public static void main(String[] args) {

        System.out.println("===== TEST MA HOA AFFINE =====");

        testEncrypt("HELLO", 5, 8);
        testEncrypt("hello", 5, 8);
        testEncrypt("Hello World", 5, 8);
        testEncrypt("ABCXYZ", 1, 0);
        testEncrypt("ABC", 3, 0);
        testEncrypt("ABC", 5, 26);
        testEncrypt("Hello 123!", 5, 8);
        testEncrypt("ABC", -5, -8);
        testEncrypt("abc xyz", 29, 34);
        testEncrypt("XYZ", 53, 80);
        testEncrypt("Java Programming", 7, 12);
        testEncrypt("Security@2026", 11, 15);

        System.out.println("\n===== TEST GIAI MA AFFINE =====");

        testDecrypt("RCLLA", 5, 8);
        testDecrypt("rclla", 5, 8);
        testDecrypt("Rclla Uorld", 5, 8);
        testDecrypt("ABCXYZ", 1, 0);
        testDecrypt("ADG", 3, 0);
        testDecrypt("AFK", 5, 26);
        testDecrypt("Rclla 123!", 5, 8);
        testDecrypt("XYZ", -5, -8);
        testDecrypt("KhoA MaT", 29, 34);
        testDecrypt("123!@#", 5, 8);

        System.out.println("\n===== TEST KHOA A KHONG HOP LE =====");

        testEncrypt("ABC", 2, 8);
        testEncrypt("ABC", 4, 8);
        testEncrypt("ABC", 13, 8);
        testEncrypt("ABC", 26, 8);
        testEncrypt("ABC", 52, 8);
        testEncrypt("ABC", 0, 5);
        testEncrypt("ABC", -2, 5);
        testEncrypt("ABC", 10, 5);

        System.out.println("\n===== TEST VAN BAN KHONG HOP LE =====");

        testEncrypt("", 5, 8);
        testEncrypt("   ", 5, 8);
        testEncrypt(null, 5, 8);

        System.out.println("\n===== TEST KHOA B DAC BIET =====");

        testEncrypt("ABC", 5, 0);
        testEncrypt("ABC", 5, 26);
        testEncrypt("ABC", 5, -26);
        testEncrypt("ABC", 5, 52);
        testEncrypt("ABC", 5, -52);

        System.out.println("\n===== TEST VAN BAN DAC BIET =====");

        testEncrypt("123456789", 5, 8);
        testEncrypt("!@#$%^&*()", 5, 8);
        testEncrypt("A B C", 5, 8);
        testEncrypt("aBcXyZ", 5, 8);
        testEncrypt("Vietnam 2026", 11, 7);

        System.out.println("\n===== TEST MA HOA VA GIAI MA =====");

        testRoundTrip("Hello World", 5, 8);
        testRoundTrip("Network 123!", 7, 3);
        testRoundTrip("abc xyz", 29, 34);
        testRoundTrip("Xin chao", 11, 7);
        testRoundTrip("Java Programming", 7, 12);
        testRoundTrip("Security@2026", 11, 15);
        testRoundTrip("A B C XYZ", 5, 8);

        System.out.println("\n===== KET THUC TEST AFFINE =====");
    }
}
