package de.pixyel.dhbw.pixyel.ConnectionManager;

/**
 * Created by Niklas on 20.10.2016.
 */

public class ConnectionManager {
    public void EstablishConnection() {
        // etabliert Verbindung mit Server

        /* Ablauf

        - Erzeuge Private und Public Key
        - hole StoreID (Google Play ID)
        - Etabliere Socket-Verbindung zu sharknoon.de:7331
        - erstelle String in XML-Format(nach Spezifikation)
        - Kompressen mit GZip
        - Sende String über Socket an Server
        */
    }

    private void CreatePrivateKey(){
        // TODO:
    }

    private void CreatePublicKey() {
        // TODO:
    }

    private void EstablishSocketConnections(){
        // TODO:
    }
}
