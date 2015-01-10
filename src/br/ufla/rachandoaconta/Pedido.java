package br.ufla.rachandoaconta;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Pedido extends Activity {
	private EditText inputItem;
	private EditText inputPreco;
	private EditText inputQuantidade;
	private ListView inputLista;
	private ArrayList<String> listaAuxiliar = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private Cursor crPessoa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedido);
		inputItem = (EditText) findViewById(R.id.fieldItem);
		inputPreco = (EditText) findViewById(R.id.fieldPreco);
		inputQuantidade = (EditText) findViewById(R.id.fieldQuantidade);
		inputLista = (ListView) findViewById(R.id.listViewPessoas);
		
		recuperarTabelaPessoa();
	}	
	
	/*Metódo responsável pela ação de todos os botões da tela.
	  Entrada: View v(necessário para identificar o botão clicado)
	  Saída: Nenhuma*/
	public void acaoBotoes2(View v){
		switch (v.getId()) {
		/*Botão "Cancelar"
		  Fecha o cursor e chama a activity MainActivity, depois fecha a Activity Pedido*/
		case R.id.buttonCancelar:
			Intent i = new Intent(Pedido.this, MainActivity.class);
			startActivity(i);
			crPessoa.close();
			finish();
			break;
			
		/*Botão "Confirmar"
		  Pega todas as informações passada na tela e cadastra no Banco de dado*/
		case R.id.buttonConfirmar:
			//Verifica quais itens foram marcados e armazena no ArrayList"itensMarcados"
			SparseBooleanArray marcados = inputLista.getCheckedItemPositions();
			ArrayList<String> itensMarcados = new ArrayList<String>();
	        for(int indice = 0; indice < marcados.size(); indice++){
	        	// Item position in adapter
	            int position = marcados.keyAt(indice);
	            // Add if it is checked) == TRUE!
	            if (marcados.valueAt(indice))
	            	itensMarcados.add(adapter.getItem(position));
	        }
	        
	        String info1 = inputItem.getText().toString();
	        String info2 = inputPreco.getText().toString();
	        String info3 = inputQuantidade.getText().toString();
	        //Verifica se algum campo foi deixado em branco ou se ninguem foi selecionado
	        if(!info1.equals("") && !info2.equals("") && !info3.equals("") && (itensMarcados.size() != 0) ){
		        try {
		        	//Cadastra o item no banco de dados
		        	DataBaseOperations dbo= new DataBaseOperations(Pedido.this);
		        	dbo.cadastrarPedido(dbo, 
		        						info1, 
		        						info2,
		        						info3,
		        						itensMarcados);
		        	
		        	//Para cada pessoa marcada na ListView, calcula o preço dividido do item que é
		        	//Somado ao que a pessoa já consumiu e cadastra no banco de dados
		        	for (String item : itensMarcados){
		        		float valorParcial = (Float.parseFloat(info2)*Float.parseFloat(info3))/itensMarcados.size();
		        		Cursor pog = dbo.recuperarIndividuo(dbo, item);
		        		pog.moveToFirst();
		        		float valorTotal = pog.getFloat(1) + valorParcial;
		        		dbo.atualizarValorPessoa(dbo, item, valorTotal);
		        	}
		        	//Volta a Activity inicial, fechando o cursor.
		        	Intent conf = new Intent(Pedido.this, MainActivity.class);
					startActivity(conf);
					crPessoa.close();
					finish();
					break;
				} catch (Exception e) {
					Toast.makeText(Pedido.this,"Falha",Toast.LENGTH_LONG).show();
				}
	        }else{
	        	Toast.makeText(Pedido.this, "Verifique se algum campo ficou em branco", Toast.LENGTH_SHORT).show();
	        }
		}
	}
	
	/*Método responsável por recuperar a tabela pessoa do banco de dados, adicionando os dados
    ao ArrayList listaAuxiliar (Será usada para popular o ListView), chama o método preencheListaConsumidores ao final.
    Entrada: nenhuma;
    Saída: nenhuma;*/
	private void recuperarTabelaPessoa() {
    	DataBaseOperations dbo = new DataBaseOperations(Pedido.this);
		crPessoa = dbo.recuperarPessoa(dbo);
		crPessoa.moveToFirst();
		for(int index = 0; index < crPessoa.getCount(); index++){
			listaAuxiliar.add(crPessoa.getString(0));
			crPessoa.moveToNext();
		}
		preencheListaConsumidores(listaAuxiliar);
	}
	
	/*Método responsável por preencher o ListView inputLista.
	  Entrada: ArrayList listaAuxiliar;
	  Saída: Nenhuma;*/
	private void preencheListaConsumidores(ArrayList<String> parametro) {
    	String[] consumidores = parametro.toArray(new String[parametro.size()]);
		adapter = new ArrayAdapter<String>(
											Pedido.this,
											android.R.layout.simple_list_item_multiple_choice,
											consumidores);
		inputLista.setAdapter(adapter);
		inputLista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
}
