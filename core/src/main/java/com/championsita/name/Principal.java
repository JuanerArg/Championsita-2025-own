package com.championsita.name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.championsita.name.personajes.Normal;

/**
 * Clase principal del juego.
 * <p>
 * Extiende {@link ApplicationAdapter} y gestiona el ciclo de vida de la
 * aplicación de LibGDX: creación de recursos, actualización y renderizado,
 * ajuste del {@code viewport} y liberación de memoria.
 */
public class Principal extends ApplicationAdapter {

    // === Atributos ===
    private SpriteBatch batch;

    private Texture canchaDeFutbol;
    private Texture texturaDelPersonaje;
    private Texture barraStamina;

    private Personaje personaje;
    private Pelota pelota;

    private FitViewport viewport;

    private ManejadorInput manejadorInput;

    // === Métodos del ciclo de vida ===

    /**
     * Inicializa todos los recursos del juego y registra el manejador de
     * entradas.
     */
    @Override
    public void create() {

        personaje = new Normal();
        batch = new SpriteBatch();

        canchaDeFutbol = new Texture("CampoDeJuego.png");
        texturaDelPersonaje = new Texture("Jugador.png");

        manejadorInput = new ManejadorInput(personaje);
        pelota = new Pelota(3, 3, 0.002f);

        viewport = new FitViewport(8, 5);

        Gdx.input.setInputProcessor(manejadorInput);
    }

    /**
     * Actualiza la lógica del juego y dibuja la escena en cada frame.
     */
    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        personaje.update(delta);
        manejadorInput.actualizar(delta);
        personaje.limitarMovimiento(viewport.getWorldWidth(), viewport.getWorldHeight());
        pelota.actualizar(delta);

        detectarColisionConPelota();

        dibujar();
    }

    /**
     * Se llama cuando se cambia el tamaño de la ventana; actualiza el
     * {@link FitViewport}.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Libera los recursos asignados al finalizar la aplicación.
     */
    @Override
    public void dispose() {
        batch.dispose();
        canchaDeFutbol.dispose();
        texturaDelPersonaje.dispose();
    }

    // === Métodos auxiliares ===

    /**
     * Dibuja todos los elementos en pantalla.
     */
    private void dibujar() {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(canchaDeFutbol, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        personaje.render(batch);
        pelota.render(batch);
        personaje.hud.dibujarBarraStamina(batch, personaje.getX(), personaje.getY());
        batch.end();
    }

    /**
     * Calcula y aplica la interacción entre el personaje y la pelota cuando se
     * produce una colisión.
     */
    private void detectarColisionConPelota() {
        if (personaje.getHitbox().overlaps(pelota.getHitbox())) {
            float dx = 0, dy = 0;

            switch (personaje.getDireccion()) {
                case DERECHA:         dx =  0.001f; break;
                case IZQUIERDA:       dx = -0.001f; break;
                case ARRIBA:          dy =  0.001f; break;
                case ABAJO:           dy = -0.001f; break;
                case ARRIBA_DERECHA:  dx =  0.001f; dy =  0.001f; break;
                case ARRIBA_IZQUIERDA:dx = -0.001f; dy =  0.001f; break;
                case ABAJO_DERECHA:   dx =  0.001f; dy = -0.001f; break;
                case ABAJO_IZQUIERDA: dx = -0.001f; dy = -0.001f; break;
            }

            if (personaje.getHitbox().overlaps(pelota.getHitbox())) {
                if (personaje.estaEspacioPresionado()) {
                    pelota.disparar(dx, dy);
                } else {
                    pelota.empujar(dx, dy);
                }
            }
        } else {
            pelota.detener();
        }
    }
}
