package com.championsita.name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * HUD sencillo para dibujar la barra de stamina del personaje sobre el
 * escenario.
 */
public class HudPersonaje {

    private Personaje personaje;

    private Texture texturaBarra;

    private NinePatch barraStamina;

    private OrthographicCamera camaraHUD;


    float anchoBarra = 0.5f;
    float altoBarra = 0.5f;

   float porcentaje;
    float stamina;
    float staminaMax;



    /**
     * @param personaje personaje cuyas estadísticas se mostrarán
     */
    public HudPersonaje(Personaje personaje) {
        this.personaje = personaje;

        texturaBarra = new Texture("interiorBarraStamina.png");
        camaraHUD = new OrthographicCamera();
        camaraHUD.setToOrtho(false, 1280, 720);

    }

    /**
     * Dibuja la barra de stamina sobre la posición del personaje.
     */
    public void dibujarBarraStamina(SpriteBatch batch, float xPersonaje, float yPersonaje) {

        //camaraHUD.update();
        //batch.setProjectionMatrix(camaraHUD.combined);

        // Calcular porcentaje de stamina
        float porcentaje = personaje.getStamina() / personaje.getStaminaMax();

        // Centrar la barra horizontalmente sobre el jugador
        float posX = (personaje.getWidth() - anchoBarra) / 2f;


        float posY = personaje.getHeight() -0.3f; // ajustar según escala del personaje

        // Dibujar barra proporcional a la stamina
        batch.draw(texturaBarra, xPersonaje + posX, yPersonaje + posY, anchoBarra * porcentaje, altoBarra);


    }



}
