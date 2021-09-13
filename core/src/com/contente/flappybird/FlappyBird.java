package com.contente.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private Random randomNumber;
	private SpriteBatch batch;
	private Texture[] passaro;
	private Texture background;
	private Texture canoAlto;
	private Texture canoBaixo;
	private BitmapFont fontePontuacao;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoAlto;
	private Rectangle retanguloCanoBaixo;
	private ShapeRenderer shape;
	private Texture gameOver;
	private BitmapFont fonteRestart;

	private float variacao=0;
	private float velocidadeQueda=0;
	private int larguraDispositivo;
	private int alturaDispositivo;
	private float posicaoPassaroVertical;
	private float posicaoPassaroHorizontal;
	private float posicaoCanosHorizontal;
	private float posicaoCanoAltoVertical;
	private float posicaoCanoBaixoVertical;
	private int espacamentoCanosMax;
	private int espacamentoCanosRandomico;
	private int estadoJogo=0;// 0- Jogo não iniciado; 1- Jogo inciado; 2 - Game Over;
	private int pontuacao=0;
	private boolean marcouPonto=false;

	private float posicaoCirculoHorizontal;
	private float posicaoCirculoVertical;
	private float raioCirculo;



	@Override
	public void create () {
		Gdx.app.log("Create", "Inicializando o jogo");
		batch = new SpriteBatch();
		randomNumber = new Random();
		passaro = new Texture[3];
		passaro[0] = new Texture("passaro1.png");
		passaro[1] = new Texture("passaro2.png");
		passaro[2] = new Texture("passaro3.png");
		background = new Texture("fundo.png");
		canoAlto = new Texture("cano_topo_maior.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		fontePontuacao = new BitmapFont();
		fontePontuacao.setColor(Color.GRAY);
		fontePontuacao.getData().setScale(7);
		circuloPassaro = new Circle();
		retanguloCanoAlto = new Rectangle();
		retanguloCanoBaixo = new Rectangle();
		shape = new ShapeRenderer();
		gameOver = new Texture("game_over.png");
		fonteRestart = new BitmapFont();
		fonteRestart.setColor(Color.WHITE);
		fonteRestart.getData().setScale(3);

		larguraDispositivo = Gdx.graphics.getWidth();
		alturaDispositivo = Gdx.graphics.getHeight();
		posicaoPassaroVertical = (alturaDispositivo/2);
		posicaoPassaroHorizontal = 130;
		espacamentoCanosMax = alturaDispositivo/8;
		posicaoCanosHorizontal = larguraDispositivo + canoBaixo.getWidth();
		posicaoCanoAltoVertical = alturaDispositivo/2 + espacamentoCanosMax;
		posicaoCanoBaixoVertical = alturaDispositivo/2 - canoBaixo.getHeight() - espacamentoCanosMax;
	}

	@Override
	public void render () {
		float tempoVariacao = Gdx.graphics.getDeltaTime() * 10;
		Gdx.app.log("Render", "Variacao do Render: " + Gdx.graphics.getDeltaTime());

		variacao += tempoVariacao;
		if (variacao > 2) variacao = 0;

		if (estadoJogo == 0) {
			if (Gdx.input.justTouched()) {
				estadoJogo = 1;
			}
		} else {
			//derruba passaro
			velocidadeQueda++;
				if (posicaoPassaroVertical > 0 || velocidadeQueda < 0) {
					posicaoPassaroVertical = posicaoPassaroVertical - velocidadeQueda;
					/*if (velocidadeQueda >= posicaoInicialVertical) velocidadeQueda = 0;
				} else {
					posicaoInicialVertical = alturaDispositivo;
				*/
				} else if (posicaoPassaroVertical <= 0) {
					posicaoPassaroVertical = 0;
					velocidadeQueda = 0;
				}
			//Verifica inicio do jogo
			if (estadoJogo==1) {
				//Verifica pontuação
				posicaoCanosHorizontal -= tempoVariacao * 50;
				if (posicaoPassaroHorizontal > posicaoCanosHorizontal) {
					if (!marcouPonto) {
						pontuacao++;
						marcouPonto = true;
					}
				}
				//Verifica se o cano saiu inteiramente da tela
				if (posicaoCanosHorizontal < -canoBaixo.getWidth()) {
					marcouPonto = false;
					posicaoCanosHorizontal = larguraDispositivo;
					espacamentoCanosRandomico = randomNumber.nextInt(espacamentoCanosMax) - (espacamentoCanosMax / 2);
					posicaoCanoAltoVertical += espacamentoCanosRandomico;
					posicaoCanoBaixoVertical += espacamentoCanosRandomico;
					if (posicaoCanoAltoVertical < alturaDispositivo / 2 || posicaoCanoBaixoVertical > alturaDispositivo / 2 - canoBaixo.getHeight()) {
						posicaoCanoAltoVertical = alturaDispositivo / 2 + espacamentoCanosMax;
						posicaoCanoBaixoVertical = alturaDispositivo / 2 - canoBaixo.getHeight() - espacamentoCanosMax;
						posicaoCanosHorizontal -= tempoVariacao * 100;
					}
				}
				//levanta voo
				if (Gdx.input.justTouched()) {
					float alturaPulo = -13;
					velocidadeQueda = alturaPulo;
				}
			//Verifica GameOver (estadoJogo==2)
			} else {
				if (Gdx.input.justTouched()) {
					estadoJogo = 0;
					pontuacao = 0;
					velocidadeQueda = 0;
					posicaoPassaroVertical = (alturaDispositivo/2);
					posicaoCanosHorizontal = larguraDispositivo + canoBaixo.getWidth();
				}
			}
		}

		batch.begin();
		//ordem do draw nas linhas define a ordem de criação:
		batch.draw(background, 0, 0, larguraDispositivo, alturaDispositivo);
		batch.draw(canoAlto, posicaoCanosHorizontal, posicaoCanoAltoVertical);
		batch.draw(canoBaixo, posicaoCanosHorizontal, posicaoCanoBaixoVertical);
		batch.draw(passaro[(int) variacao], posicaoPassaroHorizontal, posicaoPassaroVertical);
		fontePontuacao.draw(batch, String.valueOf(pontuacao), larguraDispositivo/2, alturaDispositivo - (alturaDispositivo/8));

		if(estadoJogo==2){
			batch.draw(gameOver, (larguraDispositivo-gameOver.getWidth())/2, alturaDispositivo/2);
			fonteRestart.draw(batch, "Toque para reiniciar!", (larguraDispositivo-gameOver.getWidth())/2, alturaDispositivo/2-gameOver.getHeight());
		}

		batch.end();

		posicaoCirculoHorizontal = posicaoPassaroHorizontal+passaro[0].getWidth()/2;
		posicaoCirculoVertical = posicaoPassaroVertical+passaro[0].getHeight()/2;
		raioCirculo = passaro[0].getWidth()/2;
		circuloPassaro.set(posicaoCirculoHorizontal,posicaoCirculoVertical,raioCirculo);

		retanguloCanoAlto = new Rectangle(posicaoCanosHorizontal, posicaoCanoAltoVertical, canoAlto.getWidth(), canoAlto.getHeight());
		retanguloCanoBaixo = new Rectangle(posicaoCanosHorizontal, posicaoCanoBaixoVertical, canoBaixo.getWidth(), canoBaixo.getHeight());

		//Desenhar formas
		/*shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.circle(circuloPassaro.x,circuloPassaro.y,circuloPassaro.radius);
		shape.rect(retanguloCanoBaixo.x,retanguloCanoBaixo.y,retanguloCanoBaixo.width,retanguloCanoBaixo.height);
		shape.rect(retanguloCanoAlto.x, retanguloCanoAlto.y, retanguloCanoAlto.width, retanguloCanoAlto.height);
		shape.setColor(Color.DARK_GRAY);
		shape.end();*/

		//Teste colisão
		if(	Intersector.overlaps(circuloPassaro,retanguloCanoAlto)
				|| Intersector.overlaps(circuloPassaro,retanguloCanoBaixo)
				|| posicaoPassaroVertical <= 0
				|| posicaoPassaroVertical > alturaDispositivo) {
			Gdx.app.log("COLISAO", "Houve colisão");
			estadoJogo=2;
		}
	}
}
