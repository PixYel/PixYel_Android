package de.pixyel.dhbw.pixyel.ConnectionManager;

import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import de.pixyel.dhbw.pixyel.ImageCard;
import de.pixyel.dhbw.pixyel.LikesFragment;
import de.pixyel.dhbw.pixyel.MainActivity;
import de.pixyel.dhbw.pixyel.NewFragment;
import de.pixyel.dhbw.pixyel.PicComment;
import de.pixyel.dhbw.pixyel.TopFragment;
import de.pixyel.dhbw.pixyel.UploadsFragment;


public class ConnectionManager implements Runnable {
    private static Socket socket;//Der "Kanal" zum Server
    private static ServerInputListener listener;//Ein eigener Thread, der auf eingehende Nachrichten vom Server horcht
    private static String serverIP = "sharknoon.de";//IP-Adresse des Servers, zum testes localhost (Server und Client auf dem selben Computer), wird später "sharknoon.de" sein!
    //Der öffentliche Key des Servers
    private static String serverPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmG8OfhJrkN9/rXLh7auyUPcq7UxmYModYswChY8hIMgZO4m+cxOWopxOptUAYedjA4ZAKGp/P1g6n6YaXvtPQqIbi7G5oCT4vbh0zYFgI3wNCJlKtUX1gb6uCQW3rPinANcPtlZoIyegAsn/OW0FMZtc1x8PN0H1MQTlcCctXdJdotuljeYriO1lkRfb3GsotLIYjciMqIMKGQRQ2Rhj81bnxP9FybdNuVIjlS6Rfx9fzaZ2BKIdm7O7/Dzn9TcSZEOZdOSS7CHMMKr14O26g+bR2HiGWx8AbOH2zP3DMpR9/Y8GUrjO6QPqA+GorICGYWxIlrcm4iYx8740FsDaQQIDAQAB";
    //Der private Key des Clients
    private static String clientPrivateKey;
    private static File folder = MainActivity.cacheFolder;
    private static boolean connected = false;

    public static Queue<Object> sendQueue = new LinkedList<Object>();


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
        loginXML.getFirstChild("publicKey").setContent(keyPair[0].replaceAll("\\n", ""));
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

            if(socket.isClosed()){
                ConnectionManager.connect(MainActivity.deviceID);
                return false;
            }

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

