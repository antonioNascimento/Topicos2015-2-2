/**
 * Created by Antonio on 04/10/2015.
 */

package br.grupointegrado.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

import static br.grupointegrado.flappybird.Util.PIXEL_METRO;

public class TelaGame extends TelaBase {
    private OrthographicCamera camera; // Camera do Jogo
    private World mundo; // Representa o Mundo do Box2D
    private Body chao; // Corpo do Chão
    private Passaro passaro;
    private Array<Obstaculo> obstaculos = new Array<Obstaculo>();

    private int pontuacao = 0;
    private BitmapFont fontePontuacao;
    private Stage palcoInformacoes;
    private Label lbPontuacao;
    private ImageButton btnPlay;
    private ImageButton btnGameOver;
    private OrthographicCamera cameraInfo;


    private Box2DDebugRenderer debug;//desenha o mundo na tela para ajudar no desenvolvimento

    public TelaGame(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / Util.ESCALA, Gdx.graphics.getHeight() / Util.ESCALA);
        cameraInfo = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        debug = new Box2DDebugRenderer();
        mundo = new World(new Vector2(0, -9.8f), false);
        mundo.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                detectarColisao(contact.getFixtureA(), contact.getFixtureB());
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        initChao();
        initPassaro();
        initFontes();
        initInformacoes();
    }

    private boolean gameOver = false;

    private void detectarColisao(Fixture fixtureA, Fixture fixtureB) {
        if ("PASSARO".equals(fixtureA.getUserData()) || "PASSARO".equals(fixtureB.getUserData())) {
            gameOver = true;
        }
    }

    private void initFontes() {
        FreeTypeFontGenerator.FreeTypeFontParameter fonteParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fonteParam.size = 56;
        fonteParam.color = Color.WHITE;
        fonteParam.shadowColor = Color.BLACK;
        fonteParam.shadowOffsetX = 4;
        fonteParam.shadowOffsetY = 4;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        fontePontuacao = generator.generateFont(fonteParam);

        generator.dispose();
    }

    private void initInformacoes() {
        palcoInformacoes = new Stage(new FillViewport(cameraInfo.viewportWidth, camera.viewportHeight, cameraInfo));
        Gdx.input.setInputProcessor(palcoInformacoes);

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fontePontuacao;

        lbPontuacao = new Label("0", estilo);
        palcoInformacoes.addActor(lbPontuacao);

    }

    private void initChao() {
        chao = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, 0, 0);
    }

    private void initPassaro() {
        passaro = new Passaro(mundo, camera, null);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1); // Limpa a Tela e pinta a cor de Fundo
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Mantem o buffer das cores

        capturarTeclas();

        atualizar(delta);
        renderizar(delta);

        debug.render(mundo, camera.combined.cpy().scl(PIXEL_METRO));
    }

    private boolean pulando = false;

    private void capturarTeclas() {
        pulando = false;
        if (Gdx.input.justTouched()) {
            pulando = true;
        }
    }

    /**
     * Atualiza o Cálculo dos Corpos
     *
     * @param delta
     */
    private void atualizar(float delta) {
        palcoInformacoes.act(delta);

        passaro.atualizar(delta);
        mundo.step(1f / 60f, 6, 2);

        atualizarInformacoes();
        atualizarObstaculos();
        atualizarCamera();
        atualizarChao();

        if (pulando) {
            passaro.pular();
        }
    }

    private void atualizarInformacoes() {
        lbPontuacao.setText("" + pontuacao);
        lbPontuacao.setPosition(cameraInfo.viewportWidth / 2 - lbPontuacao.getPrefWidth() / 2, cameraInfo.viewportHeight - lbPontuacao.getPrefHeight());


    }

    private void atualizarObstaculos() {
        //Enquanto a lista tiver menos de do que 4, crie obstaculos.
        while (obstaculos.size < 4) {
            Obstaculo ultimoObstaculo = null;
            if (obstaculos.size > 0)
                ultimoObstaculo = obstaculos.peek(); // peek() retorna o ultimo elemento ou um erro se a lista for vazia
            Obstaculo obstaculo = new Obstaculo(mundo, camera, ultimoObstaculo);
            obstaculos.add(obstaculo);
        }

        //Verifica se os obstaculos sairam da tela para removê-los
        for (Obstaculo obstaculo : obstaculos) {
            float inicioCamera = passaro.getCorpo().getPosition().x - (camera.viewportWidth / 2 / PIXEL_METRO) - obstaculo.getLargura();
            if (inicioCamera > obstaculo.getPosX()) {
                obstaculo.remover();
                obstaculos.removeValue(obstaculo, true);
            } else if (!obstaculo.isPassou() && obstaculo.getPosX() < passaro.getCorpo().getPosition().x) {
                obstaculo.setPassou(true);
                //Calcular Pontos
                pontuacao++;
                //Reproduzir Som
            }
        }
    }

    private void atualizarCamera() {
        camera.position.x = (passaro.getCorpo().getPosition().x + 34 / PIXEL_METRO) * PIXEL_METRO;
        camera.update();
    }

    /**
     * Atualiza a posição do Chão para acompanhar o passaro
     */
    private void atualizarChao() {
        Vector2 posicao = passaro.getCorpo().getPosition();
        chao.setTransform(posicao.x, 0, 0);
    }

    /**
     * Renderizar as Imagens
     *
     * @param delta
     */
    private void renderizar(float delta) {
        palcoInformacoes.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / Util.ESCALA, height / Util.ESCALA);
        camera.update();
        cameraInfo.setToOrtho(false, width, height);
        cameraInfo.update();

        redimensionaChao();
    }

    /**
     * Configura o Tamanho do Chão de acordo com o tamanho da tela
     */
    private void redimensionaChao() {
        chao.getFixtureList().clear();
        float largura = camera.viewportWidth / PIXEL_METRO;
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
        palcoInformacoes.dispose();
        fontePontuacao.dispose();
    }
}