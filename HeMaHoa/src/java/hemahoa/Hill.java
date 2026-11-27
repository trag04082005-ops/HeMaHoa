/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package hemahoa;

import java.text.Normalizer;
import java.util.Locale;

public class Hill {

    private static final int MOD = 26;

    // Chuan hoa so modulo 26
    private static int mod(int x) {
        return ((x % MOD) + MOD) % MOD;
    }

    // Chuan hoa van ban ve chu cai A-Z
    private static String normalizeText(String text) {
        if (text == null) {
            throw new IllegalArgumentException(
                "Van ban khong duoc de trong!"
            );
        }

        String s = text.toUpperCase(Locale.ROOT);

        // Chuyen chu D co gach thanh D
        s = s.replace("Đ", "D");

        // Loai bo dau tieng Viet
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("\\p{M}", "");

        // Chi giu chu cai A-Z
        s = s.replaceAll("[^A-Z]", "");

        return s;
    }

    // Tim UCLN
    private static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }

        return Math.abs(a);
    }

    // Tim nghich dao modulo 26
    private static int modInverse(int a) {
        a = mod(a);

        for (int i = 1; i < MOD; i++) {
            if (mod(a * i) == 1) {
                return i;
            }
        }

        throw new IllegalArgumentException(
            "Khong tim duoc nghich dao modulo 26!"
        );
    }

    // Tinh dinh thuc ma tran
    private static int determinant(int[][] matrix) {
        int n = matrix.length;

        if (n == 1) {
            return matrix[0][0];
        }

        if (n == 2) {
            return matrix[0][0] * matrix[1][1]
                 - matrix[0][1] * matrix[1][0];
        }

        int det = 0;

        for (int col = 0; col < n; col++) {
            int[][] minor = getMinor(matrix, 0, col);

            int sign = (col % 2 == 0) ? 1 : -1;

            det += sign * matrix[0][col]
                       * determinant(minor);
        }

        return det;
    }

    // Tao ma tran con
    private static int[][] getMinor(
        int[][] matrix, int row, int col
    ) {
        int n = matrix.length;
        int[][] minor = new int[n - 1][n - 1];

        int r = 0;

        for (int i = 0; i < n; i++) {
            if (i == row) {
                continue;
            }

            int c = 0;

            for (int j = 0; j < n; j++) {
                if (j == col) {
                    continue;
                }

                minor[r][c++] = matrix[i][j];
            }

            r++;
        }

        return minor;
    }

    // Kiem tra ma tran khoa
    private static void validateKey(int[][] key) {

        if (key == null) {
            throw new IllegalArgumentException(
                "Ma tran khoa khong duoc de trong!"
            );
        }

        int n = key.length;

        if (n < 2 || n > 4) {
            throw new IllegalArgumentException(
                "Chi ho tro ma tran 2x2, 3x3 va 4x4!"
            );
        }

        for (int i = 0; i < n; i++) {

            if (key[i] == null || key[i].length != n) {
                throw new IllegalArgumentException(
                    "Ma tran khoa phai co dang vuong!"
                );
            }

            for (int j = 0; j < n; j++) {

                if (key[i][j] < 0 || key[i][j] > 25) {
                    throw new IllegalArgumentException(
                        "Gia tri khoa phai nam trong khoang 0 den 25!"
                    );
                }
            }
        }

        int det = mod(determinant(key));

        if (gcd(det, MOD) != 1) {
            throw new IllegalArgumentException(
                "Khoa khong hop le! Dinh thuc phai nguyen to "
                + "cung nhau voi 26."
            );
        }
    }

    // Tinh ma tran nghich dao modulo 26
    private static int[][] inverseMatrix(int[][] key) {

        validateKey(key);

        int n = key.length;

        int det = mod(determinant(key));
        int detInverse = modInverse(det);

        int[][] inverse = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                int cofactor = determinant(
                    getMinor(key, i, j)
                );

                if ((i + j) % 2 != 0) {
                    cofactor = -cofactor;
                }

                // Ma tran phu hop la ma tran chuyen vi cua phan bu
                inverse[j][i] = mod(
                    detInverse * cofactor
                );
            }
        }

        return inverse;
    }

    // Ma hoa Hill
    public static String encrypt(
        String plaintext, int[][] key
    ) {

        validateKey(key);

        String input = normalizeText(plaintext);

        if (input.isEmpty()) {
            throw new IllegalArgumentException(
                "Van ban khong co ky tu A-Z de ma hoa!"
            );
        }

        int n = key.length;

        // Them X de du so ky tu trong mot khoi
        while (input.length() % n != 0) {
            input += "X";
        }

        StringBuilder result = new StringBuilder();

        // Xu ly tung khoi
        for (int i = 0; i < input.length(); i += n) {

            for (int row = 0; row < n; row++) {

                int sum = 0;

                for (int col = 0; col < n; col++) {

                    int value = input.charAt(i + col) - 'A';

                    sum += key[row][col] * value;
                }

                result.append(
                    (char) ('A' + mod(sum))
                );
            }
        }

        return result.toString();
    }

    // Giai ma Hill
    // Khong tu dong xoa X cuoi
    public static String decrypt(
        String ciphertext, int[][] key
    ) {

        validateKey(key);

        String input = normalizeText(ciphertext);

        if (input.isEmpty()) {
            throw new IllegalArgumentException(
                "Ban ma khong co ky tu A-Z!"
            );
        }

        int n = key.length;

        if (input.length() % n != 0) {
            throw new IllegalArgumentException(
                "Do dai ban ma phai chia het cho " + n + "!"
            );
        }

        int[][] inverse = inverseMatrix(key);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i += n) {

            for (int row = 0; row < n; row++) {

                int sum = 0;

                for (int col = 0; col < n; col++) {

                    int value = input.charAt(i + col) - 'A';

                    sum += inverse[row][col] * value;
                }

                result.append(
                    (char) ('A' + mod(sum))
                );
            }
        }

        return result.toString();
    }

    // Giai ma va cat phan X dem theo do dai ban ro goc
    public static String decrypt(
        String ciphertext, int[][] key, int originalLength
    ) {

        String result = decrypt(ciphertext, key);

        if (originalLength < 1 ||
            originalLength > result.length()) {

            throw new IllegalArgumentException(
                "Do dai ban ro ban dau khong hop le!"
            );
        }

        if (result.length() - originalLength >= key.length) {
            throw new IllegalArgumentException(
                "So ky tu dem khong hop le!"
            );
        }

        return result.substring(0, originalLength);
    }

    // Lay dinh thuc modulo 26 de hien thi
    public static int getDeterminant(int[][] key) {
        validateKey(key);
        return mod(determinant(key));
    }

    // Lay ma tran nghich dao de kiem tra
    public static int[][] getInverseMatrix(int[][] key) {
        return inverseMatrix(key);
    }
    
public static int getNormalizedLength(String text) {
    return normalizeText(text).length();
}

}