    public static boolean sendToServerUnencrypted(XML toSend) {
        try {
            String ready = XML.createNewXML("request").addChild(toSend).toString();
            PrintWriter raus = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            raus.println(ready);
            raus.flush();
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
        this.connect(MainActivity.deviceID);
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

    private static void onStringReceived(String string) {
        if (string == null) {
            System.out.println("NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL");
            return;
        }
        // Wenn string unverschlüsselt ist, ist es ein Bild
        if (string.startsWith("<reply>")) {
            System.out.println(string);
            XML xml = null;
            try {
                xml = XML.openXML(string);
            } catch (XML.XMLException e) {
                e.printStackTrace();
            }
            String sData = xml.getFirstChild("setItem").getFirstChild("data").getContent();
            String id = xml.getFirstChild("setItem").getFirstChild("id").getContent();
            byte[] bData = Base64.decode(sData, Base64.NO_WRAP);
            final File image = new File(folder, id+".jpg");
            System.out.println("Download: " + image.toString());
            BufferedOutputStream bos = null;
            try {   // Bild in Datei schreiben
                bos = new BufferedOutputStream(new FileOutputStream(image));
                bos.write(bData);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String flag = MainActivity.requestFlag; // Gibt an welche Liste das Bild angefordert hat
            if(flag.contains("Top")){
                MainActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TopFragment.onItemsLoadComplete();
                    }
                });
            }
            else if(flag.contains("New")){
                MainActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NewFragment.onItemsLoadComplete();
                    }
                });
            }
            else if(flag.contains("Like")){
                MainActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LikesFragment.onItemsLoadComplete();
                    }
                });
            }
            else if(flag.contains("Uploads")){
                MainActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UploadsFragment.onItemsLoadComplete();
                    }
                });
            }
            return;
        }
        // Entschlüsseln
        String decrypted = null;
        try {
            decrypted = Encryption.decrypt(string, clientPrivateKey);
        } catch (Encryption.EncryptionException e) {
            e.printStackTrace();
        }
        try {
            if(decrypted == null){
                return;
            }
            //Parse den String in ein XML
            XML receivedXML = XML.openXML(decrypted);
            System.out.println("Command received: \n" + receivedXML.toString());

            try {
                if (receivedXML.getFirstChild().getName().equals("setItemList")) {
                    receivedXML = receivedXML.getFirstChild("setItemList");
                    ArrayList<XML> list = receivedXML.getChild("item");
                    if (MainActivity.requestFlag.contains("Top")) {
                        for (int i = 0; i < list.size(); i++) {     // Fordere für jedes Item das entsprechende Bild an
                            XML item;
                            item = list.get(i);
                            TopFragment.imageList.add(new ImageCard(
                                    item.getFirstChild("id").getContent(),
                                    item.getFirstChild("date").getContent(),
                                    item.getFirstChild("upvotes").getContent(),
                                    item.getFirstChild("downvotes").getContent(),
                                    item.getFirstChild("votedByUser").getContent(),
                                    item.getFirstChild("rank").getContent()
                            ));
                            System.out.println(item.toStringGraph());


                            final File image = new File(folder, item.getFirstChild("id").getContent() +".jpg");
                            if(!image.exists()){
                                XML toSend = XML.createNewXML("getItem");
                                toSend.addChild("id").setContent(item.getFirstChild("id").getContent());
                                ConnectionManager.sendToServer(toSend);
                            }
                            else{
                                TopFragment.refreshItem(TopFragment.imageList.size());
                            }

                        }
                        TopFragment.onItemsLoadComplete();
                    }
                    else if(MainActivity.requestFlag.contains("New")){
                        for (int i = 0; i < list.size(); i++) {
                            XML item;
                            item = list.get(i);
                            NewFragment.imageList.add(new ImageCard(
                                    item.getFirstChild("id").getContent(),
                                    item.getFirstChild("date").getContent(),
                                    item.getFirstChild("upvotes").getContent(),
                                    item.getFirstChild("downvotes").getContent(),
                                    item.getFirstChild("votedByUser").getContent(),
                                    item.getFirstChild("rank").getContent()
                            ));
                            System.out.println(item.toStringGraph());


                            final File image = new File(folder, item.getFirstChild("id").getContent() +".jpg");
                            if(!image.exists()){
                                XML toSend = XML.createNewXML("getItem");
                                toSend.addChild("id").setContent(item.getFirstChild("id").getContent());
                                ConnectionManager.sendToServer(toSend);
                            }
                            else{
                                NewFragment.refreshItem(TopFragment.imageList.size());
                            }

                        }
                        NewFragment.onItemsLoadComplete();
                    }
                    else if(MainActivity.requestFlag.contains("Like")){
                        for (int i = 0; i < list.size(); i++) {
                            XML item;
                            item = list.get(i);
                            LikesFragment.imageList.add(new ImageCard(
                                    item.getFirstChild("id").getContent(),
                                    item.getFirstChild("date").getContent(),
                                    item.getFirstChild("upvotes").getContent(),
                                    item.getFirstChild("downvotes").getContent(),
                                    item.getFirstChild("votedByUser").getContent(),
                                    item.getFirstChild("rank").getContent()
                            ));
                            System.out.println(item.toStringGraph());


                            final File image = new File(folder, item.getFirstChild("id").getContent() +".jpg");
                            if(!image.exists()){
                                XML toSend = XML.createNewXML("getItem");
                                toSend.addChild("id").setContent(item.getFirstChild("id").getContent());
                                ConnectionManager.sendToServer(toSend);
                            }
                            else{
                                LikesFragment.refreshItem(TopFragment.imageList.size());
                            }

                        }
                        LikesFragment.onItemsLoadComplete();
                    }

                    else if(MainActivity.requestFlag.contains("Upload")){
                        for (int i = 0; i < list.size(); i++) {
                            XML item;
                            item = list.get(i);
                            UploadsFragment.imageList.add(new ImageCard(
                                    item.getFirstChild("id").getContent(),
                                    item.getFirstChild("date").getContent(),
                                    item.getFirstChild("upvotes").getContent(),
                                    item.getFirstChild("downvotes").getContent(),
                                    item.getFirstChild("votedByUser").getContent(),
                                    item.getFirstChild("rank").getContent()
                            ));
                            System.out.println(item.toStringGraph());


                            final File image = new File(folder, item.getFirstChild("id").getContent() +".jpg");
                            if(!image.exists()){
                                XML toSend = XML.createNewXML("getItem");
                                toSend.addChild("id").setContent(item.getFirstChild("id").getContent());
                                ConnectionManager.sendToServer(toSend);
                            }
                            else{
                                UploadsFragment.refreshItem(TopFragment.imageList.size());
                            }

                        }
                        UploadsFragment.onItemsLoadComplete();
                    }
                }
                else if (receivedXML.getFirstChild().getName().equals("setComments")){
                    ArrayList<XML> commentList = receivedXML.getChild("comment");
                    String id = commentList.get(0).getFirstChild("id").getContent();
                    LinkedList<String> comments = new LinkedList<>();
                    for (int i = 0; i < commentList.size(); i++){
                        comments.add(commentList.get(i).getFirstChild("content").getContent());
                    }
                    PicComment.hm.put(id,comments);

                }


                return;
            } catch (Exception e) {
                return;
            }
        } catch (XML.XMLException e) {
            e.printStackTrace();
        }
    }
}

