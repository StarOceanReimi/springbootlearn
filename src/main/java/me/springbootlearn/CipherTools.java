package me.springbootlearn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by reimi on 11/19/17.
 */
public class CipherTools {

    private static Logger LOGGER = LoggerFactory.getLogger(CipherTools.class);

    private static final int SALT_BYTES = 8;
    private static final int SALT_ITERATIONS = 5001;
    private static final int KEY_BYTES = 24;
    private static final int IV_BYTES = 16;

    private static byte[] pbkdf2(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec passSpec = new PBEKeySpec(password, salt, SALT_ITERATIONS, KEY_BYTES *8);
        SecretKeyFactory pbkdfKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = pbkdfKeyFactory.generateSecret(passSpec);
        return key.getEncoded();
    }

    private static Cipher aesCipher(int mode, byte[] keyBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec aesKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        aesCipher.init(mode, aesKeySpec);
        return aesCipher;
    }

    private static Cipher aesCbcCipher(int mode, byte[] iv, byte[] keyBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec aesKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCipher.init(mode, aesKeySpec, ivSpec);
        return aesCipher;
    }

    private static byte[] combine(byte[]... byteArray) {
        int totalLength = Arrays.stream(byteArray)
                                .reduce(0,
                                        (p, bytes) -> p + bytes.length,
                                        (p1, p2) -> p1 + p2).intValue();
        byte[] result = new byte[totalLength];
        int count = 0;
        for(int i=0; i<byteArray.length; i++) {
            System.arraycopy(byteArray[i], 0, result, count, byteArray[i].length);
            count += byteArray[i].length;
        }
        return result;
    }

    private static byte[][] split(byte[] byteArray, int... sizes) {
        byte[][] result = new byte[sizes.length][];
        for(int i=0, count=0; i<sizes.length; i++) {
            byte[] inner = new byte[sizes[i]];
            System.arraycopy(byteArray, count, inner, 0, inner.length);
            count += sizes[i];
            result[i] = inner;
        }
        return result;
    }

    public static String encryptByPassword(String text, char[] password) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        byte[] keyBytes = new byte[0];
        try {
            keyBytes = pbkdf2(password, salt);
            byte[] toBeEncoded = text.getBytes();
            Cipher aesCipher = aesCipher(Cipher.ENCRYPT_MODE, keyBytes);
            byte[] secret = aesCipher.doFinal(toBeEncoded);
            byte[] outBytes = combine(salt, secret);
            return Base64.getEncoder().encodeToString(outBytes);
        } catch (Exception e) {
            LOGGER.error("Encryption Error:", e);
            throw new RuntimeException(e);
        }
    }

    public static String decryptByPassword(String cipherText, char[] password) {
        byte[] cipherBytes = Base64.getDecoder().decode(cipherText);
        int secretBytes = cipherBytes.length-SALT_BYTES;
        byte[][] parts = split(cipherBytes, SALT_BYTES, secretBytes);
        byte[] keyBytes = new byte[0];
        try {
            keyBytes = pbkdf2(password, parts[0]);
            Cipher aesCipher = aesCipher(Cipher.DECRYPT_MODE, keyBytes);
            byte[] plainBytes = aesCipher.doFinal(parts[1]);
            return new String(plainBytes);
        } catch (Exception e) {
            LOGGER.error("Decryption Error: ", e);
            throw new RuntimeException(e);
        }
    }

    public static String encryptByPasswordWithCbc(String text, char[] password) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        byte[] keyBytes = new byte[0];
        try {
            keyBytes = pbkdf2(password, salt);
            byte[] toBeEncoded = text.getBytes();
            byte[] iv = new byte[IV_BYTES];
            random.nextBytes(iv);
            Cipher aesCipher = aesCbcCipher(Cipher.ENCRYPT_MODE, iv, keyBytes);
            byte[] secret = aesCipher.doFinal(toBeEncoded);
            byte[] outBytes = combine(salt, iv, secret);
            return Base64.getEncoder().encodeToString(outBytes);
        } catch (Exception e) {
            LOGGER.error("Encryption Error:", e);
            throw new RuntimeException(e);
        }

    }

    public static String decryptByPasswordWithCbc(String cipherText, char[] password) {
        byte[] cipherBytes = Base64.getDecoder().decode(cipherText);
        int secretBytes = cipherBytes.length-SALT_BYTES-IV_BYTES;
        byte[][] parts = split(cipherBytes, SALT_BYTES, IV_BYTES, secretBytes);
        byte[] keyBytes = new byte[0];
        try {
            keyBytes = pbkdf2(password, parts[0]);
            Cipher aesCipher = aesCbcCipher(Cipher.DECRYPT_MODE, parts[1], keyBytes);
            byte[] plainBytes = aesCipher.doFinal(parts[2]);
            return new String(plainBytes);
        } catch (Exception e) {
            LOGGER.error("Decryption Error: ", e);
            throw new RuntimeException(e);
        }
    }

    public static char[] showPasswordInputPane() {
        JPasswordField pf = new JPasswordField();
        JOptionPane pane = new JOptionPane(pf, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = pane.createDialog("Please Input Password");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                pf.requestFocusInWindow();
            }
        });
        dialog.setVisible(true);
        try {
            if (pane.getValue() != null && pane.getValue().equals(JOptionPane.OK_OPTION))
                return pf.getPassword();
            return null;
        } finally {
            dialog.dispose();
        }
    }

    public static void main(String[] args)  {
        System.out.println(Arrays.toString(showPasswordInputPane()));
//        String pass = System.getProperty("dbaccess_pass");
//        System.out.println(decryptByPassword("H9yjnGxbKmiUempZDL5xE4TIbdciK66O", pass));
    }
}
