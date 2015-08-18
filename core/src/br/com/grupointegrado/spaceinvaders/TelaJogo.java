package br.com.grupointegrado.spaceinvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Created by Jucemar on 03/08/2015.
 */
public class TelaJogo extends TelaBase {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage palco;
    private BitmapFont fonte;
    private Label lbPontucao;
	private Image jogador;
	private Texture texturaJogador;
	private Texture texturaJogadorDireita;
	private Texture texturaJogadorEsquerda;
	private boolean indoDireita;
	private boolean indoEsquerda;
    private boolean atirando;
    private Array<Image> tiros = new Array<Image>();
    private Texture texturaTiro;
    private Texture texturaMeteoro1;
    private Texture texturaMeteoro2;
    private Array<Image> meteoros1 = new Array<Image>();
    private Array<Image> meteoros2 = new Array<Image>();

    /**
     * Construtor padrão da tela de jogo
     * @param game Referência para a classe principal
     */
    public TelaJogo(MainGame game) {
        super(game);
    }

    /**
     * Método chamado quando a tela é exibida
     */
    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        initTexturas();
        initFonte();
        initInformacoes();
		initJogador();
    }

    private void initTexturas() {
        texturaTiro = new Texture("sprites/shot.png");
        texturaMeteoro1 = new Texture("sprites/enemie-1.png");
        texturaMeteoro2 = new Texture("sprites/enemie-2.png");

    }

    /**
     * Instancia os objetos do jogador e adiciona no palco
     */
    	 private void initJogador () {
		 texturaJogador = new Texture("sprites/player.png");
		 texturaJogadorDireita  = new Texture("sprites/player-right.png");
		 texturaJogadorEsquerda = new Texture("sprites/player-left.png");
		 
		 jogador = new Image(texturaJogador);
		 float x = camera.viewportWidth / 2 - jogador.getWidth() / 2;
		 float y = 15;
		 jogador.setPosition(x, y);
		 palco.addActor(jogador);
	 }

    /**
     * INSTANCIA AS INFORMAÇOES ESCRITAS NA TELA
     */
    private void initInformacoes() {
        Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;

        lbPontucao = new Label("0 pontos", lbEstilo);
        palco.addActor(lbPontucao);
    }

    /**
     * INSTANCIA OS OBJETOS DE FONTE
     */
    private void initFonte() {
        fonte = new BitmapFont();
    }

    /**
     * Método chamado a todo quadro de atualização do jogo (FPS - frame per second)
     * @param delta Tempo entre um quadro e outro (em segundos)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbPontucao.setPosition(10, camera.viewportHeight - 20);
		capturaTeclas();
		atualizarJogador(delta);
        atualizarTiros(delta);
        atualizarMeteoros(delta);
		// ATUALIZA A SITUAÇÃO DO PALCO
        palco.act(delta);
        // DESENHA O PALCO NA TELA
        palco.draw();
    }

    private void atualizarMeteoros(float delta) {
        int tipo = MathUtils.random(1, 3);
        if (tipo == 1){
            // cria meteoro 1
            Image meteoro = new Image(texturaMeteoro1);
            float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
            float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
            meteoro.setPosition(x, y);
            meteoros1.add(meteoro);
            palco.addActor(meteoro);

        } else {
            // cria meteoro 2
        }
        float velocidade = 200;
        for (Image meteoro : meteoros1) {
            float x = meteoro.getX();
            float y = meteoro.getY() - velocidade * delta;
            meteoro.setPosition(x, y);
        }
    }

    private final float MIN_INTERVALO_TIROS = 0.4f; // MINIMO DE TEMPO ENTRE OS TIROS
    private float intervaloTiros = 0; // TEMPO ACUMULADO ENTRE OS TIROS

    private void atualizarTiros(float delta) {
       //CRIA UM NOVO TIRO SE NECESSÁRIO
        if (atirando) {
            // VERIFICA SE O TEMPO MINIMO FOI ATINGIDO
            intervaloTiros = intervaloTiros + delta; // ACUMULA O TEMPO PERCORRIDO
            if (intervaloTiros >= MIN_INTERVALO_TIROS) {
                Image tiro = new Image(texturaTiro);
                float x = jogador.getX() + jogador.getWidth() / 2 - tiro.getWidth() / 2;
                float y = jogador.getY() + jogador.getHeight();
                tiro.setPosition(x, y);
                tiros.add(tiro);
                palco.addActor(tiro);
                intervaloTiros = 0;
            }
        }
        float velocidade = 200; // VELOCIDADE DE MOVIMENTAÇÃO DO TIRO
        // PERCORRE TODOS OS TIROS EXISTENTES
        for (Image tiro : tiros){
            //MOVIMENTA O TIRO EM DIREÇÃO AO TOPO
            float x = tiro.getX();
            float y = tiro.getY() + velocidade * delta;
            tiro.setPosition(x, y);
            //REMOVE OS TIROS QUE SAIRAM DA TELA
            if(tiro.getY() > camera.viewportHeight){
                tiros.removeValue(tiro, true); //REMOVE DA LISTA
                tiro.remove(); // REMOVE DO PALCO
            }
        }

    }

    /**
		* atualiza a posicao do jogador
		* @param delta
		*/
		private void atualizarJogador( float delta){
		float velocidade = 200; // velocidade de movimento do jogador
		if (indoDireita){
            // VERIFICA SE O JOGADOR ESTA DENTRO DA TELA
			if ( jogador.getX() < camera.viewportWidth - jogador.getWidth()){
				float x = jogador.getX() + velocidade * delta;
				float y = jogador.getY();
				jogador.setPosition(x, y);
			}
		}
		if (indoEsquerda){
            //VERIFICA SE O JOGADOR ESTA DENTRO DA TELA
			if ( jogador.getX() > 0){
			float x = jogador.getX() - velocidade * delta;
			float y = jogador.getY();
			jogador.setPosition(x, y);
			}
		}
		
		
		if (indoDireita){
			// Trocar imagen direita
			jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogadorDireita)));
		} else if(indoEsquerda) {
			// Trocar imagen esquerda
			jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogadorEsquerda)));
		} else {
			// Trocar imagen centro
			jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogador)));
		}
				
		
		
	}
	
	/**
	 * verifica se as teclas estão pressionadas
	 */
	private void capturaTeclas(){
		indoDireita = false;
		indoEsquerda = false;
        atirando = false;
		
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			indoEsquerda = true;
		} 
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			indoDireita = true;
		}
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            atirando = true;
        }
		
	}

    /**
     * Método chamado sempre que há uma alteração no tamanho da tela
     * @param width Novo valor de largura da tela
     * @param height Novo valor de altura da tela
     */
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    /**
     * Método chamado sempre que o jogo for minimizado
     */
    @Override
    public void pause() {

    }

    /**
     * Método chamado sempre que o jogo volta para o primeiro plano
     */
    @Override
    public void resume() {

    }

    /**
     * Método chamado quando a tela for destruída
     */
    @Override
    public void dispose() {
        batch.dispose();
        palco.dispose();
        fonte.dispose();
		texturaJogador.dispose();
		texturaJogadorDireita.dispose();
		texturaJogadorEsquerda.dispose();
        texturaTiro.dispose();
        texturaMeteoro1.dispose();
        texturaMeteoro2.dispose();
    }
}
