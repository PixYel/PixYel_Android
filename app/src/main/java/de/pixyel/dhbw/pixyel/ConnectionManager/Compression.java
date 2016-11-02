package de.pixyel.dhbw.pixyel.ConnectionManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

        import java.io.*;
        import android.util.Base64;
        import java.util.logging.Level;
        import java.util.logging.Logger;
        import java.util.zip.*;

public class Compression {

    /**
     * Compresses a String using the GZIP algorithm
     *
     * Using:
     * <p>
     * {@code //Compresses the String with gzip}
     * <p>
     * {@code String compressed = compress("Groooooooooooße Nachricht");}
     * <p>
     * <p>
     * {@code //Decompress the String}
     * <p>
     * {@code String decompressed = decompress(compressed);}
     * <p>
     * <p>
     * {@code System.out.println("Dekomprimiert: " + decompressed);}
     * <p>
     * <p>
     * @param toCompress The String to be compressed
     * @return The compressed String
     */
    public static String compress(String toCompress) {
        if (!isUTF8(toCompress)) {
            System.err.println("Error, String to compress is not in UTF8!");
            return "";
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzos = null;
        try {
            gzos = new GZIPOutputStream(baos);
            gzos.write(toCompress.getBytes("UTF8"));
        } catch (IOException e) {
            System.err.println("Could not compress String: " + e);
        } finally {
            if (gzos != null) {
                try {
                    gzos.close();
                } catch (IOException ignore) {
                }
            }
        }
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP).replaceAll("//r", "");
    }

    /**
     * Decompresses a String using the GZIP algorithm
     *
     * Using:
     * <p>
     * {@code //Compresses the String with gzip}
     * <p>
     * {@code String compressed = compress("Groooooooooooße Nachricht");}
     * <p>
     * <p>
     * {@code //Decompress the String}
     * <p>
     * {@code String decompressed = decompress(compressed);}
     * <p>
     * <p>
     * {@code System.out.println("Dekomprimiert: " + decompressed);}
     * <p>
     * <p>
     * @param toDecompress The String to be decompressed
     * @return The decompressed String
     */

    public static String decompress(String toDecompress) {
        InputStreamReader isr = null;
        try {
            byte[] input = Base64.decode(toDecompress, Base64.NO_WRAP);
            isr = new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(input)));
            StringWriter sw = new StringWriter();
            char[] chars = new char[1024];
            for (int len; (len = isr.read(chars)) > 0;) {
                sw.write(chars, 0, len);
            }
            return sw.toString();
        } catch (IOException ex) {
            Logger.getLogger(Compression.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                isr.close();
            } catch (IOException ex) {
                Logger.getLogger(Compression.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }

    private static boolean isUTF8(String toCheck) {
        byte[] pText = toCheck.getBytes();
        int expectedLength;

        for (int i = 0; i < pText.length; i++) {
            if ((pText[i] & 0b10000000) == 0b00000000) {
                expectedLength = 1;
            } else if ((pText[i] & 0b11100000) == 0b11000000) {
                expectedLength = 2;
            } else if ((pText[i] & 0b11110000) == 0b11100000) {
                expectedLength = 3;
            } else if ((pText[i] & 0b11111000) == 0b11110000) {
                expectedLength = 4;
            } else if ((pText[i] & 0b11111100) == 0b11111000) {
                expectedLength = 5;
            } else if ((pText[i] & 0b11111110) == 0b11111100) {
                expectedLength = 6;
            } else {
                return false;
            }

            while (--expectedLength > 0) {
                if (++i >= pText.length) {
                    return false;
                }
                if ((pText[i] & 0b11000000) != 0b10000000) {
                    return false;
                }
            }
        }

        return true;
    }
}
