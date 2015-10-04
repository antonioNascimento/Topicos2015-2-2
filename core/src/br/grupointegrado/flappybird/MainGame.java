package br.grupointegrado.flappybird;

import com.badlogic.gdx.Game;

public class MainGame extends Game {

    @Override
    public void create() {
        setScreen(new TelaGame(this));

    }
}