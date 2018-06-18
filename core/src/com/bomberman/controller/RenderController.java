package com.bomberman.controller;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * @author Maciej Parandyk
 * @author Mateusz Kloc
 */


public class RenderController{
    private final GameController playerController;
    private SpriteBatch batch;
    private OrthographicCamera mainCamera;

    public RenderController(GameController controller, SpriteBatch batch, OrthographicCamera camera){
        this.batch=batch;
        this.playerController=controller;
        this.mainCamera=camera;
    }

    public void update(float delta){
        playerController.update(delta);
    }

    /**
     * Odpowiada za główną funkcjonalność wyświetlania przebiegu gry
     */
    public void render(){
        batch.setProjectionMatrix(mainCamera.combined);
        this.mainCamera.update();
        batch.begin();
        playerController.render(batch);
        batch.end();
    }

    public void resize(float x,float y){
        this.mainCamera.viewportWidth=x;
        this.mainCamera.viewportHeight=y;
        this.mainCamera.update();
    }
}
