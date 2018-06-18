package com.bomberman.runner;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Application;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.AfterClass;
import org.mockito.Mockito;

/**
 * Runner zapewniający podstawową funkcjonalność frameworka Libgdx dla testów. Wszelkie testy muszą być odpalane
 * z tym runnerem, poprzez adnotację @RunWith, lub implementacji klasy z testami dziedziczącej z BombermanTestRunner.
 * Podczas dziedziczenia, zmiana implementacji metod interfejsu ApplicationListener pozwalaja na przetestowanie
 * funkcjonalności powiazanej z cyklem aplikacji
 */
public class BombermanTestRunner extends BlockJUnit4ClassRunner implements ApplicationListener{
    private static Application application;

    public BombermanTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        application = new HeadlessApplication(this,new HeadlessApplicationConfiguration());
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl= Gdx.gl20;
    }

    @AfterClass
    public static void tearDownRunner(){
        application.exit();
        application = null;
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
