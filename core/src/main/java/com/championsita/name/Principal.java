package com.championsita.name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.championsita.name.personajes.Normal;

/**
 * Clase principal del juego.
 * Maneja el ciclo de vida de la aplicación y coordina a los dos jugadores
 * junto con la pelota.
 */
public class Principal extends ApplicationAdapter {

    // === Atributos ===
    private SpriteBatch batch;

    private Texture canchaDeFutbol;

    private Personaje jugador1;
    private Personaje jugador2;
    private Pelota pelota;

    private ManejadorInput controlador1;
    private ManejadorInput controlador2;
    private InputMultiplexer multiplexer;

    private FitViewport viewport;

    // === Métodos del ciclo de vida ===
    @Override
    public void create() {
        batch = new SpriteBatch();

        canchaDeFutbol = new Texture("CampoDeJuego.png");

        jugador1 = new Normal();
        jugador2 = new Normal();
        jugador2.setPosition(6f, 1f);

        controlador1 = new ManejadorInput(jugador1, Keys.W, Keys.S, Keys.A, Keys.D, Keys.SHIFT_LEFT, Keys.SPACE);
        controlador2 = new ManejadorInput(jugador2, Keys.I, Keys.K, Keys.J, Keys.L, Keys.SHIFT_RIGHT, Keys.O);
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(controlador1);
        multiplexer.addProcessor(controlador2);
        Gdx.input.setInputProcessor(multiplexer);

        pelota = new Pelota(3, 3, 0.002f);

        viewport = new FitViewport(8, 5);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        controlador1.actualizar(delta);
        controlador2.actualizar(delta);
        jugador1.update(delta);
        jugador1.limitarMovimiento(viewport.getWorldWidth(), viewport.getWorldHeight());
        jugador2.update(delta);
        jugador2.limitarMovimiento(viewport.getWorldWidth(), viewport.getWorldHeight());

        boolean alguienEmpuja = detectarColisionConPelota();
        pelota.actualizar(delta, alguienEmpuja);

        if (jugador1.getHitbox().overlaps(jugador2.getHitbox())) {
            resolverColision(jugador1, jugador2);
        }

        dibujar();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        canchaDeFutbol.dispose();
    }

    // === Métodos auxiliares ===
    private void dibujar() {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(canchaDeFutbol, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        jugador1.render(batch);
        jugador2.render(batch);
        pelota.render(batch);
        jugador1.hud.dibujarBarraStamina(batch, jugador1.getX(), jugador1.getY());
        jugador2.hud.dibujarBarraStamina(batch, jugador2.getX(), jugador2.getY());
        batch.end();
    }

    private boolean detectarColisionConPelota() {
        boolean alguienEmpuja = false;

        if (jugador1.getHitbox().overlaps(pelota.getHitbox())) {
            alguienEmpuja = true;
            aplicarFuerzaPelota(jugador1);
        }

        if (jugador2.getHitbox().overlaps(pelota.getHitbox())) {
            alguienEmpuja = true;
            aplicarFuerzaPelota(jugador2);
        }

        return alguienEmpuja;
    }

    private void aplicarFuerzaPelota(Personaje personaje) {
        float dx = 0, dy = 0;

        switch (personaje.getDireccion()) {
            case DERECHA:         dx = 1; break;
            case IZQUIERDA:       dx = -1; break;
            case ARRIBA:          dy = 1; break;
            case ABAJO:           dy = -1; break;
            case ARRIBA_DERECHA:  dx = 1; dy = 1; break;
            case ARRIBA_IZQUIERDA:dx = -1; dy = 1; break;
            case ABAJO_DERECHA:   dx = 1; dy = -1; break;
            case ABAJO_IZQUIERDA: dx = -1; dy = -1; break;
        }

        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len != 0) { dx /= len; dy /= len; }

        float fuerza = personaje.estaEspacioPresionado() ? Pelota.getFuerzaDisparo() : Pelota.getFuerzaEmpuje();
        dx *= fuerza;
        dy *= fuerza;

        if (personaje.estaEspacioPresionado()) {
            pelota.disparar(dx, dy);
        } else {
            pelota.empujar(dx, dy);
        }
    }

    private void resolverColision(Personaje a, Personaje b) {
        float aCenterX = a.getX() + a.getHitbox().width / 2;
        float aCenterY = a.getY() + a.getHitbox().height / 2;
        float bCenterX = b.getX() + b.getHitbox().width / 2;
        float bCenterY = b.getY() + b.getHitbox().height / 2;

        float dx = aCenterX - bCenterX;
        float dy = aCenterY - bCenterY;

        if (dx == 0 && dy == 0) dy = 0.01f;

        float len = (float) Math.sqrt(dx * dx + dy * dy);
        dx /= len;
        dy /= len;

        float overlap = 0.01f;
        a.setPosition(a.getX() + dx * overlap, a.getY() + dy * overlap);
        b.setPosition(b.getX() - dx * overlap, b.getY() - dy * overlap);
    }
}
