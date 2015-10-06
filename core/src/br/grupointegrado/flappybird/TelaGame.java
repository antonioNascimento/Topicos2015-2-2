/**
 * Created by Antonio on 04/10/2015.
 */

package br.grupointegrado.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class TelaGame extends TelaBase {
    private OrthographicCamera camera; // Camera do Jogo
    private World mundo; // Representa o Mundo do Box2D
    private Body chao; // Corpo do Chão
    private Passaro passaro;

    private Box2DDebugRenderer debug;//desenha o mundo na tela para ajudar no desenvolvimento

    public TelaGame(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / Util.ESCALA, Gdx.graphics.getHeight() / Util.ESCALA);
        debug = new Box2DDebugRenderer();
        mundo = new World(new Vector2(0, -9.8f), false);

        initChao();
        initPassaro();
    }

    private void initChao() {
        chao = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, 0, 0);
    }

    private void initPassaro() {
        passaro = new Passaro(mundo, camera, null);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1); // Limoa a Tela e pinta a cor de Fundo
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Mantem o buffer das cores

        atualizar(delta);
        renderizar(delta);

        debug.render(mundo, camera.combined.cpy().scl(Util.PIXEL_METRO));
    }

    /**
     * Atualiza o Cálculo dos Corpos
     *
     * @param delta
     */
    private void atualizar(float delta) {
        mundo.step(1f / 60f, 6, 2);
        atualizarChao();
    }

    /**
     * Atualiza a posição do Chão para acompanhar o passaro
     */
    private void atualizarChao() {
        float largura = camera.viewportWidth / Util.PIXEL_METRO;
        Vector2 posicao = chao.getPosition();
        posicao.x = largura / 2;
        chao.setTransform(posicao, 0);
    }

    /**
     * Renderizar as Imagens
     *
     * @param delta
     */
    private void renderizar(float delta) {
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / Util.ESCALA, height / Util.ESCALA);
        camera.update();
        redimensionaChao();
    }

    /**
     * Configura o Tamanho do Chão de acordo com o tamanho da tela
     */
    private void redimensionaChao() {
        chao.getFixtureList().clear();
        float largura = camera.viewportWidth / Util.PIXEL_METRO;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura / 2, Util.ALTURA_CHAO / 2);
        Fixture forma = Util.criarForma(chao, shape, "CHAO");
        shape.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        debug.dispose();
        mundo.dispose();
    }
}