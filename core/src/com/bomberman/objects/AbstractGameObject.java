package com.bomberman.objects;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bomberman.utils.Directions;
import com.badlogic.gdx.math.Rectangle;
import com.bomberman.utils.GameDefinitions;

public abstract class AbstractGameObject {
    private final Vector2 position;
    private final Vector2 origin;
    private final Vector2 dimension;
    private final Vector2 scale;
    private final Rectangle boundingBox;
    private Alignment objectAlignment;

    /**
     * Wyspecjalizowany konstruktor zapewniający większą kontrolę nad położeniem, skalą, polem kolizyjnym oraz ułożeniem obiektu
     *
     * @author Maciej Parandyk
     *
     * @param position początkowa pozycja
     * @param dimension rozmiar w formie Vector2 dla wymiarów X i Y
     * @param scale skala
     * @param reduction stopień redukcji wymiarów do pełnego rozmiaru obiektu
     * @param alignment połozenie
     */

    public AbstractGameObject(Vector2 position, Vector2 dimension, Vector2 scale,Vector2 reduction,
                              AbstractGameObject.Alignment alignment) {
        this.position = position;
        this.origin = new Vector2();
        this.dimension = dimension;
        this.scale = scale;
        this.boundingBox=new Rectangle();
        setAlignment(alignment);
        setBoundingBox(reduction.x,reduction.y);
    }

    /**
     * Uproszczona wersja konstruktora z domyślnymi wartościami z wyjątkiem pozycji początkowej
     * oraz rozmiarami obiektu
     * @param position początkowa pozycja
     * @param dimension rozmiar w formie Vector2 dla wymiarów X i Y
     */
    public AbstractGameObject(Vector2 position,Vector2 dimension) {
        this.position = position;
        this.origin = new Vector2();
        this.scale=new Vector2();
        this.dimension=dimension;
        this.boundingBox=new Rectangle();
        resetScaleAndDimension();
        setAlignment(Alignment.BOTTOM_LEFT);
        setBoundingBox(0.0f,0.0f);
    }


    protected void resetScaleAndDimension() {
        this.dimension.set(1.0f,1.0f);
        this.scale.set(1.0f,1.0f);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getOrigin() {
        return origin;
    }

    public Vector2 getDimension() {
        return dimension;
    }

    public Vector2 getScale() {
        return scale;
    }

    public void setPosition(float x,float y) {
        position.set(x,y);
    }

    public void setPosition(Vector2 vec) {
        position.set(vec);
    }

    public void setOrigin(float x,float y) {
        origin.set(x,y);
    }

    public void setOrigin(Vector2 vec) {
        origin.set(vec);
    }

    public void setDimension(float x,float y) {
        this.dimension.set(x,y);
    }
    public void setDimension(Vector2 vec) {
        this.dimension.set(vec);
    }

    public void setScale(float x,float y) {
       this.scale.set(x,y);
    }
    public void setScale(Vector2 vec) {
        this.scale.set(vec);
    }

    /**
     * Metoda ustawia zasadniczy punkt położenia obiektu.
     * Dostępne opcje: TOP_LEFT, TOP_RIGHT, CENTER, BOTTOM_LEFT,BOTTOM_RIGHT
     * @param alignment - obiekt enuma Alignment
     */
    public void setAlignment(Alignment alignment){
        float width=0.0f, height=0.0f;
        float ratio=dimension.x/dimension.y;
        switch(alignment){
            case TOP_LEFT:{
                setOrigin(0.0f,dimension.y);
            }
            break;
            case TOP_RIGHT:{
                setOrigin(dimension.x,dimension.y);
            }
            break;
            case CENTER:{
                setOrigin(0.5f*dimension.x,0.5f*dimension.y);
            }
            break;
            case BOTTOM_LEFT:{

                setOrigin(0.0f,0.0f);
            }
            break;
            case BOTTOM_RIGHT:{
                setOrigin(dimension.x,0.0f);
            }
            break;
        }
        this.objectAlignment=alignment;
    }

    /**
     * Ustawienie pola kolizji dla obiektu.
     * @param percentWidthReduction - procentowa redukcja pola kolizji na osi X
     * @param percentHeightReduction - procentowa redukcja pola kolizji na osi Y
     */
    public void setBoundingBox(float percentWidthReduction,float percentHeightReduction){
       float widthReductionRate=1-percentWidthReduction;
       float heightReductionRate=1-percentHeightReduction;

       float width,height;
       if(widthReductionRate>0 && percentHeightReduction<1){
           width=widthReductionRate*dimension.x;
       }else{
           width=dimension.x;
       }

       if(heightReductionRate>0 && percentHeightReduction<1){
           height=heightReductionRate*dimension.y;
       }else{
           height=dimension.y;
       }


       float widthRatio= dimension.x/GameDefinitions.VIEWPORT_WIDTH;
       float heightRatio=dimension.y/GameDefinitions.VIEWPORT_HEIGHT;
       //adding a half of a difference between image and requested rectangle - centralizing boundering box
       float xAdjustment=((getDimension().x-width)*0.5f);
        //set bounding rectangle position and dimensions
        switch(this.objectAlignment){
            case TOP_LEFT:{
                this.boundingBox.set(position.x+widthRatio+xAdjustment,position.y+heightRatio,width,height);
            }
            break;
            case TOP_RIGHT:{
                this.boundingBox.set(position.x+xAdjustment,position.y+heightRatio,width,height);
            }
            break;
            case CENTER:{
                this.boundingBox.set(position.x+xAdjustment,position.y,width,height);
            }
            break;
            case BOTTOM_LEFT:{
                this.boundingBox.set(position.x+xAdjustment,position.y,width,height);
            }
            break;
            case BOTTOM_RIGHT:{
                this.boundingBox.set(position.x+widthRatio+xAdjustment,position.y,width,height);
            }
            break;
        }
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    abstract public void render (SpriteBatch batch);
    abstract public void update (float delta);
    abstract public void updateLocation(Directions direction, float slide);

    public enum Alignment{TOP_LEFT,TOP_RIGHT,CENTER,BOTTOM_LEFT,BOTTOM_RIGHT}
}
