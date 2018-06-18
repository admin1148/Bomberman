package com.bomberman.objects;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.bomberman.utils.Directions;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.bomberman.utils.GameDefinitions;
import com.bomberman.utils.GraphicAssets;

/**
 * Klasa bomby.
 *
 * Na wybuch bomby składają się dwa zasadnicze elementy: animacja trwajaca 1,45 sekundy podczas której zmienia
 * się wygląd bomby, oraz cząsteczkowy efekt wybuchu. W trakcie wybuch następuje rekurencyjne sprawdzenie kolizji
 * z obiektami mapy. Jeżeli wybuch napotka na swojej drodze bloku solid następuje przerwanie wykonania w danym
 * kierunku, zaś w sytuacji natrafienia na gracza lub bloku zniszczalnego następuje przekazanie informacji o trafieniu.
 * Podczas działania programu instancje klasy Bomb zostają usunięte z kontenera i ich pamięć zostaje zwolniona.
 *
 * @author Maciej Parandyk
 * @author Mateusz Kloc
 */


public abstract class Bomb extends AbstractGameObject implements Disposable{
    public static final Vector2 BOMB_SIZE=new Vector2(25.0f,25.0f);
    private final Animation<TextureRegion> bombAnimation;
    private TextureRegion frame;
    private static final float FRAME_DURATION=0.5f;
    private float duration;
    private boolean bParticleStarted;
    private final PlayerInstance player;
    private final Float radius;
    private final Array<ParticleEffect> particles;
    private Sound chunkyBlast;

    Bomb(Vector2 position, PlayerInstance player, float radius) {
        super(new Vector2(position), BOMB_SIZE,new Vector2(1.0f,1.0f),new Vector2(1.0f,1.0f),Alignment.CENTER);
        this.particles=new Array<>();
        this.radius=radius;
        this.bombAnimation=new Animation<>(FRAME_DURATION,GraphicAssets.getInstance().getBombAnimation(), Animation.PlayMode.NORMAL);
        this.player=player;
        this.chunkyBlast = Gdx.audio.newSound(Gdx.files.internal("./sounds/Explosion.mp3"));
        init();
    }

    @Override
    public void render(SpriteBatch batch) {
        if (this.frame != null && this.duration<1.75f) {
            //draw bomb animation frame
            batch.draw(this.frame.getTexture(),getPosition().x,getPosition().y,0.5f,0.5f,getDimension().x,
                    getDimension().y,getScale().x,getScale().y,0.0f,this.frame.getRegionX(),this.frame.getRegionY(),
                    this.frame.getRegionWidth(),this.frame.getRegionHeight(),false,false);
        }else{
            if (!this.bParticleStarted) {
                this.bParticleStarted=true;
                //start particles
                for(ParticleEffect p : this.particles)
                    p.start();
                destroyBlocks();
            }
            if (this.particles!=null && this.particles.size>0 && !this.particles.get(0).isComplete()) {
                final float deltaTime = Gdx.graphics.getDeltaTime();
                //draw particle effects
                for(ParticleEffect p : this.particles)
                    p.draw(batch,deltaTime);
            }
        }
    }

    @Override
    public void update(float delta) {
        this.duration+=delta;
        this.frame=this.bombAnimation.getKeyFrame(duration);
    }

    /**
     * Pusta implementacja metody oddziedziczonej po bazowej klasie abstrakcyjnej
     * @param direction kierunek
     * @param slide wartość przejscia/ruchu
     */
    @Override
    public void updateLocation(Directions direction, float slide) {}

    @Override
    public void dispose() {
        chunkyBlast.dispose();
        if(this.particles != null)
        for(ParticleEffect p : this.particles)
            p.dispose();
    }

    private void init(){
        initParticleEffect();
        this.duration=0.0f;
        this.frame=this.bombAnimation.getKeyFrame(this.duration);
        this.bParticleStarted=false;
    }

    private void initParticleEffect(){
        //central particle
        this.particles.add(GraphicAssets.getInstance().getBlastEffect(getPosition()));
        this.initParticleEffect(1.0f,false,false,false,false);
    }

