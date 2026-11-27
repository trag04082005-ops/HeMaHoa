
package hemahoa;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;

import java.util.Arrays;
import java.util.Base64;

public class DES {

    private static final SecureRandom RNG = new SecureRandom();

    public static final String FMT_TEXT = "Văn bản";
    public static final String FMT_HEX = "Hex";

    public static final String PAD_PKCS5 = "PKCS5";
    public static final String PAD_NONE = "Không đệm";

    private static final int BLOCK_SIZE = 8;

    // =====================================================
    // 1. CHUYỂN ĐỔI HEX VÀ BYTE
    // =====================================================

    private static String bytesToHex(byte[] data) {

        StringBuilder sb = new StringBuilder();

        for (byte b : data) {
            sb.append(String.format("%02X", b & 0xFF));
        }

        return sb.toString();
    }

    private static byte[] hexToBytes(String hex) {

        byte[] result = new byte[hex.length() / 2];

        for (int i = 0; i < result.length; i++) {

            result[i] = (byte) Integer.parseInt(
                hex.substring(i * 2, i * 2 + 2), 16
            );
        }

        return result;
    }

    // =====================================================
    // 2. KIỂM TRA KHÓA DES
    // =====================================================

    private static byte[] desKey(String key) {

        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Chưa nhập khóa DES."
            );
        }

        // Loại bỏ khoảng trắng ở đầu và cuối khóa.
        String t = key.trim();

        byte[] keyBytes;

        // Khóa Hex: đúng 16 ký tự.
        if (t.matches("[0-9A-Fa-f]{16}")) {

            keyBytes = hexToBytes(t);

        } else {

            // Khóa UTF-8: đúng 8 byte.
            keyBytes = t.getBytes(StandardCharsets.UTF_8);

            if (keyBytes.length != BLOCK_SIZE) {

                throw new IllegalArgumentException(
                    "Khóa DES phải đúng 8 byte UTF-8 "
                    + "hoặc 16 ký tự Hex. Khóa hiện tại dài "
                    + keyBytes.length + " byte."
                );
            }
        }

        // Kiểm tra khóa yếu và bán yếu.
        try {

            if (DESKeySpec.isWeak(keyBytes, 0)) {

                throw new IllegalArgumentException(
                    "Khóa DES yếu hoặc bán yếu. "
                    + "Vui lòng sử dụng khóa khác."
                );
            }

        } catch (InvalidKeyException e) {

            throw new IllegalArgumentException(
                "Khóa DES không hợp lệ.", e
            );
        }

        return keyBytes;
    }

    // =====================================================
    // 3. SINH KHÓA NGẪU NHIÊN
    // =====================================================

    public static String randomKey() {

        while (true) {

            byte[] key = new byte[BLOCK_SIZE];

            RNG.nextBytes(key);

            try {

                if (!DESKeySpec.isWeak(key, 0)) {
                    return bytesToHex(key);
                }

            } catch (InvalidKeyException ignored) {
                // Sinh lại nếu khóa không hợp lệ.
            }
        }
    }

    public static String desRandomKey() {
        return randomKey();
    }

    // =====================================================
    // 4. THÔNG TIN KHÓA
    // =====================================================

    public static String desInfo(String key) {

        if (key == null || key.trim().isEmpty()) {
            return null;
        }

        byte[] keyBytes = desKey(key);

        return "Khóa 64 bit (56 bit hiệu dụng): "
            + bytesToHex(keyBytes);
    }

    // =====================================================
    // 5. KIỂM TRA VÀ ĐỌC HEX
    // =====================================================

    private static byte[] parseHex(
            String text, String name) {

        String t = text == null
            ? ""
            : text.replaceAll("\\s+", "");

        if (t.startsWith("0x") || t.startsWith("0X")) {
            t = t.substring(2);
        }

        if (t.isEmpty()) {

            throw new IllegalArgumentException(
                name + " không được để trống."
            );
        }

        if (!t.matches("[0-9A-Fa-f]+")) {

            throw new IllegalArgumentException(
                name + " chứa ký tự không phải Hex."
            );
        }

        if (t.length() % 2 != 0) {

            throw new IllegalArgumentException(
                name + " phải có số lượng ký tự Hex chẵn."
            );
        }

        return hexToBytes(t);
    }

    // =====================================================
    // 6. GIẢI MÃ UTF-8 NGHIÊM NGẶT
    // =====================================================

    private static String decodeUTF8(byte[] data) {

        try {

            return StandardCharsets.UTF_8
                .newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(data))
                .toString();

        } catch (CharacterCodingException e) {

            throw new IllegalArgumentException(
                "Dữ liệu sau giải mã không phải UTF-8 hợp lệ.",
                e
            );
        }
    }

    // =====================================================
    // 7. ĐỌC BASE64
    // =====================================================

    private static byte[] parseBase64(String text) {

        if (text == null || text.trim().isEmpty()) {

            throw new IllegalArgumentException(
                "Bản mã Base64 không được để trống."
            );
        }

        String clean = text.replaceAll("\\s+", "");

        try {

            byte[] result = Base64.getDecoder().decode(clean);

            if (result.length == 0) {

                throw new IllegalArgumentException(
                    "Dữ liệu mã hóa đang rỗng."
                );
            }

            return result;

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                "Bản mã Base64 không hợp lệ.", e
            );
        }
    }

    // =====================================================
    // 8. TẠO SECRET KEY
    // =====================================================

    private static SecretKey createSecretKey(byte[] key)
            throws GeneralSecurityException {

        return SecretKeyFactory.getInstance("DES")
            .generateSecret(new DESKeySpec(key));
    }

    // =====================================================
    // 9. HÀM TƯƠNG THÍCH VỚI CODE CŨ
    // =====================================================

    public static String encrypt(String text, String key) {

        return des(
            text,
            key,
            "ECB",
            true,
            FMT_TEXT,
            PAD_PKCS5
        );
    }

    public static String decrypt(String text, String key) {

        return des(
            text,
            key,
            "ECB",
            false,
            FMT_TEXT,
            PAD_PKCS5
        );
    }

    // =====================================================
    // 10. HÀM DES MẶC ĐỊNH
    // =====================================================

    public static String des(
            String text,
            String key,
            String mode,
            boolean enc) {

        return des(
            text,
            key,
            mode,
            enc,
            FMT_TEXT,
            PAD_PKCS5
        );
    }

    // =====================================================
    // 11. THUẬT TOÁN DES
    // =====================================================

    public static String des(
            String text,
            String keyStr,
            String mode,
            boolean enc,
            String format,
            String padding) {

        // Kiểm tra dữ liệu đầu vào.
        if (text == null || text.isEmpty()) {

            throw new IllegalArgumentException(
                "Dữ liệu không được để trống."
            );
        }

        // Kiểm tra khóa.
        byte[] key = desKey(keyStr);

        // Kiểm tra định dạng.
        boolean hex = FMT_HEX.equals(format);

        if (!hex && !FMT_TEXT.equals(format)) {

            throw new IllegalArgumentException(
                "Dạng dữ liệu phải là Văn bản hoặc Hex."
            );
        }

        // Kiểm tra kiểu đệm.
        boolean pad = PAD_PKCS5.equals(padding);

        if (!pad && !PAD_NONE.equals(padding)) {

            throw new IllegalArgumentException(
                "Kiểu đệm phải là PKCS5 hoặc Không đệm."
            );
        }

        // Kiểm tra chế độ hoạt động.
        if (!"ECB".equals(mode) && !"CBC".equals(mode)) {

            throw new IllegalArgumentException(
                "Chế độ DES phải là ECB hoặc CBC."
            );
        }

        boolean cbc = "CBC".equals(mode);

        // Đọc dữ liệu đầu vào.
        byte[] input;

        if (enc) {

            input = hex
                ? parseHex(text, "Dữ liệu cần mã hóa")
                : text.getBytes(StandardCharsets.UTF_8);

        } else {

            input = hex
                ? parseHex(text, "Bản mã")
                : parseBase64(text);
        }

        try {

            SecretKey secretKey = createSecretKey(key);

            String transformation =
                "DES/" + mode + "/"
                + (pad ? "PKCS5Padding" : "NoPadding");

            Cipher cipher = Cipher.getInstance(transformation);

            // =================================================
            // 12. MÃ HÓA DES
            // =================================================

            if (enc) {

                // NoPadding yêu cầu dữ liệu chia hết cho 8 byte.
                if (!pad && input.length % BLOCK_SIZE != 0) {

                    throw new IllegalArgumentException(
                        "NoPadding yêu cầu dữ liệu có độ dài "
                        + "là bội số của 8 byte. Hiện tại có "
                        + input.length + " byte."
                    );
                }

                byte[] output;

                if (cbc) {

                    // Sinh IV ngẫu nhiên 8 byte.
                    byte[] iv = new byte[BLOCK_SIZE];

                    RNG.nextBytes(iv);

                    cipher.init(
                        Cipher.ENCRYPT_MODE,
                        secretKey,
                        new IvParameterSpec(iv)
                    );

                    byte[] encrypted = cipher.doFinal(input);

                    // Ghép IV vào đầu bản mã.
                    output = new byte[BLOCK_SIZE + encrypted.length];

                    System.arraycopy(
                        iv, 0,
                        output, 0,
                        BLOCK_SIZE
                    );

                    System.arraycopy(
                        encrypted, 0,
                        output, BLOCK_SIZE,
                        encrypted.length
                    );

                } else {

                    cipher.init(
                        Cipher.ENCRYPT_MODE,
                        secretKey
                    );

                    output = cipher.doFinal(input);
                }

                // Xuất kết quả theo định dạng đã chọn.
                return hex
                    ? bytesToHex(output)
                    : Base64.getEncoder().encodeToString(output);
            }

            // =================================================
            // 13. GIẢI MÃ DES
            // =================================================

            byte[] decrypted;

            if (cbc) {

                // IV 8 byte + ít nhất 1 khối mã hóa 8 byte.
                if (input.length < 16
                        || input.length % BLOCK_SIZE != 0) {

                    throw new IllegalArgumentException(
                        "Bản mã CBC không hợp lệ. "
                        + "Cần IV 8 byte và dữ liệu mã hóa "
                        + "có độ dài là bội số của 8 byte."
                    );
                }

                // Lấy IV từ 8 byte đầu tiên.
                byte[] iv = Arrays.copyOfRange(
                    input, 0, BLOCK_SIZE
                );

                cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    new IvParameterSpec(iv)
                );

                // Giải mã phần dữ liệu phía sau IV.
                decrypted = cipher.doFinal(
                    input,
                    BLOCK_SIZE,
                    input.length - BLOCK_SIZE
                );

            } else {

                // ECB phải có độ dài là bội số của 8 byte.
                if (input.length == 0
                        || input.length % BLOCK_SIZE != 0) {

                    throw new IllegalArgumentException(
                        "Bản mã ECB phải có độ dài "
                        + "là bội số của 8 byte."
                    );
                }

                cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey
                );

                decrypted = cipher.doFinal(input);
            }

            // Trả về kết quả.
            return hex
                ? bytesToHex(decrypted)
                : decodeUTF8(decrypted);

        } catch (BadPaddingException e) {

            throw new IllegalArgumentException(
                "Giải mã thất bại: sai khóa, sai chế độ "
                + "(ECB/CBC), sai kiểu đệm hoặc bản mã bị hỏng.",
                e
            );

        } catch (GeneralSecurityException e) {

            throw new IllegalArgumentException(
                "Lỗi DES: " + e.getMessage(), e
            );
        }
    }
}
