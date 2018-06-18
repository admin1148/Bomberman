package com.bomberman.objects;
import com.badlogic.gdx.math.Vector2;
import com.bomberman.utils.Directions;

/**
 * @author Maciej Parandyk
 * @author Mateusz Kloc
 */

public enum PlayerDetails {
    //playable character specification
    PLAYER_ONE(100.0f,"Bman_F_f0",0.09f,8,"Bman_F_f0",0.09f,8,"Bman_B_f0",0.09f,8,new Vector2(40.0f,60.0f),new Vector2(1.0f,1.0f),
            new Vector2(0.4f,0.5f), AbstractGameObject.Alignment.CENTER),

    //Non-playable character specification
    NPC(100.0f,"Creep_F_f0",0.09f,6,"Creep_S_f0",0.09f,7,"Creep_B_f0",0.09f,6,new Vector2(35.0f,35.0f),new Vector2(1.0f,1.0f),
            new Vector2(0.8f,0.8f), AbstractGameObject.Alignment.CENTER);

    private PlayerDetails(float moveSlide,String frontAnimationName,float frontFrameDuration,int frontNbrOfFrames,String sideAnimationName,
                          float sideFrameDuration, int sideNbrOfFrames, String backAnimationName,float backFrameDuration,
                          int backNbrOfFrames,Vector2 dimensions,Vector2 scale,
                          Vector2 reductionBox,AbstractGameObject.Alignment alignment){
        this.objectMoveSlide=moveSlide;
        this.objectFrontAnim=frontAnimationName;
        this.objectSideAnim=sideAnimationName;
        this.objectBackAnim=backAnimationName;
        this.objectFrontFrameDuration=frontFrameDuration;
        this.objectSideFrameDuration=sideFrameDuration;
        this.objectBackFrameDuration=backFrameDuration;
        this.objectFrontNumberOfFrames=frontNbrOfFrames;
        this.objectSideNumberOfFrames=sideNbrOfFrames;
        this.objectBackNumberOfFrames=backNbrOfFrames;
        this.objectDimension=dimensions;
        this.objectScale=scale;
        this.objectReductionRates=reductionBox;
        this.objectAlignment=alignment;
    }

    private final float objectMoveSlide;
    private final String objectFrontAnim;
    private final String objectSideAnim;
    private final String objectBackAnim;
    private final float objectFrontFrameDuration;
    private final float objectSideFrameDuration;
    private final float objectBackFrameDuration;
    private final int objectFrontNumberOfFrames;
    private final int objectSideNumberOfFrames;
    private final int objectBackNumberOfFrames;
    private final Vector2 objectDimension;
    private final Vector2 objectScale;
    private final Vector2 objectReductionRates;
    private AbstractGameObject.Alignment objectAlignment;

    public float slideDistance() {
        return objectMoveSlide;
    }

    public String animation(Directions animationType) {
        String tmp;
        switch(animationType){
            case DOWN:
                tmp=objectFrontAnim;
                break;
            case LEFT:
            case RIGHT:
                tmp=objectSideAnim;
                break;
            case UP:
                tmp=objectBackAnim;
                break;
                default:
                    tmp=null;
        }
        return tmp;
    }

    /**
     * Pobranie długości trwania pojedyńczej klatki animacji w zależnosci od typu animacji obiektu
     * @param animationType kierunek animacji/skierowanie gracza
     * @return długość trwania pojedyńczej klatki
     */
    public float frameDuration(Directions animationType) {
        float duration;
        switch(animationType){
            case DOWN:
                duration= this.objectFrontFrameDuration;
                break;
            case LEFT:
            case RIGHT:
                duration= this.objectSideFrameDuration;
                break;
            case UP:
                duration= this.objectBackFrameDuration;
                break;
            default:
               duration= 1.0f;
        }
        return duration;
    }

    public Vector2 dimensions() {
        return objectDimension;
    }

    public Vector2 scale() {
        return objectScale;
    }

    public Vector2 reductionRates() {
        return objectReductionRates;
    }

    public AbstractGameObject.Alignment alignment() {
        return objectAlignment;
    }

    /**
     * Podaje ilość klatek przypadających na daną animację
     * @param animationType kierunek animacji
     * @return ilość klatek przypadających na pełną animację
     * @return ilość klatek przypadających na pełną animację
     */
    public Integer animationFrames(Directions animationType) {
        Integer tmp;
        switch(animationType){
            case DOWN:
                tmp=this.objectFrontNumberOfFrames;
                break;
            case LEFT:
            case RIGHT:
                tmp=this.objectSideNumberOfFrames;
                break;
            case UP:
                tmp=this.objectBackNumberOfFrames;
                break;
            default:
                tmp=0;
        }
        return tmp;
    }
}
