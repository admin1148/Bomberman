package com.bomberman.utils;

import com.badlogic.gdx.graphics.Texture;
import com.bomberman.runner.BombermanTestRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.junit.runners.model.InitializationError;
import static org.junit.Assert.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bomberman.objects.PlayerDetails;
import com.bomberman.utils.Directions;
import com.bomberman.utils.GraphicAssets;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.MapLayer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GraphicAssetsTest extends BombermanTestRunner {
    private static final Logger logger = LoggerFactory.getLogger(GraphicAssetsTest.class);

    public GraphicAssetsTest() throws InitializationError {
        super(GraphicAssetsTest.class);
    }

    @Test
    public void getInstanceTest() {
        logger.info("Sprawdzanie uzyskiwania referencji GraphicAssets");
        assertNotNull(GraphicAssets.getInstance());
    }

    @Test
    public void getAnimationTest(){
        //test playable character
        PlayerDetails playerDetails = PlayerDetails.PLAYER_ONE;
        Animation<TextureRegion> animation = null;
        Directions direction = null;
        //get UP animation
        direction = Directions.UP;
        Float duration = null;
        logger.info("Sprawdzanie wyniku pobrania animacji dla głównej postaci");
        animation = GraphicAssets.getInstance().getAnimation(playerDetails,direction);
        assertNotNull(animation);
        //check amout of frames and frame duration
        int frames = playerDetails.animationFrames(direction);
        int framesAnim =(int)(animation.getAnimationDuration()/animation.getFrameDuration());
        assertEquals(frames,framesAnim);

        float frameDuration = playerDetails.frameDuration(direction);
        assertEquals(frameDuration,animation.getFrameDuration(),0.0001);
        logger.info("Testy dla animacji postacie poruszającej sie w górę, zakończone suksesem");

        //side
        direction = Directions.LEFT;
        animation = GraphicAssets.getInstance().getAnimation(playerDetails,direction);
        assertNotNull(animation);
        //check amout of frames and frame duration
        frames = playerDetails.animationFrames(direction);
        framesAnim =(int)(animation.getAnimationDuration()/animation.getFrameDuration());
        assertEquals(frames,framesAnim);

        frameDuration = playerDetails.frameDuration(direction);
        assertEquals(frameDuration,animation.getFrameDuration(),0.0001);
        logger.info("Testy dla animacji postacie poruszającej sie na bok, zakończone suksesem");
        //down
        direction = Directions.DOWN;
        animation = GraphicAssets.getInstance().getAnimation(playerDetails,direction);
        assertNotNull(animation);
        //check amout of frames and frame duration
        frames = playerDetails.animationFrames(direction);
        framesAnim =(int)(animation.getAnimationDuration()/animation.getFrameDuration());
        assertEquals(frames,framesAnim);

        frameDuration = playerDetails.frameDuration(direction);
        assertEquals(frameDuration,animation.getFrameDuration(),0.0001);
        logger.info("Testy dla animacji postacie poruszającej sie w dół, zakończone suksesem");

        //clean up
        animation = null;
        direction = null;
        playerDetails = null;
    }

    @Test
    public void getFontsTest(){
        logger.info("Pobranie referencji bitmapy czcionki");
        BitmapFont infoFont = GraphicAssets.getInstance().getInfoFont();
        assertNotNull(infoFont);
        infoFont.dispose();
        infoFont = null;
    }

    @Test
    public void bombAnimationsTest(){
        logger.info("Test pobrania animacji bomby");
        Array<TextureRegion> txArray = GraphicAssets.getInstance().getBombAnimation();
        for(TextureRegion region : txArray)
            assertNotNull(region);
        int animFrames = 3;
        assertEquals(animFrames,txArray.size);

        txArray.clear();
        txArray = null;

        logger.info("Test pobrania effektu cząstek");
        //particle effect
        ParticleEffect effect = GraphicAssets.getInstance().getBlastEffect(1.0f,2.0f);
        assertNotNull(effect);
        assertFalse(effect.isComplete());
        effect.dispose();
    }

    @Test
    public void getExplodableBlock(){
        logger.info("Sprawdzanie referencji niszczalnego bloku");
        Texture tx = GraphicAssets.getInstance().getExplodableBlock();
        assertNotNull(tx);
        tx.dispose();
    }

}
