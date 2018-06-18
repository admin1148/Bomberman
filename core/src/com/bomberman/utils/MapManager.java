package com.bomberman.utils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.Array;
import com.bomberman.objects.ExplodableBlock;
import com.bomberman.objects.PlayerInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mateusz Kloc
 */



public class MapManager implements Disposable{
    private static final Logger logger=LoggerFactory.getLogger(MapManager.class);
    private static final String COLLISION_LAYER="solid";
    private static final String START_POINT="start";
    private static final String DESTROYABLE_LAYER="destroyable";
    private TiledMap tiledMap;
    private MapLayer collisionLayer=null;
    private MapLayer startPoints=null;
    private MapLayer destroyableLayer=null;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private final Array<ExplodableBlock> explodableBlocks=new Array<>();
    private final Array<Integer> destroyableBlockList = new Array<>();

    public MapManager() {
        this.tiledMap=null;
        this.camera=new OrthographicCamera(GameDefinitions.VIEWPORT_WIDTH,GameDefinitions.VIEWPORT_HEIGHT);
        this.camera.setToOrtho(false);
        this.viewport=new FitViewport(GameDefinitions.VIEWPORT_WIDTH,
                GameDefinitions.VIEWPORT_HEIGHT,this.camera);
    }

    public void loadMap(String mapName){
      GraphicAssets.getInstance().loadMapAssets(mapName);
      this.tiledMap=GraphicAssets.getInstance().getMapAsset(mapName);
      if(this.tiledMap!=null){
        prepareMapRenderer();
        prepareMapLayers();
      }
    }


    public void render(){
        if(this.mapRenderer!=null){
           this.mapRenderer.setView(this.camera);
           this.mapRenderer.render();
        }
    }

    private void prepareMapRenderer(){
        this.mapRenderer=new OrthogonalTiledMapRenderer(this.tiledMap,1.0f);
        this.mapRenderer.setView(this.camera);
    }

    public Viewport getViewport() {
        return viewport;
    }

    private void prepareMapLayers(){
        //getting collision space
        this.collisionLayer=this.tiledMap.getLayers().get(COLLISION_LAYER);

        //get destroyable blocks positions
        this.destroyableLayer=this.tiledMap.getLayers().get(DESTROYABLE_LAYER);
        //get start positions
        logger.info("Available tart positions: "+this.tiledMap.getLayers().get(START_POINT).getObjects().getCount());
        this.startPoints=this.tiledMap.getLayers().get(START_POINT);
    }

    @Override
    public void dispose() {
        this.mapRenderer.dispose();
        this.collisionLayer=null;
    }

    /**
     * Pobranie pozycji gracza w formie instancji klasy Vector2
     * @param player flaga określająca czy poszukiwana pozycja jest dla grywalnego gracza.
     * @param position wartość określająca pozycję opartą o zero
     * @return pozycja w formie Vector2
     */
    public Vector2 getStartPosition(Boolean player, Integer position){
       if(this.startPoints!=null){
           Rectangle rect;
           if (player) {
               rect = ((RectangleMapObject)this.startPoints.getObjects().get(position)).getRectangle();
           } else {
               rect = ((RectangleMapObject)this.startPoints.getObjects().get((Integer.compare(position, 0)== 0)? 1 : 0)).getRectangle();
           }
           float scale=GraphicAssets.getMapUnitScale();
           return new Vector2(rect.x+rect.width*scale,rect.y+rect.height*scale);
       }
        return new Vector2(0,0);
    }

    public synchronized boolean isColliding(Rectangle rectangle){
            if(this.isInCollisionWithSolid(rectangle) || this.isInCollisionWithBlock(rectangle)){
                return true;
            }

        return false;
    }

    public synchronized boolean isInCollisionWithSolid(Rectangle rectangle){
        if(this.collisionLayer==null || this.collisionLayer.getObjects().getCount()==0)
            return false;

        for(MapObject obj:this.collisionLayer.getObjects()) {
            Rectangle solidRect = null;
            if (obj.getClass().equals(RectangleMapObject.class)) {
                solidRect = ((RectangleMapObject) obj).getRectangle();
            }
            if (obj.getClass().equals(PolygonMapObject.class)) {
                solidRect = ((PolygonMapObject) obj).getPolygon().getBoundingRectangle();
            }
            if (solidRect != null && solidRect.overlaps(rectangle)) {
                return true;
            }
        }
            return false;
    }

    public synchronized boolean isInCollisionWithBlock(Rectangle rect){
       for(int i=0;i<this.explodableBlocks.size;i++){
            if(this.explodableBlocks.get(i).isColliding(rect)){
                return true;
            }
        }
        return false;
    }

    /**
     * Inicjalizacja niszczalnych bloków
     * @param arrayList ciąg znakowy z indeksami bloków
     */
    public void populateExplodableBlocks(String arrayList){
        if (this.destroyableLayer != null && this.destroyableLayer.getObjects().getCount() > 0) {
            ArrayList<Integer> points = Arrays.asList(arrayList.split(",")).parallelStream()
                    .map((idx)->{
                        try {
                           return Integer.parseInt(idx);
                        } catch (Exception ex) {
                        return null;}
                            }
                    ).collect(Collectors.toCollection(ArrayList::new));
            points.removeIf(i -> i ==null);


            ArrayList<MapObject> objects = new ArrayList<>();
            for (MapObject object:this.destroyableLayer.getObjects()) {
                objects.add(object);
            }
                for (int i=0; i<points.size()-1; i++) {
                Rectangle rect=((RectangleMapObject)objects.get(points.get(i))).getRectangle();
                this.explodableBlocks.add(new ExplodableBlock(new Vector2(rect.x,rect.y)));
            }
        }
    }

    public Array<ExplodableBlock> getExplodableBlocks() {
        return explodableBlocks;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}

