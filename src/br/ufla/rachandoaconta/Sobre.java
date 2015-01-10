package br.ufla.rachandoaconta;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Sobre extends Activity {
	private DataBaseOperations dbo = new DataBaseOperations(Sobre.this);
	private Cursor game;
	private Button um;
	private Button dois;
	private Button tres;
	private Button quatro;
	private TableLayout umL;
	private TableLayout doisL;
	private TableLayout tresL;
	private TableLayout quatroL;
	private ImageView gameQtdI;
	private TextView gameQtdT;
	private ImageView gameValorI;
	private TextView gameValorT;
	private TableRow mesmoItens;
	private TableRow metadeConta;
	private TableRow bolsoFurado;
	private TableRow partesIguais;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sobre);
		
		Log.d("DEBUG SOBRE", "Iniciou essa joça");
		um = (Button) findViewById(R.id.buttonSobreApp);
		dois = (Button) findViewById(R.id.buttonSobrePessoa);
		tres = (Button) findViewById(R.id.buttonSobreItem);
		quatro = (Button) findViewById(R.id.buttonSobreConta);
		umL = (TableLayout) findViewById(R.id.rowU);
		doisL = (TableLayout) findViewById(R.id.rowDois);
		tresL = (TableLayout) findViewById(R.id.rowTres);
		quatroL = (TableLayout) findViewById(R.id.rowQuatro);
		gameQtdI = (ImageView) findViewById(R.id.imageGameQtd);
		gameQtdT = (TextView) findViewById(R.id.txtGameQtd);
		gameValorI = (ImageView) findViewById(R.id.imageGameValor);
		gameValorT = (TextView) findViewById(R.id.txtGameValor);
		mesmoItens = (TableRow) findViewById(R.id.gameDiverso1);
		metadeConta = (TableRow) findViewById(R.id.gameDiverso2);
		bolsoFurado = (TableRow) findViewById(R.id.gameDiverso3);
		partesIguais = (TableRow) findViewById(R.id.gameDiverso4);
		
		game = dbo.recuperarGamefication(dbo, "recompensa");
		game.moveToFirst();
		gamefication();
	}
	
	public void botoesSobre(View v){
		switch (v.getId()) {
		case R.id.buttonSobreVoltar:
			Intent voltar = new Intent(Sobre.this, MainActivity.class);
			startActivity(voltar);
			finish();
				
			break;
			
		case R.id.buttonSobreApp:
			um.setTextColor(getResources().getColor(R.color.azulbebe));
			dois.setTextColor(getResources().getColor(R.color.preto));
			tres.setTextColor(getResources().getColor(R.color.preto));
			quatro.setTextColor(getResources().getColor(R.color.preto));
			
			umL.setVisibility(View.VISIBLE);
			doisL.setVisibility(View.GONE);
			tresL.setVisibility(View.GONE);
			quatroL.setVisibility(View.GONE);
			gamefication();
			
			
			break;
			
		case R.id.buttonSobrePessoa:
			um.setTextColor(getResources().getColor(R.color.preto));
			dois.setTextColor(getResources().getColor(R.color.azulbebe));
			tres.setTextColor(getResources().getColor(R.color.preto));
			quatro.setTextColor(getResources().getColor(R.color.preto));
			
			umL.setVisibility(View.GONE);
			doisL.setVisibility(View.VISIBLE);
			tresL.setVisibility(View.GONE);
			quatroL.setVisibility(View.GONE);
			gameQtdI.setVisibility(View.GONE);
			gameQtdT.setVisibility(View.GONE);
			gameValorI.setVisibility(View.GONE);
			gameValorT.setVisibility(View.GONE);
			mesmoItens.setVisibility(View.GONE);
			metadeConta.setVisibility(View.GONE);
			bolsoFurado.setVisibility(View.GONE);
			partesIguais.setVisibility(View.GONE);
			
			break;
		
		case R.id.buttonSobreItem:
			um.setTextColor(getResources().getColor(R.color.preto));
			dois.setTextColor(getResources().getColor(R.color.preto));
			tres.setTextColor(getResources().getColor(R.color.azulbebe));
			quatro.setTextColor(getResources().getColor(R.color.preto));
			
			umL.setVisibility(View.GONE);
			doisL.setVisibility(View.GONE);
			tresL.setVisibility(View.VISIBLE);
			quatroL.setVisibility(View.GONE);
			gameQtdI.setVisibility(View.GONE);
			gameQtdT.setVisibility(View.GONE);
			gameValorI.setVisibility(View.GONE);
			gameValorT.setVisibility(View.GONE);
			mesmoItens.setVisibility(View.GONE);
			metadeConta.setVisibility(View.GONE);
			bolsoFurado.setVisibility(View.GONE);
			partesIguais.setVisibility(View.GONE);
			
			break;
		
		case R.id.buttonSobreConta:
			um.setTextColor(getResources().getColor(R.color.preto));
			dois.setTextColor(getResources().getColor(R.color.preto));
			tres.setTextColor(getResources().getColor(R.color.preto));
			quatro.setTextColor(getResources().getColor(R.color.azulbebe));
			
			umL.setVisibility(View.GONE);
			doisL.setVisibility(View.GONE);
			tresL.setVisibility(View.GONE);
			quatroL.setVisibility(View.VISIBLE);
			gameQtdI.setVisibility(View.GONE);
			gameQtdT.setVisibility(View.GONE);
			gameValorI.setVisibility(View.GONE);
			gameValorT.setVisibility(View.GONE);
			mesmoItens.setVisibility(View.GONE);
			metadeConta.setVisibility(View.GONE);
			bolsoFurado.setVisibility(View.GONE);
			partesIguais.setVisibility(View.GONE);
			
			break;
		}
	}
	
	
	public void gamefication(){
		if(game.getInt(1) >= 1){
			gameQtdI.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
			gameQtdT.setText("Utilizou o App 1 vez. Ainda tem muito que aprender");
			gameQtdI.setVisibility(View.VISIBLE);
			gameQtdT.setVisibility(View.VISIBLE);
		}
		if(game.getInt(1) >= 10){
			gameQtdI.setImageDrawable(getResources().getDrawable(R.drawable.qtdbronze));
			gameQtdT.setText("Utilizou o App 10 vezes. Não gosta de ficar em casa??");
			gameQtdI.setVisibility(View.VISIBLE);
			gameQtdT.setVisibility(View.VISIBLE);
		}
		if(game.getInt(1) >= 25){
			gameQtdI.setImageDrawable(getResources().getDrawable(R.drawable.qtdprata));
			gameQtdT.setText("Utilizou o App 25 vezes. Vida Social movimentada!");
			gameQtdI.setVisibility(View.VISIBLE);
			gameQtdT.setVisibility(View.VISIBLE);
		}
		if(game.getInt(1) >= 50){
			gameQtdI.setImageDrawable(getResources().getDrawable(R.drawable.qtdprata));
			gameQtdT.setText("Utilizou o App 50 vezes. Baladeiro você hein?!");
			gameQtdI.setVisibility(View.VISIBLE);
			gameQtdT.setVisibility(View.VISIBLE);
		}
		gamefication2();
	}

	private void gamefication2() {
		if(game.getFloat(2) >= 1){
			gameValorI.setImageDrawable(getResources().getDrawable(R.drawable.valorazul));
			gameValorT.setText("Gastaram menos de R$50,00 em uma noite, deu pra conversar um pouco.");
			gameValorI.setVisibility(View.VISIBLE);
			gameValorT.setVisibility(View.VISIBLE);
		}
		if(game.getFloat(2) >= 50){
			gameValorI.setImageDrawable(getResources().getDrawable(R.drawable.valorbronze));
			gameValorT.setText("Gastaram mais de R$50,00 em uma noite, uma boa happy hour.");
			gameValorI.setVisibility(View.VISIBLE);
			gameValorT.setVisibility(View.VISIBLE);
		}
		if(game.getFloat(2) >= 100){
			gameValorI.setImageDrawable(getResources().getDrawable(R.drawable.valorprata));
			gameValorT.setText("Gastaram mais de R$100,00 em uma noite, alguém deve ter ido bêbado pra casa.");
			gameValorI.setVisibility(View.VISIBLE);
			gameValorT.setVisibility(View.VISIBLE);
		}
		if(game.getFloat(2) >= 500){
			gameValorI.setImageDrawable(getResources().getDrawable(R.drawable.valorouro));
			gameValorT.setText("Gastaram mais de R$500,00 em uma noite, pagando de VIP na noitada.");
			gameValorI.setVisibility(View.VISIBLE);
			gameValorT.setVisibility(View.VISIBLE);
		}
		gamefication3();
	}

	private void gamefication3() {
		if(game.getFloat(3) == 1){
			mesmoItens.setVisibility(View.VISIBLE);
		}
		if(game.getFloat(4) == 1){
			metadeConta.setVisibility(View.VISIBLE);
		}
		if(game.getFloat(5) == 1){
			bolsoFurado.setVisibility(View.VISIBLE);
		}
		if(game.getFloat(6) == 1){
			partesIguais.setVisibility(View.VISIBLE);
		}
	}
}