    /**
     * Rekursywne określenie położenia efektu cząsteczek względem początkowej pozycji obiektu
     * oraz prawdopodobnych zniszczalnych bloków i solidnych blokad
     * @param radiusValue promień rozprzestrzenienia się effektu od punktu początkowego po obu osiach X i Y
     * @param bUp flaga możliwości wystapienia efektu na pozycji powyżej
     * @param bDown flaga możliwości wystapienia efektu na pozycji poniżej
     * @param bLeft flaga możliwości wystapienia efektu na pozycji na lewo
     * @param bRight flaga możliwości wystapienia efektu na pozycji na prawo
     */
    private void initParticleEffect(float radiusValue,boolean bUp, boolean bDown,boolean bLeft, boolean bRight){
        if(Float.compare(this.radius*0.5f-1.0f,radiusValue)<0) return;

        final float tileSize = GameDefinitions.TILE_SIZE;
        Rectangle tmpRectangle = null;
        //up
        if(!bUp){
            tmpRectangle = new Rectangle(getPosition().x,getPosition().y+(radiusValue*tileSize),
                    getDimension().x, getDimension().y);
            if (!this.player.checkCollisionWithSolid(tmpRectangle)) {
                this.particles.add(GraphicAssets.getInstance().getBlastEffect(tmpRectangle.x,tmpRectangle.y));
            }else{
                bUp = true;
            }
        }

        //down
        if(!bDown){
            tmpRectangle = new Rectangle(getPosition().x,getPosition().y-(radiusValue*tileSize),
                    getDimension().x, getDimension().y);
            if (!this.player.checkCollisionWithSolid(tmpRectangle)) {
                this.particles.add(GraphicAssets.getInstance().getBlastEffect(tmpRectangle.x,tmpRectangle.y));
            }else{
                bDown = true;
            }
        }

        //left
        if(!bLeft){
            tmpRectangle =  new Rectangle(getPosition().x-(radiusValue*tileSize), getPosition().y,
                    getDimension().x, getDimension().y);
            if (!this.player.checkCollisionWithSolid(tmpRectangle)) {
                this.particles.add(GraphicAssets.getInstance().getBlastEffect(tmpRectangle.x,tmpRectangle.y));
            }else{
                bLeft = true;
            }
        }

        //right
        if(!bRight){
            tmpRectangle = new Rectangle(getPosition().x+(radiusValue*tileSize), getPosition().y,
                    getDimension().x, getDimension().y);
            if (!this.player.checkCollisionWithSolid(tmpRectangle)) {
                this.particles.add(GraphicAssets.getInstance().getBlastEffect(tmpRectangle.x,tmpRectangle.y));
            }else{
                bRight = true;
            }
        }

        tmpRectangle = null;
        initParticleEffect(++radiusValue, bUp,  bDown, bLeft, bRight);
    }

    private void destroyBlocks(){
        this.player.destroyBoxes(getBoundingBox());
        destroyBlocks(1.0f,false,false,false,false);
    }

    /**
     * Metoda za pomocą rekursywnego wywołanie decyduje o usunięciu zniszczalnych bloczków.
     * W sytuacji napotkania solidnej przeszkody lub osiągnięcia skraju promienia następuje przerwanie eksploracji w danym kierunku
     * @param radiusValue describes maximal expansion range for both axis X & Y
     * @param bUp flaga określająca eksplorację w górę dla eksplozji
     * @param bDown flaga określająca eksplorację w dół dla eksplozji
     * @param bLeft flaga określająca eksplorację w lewo dla eksplozji
     * @param bRight flaga określająca eksplorację w prawo dla eksplozji
     */
    private void destroyBlocks(float radiusValue,boolean bUp, boolean bDown,boolean bLeft, boolean bRight){
        if(Float.compare(this.radius*0.5f-1.0f,radiusValue)<0) return;
        final float tileSize = GameDefinitions.TILE_SIZE;
        chunkyBlast.play();
        Rectangle tmpRectangle = null;
        //up
        if(!bUp){
            tmpRectangle = new Rectangle(getPosition().x,getPosition().y+(radiusValue*tileSize),
                    getDimension().x, getDimension().y);
            if (this.player.checkCollisionWithSolid(tmpRectangle)) {
                bUp=true;
            }else {
                this.player.destroyBoxes(tmpRectangle);
            }
        }

        //down
        if(!bDown){
            tmpRectangle = new Rectangle(getPosition().x,getPosition().y-(radiusValue*tileSize),
                    getDimension().x, getDimension().y);
            if (this.player.checkCollisionWithSolid(tmpRectangle)) {
                bDown=true;
            }else {
                this.player.destroyBoxes(tmpRectangle);
            }
        }

        //left
        if(!bLeft){
            tmpRectangle =  new Rectangle(getPosition().x-(radiusValue*tileSize), getPosition().y,
                    getDimension().x, getDimension().y);
            if (this.player.checkCollisionWithSolid(tmpRectangle)) {
                bLeft=true;
            }else {
                this.player.destroyBoxes(tmpRectangle);
            }
        }

        //right
        if(!bRight){
            tmpRectangle = new Rectangle(getPosition().x+(radiusValue*tileSize), getPosition().y,
                    getDimension().x, getDimension().y);
            if (this.player.checkCollisionWithSolid(tmpRectangle)) {
                bRight=true;
            }else {
                this.player.destroyBoxes(tmpRectangle);
            }
        }

        tmpRectangle = null;
        destroyBlocks(++radiusValue, bUp,  bDown, bLeft, bRight);
    }

    public boolean isDone(){
        return this.particles!=null && this.particles.size>0 && this.particles.get(0).isComplete();
    }
}
