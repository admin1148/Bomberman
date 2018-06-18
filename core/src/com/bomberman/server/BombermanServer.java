package com.bomberman.server;
import java.io.File;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.Map;
import java.io.IOException;

import com.badlogic.gdx.utils.Json;
import com.bomberman.objects.Bomb;
import com.bomberman.utils.MockPlayerResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.io.File.separator;

/**
 * Klasa implementująca logikę serwera
 * @author Mateusz Kloc
 * @author Maciej Parandyk
 */

public class BombermanServer {

    private static final Logger logger=LoggerFactory.getLogger(BombermanServer.class);
    private SocketAddress socketAddress;
    private static final String POSITION = "PST;";
    private static final int BUFFER_SIZE = 1024;
    private DatagramPacket packet = new DatagramPacket(new byte[1024],0,1024);
    private DatagramSocket serverSocket;
    private final Map<String,String> addressesSlots = new ConcurrentHashMap<>();
    private final Map<String,String> playerSlots= new ConcurrentHashMap<>();
    private static final String EXPL_BLOCK_ROOT_DIR=".."+separator+"core"+separator+"resources"+separator+"maps"+separator+"test"+separator;
    //private static final String EXPL_BLOCK_ROOT_DIR="core"+separator+"resources"+separator+"maps"+separator+"test"+separator;
    private static String ARRAY_LIST = "";
    private int playerNo = 0;

    public void runServer() {
        try{

            InetAddress inetAddress=InetAddress.getByName("192.168.0.2");
            serverSocket = new DatagramSocket(40500,inetAddress);
            logger.info("Socket connected to " + this.serverSocket.getLocalAddress()+" port: " +
                    this.serverSocket.getLocalPort());
            ARRAY_LIST = BombermanServer.populateExplodableBlocks("test.tmx");
            //commented block contains code for dynamic setting of socket in contrary to above
/*            for (int i = 0; i < 65536; i++) {
                try {
                    serverSocket = new DatagramSocket(i,inetAddress);
                        //Inform user about an ip address and port of the server
                        logger.info("Socket connected to " + this.serverSocket.getLocalAddress()+" port: " +
                        this.serverSocket.getLocalPort());
                        break;
                } catch (SocketException ex) {
                    this.serverSocket = null;
                } catch (Exception e) {
                    serverSocket = null;
                }
            }*/

            if (serverSocket != null && this.serverSocket.isBound()) {


                while(true){
                    retrievePlayerData();
                    sendBackResponse();
                }
            }else{
                logger.info("Server Socket is not connected");
                System.exit(1);
            }
        }catch(UnknownHostException ex){
            logger.error("BombermanServer$Main: host error",ex);
            System.exit(1);
        } catch(Exception ex){
            logger.error("BombermanServer$Main",ex);
            System.exit(1);
        }
        finally {
            if (serverSocket != null) {
                    serverSocket.close();
            }
            if (addressesSlots.size() > 0) {
                addressesSlots.clear();
            }
        }


    }

    /**
     * Metoda ma dwojaką funkcję poczynającą się od otrzymania żądania od gracza z żądanie pozycji lub przesłaniem
     * aktualnych danych gracza.
     * W sytuacji żądania pozycji serwer wysyła odpowiedz w postaci pozycji startowej gracza oraz rozkładem zniszczalnych bloków
     */
    private void retrievePlayerData(){

        String receivedPacket = null;
        try {
            this.packet.setData(new byte[BUFFER_SIZE]);
            this.serverSocket.receive(this.packet);
            this.socketAddress = this.packet.getSocketAddress();
            receivedPacket = new String(this.packet.getData()).trim();
            if(receivedPacket.contains(POSITION)){
                if (!playerSlots.containsKey(socketAddress.toString())) {
                    playerSlots.put(socketAddress.toString(),playerNo + ";" + ARRAY_LIST);
                    logger.info("PLAYER NUMBER: " + playerNo);
                    playerNo++;
                }
                String  request = playerSlots.get(socketAddress.toString());
                this.packet.setData(new byte[BUFFER_SIZE]);
                this.packet.setData(request.getBytes());
                this.serverSocket.send(this.packet);
                return;
            }
          //  logger.info(receivedPacket);
        }catch(IOException ex){

        }

        if (receivedPacket != null && receivedPacket.length() > 0) {
            this.addressesSlots.put(this.socketAddress.toString(),receivedPacket);
         //   logger.info(this.socketAddress.toString()+":"+receivedPacket);
        }
    }

    /**
     * Metoda odpowiada za przesłanie odpowiedzi do gracza z danymi oponenta
     */
    private void sendBackResponse(){
        String responsePacket = null;
        for (Map.Entry<String, String> entry : this.addressesSlots.entrySet()) {
            if (!this.socketAddress.toString().equals(entry.getKey())) {
                responsePacket = entry.getValue();
                break;
            }
        }

        if (responsePacket != null && responsePacket.length() > 0) {
            try {
                this.packet.setData(new byte[BUFFER_SIZE]);
                byte[] byteArray = responsePacket.getBytes();
                this.packet.setData(byteArray);
                this.serverSocket.send(this.packet);
             //   logger.info(this.socketAddress.toString() + ":" + responsePacket);
            } catch (Exception ex) {
            }
        } else {
            try {
                byte[] byteArray = "".getBytes();
                this.packet.setData(byteArray);
                this.serverSocket.send(this.packet);
            //    logger.info("responsePacket was empty");
            } catch (Exception ex) {
            }

        }
    }

    /**
     * Wylosowanie układu niszczalnych bloków dla danej gry.
     * @param mapName nazwa mapy
     * @return String z indeksami wyświetlanymi blokami
     */
    public static String populateExplodableBlocks(String mapName){
        ArrayList<Integer> lista = new ArrayList<>();
        int size = 0;
        try{
            File file = new File(EXPL_BLOCK_ROOT_DIR+mapName);
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = documentBuilder.parse(file);

            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("objectgroup");
            for(int i =0 ;i<nodeList.getLength();i++) {
                Node node = nodeList.item(i);
                if(node.getAttributes().getNamedItem("name").getNodeValue().equals("destroyable")){
                    size = (int)(nodeList.item(i).getChildNodes().getLength()*0.5f);
                }
            }
            System.out.println("Size: "+size);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        for (int i = 0; i < size; i++) {
            i = i+(int)Math.floor(Math.random() * 3);
            lista.add(i);
        }
        return lista.parallelStream().map(i->i.toString()).collect(Collectors.joining(","));
    }

    public static void main(String[] args) {
        new BombermanServer().runServer();
    }
}
