package sqlite;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

// code was copied from https://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example (BullyWiiPlaza's contribution)

// TODO: remove suppress warnings annotation
@SuppressWarnings("all")
public class AesEncryptionHelper {

    private static final String ALGORITHM = "AES";

    /**
     * Encrypts the given plain text
     *
     * @param plainText The plain text to encrypt
     * @param key The key used to encrypt data
     */
    public byte[] encrypt(byte[] plainText, byte[] key) throws Exception {

        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(plainText);
    }

    /**
     * Decrypts the given byte array
     *
     * @param cipherText The data to decrypt
     * @param key The key used to decrypt data
     */
    public byte[] decrypt(byte[] cipherText, byte[] key) throws Exception {

        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(cipherText);
    }

}
