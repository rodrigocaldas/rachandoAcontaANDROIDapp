package br.ufla.rachandoaconta;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DetalhePessoa extends Activity {
	private TextView nome;
	private TextView total;
	private Button pagar;
	private ListView lista;
	private DataBaseOperations dbo = new DataBaseOperations(DetalhePessoa.this);
	private Cursor crItem;
	private Cursor crPessoa;
	private String name;
	private ArrayList<String> listando = new ArrayList<String>();
	private float valorTotal = 0;
	private Bundle receber;
	private boolean percentagem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhe_pessoa);
		
		nome = (TextView) findViewById(R.id.txtDetalheNome);
		total = (TextView) findViewById(R.id.txtDetalheTotal);
		lista = (ListView) findViewById(R.id.listViewDetalhe);
		pagar = (Button) findViewById(R.id.buttonDetalhePago);
		
		Intent it = getIntent();
		receber = it.getExtras();
		
		name = receber.getString("nome");
		nome.setText(name);
		percentagem = receber.getBoolean("percent");
		crPessoa = dbo.recuperarIndividuo(dbo, receber.getString("nome"));
		crPessoa.moveToFirst();
		
		if(crPessoa.getInt(2) == 0){
			pagar.setText("Marcar como pago");
		}else{
			pagar.setText("Marcar não pago");
		}
		
		crItem = dbo.recuperarPedido(dbo);
		crItem.moveToFirst();
		while(crItem.isAfterLast() == false){
			String[] json = crItem.getString(3).split(",");
			for (String algo : json){
				if (algo.equals(name)){
					Float valor = (crItem.getFloat(1)* crItem.getFloat(2));
					Float valorDividido = valor/json.length;
					valorTotal += valorDividido;
					listando.add("("+crItem.getInt(2)+") "+crItem.getString(0)+" R$"+valor+"\n"+
							"Dividido para "+json.length+" pessoas = R$"+valorDividido);
				}
			}
			crItem.moveToNext();
		}
		if (percentagem){
			listando.add("10% do garçom = R$"+valorTotal*10/100);
			valorTotal += valorTotal*10/100;
		}
		total.setText(String.valueOf(valorTotal));
		preencheListaConsumidores(listando);
	}
	
	private void preencheListaConsumidores(ArrayList<String> parametro) {
    	String[] consumidores = parametro.toArray(new String[parametro.size()]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
											DetalhePessoa.this,
											android.R.layout.simple_list_item_1,
											consumidores);
		lista.setAdapter(adapter);
		calcularHeightListView(lista);		
	}
	
    /*Método responsável por calcular o tamanho de um ListView para não precisar da rolagem.
	  Entrada: ListView qualquer;
	  Saída: Nenhuma;*/
	public static void calcularHeightListView(ListView lv) {  
        int totalHeight = 0;  
  
        ListAdapter adapter = lv.getAdapter();  
        int lenght = adapter.getCount();  
  
        for (int i = 0; i < lenght; i++) {  
            View listItem = adapter.getView(i, null, lv);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
  
        ViewGroup.LayoutParams params = lv.getLayoutParams();  
        params.height = totalHeight  
                + (lv.getDividerHeight() * (adapter.getCount() - 1));  
        lv.setLayoutParams(params);  
        lv.requestLayout();  
    } 
	
	public void acaoDetalhe(View v){
		switch (v.getId()) {
		case R.id.buttonDetalhePago:
			if(crPessoa.getInt(2) == 0){
				try{
					dbo.situacaoPessoa(dbo, crPessoa.getString(0), 1);
					pagar.setText("Marcar não pago");
					Toast.makeText(DetalhePessoa.this, "Valor pago", Toast.LENGTH_SHORT).show();
					crPessoa = dbo.recuperarIndividuo(dbo, receber.getString("nome"));
					crPessoa.moveToFirst();
				}catch(Exception e){}
			}else{
				try{
					dbo.situacaoPessoa(dbo, crPessoa.getString(0), 0);
					pagar.setText("Marcar como pago");
					Toast.makeText(DetalhePessoa.this, "Valor não pago", Toast.LENGTH_SHORT).show();
					crPessoa = dbo.recuperarIndividuo(dbo, receber.getString("nome"));
					crPessoa.moveToFirst();
				}catch(Exception e){}
			}
			break;
			
		case R.id.buttonDetalheVoltar:
			crPessoa.close();
			crItem.close();
			Intent voltar = new Intent(DetalhePessoa.this, MainActivity.class);
			startActivity(voltar);
			finish();
			break;
		}
	}
	
	@Override
    public void onBackPressed() {
		super.onBackPressed();
		Intent voltar = new Intent(DetalhePessoa.this, MainActivity.class);
		startActivity(voltar);
		finish();
    }
}
