package com.bomberman.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.bomberman.objects.support.Pair;
import com.bomberman.objects.support.PlayerFontController;
import com.bomberman.objects.support.PlayerStateData;
import com.bomberman.utils.Directions;
import com.bomberman.utils.GameDefinitions;
import com.bomberman.utils.GraphicAssets;
import com.bomberman.utils.MapManager;
import com.badlogic.gdx.utils.Array;

/**
 * @author Maciej Parandyk
 * @author Mateusz Kloc
 */

public class PlayerInstance extends AbstractGameObject implements Disposable {
    private final PlayerDetails playerDetails;
    private TextureRegion currentFrame;
    private Directions direction;
    private Animation<TextureRegion> animFront;
    private Animation<TextureRegion> animBack;
    private Animation<TextureRegion> animSide;
    private float frameTime;
    private MapManager mapManager;
    private PlayerStateData playerState;
    private Array<Bomb> bombList = new Array<>();
    private int startPosition;
    private int lives = GameDefinitions.MAX_LIVES;
    private Vector2 vector2;
    private final PlayerFontController displayedText = new PlayerFontController(2500, 0.01f);
    private int bombPacketCount = 10;
    private int bombPacketCountTemp;


    public PlayerInstance(PlayerDetails player, Vector2 vector2) {
        super(vector2, player.dimensions(), player.scale(), player.reductionRates(), player.alignment());
        this.playerDetails = player;
        loadGraphicAssets();
        initPlayerState();
        this.displayedText.setReductionY(0.7f);
    }

