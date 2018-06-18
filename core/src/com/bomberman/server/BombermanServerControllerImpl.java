package com.bomberman.server;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.bomberman.objects.PlayerInstance;
import static java.io.File.separator;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import com.bomberman.objects.support.PlayerStateData;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.BindException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.ConnectException;

/**
 * Implementacja interfejsu ServerController, która dostarcza
 * logikę komunikacji ze serwerem oraz interpretację danych wyjściowych
 * na przebieg rozgrywki
 *
 * @author Mateusz Kloc
 * @author Maciej Parandyk
 */

public class BombermanServerControllerImpl implements ServerController {

    private static final Logger logger=LoggerFactory.getLogger(BombermanServerControllerImpl.class);

    private static final String POSITION = "PST;";
    private static final String SERVER_CONFIG_FILE= separator+"serverConfig.properties";
    private static final String NPC="NPC;";
    private static final String BOMB="BMB;";
    private static final String INIT_REQ="REQ;";
    private static final String BOXES="BX;";
    private static final int BUFFER_SIZE=1024;
    private DatagramSocket socket;
    private DatagramPacket buffer = new DatagramPacket(new byte[BUFFER_SIZE],0,BUFFER_SIZE);
    private Array<PlayerInstance> players = null;
    private final Json json;
    private final StringBuilder builder=new StringBuilder(BUFFER_SIZE);
    private Integer position = 0;
    private String serverResponse = null;

    public BombermanServerControllerImpl() {
        initSocket();
        this.json=new Json();
        setStartPosition();
    }

    public void setPlayers(Array<PlayerInstance> players) {
        this.players = players;
    }

    private void initSocket(){
        try {
            Properties serverProp = getServerProperties();
            String host = serverProp.get("server_ip").toString();
            Integer port = new Integer(serverProp.get("server_port").toString());
            this.socket = new DatagramSocket();
            this.socket.connect(InetAddress.getByName(host),port);
            logger.info("Connection was established: " + this.socket.getLocalAddress());
        } catch (BindException ex) {
            logger.error("initSocket - could not bind: priviledges?", ex);
            System.exit(1);
        } catch (ConnectException ex) {
            logger.error("initSocket - connection refused", ex);
            System.exit(1);
        } catch (SocketException ex) {
            logger.error("initSocket - could not connect", ex);
            System.exit(1);
        } catch (IOException ex) {
            logger.error("initSocket", ex);
            System.exit(1);
        }
    }

    private Properties getServerProperties(){
        Properties props=new Properties();
        try(FileInputStream fis=new FileInputStream(Gdx.files.local(SERVER_CONFIG_FILE).file())){
            props.load(fis);
        }catch(IOException ex){
            logger.error("getServerProperties",ex);
        }
        return props;
    }

    @Override
    public void closeConnection() {
        if (this.socket != null) {
                this.socket.close();
        }
    }

    @Override
    public void update() {
        sendDataToServer();
    }

    private void sendDataToServer(){
        if (this.players != null) {
            PlayerInstance playable = null;
            PlayerInstance npc = null;
            for (PlayerInstance instance : this.players) {
                if (instance.isPlayable()) {
                    playable = instance;
                } else {
                    npc = instance;
                }
            }
            byte[] byteArray = null;
            if(playable !=null){
                //send playable player's status
                playable.updatePlayerState();
                String msg=NPC+json.toJson(playable.getPlayerState())+";"+BOMB+playable.updateBombGlobalList();
                try {
                    this.buffer.setData(new byte[BUFFER_SIZE]);
                    byteArray = msg.getBytes("UTF-8");
                    this.buffer.setData(byteArray,0,byteArray.length);
                    this.socket.send(this.buffer);
                    logger.info("Send "+msg);
                }catch(IOException ex){
                        logger.error("sendDataToServer - send",ex);
                    }
                }

                if(npc != null){
                    //receive npc's status and update it
                    this.builder.setLength(0);
                    try {
                        this.buffer.setData(new byte[BUFFER_SIZE]);
                        this.socket.receive(this.buffer);
                        byteArray = this.buffer.getData();

                        String msg = new String(byteArray,"UTF-8").trim();
                        logger.info("Received "+msg);

                        //extract and update npc
                        String npcData = extractDataByPrefix(msg,NPC);
                        logger.info("NPC; "+npcData);
                        npc.updatePlayer(this.json.fromJson(PlayerStateData.class,npcData));

                        playable.placeBombs(extractDataByPrefix(msg,BOMB));
                    } catch (Exception ex) {
                        logger.error("sendDataToServer: receive",ex);
                    }
                }
        }
    }

    /**
     * Uzyskuje część odpowiadającą za dane okreslone prefiksem
     * @param msg pełna treść odpopwiedzi ze serwera
     * @param prefix prefiks poszukiwanej danej
     * @return w sytuacji obecności prefiksu następuje zwrot powiązanej z nią danej.
     * W przeciwnym wypadku zostaje zwrócony pusty String
     */
    private String extractDataByPrefix(String msg, String prefix){
        if(msg == null || msg.length() == 0 || !msg.contains(prefix)) return "";

        String[] strArray = msg.split(";");
        int iter = -1;
        for(int i=0;i<strArray.length;i++) {
            if(prefix.contains(strArray[i])){
                iter = i+1;
                break;
            }
        }

        return (iter < strArray.length && iter != -1) ? strArray[iter] : "";
    }

    private void setStartPosition() {
        this.buffer.setData(new byte[BUFFER_SIZE]);
        try {
            this.buffer.setData(POSITION.getBytes("UTF-8"));
            this.socket.send(this.buffer);
            this.buffer.setData(new byte[BUFFER_SIZE]);
            this.socket.receive(this.buffer);
            this.serverResponse = new String(this.buffer.getData(),"UTF-8");
            position = Integer.parseInt(extractDataByPrefix(serverResponse,POSITION));
        } catch (IOException ex) {
        } catch (NumberFormatException ex) {
        }
    }

    public String getBlocks(){
            return serverResponse.trim().split(";")[1];
    }
    public Integer getPosition(){
        return Integer.parseInt(serverResponse.trim().split(";")[0]);
    }
}
