package com.bomberman.utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Array;
import com.bomberman.objects.PlayerDetails;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.io.File.separator;

/**
 * GraphicAssets odpowiada za zarządzanie zasobami gry: atlasy/strony, animacje, mapy oraz efekt cząstek
 * @author Maciej Parandyk
 */
public class GraphicAssets implements Disposable,AssetErrorListener{
    private static final Logger logger=LoggerFactory.getLogger(GraphicAssets.class);
    //resources paths
    private static final String ATLAS_PATH="atlases"+separator;
    private static final String FONTS="fonts"+separator;
    private static final String PLAYABLE="bomberman";
    private static final String NPC="creep";
    //playable character resources
    private static final AssetDescriptor<TextureAtlas> PLAYER_FRONT=
            new AssetDescriptor<TextureAtlas>(ATLAS_PATH+PLAYABLE+separator+"front.atlas",TextureAtlas.class);
    private static final AssetDescriptor<TextureAtlas> PLAYER_BACK=
            new AssetDescriptor<TextureAtlas>(ATLAS_PATH+PLAYABLE+separator+"back.atlas",TextureAtlas.class);
    private static final AssetDescriptor<TextureAtlas> PLAYER_SIDE=
            new AssetDescriptor<TextureAtlas>(ATLAS_PATH+PLAYABLE+separator+"side.atlas",TextureAtlas.class);
    //NPC animation resources
    private static final AssetDescriptor<TextureAtlas> NPC_FRONT=
            new AssetDescriptor<TextureAtlas>(ATLAS_PATH+NPC+separator+"front.atlas",TextureAtlas.class);
    private static final AssetDescriptor<TextureAtlas> NPC_BACK=
            new AssetDescriptor<TextureAtlas>(ATLAS_PATH+NPC+separator+"back.atlas",TextureAtlas.class);
    private static final AssetDescriptor<TextureAtlas> NPC_SIDE=
            new AssetDescriptor<TextureAtlas>(ATLAS_PATH+NPC+separator+"side.atlas",TextureAtlas.class);
    private static final AssetDescriptor<TextureAtlas> HUD_ELEMENTS=
            new AssetDescriptor<TextureAtlas>(ATLAS_PATH+"other"+separator+"hudelements.atlas",TextureAtlas.class);
    //font descriptors
    private static final AssetDescriptor<BitmapFont> HUD_FONT=
            new AssetDescriptor<BitmapFont>(FONTS+"hud_font.fnt",BitmapFont.class);
    private static final AssetDescriptor<BitmapFont> INFO_FONT =
            new AssetDescriptor<>(FONTS+"info.fnt",BitmapFont.class);
    //bomb descriptor
    private static final AssetDescriptor<TextureAtlas> BOMB=
            new AssetDescriptor<TextureAtlas>(ATLAS_PATH+"bomb"+separator+"bomb.atlas",TextureAtlas.class);
    private static final String BOMB_FRAME_NAME="Bomb_f0";
    private static final int BOMB_FRAMES=3;
    //explodable box assets
    private static final String EXPL_BLOCK_ROOT_DIR="maps"+separator+"test";
    private static final String EXPL_BLOCK_FILE="ExplodableBlock.png";
    private static final AssetDescriptor<Texture> EXPLODABLE_BLOCK=
            new AssetDescriptor<>(EXPL_BLOCK_ROOT_DIR+separator+EXPL_BLOCK_FILE,Texture.class);
    //particle effect resources
    private static final String PARTICLE_ROOT_DIR="sfx";
    private static final String PARTICLE_EFFECT_FILE="explosion.p";
    //tiledmap resources
    private static final float UNIT_SCALE=1/64.0f;
    private static final String MAPS_MAIN_PATH="maps"+separator+"test"+separator;
    private static GraphicAssets instance;
    private AssetManager assetManager;
    private InternalFileHandleResolver fileResolver;

    private GraphicAssets() {
        this.fileResolver=new InternalFileHandleResolver();
        this.assetManager=new AssetManager();
        this.assetManager.setErrorListener(this);
        //load playable
        this.assetManager.load(PLAYER_FRONT);
        this.assetManager.load(PLAYER_SIDE);
        this.assetManager.load(PLAYER_BACK);
        //load NPC
        this.assetManager.load(NPC_FRONT);
        this.assetManager.load(NPC_SIDE);
        this.assetManager.load(NPC_BACK);
        //load info font
        this.assetManager.load(HUD_ELEMENTS);
        this.assetManager.load(HUD_FONT);
        this.assetManager.load(INFO_FONT);
        //load bomb
        this.assetManager.load(BOMB);
        //load explodable block asset
        this.assetManager.load(EXPLODABLE_BLOCK);
        this.assetManager.finishLoading();
    }