    @Override
    public void render(SpriteBatch batch) {
        renderBombs(batch);
        batch.draw(this.currentFrame.getTexture(), getPosition().x, getPosition().y, getOrigin().x,
                getOrigin().y, getDimension().x, getDimension().y, getScale().x, getScale().y, 0.0f, currentFrame.getRegionX(),
                currentFrame.getRegionY(), currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),
                (this.direction.equals(Directions.LEFT)) ? true : false, false);
        if (this.displayedText.isVisible()) {
            this.displayedText.render(batch);
        }
    }

    private void loadGraphicAssets() {
        //set to initial frame time
        this.frameTime = 0.0f;

        this.animFront = GraphicAssets.getInstance().getAnimation(playerDetails, Directions.DOWN);
        this.animSide = GraphicAssets.getInstance().getAnimation(playerDetails, Directions.RIGHT);
        this.animBack = GraphicAssets.getInstance().getAnimation(playerDetails, Directions.UP);

        //head player down
        setDirection(Directions.DOWN);
    }

    @Override
    public void update(float delta) {
        this.frameTime = (this.frameTime + delta) % this.playerDetails.animationFrames(this.direction);
        this.updateBombList(delta);
    }

    public void setDirection(Directions direction) {
        if (this.direction != null && !this.direction.equals(direction)) {
            this.frameTime = 0.0f;
        }

        this.direction = direction;

        switch (direction) {
            case UP: {
                this.currentFrame = this.animBack.getKeyFrame(this.frameTime);
            }
            break;
            case DOWN: {
                this.currentFrame = this.animFront.getKeyFrame(this.frameTime);
            }
            break;
            case LEFT:
            case RIGHT: {
                this.currentFrame = this.animSide.getKeyFrame(this.frameTime);
            }
            break;
            default:
                break;
        }
    }

    /**
     * Dopasowanie pozycji i przesunięcie gracza
     * @param direction kierunek gracza
     * @param slide wartość przesunięcia
     */
    @Override
    public void updateLocation(Directions direction, float slide) {
        Rectangle boundingBox = this.getBoundingBox();

        switch (direction) {
            case UP: {
                if (!this.checkCollisionBox(boundingBox.x, boundingBox.y + slide)) {
                    this.getPosition().y += slide;
                    this.getBoundingBox().y += slide;
                }
            }
            break;
            case DOWN: {
                if (!this.checkCollisionBox(boundingBox.x, boundingBox.y - slide)) {

                    this.getPosition().y -= slide;
                    this.getBoundingBox().y -= slide;
                }
            }
            break;
            case LEFT: {
                if (!this.checkCollisionBox(boundingBox.x - slide, boundingBox.y)) {

                    this.getPosition().x -= slide;
                    this.getBoundingBox().x -= slide;
                }
            }
            break;
            case RIGHT: {
                if (!this.checkCollisionBox(boundingBox.x + slide, boundingBox.y)) {

                    this.getPosition().x += slide;
                    this.getBoundingBox().x += slide;
                }
            }
            break;
        }
        setDirection(direction);
    }

    public void setMapManager(MapManager mapManager) {
        this.mapManager = mapManager;
        updatePlayerState();
    }

    /**
     * Sprawdzenie możliwości zderzenia ze solidną przeszkodą
     * @param rectangle pole kolizji podlegające sprawdzeniu
     * @return true na obecność kolizji, false w przeciwnym wypadku
     */
    public boolean checkCollisionWithSolid(Rectangle rectangle) {
        return this.mapManager.isInCollisionWithSolid(rectangle);
    }

    /**
     * Sprawdzenie czy następuje kolizja z jakimkolwiek z elementów
     * @param x pozycja na osi X
     * @param y pozycja na osi Y
     * @return true w sytuacji kolizji, false w sytuacji braku kolizji
     */
    private boolean checkCollisionBox(float x, float y) {
        if (this.mapManager != null) {
            return this.mapManager.isColliding(new Rectangle(x, y, this.getBoundingBox().width,
                    this.getBoundingBox().height));
        } else {
            return false;
        }
    }


    public boolean isPlayable() {
        return PlayerDetails.PLAYER_ONE.equals(this.playerDetails);
    }

    public PlayerStateData getPlayerState() {
        return this.playerState;
    }

    /**
     * Metoda uaktualnia stan danej istancji gracza obiektem PlayerStateData
     * @param stateData dane do aktualizacji stanu gracza
     */
    public synchronized void updatePlayer(PlayerStateData stateData) {
        if (stateData != null && this.playerState != null && !this.isPlayable()) {
            if (!stateData.isMoving()) {
                //reset frame count if npc is not moving
                this.frameTime = 0.0f;
            }
            this.direction = stateData.getDirection();
            this.getPosition().set(stateData.getPosX(), stateData.getPosY());
        }
    }

    private void initPlayerState() {
        this.playerState = new PlayerStateData();
        updatePlayerState();
    }

    /**
     * Uaktualnienie wewnętrznej instancji PlayerStateData aktualnym stanem gracza
     */
    public void updatePlayerState() {
        this.playerState.setMoving(Float.compare(this.frameTime, 0.0f) > 0);
        this.playerState.setDirection(this.direction);
        this.playerState.setPosX(this.getPosition().x);
        this.playerState.setPosY(this.getPosition().y);
    }

    /**
     * Renderowanie obiektów bomb.
     * W przypadku zakończenia efektu cząstek następuje zwolnienie pamięci zajmowanej przez zbedny obiekt
     * @param batch - instancja Batch niezbędna do renderowania
     */
    private void renderBombs(SpriteBatch batch) {
        for (int i = 0; i < this.bombList.size; i++) {
            if (this.bombList.get(i).isDone()) {
                this.bombList.get(i).dispose();
                this.bombList.removeIndex(i);
            } else {
                this.bombList.get(i).render(batch);
            }
        }
    }

    private void updateBombList(float delta) {
        for (int i = 0; i < this.bombList.size; i++) {
            this.bombList.get(i).update(delta);
        }
    }

    public synchronized String updateBombGlobalList() {
        Pair pair = null;

        if (Integer.compare(bombPacketCountTemp, 0) > 0) {
            for (int i = 0; i < this.bombList.size; i++) {
                if (vector2.equals(bombList.get(i).getPosition()))
                    pair = new Pair(this.bombList.get(i).getPosition().x, this.bombList.get(i).getPosition().y);
            }
            bombPacketCountTemp--;
        }

        return (pair != null) ? new Json().toJson(pair) : "";
    }

    /**
     * Dodanie bomb
     */
    public void dropDaBomb() {
        final float offset = 0.25f;
        int squareX = (int) (getPosition().x / GameDefinitions.TILE_SIZE);
        int playerTile = (int) ((getPosition().x - Bomb.BOMB_SIZE.x) / GameDefinitions.TILE_SIZE);
        if (squareX == 0 || playerTile == squareX) return;
        int squareY = (int) (getPosition().y / GameDefinitions.TILE_SIZE);
        if (squareY == 0) return;

        Rectangle bombRectangle = new Rectangle((squareX + offset) * GameDefinitions.TILE_SIZE,
                (squareY + offset) * GameDefinitions.TILE_SIZE, Bomb.BOMB_SIZE.x, Bomb.BOMB_SIZE.y);
        if (this.mapManager.isColliding(bombRectangle)) {
            return;
        }


        vector2 = new Vector2(bombRectangle.x, bombRectangle.y);
        Bomb bomba = null;
        bomba = new MiniBomb(vector2, this);
        //for further game development possibility
/*        switch(new Random().nextInt(3)){
            case 0:{
                bomba = new MiniBomb(new Vector2(bombRectangle.x, bombRectangle.y),this);
            }
            break;
            case 1:{
                bomba = new MediumBomb(new Vector2(bombRectangle.x, bombRectangle.y),this);
            }
            break;
            case 2:{
                bomba = new MaxBomb(new Vector2(bombRectangle.x, bombRectangle.y),this);
            }break;
        }*/
        this.bombList.add(bomba);
        bombPacketCountTemp = bombPacketCount;
    }

    /**
     * Metoda sprawdza jeśli obszar eksplozji pokrywa się z obszarem kolizyjnym
     * niszczalnego bloku i/lub gracza lub niegrywalnej postaci
     * W sutuacji kolizji obiekt jest usuwany, zaś gracz/niegrywalna postać tracą życie
     * @param rectangle pole eksplozji bedace przyrównywane do bloczków/gracza
     */
    public void destroyBoxes(Rectangle rectangle) {

        for (ExplodableBlock block : this.mapManager.getExplodableBlocks()) {
            if (block.isColliding(rectangle)) {
                block.destroy();
            }
            if (rectangle.overlaps(getBoundingBox())) {
                if (this.isPlayable()) {
                    playerHit();
                } else {
                    npcHit();
                }
            }
        }
    }

    /**
     * Definiuje logikę trafienia gracza. Po trafieniu gracz zostaje cofnięty na pozycję startową oraz zostaje pozbawiony życia.
     * W zależności od ilości żyć po trafieniu następuje wyświetlenie stosownego komunikatu. W trakcie wyświetlania komunikatu
     * gracz nie może być ponownie trafiony;
     */
    private void playerHit() {
        if (!this.displayedText.isVisible()) {
            this.lives--;
            if (this.lives != 0) {
                resetPlayer();
                //set text
                this.displayedText.setTextContent("Player Killed!");
            } else {
                restartGame();
                //set text
                this.displayedText.setTextContent("Game over");
            }
            //execute action in separate thread
            this.displayedText.setThread();
        }
    }

    /**
     * Analogiczna funkcjonalnośc do metody playerHit, ale dopasowana dla niegrywalnego gracza
     */
    private void npcHit() {
        if (!this.displayedText.isVisible()) {
            this.lives--;
            if (this.lives != 0) {
                resetPlayer();
                //set text
                this.displayedText.setTextContent("NPC was Killed!");
            } else {
                restartGame();
                //set text
                this.displayedText.setTextContent("You won");
            }
            //execute action in separate thread
            this.displayedText.setThread();
        }
    }

    /**
     * Dodanie bomby otrzymanej od serwera do kolekcji gracza
     * @param bombPosition  JSON z strukturą Pair
     */
    public void placeBombs(String bombPosition) {
        Pair pair = new Json().fromJson(Pair.class, bombPosition.trim());
        if (pair != null) {
            this.bombList.add(new MiniBomb(new Vector2(pair.getPosX(), pair.getPosY()), this));
        }

    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getStartPosition() {
        return startPosition;
    }

    private void resetPlayer() {
        //reset position, collision rectangle and direct player down
        getPosition().set(this.mapManager.getStartPosition(true, startPosition));
        this.setBoundingBox(this.playerDetails.reductionRates().x, this.playerDetails.reductionRates().y);
        this.setDirection(Directions.DOWN);
    }

    private void restartGame() {
        resetPlayer();
        this.lives = GameDefinitions.MAX_LIVES;
    }

    @Override
    public void dispose() {
        if (this.displayedText != null) {
            this.displayedText.dispose();
        }
    }
}
