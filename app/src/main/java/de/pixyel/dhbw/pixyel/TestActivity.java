package de.pixyel.dhbw.pixyel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import de.pixyel.dhbw.pixyel.ConnectionManager.Compression;
import de.pixyel.dhbw.pixyel.ConnectionManager.ConnectionManager;
import de.pixyel.dhbw.pixyel.ConnectionManager.Encryption;
import de.pixyel.dhbw.pixyel.ConnectionManager.XML;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent = getIntent();
    }

    public void buttonConnect(View view) {
        new Thread(new Runnable() {
            public void run() {

                ConnectionManager manager = new ConnectionManager();

                //Ping dient nur zum Überprüfen der Socketverbindung, NICHT zum Überprüfen der Verschlüsselung und Compression
                //ping();
                //**Verbindung zum Server herstellen ENTWEDER Ping ODER connect aufrufen!!!!!!!!!!!11!!!!!elf!!
                manager.connect("Jan");
                //**Beispiel: sendet ein xml mit dem node "echo" an den Server, der server schickt daraufhin selbiges zurück
                manager.sendToServer(XML.createNewXML("echo").toXMLString());
                //**Wenn man die App schließt oder ähnliches, einfach die disconnect Methode aufrufen */

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //  manager.disconnect();

            }
        }).start();

    }

    public void buttonTest(View view) {
        String[] keys = Encryption.generateKeyPair();
      /* String toCompress = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
        String compressed = Compression.compress(toCompress);
        String encrypted = Encryption.encrypt(compressed, keys[0]);
        String decrypted = Encryption.decrypt(encrypted, keys[1]);
        String decompressed = Compression.decompress(decrypted);
        System.out.println(decompressed);

        private key: MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC3C8phKjWkKOjLy5NI68J2Q5YCnHIWNta8z2isTu0lDZLc/Sivj/cadP9dA9dWyy08RWSFkOuq7QsplH2+QYAA7MUHSpD4khIyp6Uee3IM23h/kCEruEJ65M0w9ECR8syhlWHCOuMezCBHDm8Hcl4Yy+0rbm7pSFvCgwOeloZazmm/j3dlssfLiqn8BfntJgMETXEN15pnEx+QpYBN/98is/UIrkesQWMghUxs6mW6fORQNRYT81f67cBdgMOwzRqFXZZoLT0AtL1v7v+/uaGRPxT3/t2WUSVpUv/lrAZJgZU/t5IZ026SCN8Wckurd3+p2nEbuO2hAe1DTGDd84xFAgMBAAECggEARPZhOfhAG2f6RQWeqOOB8TKHatZsOShSAPKXeguGVuZlRXLviC1SKJqZtv6Ce8WydjmipJuz3kLOaMz0DuD4i+CoXpXeRngp+QGmOPTDRErPfCfeB221Rj27fM0ZIqN6KwpvuVCcTvcS1hJ/DCwvfWIfKbTCfotvXroje/U0CzOED4J5F5DmgdIKl4NZ8KVMU623db9LTiJve4Fnzeg328okmKP30yjT9xo5nPY3qDP3wQc6JhULIz0/mneP/xcNmoTCXyRV4llWjd/DOpghLezG15Uis8X95tTKKU5DOHNYuW9FOSANmUEV5r78s0CVzLFO4y7YkBThAdidw3FRwQKBgQDaOIoAROYlMuuadVY9hDMA85yiIvFlehpwG7WOwWeHQbxKdQ5ZwuINA/p+nmfr2wFpR2hkr5TPc//sviNLP68HULnE4FnPJs7mzNGWYHevGCgrcOKU4A1u2kHcTuQw819SGytaULf1xfEB75vt6Y8MvvlEhxFi88AzNEmrOmSqEQKBgQDWvFGHvMxyoCnlckhUaDXspRREvIZqNAGdx5qMWlHXn+rre8hRnuwdI6R1KtxMnzwcZNkkrB1onw/jPWpdGCMzN2+Afn9idbfrPIocOA/xpJrmtDOpSChtccpduOMt14sPzUy/ZZnvX+T8Wel7AObkpJJxWPfAmi9lHX+w9lYq9QKBgQCv8lBKgULzEUhnpF3hgbrB9KoBDJ2IDVhNtfnAXW5E3xqbCiCE/R3YErIg70WpJE7R/NJOBEe+sK/TyUaZwII2Kr9t3tpqrMa7zr7aOXOmiJqy0/TMxo3uKCpcIHvagBAGGoPs8RIyJuP4hKI7xEojo8NFnoXFO/VnE6ymU5GgkQKBgQCc6gvh1pLHiEZVRkeYdnZBMPg4qlLGtK4hr8Q9n5qCqHcwEMn26ty7Blqcy+8jhpCUZxVN+P+cfe9MLIrW8FZ4jJ/ME6PePQtiuGBr8oOkHpaa0z+VCgeQwkAQcJSB4SnbKDhW/FWaWEaqKMG2gpg2qBPjBBI3CfRsPTwcAkZBKQKBgDb+ZhipNduRaUEEEGUpFbRCKhdQSeUlSHnH5xwD2AtXz2tcpFPJOnsJiZMsVBGnKswBCYPUFXIS5O5TW9dTJxpXq1o4bUpDGqetHnNCosJGzY32kgY55PGMUhlXwYjrJR03Ow/2e1oH3EFJsJdkW7YxWESLy9zyy7l8yTGF2mwg
    */
        String toDecrypt = "GYpbWl+/oa3pTL91n9fmtAw1S1/D9yo040MI9GjveEjQU0xHsG2OhcO+YOtXb3IiXrqYmz7gRWUzYXh9QdPzOx0TBvor+QO6UKrbtbz6LdjwVwj0uSBavR7dutzLRcTwCHijKoEbu1/pMbzvLd4WNDvc5ZS/78KUGYTgj2oKzofgIJISDltopByFROoprdEpAQ128zoqAwYkBmGjaUKwnYN4C8pbX+hHU2lDpdaHaJuEGntye6OS5x8MY7SD+Uf9PN28aVZpcXrc5BsngUWSLa0x1dQdHPlbJBmh8mSGif+ahRRcIuuvt0s3DVPG/2JHuTL12Wu4CUbTtwlezlnX7w==";
        String decrypted = Encryption.decrypt(toDecrypt, "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC3C8phKjWkKOjLy5NI68J2Q5YCnHIWNta8z2isTu0lDZLc/Sivj/cadP9dA9dWyy08RWSFkOuq7QsplH2+QYAA7MUHSpD4khIyp6Uee3IM23h/kCEruEJ65M0w9ECR8syhlWHCOuMezCBHDm8Hcl4Yy+0rbm7pSFvCgwOeloZazmm/j3dlssfLiqn8BfntJgMETXEN15pnEx+QpYBN/98is/UIrkesQWMghUxs6mW6fORQNRYT81f67cBdgMOwzRqFXZZoLT0AtL1v7v+/uaGRPxT3/t2WUSVpUv/lrAZJgZU/t5IZ026SCN8Wckurd3+p2nEbuO2hAe1DTGDd84xFAgMBAAECggEARPZhOfhAG2f6RQWeqOOB8TKHatZsOShSAPKXeguGVuZlRXLviC1SKJqZtv6Ce8WydjmipJuz3kLOaMz0DuD4i+CoXpXeRngp+QGmOPTDRErPfCfeB221Rj27fM0ZIqN6KwpvuVCcTvcS1hJ/DCwvfWIfKbTCfotvXroje/U0CzOED4J5F5DmgdIKl4NZ8KVMU623db9LTiJve4Fnzeg328okmKP30yjT9xo5nPY3qDP3wQc6JhULIz0/mneP/xcNmoTCXyRV4llWjd/DOpghLezG15Uis8X95tTKKU5DOHNYuW9FOSANmUEV5r78s0CVzLFO4y7YkBThAdidw3FRwQKBgQDaOIoAROYlMuuadVY9hDMA85yiIvFlehpwG7WOwWeHQbxKdQ5ZwuINA/p+nmfr2wFpR2hkr5TPc//sviNLP68HULnE4FnPJs7mzNGWYHevGCgrcOKU4A1u2kHcTuQw819SGytaULf1xfEB75vt6Y8MvvlEhxFi88AzNEmrOmSqEQKBgQDWvFGHvMxyoCnlckhUaDXspRREvIZqNAGdx5qMWlHXn+rre8hRnuwdI6R1KtxMnzwcZNkkrB1onw/jPWpdGCMzN2+Afn9idbfrPIocOA/xpJrmtDOpSChtccpduOMt14sPzUy/ZZnvX+T8Wel7AObkpJJxWPfAmi9lHX+w9lYq9QKBgQCv8lBKgULzEUhnpF3hgbrB9KoBDJ2IDVhNtfnAXW5E3xqbCiCE/R3YErIg70WpJE7R/NJOBEe+sK/TyUaZwII2Kr9t3tpqrMa7zr7aOXOmiJqy0/TMxo3uKCpcIHvagBAGGoPs8RIyJuP4hKI7xEojo8NFnoXFO/VnE6ymU5GgkQKBgQCc6gvh1pLHiEZVRkeYdnZBMPg4qlLGtK4hr8Q9n5qCqHcwEMn26ty7Blqcy+8jhpCUZxVN+P+cfe9MLIrW8FZ4jJ/ME6PePQtiuGBr8oOkHpaa0z+VCgeQwkAQcJSB4SnbKDhW/FWaWEaqKMG2gpg2qBPjBBI3CfRsPTwcAkZBKQKBgDb+ZhipNduRaUEEEGUpFbRCKhdQSeUlSHnH5xwD2AtXz2tcpFPJOnsJiZMsVBGnKswBCYPUFXIS5O5TW9dTJxpXq1o4bUpDGqetHnNCosJGzY32kgY55PGMUhlXwYjrJR03Ow/2e1oH3EFJsJdkW7YxWESLy9zyy7l8yTGF2mwg");
        System.out.println(decrypted);
    }
}
