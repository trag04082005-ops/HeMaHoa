package hemahoa;

public class Caesar {

    private static String process(String text, long key) {

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Van ban khong duoc de trong!"
            );
        }

        int shift = (int) Math.floorMod(key, 26);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                c = (char) ('A' + (c - 'A' + shift) % 26);

            } else if (c >= 'a' && c <= 'z') {
                c = (char) ('a' + (c - 'a' + shift) % 26);
            }

            result.append(c);
        }

        return result.toString();
    }

    public static String encrypt(String text, int key) {
        return process(text, key);
    }

    public static String decrypt(String text, int key) {
        return process(text, -(long) key);
    }
}