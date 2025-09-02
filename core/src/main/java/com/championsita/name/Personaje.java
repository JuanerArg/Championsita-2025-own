package com.championsita.name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Clase base que representa a un personaje controlable dentro del juego.
 * Encapsula la lógica de movimiento, animaciones y manejo de stamina.
 */
public abstract class Personaje {

    /** HUD asociado al personaje para dibujar información en pantalla */
    HudPersonaje hud;




    // === Constantes ===

    private static final float LIMITE_STAMINA_BLOQUEO = 0.9f;
    private static final float TIEMPO_BLOQUEO_RECARGA = 2f;

    private final float escala;
    private final String nombre;

    private final float velocidadBase;      // Velocidad normal (pasada en constructor)
    private final float velocidadSprint;    // Velocidad al sprintar
    private float stamina;                  // Stamina actual
    private final float staminaMax; // Máximo de stamina
    private final float consumoSprint = 5f; // Stamina que se consume por segundo al sprintar
    private final float recargaStamina = 10f; // Stamina que se recupera por segundo cuando no sprinta

    private boolean sprintActivo = false;

    private boolean bloqueoRecarga = false;
    private float tiempoDesdeShiftSoltado = 0f;


    // === Texturas y Animaciones ===
    private Texture textureQuieto;
    private TextureRegion frameQuieto;

    private Animation<TextureRegion> animacionDerecha;
    private Animation<TextureRegion> animacionIzquierda;
    private Animation<TextureRegion> animacionArriba;
    private Animation<TextureRegion> animacionAbajo;
    private Animation<TextureRegion> animacionArribaDerecha;
    private Animation<TextureRegion> animacionArribaIzquierda;
    private Animation<TextureRegion> animacionAbajoDerecha;
    private Animation<TextureRegion> animacionAbajoIzquierda;

    // === Estado del personaje ===
    private float x, y;
    private float width, height;
    private float stateTime;
    private boolean estaMoviendo;
    private boolean espacioPresionado = false;
    private Direccion direccionActual = Direccion.ABAJO;

    private Rectangle hitbox;

    // === Constructor ===
    /**
     * Construye un nuevo personaje base.
     *
     * @param nombre nombre visible del personaje
     * @param escala factor de escala usado para las texturas
     * @param velocidadBase velocidad normal de movimiento
     * @param velocidadSprint velocidad al sprintar
     * @param staminaMax valor máximo de stamina
     */
    public Personaje(String nombre, float escala, float velocidadBase, float velocidadSprint, float staminaMax) {
        // Cargar texturas

        this.nombre = nombre;
        this.escala = escala;
        this.velocidadBase = velocidadBase;
        this.velocidadSprint = velocidadSprint;
        this.staminaMax = staminaMax;
        this.stamina = staminaMax;

        hud = new HudPersonaje(this);


        textureQuieto = new Texture("Jugador.png");
        frameQuieto = new TextureRegion(textureQuieto);

        Texture sheetDerecha = new Texture("jugadorCorriendoDerecha.png");
        Texture sheetIzquierda = new Texture("jugadorCorriendoIzquierda.png");
        Texture sheetArriba = new Texture("jugadorCorriendoArriba.png");
        Texture sheetAbajo = new Texture("jugadorCorriendoAbajo.png");
        Texture sheetArribaDerecha = new Texture("jugadorCorriendoArribaDerecha.png");
        Texture sheetArribaIzquierda = new Texture("jugadorCorriendoArribaIzquierda.png");
        Texture sheetAbajoDerecha = new Texture("jugadorCorriendoAbajoDerecha.png");
        Texture sheetAbajoIzquierda = new Texture("jugadorCorriendoAbajoIzquierda.png");

        // Crear animaciones
        animacionDerecha = crearAnimacion(sheetDerecha, 7, 1);
        animacionIzquierda = crearAnimacion(sheetIzquierda, 7, 1);
        animacionArriba = crearAnimacion(sheetArriba, 6, 1);
        animacionAbajo = crearAnimacion(sheetAbajo, 6, 1);
        animacionArribaDerecha = crearAnimacion(sheetArribaDerecha, 6, 1);
        animacionArribaIzquierda = crearAnimacion(sheetArribaIzquierda, 6, 1);
        animacionAbajoDerecha = crearAnimacion(sheetAbajoDerecha, 6, 1);
        animacionAbajoIzquierda = crearAnimacion(sheetAbajoIzquierda, 6, 1);

        // Calcular tamaño
        TextureRegion frame = animacionDerecha.getKeyFrame(0);
        width = frame.getRegionWidth() * escala;
        height = frame.getRegionHeight() * escala;

        // Posición inicial
        x = 1;
        y = 1;
        stateTime = 0f;

        hitbox = new Rectangle(x, y, width, height);
    }

    // === Métodos privados auxiliares ===

