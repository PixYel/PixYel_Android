package de.pixyel.dhbw.pixyel.ConnectionManager;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import android.util.Base64;

/**
 * @author Josua Frank
 *
 */
public class Encryption {

    static int keyLength = 2048;

    /**
     * Generates the Key-Pair which contains the public and the private key<p>
     *
     * IMPORTANT: DO NOT SEND DATA LARGER THAN 526 GB!!!!! (because Max Integer,
     * but if you need to, change all ints to longs)
     *
     * Using:
     * <p>
     * {@code //Generate KeyPair (PublicKey AND PrivateKey)}
     * <p>
     * {@code String[] keyPair = generateKeyPair();}
     * <p>
     * <p>
     * {@code //Encryptes the text with the public key}
     * <p>
     * {@code String encrypted = encrypt("Geheime Nachricht", keyPair[0]);}
     * <p>
     * <p>
     * {@code //Decrypted the text with the private key}
     * <p>
     * {@code String decrypted = decrypt(encrypted, keyPair[1]);}
     * <p>
     * <p>
     * {@code System.out.println("Entschlüsselt: " + decrypted);}
     * <p>
     * <p>
     * @return A StringArray which contains the first the public and second the
     * private key
     */
    public static String[] generateKeyPair() {
        String[] result = new String[2];
        try {
            // Get an instance of the RSA/ECB/PKCS1Padding key generator
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            //Initialize it with 2048 Bit Encryption (keysize)
            kpg.initialize(keyLength);
            // Generate the keys — might take sometime on slow computers
            KeyPair kp = kpg.generateKeyPair();
            //Gets the encoded public and private Keys as String
            result[0] = Base64.encodeToString(kp.getPublic().getEncoded(), Base64.NO_WRAP);//PublicKey
            result[1] = Base64.encodeToString(kp.getPrivate().getEncoded(), Base64.NO_WRAP);//PrivateKey
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Could not create KeyPair: " + ex);
        }
        return result;
    }

    /**
     * Encryptes text with a public key
     *
     * IMPORTANT: DO NOT SEND DATA LARGER THAN 526 GB!!!!! (because Max Integer,
     * but if you need to, change all ints to longs)
     *
     * Using:
     * <p>
     * {@code //Generate KeyPair (PublicKey AND PrivateKey)}
     * <p>
     * {@code String[] keyPair = generateKeyPair();}
     * <p>
     * <p>
     * {@code //Encryptes the text with the public key}
     * <p>
     * {@code String encrypted = encrypt("Geheime Nachricht", keyPair[0]);}
     * <p>
     * <p>
     * {@code //Decrypted the text with the private key}
     * <p>
     * {@code String decrypted = decrypt(encrypted, keyPair[1]);}
     * <p>
     * <p>
     * {@code System.out.println("Entschlüsselt: " + decrypted);}
     * <p>
     * <p>
     * @param text The text as String to encrypt
     * @param publicKey The public key from the KeyPair to encrypt with in
     * BASE64 encoded!!
     * @return The result of the encryption as a BASE64 encoded String
     */
    public static String encrypt(String text, String publicKey) {
        try {
            //Creates the Public Key from the String
            PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(publicKey, Base64.NO_WRAP)));
            // Get an instance of the Cipher for RSA/ECB/PKCS1Padding encryption/decryption
            Cipher encrypter = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            // Initiate the Cipher, telling it that it is going to Encrypt, giving it the public key
            encrypter.init(Cipher.ENCRYPT_MODE, pubKey);

            byte[] textAsBytes = text.getBytes("UTF8");
            //I dont know why but this is the max amount of encryption data
            int encryptedKeyLength = (keyLength / 8);
            int maxBytesToEncrypt = encryptedKeyLength - 11;
            int amountOfSplitting = textAsBytes.length / maxBytesToEncrypt;
            if (textAsBytes.length % maxBytesToEncrypt != 0) {
                amountOfSplitting += 1;
            }
            int residual;

            //String to byte Array
            byte[][] textAsList = new byte[amountOfSplitting][maxBytesToEncrypt];
            byte[] encrypted = new byte[amountOfSplitting * encryptedKeyLength];

            for (int i = 0; i < amountOfSplitting; i++) {
                //Split the byte array into the right length to encrypt it
                if ((i + 1) * maxBytesToEncrypt <= textAsBytes.length) {
                    System.arraycopy(textAsBytes, i * maxBytesToEncrypt, textAsList[i], 0, maxBytesToEncrypt);
                } else {
                    textAsList[i] = new byte[(residual = textAsBytes.length % maxBytesToEncrypt)];
                    System.arraycopy(textAsBytes, i * maxBytesToEncrypt, textAsList[i], 0, residual);
                }
                //encrypt every byte array
                textAsList[i] = encrypter.doFinal(textAsList[i]);
                //copy the encrypted byte arrays into one single byte array
                System.arraycopy(textAsList[i], 0, encrypted, i * encryptedKeyLength, encryptedKeyLength);
            }

