package com.bomberman.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.bomberman.BombermanGame;
import com.bomberman.controller.GameController;
import com.bomberman.controller.RenderController;
import com.bomberman.objects.PlayerDetails;
import com.bomberman.objects.PlayerInstance;
import com.badlogic.gdx.graphics.GL20;
import com.bomberman.server.BombermanServerControllerImpl;
import com.bomberman.server.ServerController;
import com.bomberman.utils.MapManager;
import com.badlogic.gdx.utils.Array;
import java.util.Arrays;

/**
 * @author Maciej Parandyk
 * @author Mateusz Kloc
 */


public class TestScreen implements Screen{
    private final BombermanGame game;
    private RenderController renderer;
    private GameController controller;
    private Array<PlayerInstance> players;
    private MapManager mapManager;
    private ServerController serverController;
    public TestScreen(BombermanGame game) {
        this.game=game;
    }

    /**
     * Metoda uruchamiająca. Na tym etapie wczytywane są wszystkie potrzebne pliki (takie jak obrazy lub dźwięki),
     * tworzone są obiekty gry, a wartości są inicjalizowane.
     */

    @Override
    public void show() {
        this.serverController=new BombermanServerControllerImpl();
        this.mapManager=new MapManager();
        this.mapManager.loadMap("test.tmx");
        this.mapManager.populateExplodableBlocks(this.serverController.getBlocks());
        initPlayersArray();

        //init ServerController
        this.controller=new GameController(this.players,this.mapManager.getExplodableBlocks());
        this.renderer=new RenderController(this.controller,this.game.getBatch(),this.mapManager.getCamera());
    }

    private void initPlayersArray() {
        this.players=new Array<>();
        //playable character
        Integer position = this.serverController.getPosition();
        this.players.add(new PlayerInstance(PlayerDetails.PLAYER_ONE,this.mapManager.getStartPosition(true,position)));
        this.players.get(0).setStartPosition(position);
        this.players.add(new PlayerInstance(PlayerDetails.NPC,this.mapManager.getStartPosition(false,position)));
        this.serverController.setPlayers(this.players);
        for(PlayerInstance instance : this.players){
            instance.setMapManager(this.mapManager);
        }
    }

    /**
     * Metoda renderowania. Rysuje wszystkie grafiki na ekranie, takie jak obrazy tła,
     * świat gry oraz interfejs użytkownika.
     * @param delta czas jaki upłynął od ostatniej ramki do obecnej ramki
     */

    @Override
    public void render(float delta) {
        if(!this.controller.isPaused()){
            this.renderer.update(delta);
            this.serverController.update();
        }
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.mapManager.render();
        this.renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        this.renderer.resize(width,height);
    }

    @Override
    public void pause() {
        this.controller.pause();
    }

    @Override
    public void resume() {
        this.controller.resume();
    }

    @Override
    public void hide() {

    }

    /**
     * Metoda wyłączająca grę.
     * Etap ten rozpoczyna się, gdy gracz wprowadzi dane do komputera wskazujące, że zakończył on korzystanie z oprogramowania
     * (na przykład klikając przycisk Zakończ) i obejmuje usunięcie obrazów i danych z pamięci,
     * zapisanie danych gry lub stanu gry, sygnalizując komputerowi zaprzestanie monitorowania urządzeń sprzętowych
     * pod kątem wprowadzania i zamykania wszystkich okien utworzonych w grze.
     */

    @Override
    public void dispose() {
        this.mapManager.dispose();
        this.serverController.closeConnection();
    }
}
