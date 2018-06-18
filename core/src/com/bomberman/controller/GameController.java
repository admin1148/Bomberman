package com.bomberman.controller;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;
import com.bomberman.objects.ExplodableBlock;
import com.bomberman.objects.PlayerInstance;
import com.bomberman.utils.Directions;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bomberman.utils.GameDefinitions;

/**
 * @author Mateusz Kloc
 */

public class GameController extends InputAdapter{
    private boolean isPaused;
    private int playableIndex;
    private Array<PlayerInstance> players;
    private final Array<ExplodableBlock> explodableBlocks;

    public GameController(Array<PlayerInstance> players, Array<ExplodableBlock> blocks){
        this.players=players;
        this.setPlayableCharacterIndex();
        this.explodableBlocks=blocks;
        Gdx.input.setInputProcessor(this);
        isPaused=false;
    }

    /**
     * Metoda zapewnia obsługę spacji i klucza p.
     * Efekt: przyciśnięcie spacji - dodanie bomby.
     * przyciśnięcie klucza p - wstrzymanie gry
     * @param keycode wartość kodowa klucza, przekazywana przez framework
     * @return false - propagowanie eventu dalej, true powstrzymanie propagowania eventu dalej
     */
    @Override
    public boolean keyDown(int keycode) {

        switch (keycode){

           case Keys.P:{
               this.isPaused=(this.isPaused)?false:true;
               return true;
           }
            case Keys.SPACE:{
               this.players.get(this.playableIndex).dropDaBomb();
               return true;
            }
        }

        return false;
    }

    public void render(SpriteBatch batch){
        this.renderBlocks(batch);
        for (PlayerInstance instance : this.players) {
            instance.render(batch);
        }
    }

    public void update(float delta){
        updatePlayer(delta);
    }

    /**
     * Aktualizuje pozycję i przesunięcie gracza w zależności od różnicy w czasie klatki
     * @param delta różnica pomiędzy poprzednią, a obecną klatką
     */
    private void updatePlayer(float delta){
        //player movement
        Directions direction=null;
        this.players.get(this.playableIndex).update(delta);
        float playerMoveFactor= GameDefinitions.PLAYER_MOVE_SPEED*delta;
        if(Gdx.input.isKeyPressed(Keys.W)){
            direction=Directions.UP;
        }
        if(Gdx.input.isKeyPressed(Keys.S)){
            direction=Directions.DOWN;
        }
        if(Gdx.input.isKeyPressed(Keys.A)){
            direction=Directions.LEFT;
        }if(Gdx.input.isKeyPressed(Keys.D)){
            direction=Directions.RIGHT;
        }

        if(direction!=null){
            this.players.get(this.playableIndex).updateLocation(direction,playerMoveFactor);
        }
    }

    public void pause(){
        this.isPaused=true;
    }

    public void resume(){
        this.isPaused=false;
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void setPlayableCharacterIndex(){
        for (int i=0;i<this.players.size;i++) {
            if (this.players.get(i).isPlayable()) {
                this.playableIndex=i;
                break;
            }
        }
    }

    private void renderBlocks(SpriteBatch batch){
        for(int i=0;i<this.explodableBlocks.size;i++) {
            if(this.explodableBlocks.get(i).isDestroyed()){
                this.explodableBlocks.removeIndex(i);
                continue;
            }
            this.explodableBlocks.get(i).render(batch);
        }
    }
}
