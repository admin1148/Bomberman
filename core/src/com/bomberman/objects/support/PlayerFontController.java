package com.bomberman.objects.support;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Disposable;
import com.bomberman.utils.GameDefinitions;
import com.bomberman.utils.GraphicAssets;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Maciej Parandyk
 */


final public class PlayerFontController implements Disposable{
    private volatile boolean bDisplay = false;
    private BitmapFont font = GraphicAssets.getInstance().getInfoFont();
    private String textContent;
    private ThreadPoolExecutor executor;
    private final float resizeRatio;
    private final boolean bResize;
    private final long sleepTime;
    private float reductionX = 0.5f;
    private float reductionY = 0.5f;

    public PlayerFontController(long sleepTime){
        this.sleepTime = sleepTime;
        this.resizeRatio = 0.0f;
        this.bResize = false;
        initExecutor();
    }
    public PlayerFontController(long sleepTime, float increaseRate){
        this.sleepTime = sleepTime;
        this.resizeRatio = increaseRate;
        this.bResize = true;
        initExecutor();
    }

    private void initExecutor(){
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    }

    private void tearDownExecutor(){
        if(this.executor!= null && !this.executor.isShutdown()){
            this.executor.shutdown();
            if(!this.executor.isShutdown()){
                try{
                    this.executor.awaitTermination(4000, TimeUnit.MILLISECONDS);
                }catch(InterruptedException ex){}
            }
        }
    }

    public synchronized void setThread(){
        if(this.bResize) this.font.getData().setScale(1.0f);

        this.executor.submit(()->{
            try{
                this.bDisplay = true;
                Thread.sleep(this.sleepTime);
            }catch(InterruptedException ex){}
            finally {
                this.bDisplay = false;
            }
        });
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public boolean isVisible() {
        return bDisplay;
    }

    /**
     * Metoda odpowiada za wyświetlanie tekstu zdefiniowanego przez pola
     * 2 tryby:
     * 1. statyczny tekst
     * 2. dynamicznie zwiększający się tekst wraz z zmianą delty
     * @param batch obiekt klasy batch
     */
    public synchronized void render(Batch batch){
        if(bResize){
            font.getData().setScale(font.getScaleX() + this.resizeRatio);
        }

        GlyphLayout glyph = new GlyphLayout(this.font,this.textContent);
        final float posX = GameDefinitions.VIEWPORT_WIDTH * this.reductionX - glyph.width * 0.5f;
        final float posY =GameDefinitions.VIEWPORT_HEIGHT * this.reductionY - glyph.height;

        font.draw(batch,this.textContent,posX,posY);
    }

    public void setReductionX(float reductionX) {
        this.reductionX = reductionX;
    }

    public void setReductionY(float reductionY) {
        this.reductionY = reductionY;
    }

    @Override
    public void dispose() {
        tearDownExecutor();
    }
}
