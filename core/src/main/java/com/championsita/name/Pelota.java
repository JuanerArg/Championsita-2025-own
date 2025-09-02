package com.championsita.name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Representa la pelota del juego y su comportamiento físico básico.
 */
public class Pelota {

    // === Constantes ===
    private static final float FRICCION = 0.95f;
    private static final float FUERZA_DISPARO = 2.5f;
    private static final float FUERZA_EMPUJE = 1f; // más chico que disparo

    // === Atributos ===
    private Texture sheet;
    private Animation<TextureRegion> animacion;

    private float x, y;
    private float width, height;
    private float stateTime;

    private float velocidadX = 0;
    private float velocidadY = 0;
    private boolean animar = false;

    private Rectangle hitbox;

    // === Constructor ===
    /**
     * @param xInicial posición inicial en X
     * @param yInicial posición inicial en Y
     * @param escala factor de escala aplicado a la textura
     */
    public Pelota(float xInicial, float yInicial, float escala) {
        sheet = new Texture("pelotaAnimada.png");

        int columnas = 6;
        int filas = 1;

        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / columnas, sheet.getHeight() / filas);
        TextureRegion[] frames = new TextureRegion[columnas * filas];

        int index = 0;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        animacion = new Animation<>(0.08f, frames);
        stateTime = 0f;

        width = frames[0].getRegionWidth() * escala;
        height = frames[0].getRegionHeight() * escala;

        this.x = xInicial;
        this.y = yInicial;

        hitbox = new Rectangle(x, y, width, height);
    }

    // === Métodos públicos ===
    /**
     * Actualiza la posición de la pelota, aplicando fricción y animación.
     *
     * @param delta tiempo transcurrido
     * @param alguienEmpuja indica si un personaje la está empujando
     */
    public void actualizar(float delta, boolean alguienEmpuja) {
        if (Math.abs(velocidadX) > 0.01f || Math.abs(velocidadY) > 0.01f || alguienEmpuja) {
            animar = true;
            stateTime += delta;
        } else {
            animar = false;
        }

        x += velocidadX * delta;
        y += velocidadY * delta;

        velocidadX *= FRICCION;
        velocidadY *= FRICCION;

        if (Math.abs(velocidadX) < 0.01f) velocidadX = 0;
        if (Math.abs(velocidadY) < 0.01f) velocidadY = 0;

        hitbox.setPosition(x, y);
    }

    /** Actualiza sin considerar empujes externos. */
    public void actualizar(float delta) {
        actualizar(delta, false); // llama al nuevo con un valor por defecto
    }



    /** Aplica un disparo fuerte en la dirección indicada. */
    public void disparar(float dx, float dy) {
        velocidadX = dx * getFuerzaDisparo();
        velocidadY = dy * getFuerzaDisparo();
        animar = true;
        stateTime = 0f; // reiniciar animación
    }

    /** Aplica un empuje suave en la dirección indicada. */
    public void empujar(float dx, float dy) {
        velocidadX = dx * getFuerzaEmpuje();
        velocidadY = dy * getFuerzaEmpuje();
        animar = true;
        stateTime = 0f; // reiniciar animación
    }

    /** Detiene la animación de movimiento. */
    public void detener() {
        animar = false;
    }

    /** Dibuja la pelota en pantalla. */
    public void render(SpriteBatch batch) {
        TextureRegion frameActual = animacion.getKeyFrame(stateTime, true);
        batch.draw(frameActual, x, y, width, height);
    }

    /** Libera los recursos gráficos. */
    public void dispose() {
        sheet.dispose();
    }

    // === Getters y Setters ===
    /** @return área de colisión de la pelota */
    public Rectangle getHitbox() {
        return hitbox;
    }

    /** @return posición X actual */
    public float getX() { return x; }
    /** @return posición Y actual */
    public float getY() { return y; }

    /**
     * Ajusta la posición de la pelota.
     */
    public void setPosition(float nuevaX, float nuevaY) {
        this.x = nuevaX;
        this.y = nuevaY;
        hitbox.setPosition(nuevaX, nuevaY);
    }

        public static float getFuerzaEmpuje() {
                return FUERZA_EMPUJE;
        }

        public static float getFuerzaDisparo() {
                return FUERZA_DISPARO;
        }
}
