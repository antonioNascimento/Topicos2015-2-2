package br.grupointegrado.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Antonio on 05/10/2015.
 */
public class Passaro {

    private final World mundo;
    private final OrthographicCamera camera;
    private final Texture[] texturas;
    private Body corpo;
    private Sprite sprite;

    public Passaro(World mundo, OrthographicCamera camera, Texture[] texturas) {

        this.mundo = mundo;
        this.camera = camera;
        this.texturas = texturas;
        this.sprite = new Sprite(texturas[0]);

        initCorpo();
    }

    private void initCorpo() {
        float x = (camera.viewportWidth / 2) / Util.PIXEL_METRO;
        float y = (camera.viewportHeight / 2) / Util.PIXEL_METRO;
        corpo = Util.criarCorpo(mundo, BodyDef.BodyType.DynamicBody, x, y);

        FixtureDef definicao = new FixtureDef();
        definicao.density = 1;
        definicao.friction = 0.4f;
        definicao.restitution = 0.3f;

        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("physics/bird.json"));
        loader.attachFixture(corpo, "bird", definicao, 1, "PASSARO");
    }

    /**
     * Aplica um força positiva na vertical (y) para simular o Pulo
     */
    public void pular() {
        corpo.setLinearVelocity(corpo.getLinearVelocity().x, 0);
        corpo.applyForceToCenter(0, 115, false);
    }

    /**
     * Atualiza o comportamento do Passaro
     *
     * @param delta
     * @param movimentar
     */
    public void atualizar(float delta, boolean movimentar) {
        if (movimentar) {
            atualizarVelocidade();
            atualizarRotacao();
        }
    }

    private void atualizarRotacao() {
        float velocidadeY  = corpo.getLinearVelocity().y;
        float rotacao = 0;

        if (velocidadeY < 0) {
            // Caindo
            rotacao = -15;
        } else if (velocidadeY > 0) {
            // Subindo
            rotacao = 10;
        } else {
            // Reto
            rotacao = 0;
        }
        rotacao = (float) Math.toRadians(rotacao);
        corpo.setTransform(corpo.getPosition(), rotacao);
    }

    private void atualizarVelocidade() {
        corpo.setLinearVelocity(2f, corpo.getLinearVelocity().y);
    }

    public Body getCorpo() {
        return corpo;
    }

    public void renderizar(SpriteBatch spriteBatch) {
        Vector2 posicao = corpo.getPosition();
        sprite.setPosition(posicao.x * Util.PIXEL_METRO, posicao.y * Util.PIXEL_METRO);
        sprite.setOrigin(0,0);
        sprite.setRotation((float) Math.toDegrees(corpo.getAngle()));
        sprite.draw(spriteBatch);

    }

}
