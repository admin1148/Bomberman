package com.bomberman.objects;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.bomberman.utils.Directions;
import com.bomberman.utils.GraphicAssets;

/**
 * @author Maciej Parandyk
 * @author Mateusz Kloc
 */


public class ExplodableBlock extends AbstractGameObject {
    private boolean bDestroyed;
    private static final Sprite blockSprite= new Sprite(GraphicAssets.getInstance().getExplodableBlock());

    public ExplodableBlock(Vector2 position) {
        super(position, new Vector2(40.0f,40.0f),new Vector2(1.0f,1.0f),new Vector2(0.0f,0.0f),Alignment.BOTTOM_LEFT);
        this.bDestroyed=false;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(blockSprite,getPosition().x,getPosition().y,getOrigin().x,getOrigin().y,40.0f,40.0f,
                getScale().x, getScale().y,0.0f,false);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void updateLocation(Directions direction, float slide) {

    }

    public boolean isDestroyed() {
        return bDestroyed;
    }

    public void destroy(){
        this.bDestroyed=true;
    }

    public synchronized boolean isColliding(Rectangle rect){
        return this.getBoundingBox().overlaps(rect) && !this.bDestroyed;
    }
}
