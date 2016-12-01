package de.pixyel.dhbw.pixyel.ConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class ConnectionManager implements Runnable{
    private static Socket socket;//Der "Kanal" zum Server
    private static ServerInputListener listener;//Ein eigener Thread, der auf eingehende Nachrichten vom Server horcht
    private static String serverIP = "sharknoon.de";//IP-Adresse des Servers, zum testes localhost (Server und Client auf dem selben Computer), wird später "sharknoon.de" sein!
    //Der öffentliche Key des Servers
    private static String serverPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmG8OfhJrkN9/rXLh7auyUPcq7UxmYModYswChY8hIMgZO4m+cxOWopxOptUAYedjA4ZAKGp/P1g6n6YaXvtPQqIbi7G5oCT4vbh0zYFgI3wNCJlKtUX1gb6uCQW3rPinANcPtlZoIyegAsn/OW0FMZtc1x8PN0H1MQTlcCctXdJdotuljeYriO1lkRfb3GsotLIYjciMqIMKGQRQ2Rhj81bnxP9FybdNuVIjlS6Rfx9fzaZ2BKIdm7O7/Dzn9TcSZEOZdOSS7CHMMKr14O26g+bR2HiGWx8AbOH2zP3DMpR9/Y8GUrjO6QPqA+GorICGYWxIlrcm4iYx8740FsDaQQIDAQAB";
    //Der private Key des Clients
    private static String clientPrivateKey;


    public static void ping() {
        //Falls der Server unerreichbar ist, versucht er es 'attemps' mal
        int attempts = 10;
        while (attempts > 0 && (socket == null || !socket.isConnected())) {
            attempts--;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverIP, 7331), 500);
                System.out.println("Erfolgreich verbunden");
                listener = new ServerInputListener();
                new Thread(listener).start();
                System.out.println("Sende Echo...");
                PrintWriter raus = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                raus.println("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
                raus.flush();
            } catch (UnknownHostException e) {
                System.err.println("Unbekannter Host: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Server konnte nicht erreicht werden: " + e.getMessage());
            }
        }
    }

    public static boolean connect(String storeID) {
        //Falls der Server unerreichbar ist, versucht er es 'attemps' mal
        int attempts = 10;
        while (attempts > 0 && (socket == null || !socket.isConnected())) {
            attempts--;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverIP, 7331), 500);
                System.out.println("Erfolgreich verbunden");
                listener = new ServerInputListener();
                new Thread(listener).start();
                login(storeID);
                return true;
            } catch (UnknownHostException e) {
                System.err.println("Unbekannter Host: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Server konnte nicht erreicht werden: " + e.getMessage());
            }
        }
        return false;
    }

    private static void login(String storeID) {
        //Erzeuge Client Private und Public Key
        String[] keyPair = new String[0];
        try {
            keyPair = Encryption.generateKeyPair();
        } catch (Encryption.EncryptionException e) {
            e.printStackTrace();
            return;
        }
        //Speichere den Private Key für andere Methoden sichtbar
        clientPrivateKey = keyPair[1].replaceAll("\\n", "");
        //Erzeuge ein XML mit einem Tag namens publickey und einem tag namens storeid, siehe Spezifikation!
        XML loginXML = XML.createNewXML("login");
        loginXML.addChildren("storeId", "publicKey");
        loginXML.getFirstChild("storeId").setContent(storeID);
        loginXML.getFirstChild("publicKey").setContent(keyPair[0].replaceAll("\\n",""));
        //Übermittle dem Server meinen Public Key
        sendToServer(loginXML);
    }

    public static void disconnect() {
        //Unwahrscheinlicher Fall, dass der Socket sich unerwartet beendet hat
        if (socket == null) {
            System.out.println("Server unerreichbar");
        } else {
            try {
                sendToServer(XML.createNewXML("disconnect"));
                listener.stop();
                //"Kanal" zum Server schließen
                socket.close();
                System.out.println("Habe mich beim Server abgemeldet");
            } catch (IOException ex) {
                System.out.println("Konnte Socket nicht schliessen!");
            }

        }
    }

    public static boolean sendToServer(XML toSend) {
        try {
            String ready = XML.createNewXML("request").addChild(toSend).toString();
            String encrypted = Encryption.encrypt(ready, serverPublicKey);
            System.out.println("Encrypted 1: \"" + encrypted + "\" ===========================================!");
            //encrypted.replaceAll("\\n", "");
            PrintWriter raus = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            raus.println(encrypted);
            raus.flush();
            System.out.println("");
            System.out.println("Encrypted 2: \"" + encrypted + "\" gesendet!");
            System.out.println("Erfolgreich \"" + toSend + "\" gesendet!");
            return true;
        } catch (Exception e) {
            if (e.toString().contains("Socket is closed")) {
                System.err.println("Could not send String beacuase the socket is closed, closing the connection now: " + e);
                disconnect();
                return false;
            } else if (e.toString().contains("socket write error")) {
                System.err.println("Could not write on Socket: " + e);
                return false;
            } else {
                System.err.println("String(" + toSend + ") could not be send: " + e);
                return false;
            }
        }
    }

    @Override
    public void run() {
        this.connect("Jan");
    }

    private static class ServerInputListener implements Runnable {

        //Dient zum einfachen Beenden dieses Threads
        boolean run = true;

        @Override
        public void run() {
            System.out.println("Listener für eingehende Nachrichten vom Server in eigenem Thread erfolgreich gestartet!");
            BufferedReader rein;
            String string;
            while (!socket.isClosed() && socket.isConnected() && socket.isBound() && run) {
                try {
                    rein = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    string = rein.readLine();
                    if (run) {
                        onStringReceived(string);
                    }
                } catch (IOException exe) {
                    if (run) {
                        switch (exe.toString()) {
                            case "java.net.SocketException: Connection reset":
                            case "java.net.SocketException: Socket closed":
                                System.err.println("Server unreachable: " + exe + ", shuting down the connection to the Server");
                                disconnect();
                                break;
                            case "invalid stream header":
                                //Jemand sendet zu lange Strings
                                System.err.println("Steam header too long, received String too long??!?: " + exe);
                                disconnect();
                                break;
                            default:
                                System.err.println("Could not read incomming message: " + exe);
                                break;
                        }
                    }
                }
            }
        }

        void stop() {
            run = false;
            System.out.println("Listener für Nachrichten vom Server erfolgreich gestoppt!");
        }
    }

    private static String onStringReceived(String string) {
        if (string.contains("echo")) {
            System.out.println("Nachricht vom Server: " + string);
            return string;
        }
        //Decomprimiere den String
        String decrypted = null;
        try {
            decrypted = Encryption.decrypt(string, clientPrivateKey);
        } catch (Encryption.EncryptionException e) {
            e.printStackTrace();
        }
        try {
            //Parse den String in ein XML
            XML receivedXML = XML.openXML(decrypted);
            //Beispielvorgehen: Zeige den XML Baum in der Ausgabe an
            System.out.println("Command received: \n" + receivedXML.toString());
            return receivedXML.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }
}
