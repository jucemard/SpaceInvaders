package br.com.grupointegrado.spaceinvaders;

import com.badlogic.gdx.Screen;

/**
 * Created by Jucemar on 03/08/2015.
 */
public abstract class TelaBase implements Screen {
    protected MainGame game;

    public TelaBase(MainGame game) {
        this.game = game;
    }

    @Override
    public void hide() {
        dispose();
    }
}
