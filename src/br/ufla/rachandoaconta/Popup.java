package br.ufla.rachandoaconta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Popup extends Activity {
	private TextView info2;
	private TextView info3;
	private TextView info4;
	private Bundle receber;
	private String[] qtdPessoas;
	private Cursor pog;
	private boolean abriu = false;
	private String[] pessoasDivididas;
	private DataBaseOperations dbo = new DataBaseOperations(Popup.this);
	private boolean ok = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_popup);
		this.setFinishOnTouchOutside(false);
		
		info2 = (TextView) findViewById(R.id.textView2);
		info3 = (TextView) findViewById(R.id.textView4);
		info4 = (TextView) findViewById(R.id.textView5);
		
		Intent it = getIntent();
		receber = it.getExtras();
		
		this.setTitle(receber.getString("nome"));
		pessoasDivididas = receber.getString("pessoas").split(",");
		
		info2.setText("Valor unitário: "+receber.getString("valor"));
		info3.setText(receber.getString("quantidade"));
		if(pessoasDivididas.length > 1){
			info4.setText(pessoasDivididas.length+" Pessoas consumiram");
			Toast.makeText(Popup.this, receber.getString("pessoas"), Toast.LENGTH_SHORT).show();
		}else{
			info4.setText(pessoasDivididas.length+" Pessoa consumiu");
			Toast.makeText(Popup.this, receber.getString("pessoas"), Toast.LENGTH_SHORT).show();
		}
	}

	/*Método responsável pela ação dos botões da tela.
	  Entrada: View v (necessário para identificar o botão clicado)*/
	public void acao(View v){
		switch (v.getId()) {
		/*Botão "mais"
		  Atualiza a quantidade de itens na tabela de itens e chama "atualizarValorPessoa".*/
		case R.id.imageButton1:
			for(String algo : pessoasDivididas){
	        	Cursor pessoainviavel = dbo.recuperarIndividuo(dbo, algo);
	        	pessoainviavel.moveToFirst();
	        	if (pessoainviavel.getInt(2) == 1){
	        		ok = false;
	        	}
			}
			if (!ok){
		       	Toast.makeText(Popup.this, "Não é possivel marcar alguém que já tenha pago sua parte da conta.", Toast.LENGTH_SHORT).show();
		    }
		    else{
				int mais = Integer.parseInt(info3.getText().toString());
				mais++;
				info3.setText(String.valueOf(mais));
				dbo.atualizarPedido(dbo, receber.getString("nome"), receber.getString("pessoas"), mais);
				atualizarValorPessoa(true);
		    }
			break;
		
		/*Botão "menos"
		  Responsável por decrementar a quantidade do item e por recalcular o valor consumido por
		  cada pessoa que encontra-se listada.*/
		case R.id.imageButton2:
			for(String algo : pessoasDivididas){
	        	Cursor pessoainviavel = dbo.recuperarIndividuo(dbo, algo);
	        	pessoainviavel.moveToFirst();
	        	if (pessoainviavel.getInt(2) == 1){
	        		ok = false;
	        	}
			}
			if (!ok){
		       	Toast.makeText(Popup.this, "Não é possivel marcar alguém que já tenha pago sua parte da conta.", Toast.LENGTH_SHORT).show();
		    }
		    else{
				int menos = Integer.parseInt(info3.getText().toString());
				if(menos > 1){
					menos--;
					info3.setText(String.valueOf(menos));
					dbo.atualizarPedido(dbo, receber.getString("nome"), receber.getString("pessoas"), menos);
					atualizarValorPessoa(false);
				}else{
					Toast.makeText(Popup.this, "Não é possível zerar a quantidade", Toast.LENGTH_SHORT).show();
				}
		    }
			break;
		
		case R.id.buttonPopup:
			if(abriu){
				pog.close();
			}
			finish();
			break;
			
		/*Botão "Apagar item"
		  Responsável por excluir um item do banco de dado retirando o valor do mesmo de todos que
		  foram listados na hora de cadastrar.*/
		case R.id.buttonPopup2:
			new AlertDialog.Builder(Popup.this)
			.setTitle("Apagar item")
			.setMessage("Ao apagar o valor deste item será subtraído de todos que consumiram.")
			.setPositiveButton("Apagar", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					for(String algo : pessoasDivididas){
						float valorParcial = (Float.parseFloat(receber.getString("valor"))*Float.parseFloat(receber.getString("quantidade")))/pessoasDivididas.length;
						pog = dbo.recuperarIndividuo(dbo, algo);
		        		pog.moveToFirst();
		        		float valorTotal = pog.getFloat(1) - valorParcial;
		        		abriu = true;
		        		try {
		        			dbo.atualizarValorPessoa(dbo, algo, valorTotal);
			        		dbo.apagarPedido(dbo, receber.getString("nome"), receber.getString("pessoas"));
			        		Toast.makeText(Popup.this, "Excluido com sucesso.\nAperte OK", Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			})
			.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Faz nada, somente fecha o AlertDialog
				}
			})
			.setIcon(android.R.drawable.ic_dialog_alert)
			.show();
			
			break;
			
			
		}
	}
	/*Método responsável por recalcular o valor consumido por cada pessoa que encontra-se listada
	  levando em conta se o item foi incrementado ou decrementado.
	  Entrada: boolean flag (True se incrementado, false se decrementado)
	  Saída: Nenhuma*/
	private void atualizarValorPessoa(boolean flag){
		qtdPessoas = receber.getString("pessoas").split(",");
		float valorParcial = Float.parseFloat(receber.getString("valor")) / qtdPessoas.length;
		abriu = true;
		for (String algo : qtdPessoas){
			pog = dbo.recuperarIndividuo(dbo, algo);
    		pog.moveToFirst();
    		if(flag){
	    		float valorTotal = pog.getFloat(1) + valorParcial;
	    		dbo.atualizarValorPessoa(dbo, algo, valorTotal);
    		}else{
    			float valorTotal = pog.getFloat(1) - valorParcial;
	    		dbo.atualizarValorPessoa(dbo, algo, valorTotal);
    		}
		}
	}
}