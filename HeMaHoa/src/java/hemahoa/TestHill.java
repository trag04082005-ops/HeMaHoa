
package hemahoa;

public class TestHill {

    public static void main(String[] args) {

        // TEST HILL 2x2
        int[][] key2 = {
            {3, 3},
            {2, 5}
        };

        // TEST HILL 3x3
        int[][] key3 = {
            {6, 24, 1},
            {13, 16, 10},
            {20, 17, 15}
        };

        // TEST HILL 4x4
      int[][] key4 = {
    {1, 0, 0, 0},
    {0, 1, 0, 0},
    {0, 0, 3, 3},
    {0, 0, 2, 5}
};
        test("HELLO", key2, 5);
        test("HELLO", key3, 5);
        test("HELLO", key4, 5);

        // TEST KHOA KHONG HOP LE
        try {
            int[][] badKey = {
                {2, 4},
                {6, 8}
            };

            Hill.encrypt("HELLO", badKey);

        } catch (IllegalArgumentException e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }

    public static void test(
        String text, int[][] key, int originalLength
    ) {

        try {
            System.out.println("\nMa tran "
                + key.length + "x" + key.length);

            String encrypted = Hill.encrypt(text, key);

            System.out.println("Ban ro: " + text);
            System.out.println("Ban ma: " + encrypted);

            String decrypted = Hill.decrypt(
                encrypted, key, originalLength
            );

            System.out.println("Giai ma: " + decrypted);

        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }
}
