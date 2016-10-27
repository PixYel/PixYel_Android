package de.pixyel.dhbw.pixyel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import de.pixyel.dhbw.pixyel.ConnectionManager.ConnectionManager;
import de.pixyel.dhbw.pixyel.ConnectionManager.XML;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent = getIntent();
    }

    public void buttonConnect(View view){
        new Thread(new Runnable() {
            public void run() {

                ConnectionManager manager = new ConnectionManager();
                //Ping dient nur zum Überprüfen der Socketverbindung, NICHT zum Überprüfen der Verschlüsselung und Compression
                //ping();
                //**Verbindung zum Server herstellen ENTWEDER Ping ODER connect aufrufen!!!!!!!!!!!11!!!!!elf!!
                manager.connect("HanswurstID");
                //**Beispiel: sendet ein xml mit dem node "echo" an den Server, der server schickt daraufhin selbiges zurück
                manager.sendToServer(XML.createNewXML("echo").toXMLString());
                //**Wenn man die App schließt oder ähnliches, einfach die disconnect Methode aufrufen
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                manager.disconnect();

            }
        }).start();

    }

    public void buttonSend(){

    }
}
