package com.championsita.name;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Convierte las entradas del teclado en acciones sobre un {@link Personaje}.
 */
public class ManejadorInput implements InputProcessor {

    private final Personaje personaje;

    private final int arribaKey;
    private final int abajoKey;
    private final int izquierdaKey;
    private final int derechaKey;
    private final int sprintKey;
    private final int accionKey;

    private boolean arriba, abajo, izquierda, derecha;
    private boolean accionPresionada;
    private boolean sprintPresionado;

    /**
     * @param personaje personaje controlado
     * @param arriba tecla para mover hacia arriba
     * @param abajo tecla para mover hacia abajo
     * @param izquierda tecla para mover a la izquierda
     * @param derecha tecla para mover a la derecha
     * @param sprint tecla de sprint
     * @param accion tecla de disparo/empuje
     */
    public ManejadorInput(Personaje personaje, int arriba, int abajo, int izquierda,
                          int derecha, int sprint, int accion) {
        this.personaje = personaje;
        this.arribaKey = arriba;
        this.abajoKey = abajo;
        this.izquierdaKey = izquierda;
        this.derechaKey = derecha;
        this.sprintKey = sprint;
        this.accionKey = accion;
    }

    /** Registra las teclas presionadas. */
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == arribaKey) arriba = true;
        if (keycode == abajoKey) abajo = true;
        if (keycode == izquierdaKey) izquierda = true;
        if (keycode == derechaKey) derecha = true;
        if (keycode == sprintKey) sprintPresionado = true;
        if (keycode == accionKey) accionPresionada = true;
        return true;
    }

    /** Registra las teclas liberadas. */
    @Override
    public boolean keyUp(int keycode) {
        if (keycode == arribaKey) arriba = false;
        if (keycode == abajoKey) abajo = false;
        if (keycode == izquierdaKey) izquierda = false;
        if (keycode == derechaKey) derecha = false;
        if (keycode == sprintKey) sprintPresionado = false;
        if (keycode == accionKey) accionPresionada = false;
        return true;
    }

    // Llamá esto desde Principal.render()
    /**
     * Actualiza el estado del personaje en cada frame con los valores de
     * entrada actuales.
     */
    public void actualizar(float delta) {
        personaje.actualizarEstadojugador(arriba, abajo, izquierda, derecha, sprintPresionado, delta);
        personaje.setEspacioPresionado(accionPresionada); // para disparo
    }

    // Métodos no usados pero obligatorios
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
	@Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {return false;}

}
