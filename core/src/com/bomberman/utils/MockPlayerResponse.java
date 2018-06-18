package com.bomberman.utils;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Klasa mająca pozorować dodatkowego gracza, poprzez
 * dostarczanie danych dla serwera, które z kolei trafiają do głównego gracza.
 * @author Maciej Parandyk
 */
public class MockPlayerResponse {
    ///192.168.122.1:57024:NPC;{frameTime:5.213393,direction:DOWN,posX:40.867188,posY:520.6172};BMB;;#50.000000|530.000000#
    private static final String response_pattern = "/MOCK:666:NPC;{frameTime:5.213393,direction:DOWN,posX:%.6f,posY:%.6f};BMB;%s";
    private static final float CONST_X = 500.0f;
    private static final float MIN_Y = 40.0f;
    private static final float MAX_Y = 520.0f;
    private static final float PROGRESS = 50.25f;
    private float currentY = MIN_Y;
    private String bombPart = "";
    private boolean bSet = true;

    public MockPlayerResponse() {
        initThread();
    }

    public synchronized String getResponse(){
        return String.format(response_pattern,CONST_X,currentY,bombPart);
    }

    private void progress(){
        this.currentY+=PROGRESS;
        if(Float.compare(currentY,MAX_Y)==1)
            this.currentY = MIN_Y;
    }

    private void setBomb(){
        if(bSet){
            this.bombPart = "{posX:40.867188,posY:402.37793}";
        }else{
            this.bombPart = "";
        }
    }

    private void initThread(){
        ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        exec.submit(()->{
            try{
                progress();
                Thread.sleep(1000);
            }catch (InterruptedException ex){}
        });

        exec.submit(()->{
            try{
                setBomb();
                Thread.sleep(5000);
            }catch (InterruptedException ex){
                this.bSet = this.bSet ? false : true;
            }
            try{
                setBomb();
                Thread.sleep(15000);
            }catch (InterruptedException ex){
            }
        });
    }
}
