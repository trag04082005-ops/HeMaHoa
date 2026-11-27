
package hemahoa;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSASignature {

    private static final String ALGORITHM = "SHA256withRSA";

    private static final int DEFAULT_KEY_SIZE = 2048;

    private static final int MIN_KEY_SIZE = 2048;
    private static final int MAX_KEY_SIZE = 4096;

    private RSASignature() {
    }

    // Tạo cặp khóa RSA mặc định 2048 bit.
    public static KeyPair generateKeyPair() throws Exception {
        return generateKeyPair(DEFAULT_KEY_SIZE);
    }

    // Tạo cặp khóa RSA theo kích thước yêu cầu.
    public static KeyPair generateKeyPair(int keySize) throws Exception {

        if (keySize < MIN_KEY_SIZE
                || keySize > MAX_KEY_SIZE
                || keySize % 256 != 0) {

            throw new IllegalArgumentException(
                "Kích thước khóa RSA phải từ 2048 đến 4096 bit "
                + "và là bội số của 256!"
            );
        }

        KeyPairGenerator generator =
                KeyPairGenerator.getInstance("RSA");

        generator.initialize(keySize);

        return generator.generateKeyPair();
    }

    // Lấy khóa riêng dưới dạng Base64.
    public static String getPrivateKey(KeyPair keyPair) {

        if (keyPair == null || keyPair.getPrivate() == null) {
            throw new IllegalArgumentException(
                "Cặp khóa không hợp lệ!"
            );
        }

        return Base64.getEncoder().encodeToString(
                keyPair.getPrivate().getEncoded()
        );
    }

    // Lấy khóa công khai dưới dạng Base64.
    public static String getPublicKey(KeyPair keyPair) {

        if (keyPair == null || keyPair.getPublic() == null) {
            throw new IllegalArgumentException(
                "Cặp khóa không hợp lệ!"
            );
        }

        return Base64.getEncoder().encodeToString(
                keyPair.getPublic().getEncoded()
        );
    }

    // Kiểm tra và giải mã chuỗi Base64.
    private static byte[] decodeBase64(
            String value,
            String name) {

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                name + " không được để trống!"
            );
        }

        try {
            // Loại bỏ khoảng trắng và ký tự xuống dòng.
            String clean = value.replaceAll("\\s+", "");

            return Base64.getDecoder().decode(clean);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                name + " không đúng định dạng Base64!"
            );
        }
    }

    // Tạo đối tượng khóa riêng từ chuỗi Base64.
    private static PrivateKey decodePrivateKey(
            String privateKeyText) throws Exception {

        byte[] keyBytes = decodeBase64(
                privateKeyText,
                "Khóa riêng"
        );

        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory factory =
                KeyFactory.getInstance("RSA");

        return factory.generatePrivate(keySpec);
    }

    // Tạo đối tượng khóa công khai từ chuỗi Base64.
    private static PublicKey decodePublicKey(
            String publicKeyText) throws Exception {

        byte[] keyBytes = decodeBase64(
                publicKeyText,
                "Khóa công khai"
        );

        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(keyBytes);

        KeyFactory factory =
                KeyFactory.getInstance("RSA");

        return factory.generatePublic(keySpec);
    }

    // Ký số dữ liệu bằng khóa riêng.
    public static String sign(
            String text,
            String privateKeyText) throws Exception {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(
                "Nội dung cần ký không được để trống!"
            );
        }

        PrivateKey privateKey =
                decodePrivateKey(privateKeyText);

        Signature signature =
                Signature.getInstance(ALGORITHM);

        signature.initSign(privateKey);

        signature.update(
                text.getBytes(StandardCharsets.UTF_8)
        );

        byte[] signedData = signature.sign();

        return Base64.getEncoder().encodeToString(
                signedData
        );
    }

    // Xác thực chữ ký số.
    public static boolean verify(
            String text,
            String signatureText,
            String publicKeyText) throws Exception {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(
                "Nội dung cần xác thực không được để trống!"
            );
        }

        PublicKey publicKey =
                decodePublicKey(publicKeyText);

        byte[] signatureBytes = decodeBase64(
                signatureText,
                "Chữ ký số"
        );

        Signature signature =
                Signature.getInstance(ALGORITHM);

        signature.initVerify(publicKey);

        signature.update(
                text.getBytes(StandardCharsets.UTF_8)
        );

        return signature.verify(signatureBytes);
    }

    // Trả về thông báo kết quả xác thực.
    public static String verifyResult(
            String text,
            String signatureText,
            String publicKeyText) throws Exception {

        boolean valid = verify(
                text,
                signatureText,
                publicKeyText
        );

        if (valid) {
            return "Chữ ký hợp lệ! "
                    + "Nội dung chưa bị thay đổi.";
        }

        return "Chữ ký không hợp lệ! "
                + "Nội dung có thể đã bị thay đổi "
                + "hoặc chữ ký không đúng.";
    }
}