            //Returns the encrypted byte array as a string
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException ex) {
            System.err.println("Could String not encrypt: " + ex);
        } catch (UnsupportedEncodingException ex) {
            System.err.println("UFT-8 not supported");
        }
        return "";
    }

    /**
     * Decryptes a byte-array with a private key
     *
     * IMPORTANT: DO NOT SEND DATA LARGER THAN 526 GB!!!!! (because Max Integer,
     * but if you need to, change all ints to longs)
     *
     * Using:
     * <p>
     * {@code //Generate KeyPair (PublicKey AND PrivateKey)}
     * <p>
     * {@code String[] keyPair = generateKeyPair();}
     * <p>
     * <p>
     * {@code //Encryptes the text with the public key}
     * <p>
     * {@code String encrypted = encrypt("Geheime Nachricht", keyPair[0]);}
     * <p>
     * <p>
     * {@code //Decrypted the text with the private key}
     * <p>
     * {@code String decrypted = decrypt(encrypted, keyPair[1]);}
     * <p>
     * <p>
     * {@code System.out.println("Entschlüsselt: " + decrypted);}
     * <p>
     * <p>
     * @param toDecrypt The String to be encrypted as a BASE64 String
     * @param privateKey The private Key from the KeyPair (in BASE64!) to decrypt with
     * @return The result of the decryption as String
     */
    public static String decrypt(String toDecrypt, String privateKey) {
        try {
            //Generates the Private Key from the byteArray
            PrivateKey privKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(privateKey, Base64.NO_WRAP)));
            // Get an instance of the Cipher for RSA/ECB/PKCS1Padding encryption/decryption
            Cipher decrypter = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            // Initiate the Cipher, telling it that it is going to Decrypt, giving it the private key
            decrypter.init(Cipher.DECRYPT_MODE, privKey);

            //converts the encrypted String to a encrypted byte array
            byte[] encrypted = Base64.decode(toDecrypt, Base64.NO_WRAP);

            //I dont know why but this is the max amount of encryption data
            int encryptedKeyLength = (keyLength / 8);
            int maxBytesToEncrypt = encryptedKeyLength - 11;
            int amountOfSplitting = encrypted.length / encryptedKeyLength;
            int textLength = (amountOfSplitting - 1) * maxBytesToEncrypt;//Not the final value!
            boolean once = true;

            byte[][] textAsList = new byte[amountOfSplitting][encryptedKeyLength];
            byte[] textAsBytes = null;

            for (int i = amountOfSplitting - 1; i >= 0; i--) {//Count downwards because this way you know in the first run the amount of bytes of the last splitted array
                //splitts the array so that the individual arrays can be decrypted, on large array would not fit in the RSA/ECB/PKCS1Padding keylenth
                System.arraycopy(encrypted, i * encryptedKeyLength, textAsList[i], 0, encryptedKeyLength);
                //decrypt every byte array
                textAsList[i] = decrypter.doFinal(textAsList[i]);
                if (once) {//determines the length of the final string, this is done only in the first run of the for.
                    once = false;
                    //merges the arrays to one single array
                    if (textAsList[textAsList.length - 1].length != maxBytesToEncrypt) {//If the last array of the textlist is != maxbytesToEncrypt (e.g. 245)
                        textLength += textAsList[textAsList.length - 1].length;//add the length of the last array to finalize the value of textLength
                    } else {
                        textLength += maxBytesToEncrypt;//simply add another maxbytestoencrypt, very rare case, because the textLength needs to be textLength % mayBytesToEncrypt (e.g.245) = 0
                    }
                    textAsBytes = new byte[textLength];
                }
                //merges the text as list to one single textarray
                if (((i + 1) * maxBytesToEncrypt) <= textAsList[i].length) {
                    System.arraycopy(textAsList[i], 0, textAsBytes, i * maxBytesToEncrypt, maxBytesToEncrypt);
                } else {
                    System.arraycopy(textAsList[i], 0, textAsBytes, i * maxBytesToEncrypt, textAsList[i].length);
                }
            }
            return new String(textAsBytes, "UTF8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException ex) {
            System.err.println("Could not decrypt byte-array: " + ex);
        } catch (UnsupportedEncodingException ex) {
            System.err.println("UTF-8 not supported!");
        }
        return "";
    }

}