    public static GraphicAssets getInstance(){
        if(instance==null){
            instance=new GraphicAssets();
        }
        return instance;
    }

    public Animation<TextureRegion> getAnimation(PlayerDetails playerDetails,Directions animationType){
        if(playerDetails==null || animationType==null) return null;

        AssetDescriptor<TextureAtlas> assetDescriptor;
        switch (animationType){
            case DOWN:{
                if (playerDetails.equals(PlayerDetails.PLAYER_ONE)) {
                    assetDescriptor=PLAYER_FRONT;
                } else {
                    assetDescriptor=NPC_FRONT;
                }
            }
            break;
            //by default sprites are directed to right
            case LEFT:
            case RIGHT:{
                if (playerDetails.equals(PlayerDetails.PLAYER_ONE)) {
                    assetDescriptor=PLAYER_SIDE;
                } else {
                    assetDescriptor=NPC_SIDE;
                }
            }
            break;
            case UP:{
                if (playerDetails.equals(PlayerDetails.PLAYER_ONE)) {
                    assetDescriptor=PLAYER_BACK;
                } else {
                    assetDescriptor=NPC_BACK;
                }
            }
            break;
            default:
                    return null;
        }


        return new Animation<TextureRegion>(playerDetails.frameDuration(animationType),
                getTextureArray(playerDetails.animation(animationType),playerDetails.animationFrames(animationType),assetDescriptor),PlayMode.LOOP);
    }

    public TextureRegion getHudElement(String texture){
        TextureAtlas txtAtl=this.assetManager.get(HUD_ELEMENTS);
        return txtAtl.findRegion(texture);
    }

    public BitmapFont getHudFont(){
        return this.assetManager.get(HUD_FONT);
    }

    public BitmapFont getInfoFont(){
        return this.assetManager.get(INFO_FONT);
    }

    private  Array<TextureRegion> getTextureArray(String name,int textures,AssetDescriptor<TextureAtlas> descriptor){
        TextureAtlas atl=this.assetManager.get(descriptor);
        Array<TextureRegion> array=new Array<TextureRegion>();
        for(int i=0;i<textures;i++){
            array.add(atl.findRegion(name+i));
            array.get(i).getTexture().setFilter(TextureFilter.Linear,TextureFilter.Linear);
        }
        return array;
    }

    public void loadMapAssets(String path){
        String map=MAPS_MAIN_PATH+path;
        if(this.fileResolver.resolve(map).exists()){
            //load map
            this.assetManager.setLoader(TiledMap.class,new TmxMapLoader(this.fileResolver));
            this.assetManager.load(map,TiledMap.class);
            this.assetManager.finishLoadingAsset(map);
        }else{
            logger.info("Map "+path+" does not exists under: "+MAPS_MAIN_PATH);
        }
    }

    public TiledMap getMapAsset(String filename) {
        TiledMap tiledMap=null;
        String mapFile=MAPS_MAIN_PATH+filename;
        if(this.assetManager.isLoaded(mapFile)){
            tiledMap=this.assetManager.get(mapFile,TiledMap.class);
        }else{
            logger.error("Requested map \""+filename+"\" is not loaded");
        }
        return tiledMap;
    }

    public static float getMapUnitScale() {
        return UNIT_SCALE;
    }

    public Array<TextureRegion> getBombAnimation(){
        Array<TextureRegion> anim=new Array<>();
        TextureAtlas tmp=this.assetManager.get(BOMB);
        for(int i=0;i<BOMB_FRAMES;i++) {
            String name=String.format("%s%d",BOMB_FRAME_NAME,i);
            anim.add(tmp.findRegion(name));
            anim.get(i).getTexture().setFilter(TextureFilter.Linear,TextureFilter.Linear);
        }
        return anim;
    }

    public ParticleEffect getBlastEffect(float x, float y){
        ParticleEffect p=new ParticleEffect();
        p.load(Gdx.files.internal(PARTICLE_ROOT_DIR+separator+PARTICLE_EFFECT_FILE),Gdx.files.local(PARTICLE_ROOT_DIR));
        final float halfTileOffset=GameDefinitions.TILE_SIZE*0.3f;
        p.setPosition(x+halfTileOffset,y);
        p.scaleEffect(1.0f);
        p.setDuration(1);
        return p;
    }

    public ParticleEffect getBlastEffect(Vector2 position){
       return getBlastEffect(position.x,position.y);
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        logger.error(String.format("Could not load: %s, %s",asset.file,asset.fileName),throwable);
    }

    public Texture getExplodableBlock(){
        return this.assetManager.get(EXPLODABLE_BLOCK);
    }

    @Override
    public void dispose() {
        this.assetManager.dispose();
    }
}