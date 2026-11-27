
package hemahoa;

import java.math.BigInteger;

public class DiffieHellman {

    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.TWO;

    // Gioi han dau vao: toi da 4096 bit.
    private static final int MAX_BITS = 4096;

    // Kiem tra va chuyen chuoi thanh BigInteger.
    private static BigInteger parseNumber(String value, String name) {

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                name + " khong duoc de trong!"
            );
        }

        String input = value.trim();

        // Chi chap nhan so nguyen duong dang thap phan.
        if (!input.matches("[0-9]+")) {
            throw new IllegalArgumentException(
                name + " phai la so nguyen duong!"
            );
        }

        // Chan dau vao qua dai truoc khi tao BigInteger.
        if (input.length() > 1234) {
            throw new IllegalArgumentException(
                name + " qua lon! Toi da 1234 chu so."
            );
        }

        BigInteger number;

        try {
            number = new BigInteger(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                name + " khong hop le!"
            );
        }

        if (number.bitLength() > MAX_BITS) {
            throw new IllegalArgumentException(
                name + " vuot qua gioi han 4096 bit!"
            );
        }

        return number;
    }

    // Kiem tra p la so nguyen to.
    private static void validatePrime(BigInteger p) {

        if (p.compareTo(TWO) < 0) {
            throw new IllegalArgumentException(
                "p phai lon hon hoac bang 2!"
            );
        }

        if (!p.isProbablePrime(100)) {
            throw new IllegalArgumentException(
                "p khong phai so nguyen to!"
            );
        }

        // Diffie-Hellman trong lop nay yeu cau nhom co kich thuoc du lon.
        if (p.bitLength() < 2048) {
            throw new IllegalArgumentException(
                "p qua nho! Can p co it nhat 2048 bit "
                + "de phu hop voi cau hinh bao mat."
            );
        }
    }

    // Kiem tra co so g.
    private static void validateGenerator(
        BigInteger g,
        BigInteger p
    ) {

        if (g.compareTo(TWO) < 0
                || g.compareTo(p.subtract(TWO)) > 0) {
            throw new IllegalArgumentException(
                "g phai nam trong khoang tu 2 den p-2!"
            );
        }
    }

    // Kiem tra khoa bi mat.
    private static void validatePrivateKey(
        BigInteger privateKey,
        BigInteger p,
        String name
    ) {

        if (privateKey.compareTo(ONE) < 0) {
            throw new IllegalArgumentException(
                name + " phai lon hon hoac bang 1!"
            );
        }

        if (privateKey.compareTo(p.subtract(ONE)) >= 0) {
            throw new IllegalArgumentException(
                name + " phai nho hon p-1!"
            );
        }
    }

    // Kiem tra khoa cong khai nhan duoc.
    private static void validatePublicKey(
        BigInteger publicKey,
        BigInteger p,
        String name
    ) {

        if (publicKey == null) {
            throw new IllegalArgumentException(
                name + " khong duoc de trong!"
            );
        }

        if (publicKey.compareTo(TWO) < 0
                || publicKey.compareTo(p.subtract(TWO)) > 0) {
            throw new IllegalArgumentException(
                name + " khong nam trong mien gia tri hop le!"
            );
        }

        // Kiem tra phan tu co thuoc nhom con mong muon khong.
        BigInteger q = p.subtract(ONE).divide(TWO);

        if (!publicKey.modPow(q, p).equals(ONE)) {
            throw new IllegalArgumentException(
                name + " khong thuoc nhom con bac q!"
            );
        }
    }

    // Kiem tra p, g va hai khoa bi mat.
    private static BigInteger[] validateAll(
        String pText,
        String gText,
        String aText,
        String bText
    ) {

        BigInteger p = parseNumber(pText, "p");
        BigInteger g = parseNumber(gText, "g");
        BigInteger a = parseNumber(aText, "Khoa bi mat A");
        BigInteger b = parseNumber(bText, "Khoa bi mat B");

        validatePrime(p);

        // Cau hinh nay su dung nhom so nguyen to an toan p = 2q + 1.
        BigInteger q = p.subtract(ONE).divide(TWO);

        if (!q.isProbablePrime(100)) {
            throw new IllegalArgumentException(
                "p khong phai so nguyen to an toan (p=2q+1, q nguyen to)!"
            );
        }

        validateGenerator(g, p);
        validatePrivateKey(a, p, "Khoa bi mat A");
        validatePrivateKey(b, p, "Khoa bi mat B");

        // Kiem tra g thuoc nhom con bac q.
        if (!g.modPow(q, p).equals(ONE)) {
            throw new IllegalArgumentException(
                "g khong thuoc nhom con bac q!"
            );
        }

        return new BigInteger[]{p, g, a, b};
    }

    // Tinh khoa cong khai A = g^a mod p.
    public static BigInteger calculatePublicKey(
        String pText,
        String gText,
        String privateKeyText
    ) {

        BigInteger p = parseNumber(pText, "p");
        BigInteger g = parseNumber(gText, "g");
        BigInteger a = parseNumber(
            privateKeyText,
            "Khoa bi mat"
        );

        validatePrime(p);

        BigInteger q = p.subtract(ONE).divide(TWO);

        if (!q.isProbablePrime(100)) {
            throw new IllegalArgumentException(
                "p phai la so nguyen to an toan!"
            );
        }

        validateGenerator(g, p);
        validatePrivateKey(a, p, "Khoa bi mat");

        if (!g.modPow(q, p).equals(ONE)) {
            throw new IllegalArgumentException(
                "g khong thuoc nhom con bac q!"
            );
        }

        BigInteger publicKey = g.modPow(a, p);

        validatePublicKey(publicKey, p, "Khoa cong khai");

        return publicKey;
    }

    // Tinh khoa bi mat chung cua A.
    public static BigInteger calculateSharedKeyA(
        String pText,
        String gText,
        String privateKeyAText,
        String privateKeyBText
    ) {

        BigInteger[] values = validateAll(
            pText,
            gText,
            privateKeyAText,
            privateKeyBText
        );

        BigInteger p = values[0];
        BigInteger g = values[1];
        BigInteger a = values[2];
        BigInteger b = values[3];

        BigInteger publicB = g.modPow(b, p);

        validatePublicKey(publicB, p, "Khoa cong khai B");

        BigInteger sharedKey = publicB.modPow(a, p);

        if (sharedKey.compareTo(ONE) <= 0) {
            throw new IllegalArgumentException(
                "Khoa chung khong hop le!"
            );
        }

        return sharedKey;
    }

    // Tinh khoa bi mat chung cua B.
    public static BigInteger calculateSharedKeyB(
        String pText,
        String gText,
        String privateKeyAText,
        String privateKeyBText
    ) {

        BigInteger[] values = validateAll(
            pText,
            gText,
            privateKeyAText,
            privateKeyBText
        );

        BigInteger p = values[0];
        BigInteger g = values[1];
        BigInteger a = values[2];
        BigInteger b = values[3];

        BigInteger publicA = g.modPow(a, p);

        validatePublicKey(publicA, p, "Khoa cong khai A");

        BigInteger sharedKey = publicA.modPow(b, p);

        if (sharedKey.compareTo(ONE) <= 0) {
            throw new IllegalArgumentException(
                "Khoa chung khong hop le!"
            );
        }

        return sharedKey;
    }

    // Tinh toan day du qua trinh trao doi khoa.
    public static String exchangeKeys(
        String pText,
        String gText,
        String privateKeyAText,
        String privateKeyBText
    ) {

        BigInteger[] values = validateAll(
            pText,
            gText,
            privateKeyAText,
            privateKeyBText
        );

        BigInteger p = values[0];
        BigInteger g = values[1];
        BigInteger a = values[2];
        BigInteger b = values[3];

        BigInteger publicA = g.modPow(a, p);
        BigInteger publicB = g.modPow(b, p);

        validatePublicKey(publicA, p, "Khoa cong khai A");
        validatePublicKey(publicB, p, "Khoa cong khai B");

        BigInteger sharedA = publicB.modPow(a, p);
        BigInteger sharedB = publicA.modPow(b, p);

        if (sharedA.compareTo(ONE) <= 0
                || sharedB.compareTo(ONE) <= 0) {
            throw new IllegalArgumentException(
                "Khoa chung khong hop le!"
            );
        }

        if (!sharedA.equals(sharedB)) {
            throw new IllegalStateException(
                "Hai khoa chung khong trung nhau!"
            );
        }

        return "Khoa cong khai A: " + publicA
            + "\nKhoa cong khai B: " + publicB
            + "\nKhoa chung cua A: " + sharedA
            + "\nKhoa chung cua B: " + sharedB
            + "\nKet qua: Hai ben da tao khoa chung trung khop!";
    }
}