    /**
     * Genera una animación a partir de un {@link Texture} dividido en columnas
     * y filas.
     */
    private Animation<TextureRegion> crearAnimacion(Texture sheet, int columnas, int filas) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / columnas, sheet.getHeight() / filas);
        TextureRegion[] frames = new TextureRegion[columnas * filas];

        int index = 0;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        return new Animation<>(0.1f, frames);
    }

    /**
     * Determina la {@link Direccion} del movimiento según las teclas presionadas.
     */
    private Direccion calcularDireccion(boolean izquierda, boolean derecha, boolean arriba, boolean abajo) {
        if (izquierda && arriba) return Direccion.ARRIBA_IZQUIERDA;
        if (derecha && arriba) return Direccion.ARRIBA_DERECHA;
        if (izquierda && abajo) return Direccion.ABAJO_IZQUIERDA;
        if (derecha && abajo) return Direccion.ABAJO_DERECHA;
        if (izquierda) return Direccion.IZQUIERDA;
        if (derecha) return Direccion.DERECHA;
        if (arriba) return Direccion.ARRIBA;
        if (abajo) return Direccion.ABAJO;
        return direccionActual;
    }

    // === Métodos públicos ===

    /**
     * Actualiza el estado temporal de las animaciones.
     *
     * @param delta tiempo transcurrido desde el último frame
     */
    public void update(float delta) {
        if (estaMoviendo) {
            stateTime += delta;
        }
    }

    float move;

    /**
     * Calcula la velocidad de movimiento aplicando sprint y gestión de
     * stamina.
     */
    private void actualizarVelocidad(boolean sprint, float delta){

        float velocidadActual = velocidadBase;

        if (sprint && stamina > 0) {
            velocidadActual = velocidadSprint;

            stamina -= consumoSprint * delta;
            if (stamina < 0) stamina = 0;

            if(stamina < LIMITE_STAMINA_BLOQUEO){
                velocidadActual = velocidadBase; //Si se gasta , baja la velocidad
                bloqueoRecarga = true;
                tiempoDesdeShiftSoltado = 0f;
            }

        }
        else{
            if(bloqueoRecarga){

                if(!sprint){
                    tiempoDesdeShiftSoltado += delta;
                    if(tiempoDesdeShiftSoltado >= TIEMPO_BLOQUEO_RECARGA){
                        bloqueoRecarga = false;
                    }

                }
            }

            else {
                velocidadActual = velocidadBase;
                stamina += recargaStamina * delta;  //recarga
                if (stamina > staminaMax) stamina = staminaMax;
            }
        }

        move = velocidadActual * delta;
    }

    /**
     * Actualiza la dirección y posición del personaje según las teclas
     * presionadas.
     */
    private void actualizarDireccion(boolean izquierda, boolean derecha, boolean arriba, boolean abajo) {
        estaMoviendo = izquierda || derecha || arriba || abajo;

        if (estaMoviendo) {
            direccionActual = calcularDireccion(izquierda, derecha, arriba, abajo);

            float dx = 0, dy = 0;
            if (izquierda) dx -= 1;
            if (derecha) dx += 1;
            if (arriba) dy += 1;
            if (abajo) dy -= 1;

            float len = (float) Math.sqrt(dx * dx + dy * dy);
            if (len != 0) {
                dx /= len;
                dy /= len;
            }

            x += dx * move;
            y += dy * move;
            hitbox.setPosition(x, y);


        }
    }



    // Esto lo llama el ManejadorInput
    /**
     * Actualiza el estado del personaje a partir de la entrada del usuario.
     */
    public void actualizarEstadojugador(boolean arriba, boolean abajo, boolean izquierda, boolean derecha,boolean sprint, float delta) {

        actualizarVelocidad(sprint, delta);
        actualizarDireccion(izquierda, derecha, arriba, abajo);

    }

    /**
     * Dibuja el personaje en la posición actual usando la animación adecuada.
     */
    public void render(SpriteBatch batch) {
        TextureRegion frameActual;

        if (estaMoviendo) {
            switch (direccionActual) {
                case DERECHA:
                    frameActual = animacionDerecha.getKeyFrame(stateTime, true); break;
                case IZQUIERDA:
                    frameActual = animacionIzquierda.getKeyFrame(stateTime, true); break;
                case ARRIBA:
                    frameActual = animacionArriba.getKeyFrame(stateTime, true); break;
                case ABAJO:
                    frameActual = animacionAbajo.getKeyFrame(stateTime, true); break;
                case ARRIBA_DERECHA:
                    frameActual = animacionArribaDerecha.getKeyFrame(stateTime, true); break;
                case ARRIBA_IZQUIERDA:
                    frameActual = animacionArribaIzquierda.getKeyFrame(stateTime, true); break;
                case ABAJO_DERECHA:
                    frameActual = animacionAbajoDerecha.getKeyFrame(stateTime, true); break;
                case ABAJO_IZQUIERDA:
                    frameActual = animacionAbajoIzquierda.getKeyFrame(stateTime, true); break;
                default:
                    frameActual = frameQuieto;
            }
        } else {
            frameActual = frameQuieto;
        }


        batch.draw(frameActual, x, y, width, height);


    }

    /**
     * Restringe el movimiento del personaje para que permanezca dentro del
     * área visible.
     */
    public void limitarMovimiento(float worldWidth, float worldHeight) {
        x = MathUtils.clamp(x, 0, worldWidth - width);
        y = MathUtils.clamp(y, 0, worldHeight - height);
    }

    /** @return dirección actual de movimiento */
    public Direccion getDireccion() {
        return direccionActual;
    }

    /** @return rectángulo de colisión del personaje */
    public Rectangle getHitbox() {
        return hitbox;
    }

    /** Indica si la tecla espacio está presionada. */
    public void setEspacioPresionado(boolean valor) {
        this.espacioPresionado = valor;
    }

    /** @return true si la tecla espacio está presionada */
    public boolean estaEspacioPresionado() {
        return espacioPresionado;
    }

    /** @return stamina actual */
    public float getStamina(){
        return stamina;

    }

    /** @return stamina máxima */
    public float getStaminaMax(){
        return staminaMax;
    }

    /** Libera recursos asociados al personaje. */
    public void dispose() {
        textureQuieto.dispose();
    }

    /** @return ancho del personaje en el mundo */
    public float getWidth() {
        return width;
    }

    /** @return posición X en el mundo */
    public float getX() {
        return x;
    }
    /** @return posición Y en el mundo */
    public float getY() {
        return y;
    }

    /** @return altura del personaje en el mundo */
    public float getHeight() {
        return height;
    }
}
