package br.grupointegrado.flappybird;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Antonio on 05/10/2015.
 */
public class Util {

    public static final float ESCALA = 2;
    public static final float PIXEL_METRO = 32;
    public static final float ALTURA_CHAO = 80 / PIXEL_METRO; // Altura do Chão em Metros

    /**
     * Cria um corpo dento do Mundo
     *
     * @param mundo
     * @param tipo
     * @param x
     * @param y
     * @return
     */
    public static Body criarCorpo (World mundo,  BodyDef.BodyType tipo, float x, float y) {
        BodyDef definicao = new BodyDef();
        definicao.type = tipo;
        definicao.position.set(x, y);
        definicao.fixedRotation = true;
        Body corpo = mundo.createBody(definicao);
        return corpo;
    };

    /**
     * Cria uma forma para o Corpo
     *
     * @param corpo
     * @param shape Forma Geometrica do Corpo
     * @param nome Nome utilizado para identificar a Colisão
     * @return
     */
    public static Fixture criarForma(Body corpo, Shape shape, String nome) {
        FixtureDef definicao = new FixtureDef();
        definicao.density = 1; // Densidade do Corpo
        definicao.friction = 0.06f; // Fricção/Atrito entre um corpo e outro
        definicao.restitution = 0.3f; // Elasticidade do Corpo
        definicao.shape = shape;
        Fixture forma = corpo.createFixture(definicao);
        forma.setUserData(nome); // Identificação da Forma
        return forma;
    }
}
