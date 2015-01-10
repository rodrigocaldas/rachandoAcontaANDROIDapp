package br.ufla.rachandoaconta;

import java.util.ArrayList;
import br.ufla.rachandoaconta.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ListView listaConsumidor;
	private ListView listaItem;
	private TextView total;
	private ArrayList<String> listaPessoas = new ArrayList<String>();
	private ArrayList<String> listaAlternativa = new ArrayList<String>();
	private ArrayList<String> listaPedidos = new ArrayList<String>();
	private Cursor crItem;
	private Cursor crPessoa;
	private Cursor crGame;
	private boolean ja = false;
	private boolean mesmo = true;
	private boolean metade = false;
	private boolean bolsoFurado = false;
	private boolean partesIguais = true;
	private float valTotal;
	private DataBaseOperations dbo = new DataBaseOperations(MainActivity.this);
	
	@Override
	protected void onResume(){
		super.onResume();
		if(!ja){
			listaItem.setBackground(null);
			listaPessoas.clear();
			listaPedidos.clear();
			listaAlternativa.clear();
			recuperarTabelaItem();
			recuperarTabelaPessoa();
			if(listaPedidos.size() != 0){
	        	listaItem.setBackground(getResources().getDrawable(R.drawable.edtshape));
			}
		}
		ja = true;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listaConsumidor = (ListView) findViewById(R.id.listViewConsumidor);
        listaItem = (ListView) findViewById(R.id.listViewItem);
        total = (TextView) findViewById(R.id.textViewValor);

        recuperarTabelaPessoa();
        recuperarTabelaItem();
        ja = true;
        
        if(listaPessoas.size() != 0){
        	listaConsumidor.setBackground(getResources().getDrawable(R.drawable.edtshape));
        }
        if(listaPedidos.size() != 0){
        	listaItem.setBackground(getResources().getDrawable(R.drawable.edtshape));
        }
        
        
        listaConsumidor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				verDetalhePessoa(position, crPessoa);
			}

		});
        
        listaItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ja = false;
				editarQuantidadeItem(position, crItem);
			}
		});
        
		try {
			crGame = dbo.recuperarGamefication(dbo, "recompensa");
			crGame.moveToFirst();
			Log.d("DEBUG MAIN","Primeira tentativa de debugar: "+String.valueOf(crGame.getInt(1)));
		} catch (Exception e) {
			dbo.cadastrargamefication(dbo, "recompensa");
			Log.d("DEBUG MAIN","Voltou ao Main");
			crGame = dbo.recuperarGamefication(dbo, "recompensa");
			crGame.moveToFirst();
			Log.d("DEBUG MAIN","Quantidade: "+String.valueOf(crGame.getInt(1)));
			Log.d("DEBUG MAIN","Segunda tentativa de debugar: "+String.valueOf(crGame.getInt(1)));
		}
        
    }
    
    /*Método responsável por recuperar a tabela pessoa do banco de dados, adicionando os dados
      ao ArrayList listaPessoas (Será usada para popular o ListView depois de ser adicionado
      o valor que cada pessoas já consumiu) e ao ArrayList listaAlternativa (que será mantido
      sem alteração em relação ao Banco de dados para servir de parâmetro para evitar a adição
      de nomes repetidos), chama o método preencheListaConsumidores ao final.
      Entrada: nenhuma;
      Saída: nenhuma;*/
    private void recuperarTabelaPessoa() {
		crPessoa = dbo.recuperarPessoa(dbo);
		crPessoa.moveToFirst();
		valTotal = 0;
		for(int index = 0; index < crPessoa.getCount(); index++){
			float valPes = crPessoa.getFloat(1);
			if(crPessoa.getInt(2) == 0){
				listaPessoas.add(crPessoa.getString(0)+"\nR$ "+String.format("%.2f", valPes));
				valTotal += valPes;
			}else{
				listaPessoas.add(crPessoa.getString(0)+"\nR$ "+String.format("%.2f", valPes)+
						"    PAGO");
			}
			listaAlternativa.add(crPessoa.getString(0));
			crPessoa.moveToNext();
		}
		total.setText("R$ "+String.format("%.2f",valTotal));
		preencheListaConsumidores(listaPessoas);
	}

    /*Método responsável por recuperar a tabela item do banco de dados, adicionando os dados
    ao ArrayList listaPedidos (Será usada para popular o ListView utilizando a quantidade,
    nome do item, valor unitário e valor total do item), chama o método preencheListaItens ao final
    e calcula o preço final da conta.
    Entrada: nenhuma;
    Saída: nenhuma;*/
	private void recuperarTabelaItem() {
		crItem = dbo.recuperarPedido(dbo);
		crItem.moveToFirst();
		//float valTotal = 0;
		for(int index = 0; index < crItem.getCount(); index++){
			float quant = crItem.getFloat(2);
			float val = crItem.getFloat(1);
			listaPedidos.add("("+quant+") "+crItem.getString(0)+
					"    R$ "+String.format("%.2f", val)+"\nTOTAL: "+String.format("%.2f", quant*val));
			crItem.moveToNext();
			//valTotal += val*quant;
		}
		preencheListaItens(listaPedidos);
		//total.setText("R$ "+String.format("%.2f",valTotal));
	}
	
	/*Método responsável por chamar a Activity Popup para haver alteração na quantidade de itens
	  passando entre as Activities(item, valor, quantidade e pessoas em string única).
	  Entrada: Int position (posição clicada no ListView listaItem), Cursor crItem(contém todas as 
	  informações registradas no Banco de dados);
	  Saída: Nenhuma*/
	private void editarQuantidadeItem(int position, final Cursor cursorItem) {
		cursorItem.moveToPosition(position);
		Intent pop = new Intent(MainActivity.this, Popup.class);
		Bundle passar = new Bundle();
		passar.putString("nome", cursorItem.getString(0));
		passar.putString("valor", cursorItem.getString(1));
		passar.putString("quantidade", cursorItem.getString(2));
		passar.putString("pessoas", cursorItem.getString(3));
		pop.putExtras(passar);
		startActivity(pop);
	}
	
	/*Método responsável por chamar a Activity DetalhePessoa para mostrar o valor de tudo que a
	  pessoa consumiu.
	  Entrada: Int position (posição clicada no ListView listaItem), Cursor crPessoa(contém todas as
	  informações registradas no Banco de dados);
	  Saída: Nenhuma*/
	private void verDetalhePessoa(int position, Cursor cursorPessoa) {
		cursorPessoa.moveToPosition(position);
		Intent detalhe = new Intent(MainActivity.this, DetalhePessoa.class);
		Bundle passar = new Bundle();
		passar.putString("nome", cursorPessoa.getString(0));
		detalhe.putExtras(passar);
		startActivity(detalhe);
		finish();
	}

	/*Método responsável pela acção de todos os botões da tela.
	  Entrada: View v(necessário para a identificação do botão que foi clicado)
	  Saída: Nenhuma*/
	public void acaoBotoes(View v){
    	switch (v.getId()) {
    	/*Botão "Adicionar consumidor"
    	  Abre um AlertDialog com um EditText para cadastrar um consumidor no banco de dado,
    	  não é possível cadastrar nome repetido nem nome vazio.*/
		case R.id.buttonConsumidor:
			AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
			dialog.setTitle("Nova pessoa");
			dialog.setIcon(getResources().getDrawable(R.drawable.pessoa));
			dialog.setMessage("Digite o nome da pessoa.");
			final EditText input = new EditText(MainActivity.this);
			input.setBackground(getResources().getDrawable(drawable.edtshape));
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
			input.setLayoutParams(lp);
			dialog.setView(input);
			
			/*Resultado de quando o botão "Confirmar" do AlertDialog for clicado (cadastra o nome
			  no BD, adiciona o nome ao ListView lista pessoa adicionando "\nR$ 0,00") e ao 
			  ListView listaAlternativa sem alteração e chama o preencheListaConsumidores
			  com a ListView alterada).*/
			dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
		         public void onClick(DialogInterface dialog, int which) {
		             String entrada = input.getText().toString().toLowerCase();
		        	 if (entrada.equals("") || listaAlternativa.contains(entrada)) {
		        		 Toast.makeText(MainActivity.this, "Digite um nome", Toast.LENGTH_SHORT).show();
		             }else{
		            	 try {
		 		        	DataBaseOperations dbo= new DataBaseOperations(MainActivity.this);
		 		        	dbo.cadastrarPessoa(dbo, entrada);
		 				} catch (Exception e) {
		 					Toast.makeText(MainActivity.this,"Falha",Toast.LENGTH_LONG).show();
		 				}
		            	 listaPessoas.add(entrada+"\nR$ 0,00");
		            	 listaAlternativa.add(entrada);
		                 preencheListaConsumidores(listaPessoas);
		             }
		         }
		     });
			/*Resultado de quando o botão "Cancelar" do AlertDialog for clicado(fecha o AlertDialog)*/
			dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				         public void onClick(DialogInterface dialog, int which) {
				             dialog.cancel();
				         }
				     });
			dialog.show();
			break;
		
		/*Botão "Adicionar Item"
		  Chama a activity Pedido caso já tenha ao menos uma pessoa cadastrada, fecha os cursores
		  abertos.*/
		case R.id.buttonItem:
			if(listaPessoas.size() != 0){
				Intent i = new Intent(MainActivity.this, Pedido.class);
				startActivity(i);
				crItem.close();
				crPessoa.close();
				finish();
			}else{
				Toast.makeText(MainActivity.this, "É preciso ter uma pessoa cadastrada", Toast.LENGTH_SHORT).show();
			}
			break;
		
		/*Botão "Limpar conta"
		  Limpa todas as ArrayList e ListViews além de apagar todo o banco de dados e seta o 
		  total da conta para R$0,00*/
		case R.id.buttonLimpar:
			new AlertDialog.Builder(MainActivity.this)
			.setTitle("Tem certeza que deseja limpar a conta?")
			.setMessage("Todas as informações serão perdidas.")
			.setPositiveButton("Limpar", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//Responsável pela parte do Gamefication de quantidade de uso
					if(listaAlternativa.size() >= 2){
						int gameQtd = crGame.getInt(1); 
						gameQtd += 1;
						dbo.alterarGamefication(dbo, "recompensa", gameQtd, 1);
						crGame = dbo.recuperarGamefication(dbo, "recompensa");
						crGame.moveToFirst();
						
						
						try {
							//Responsável pela parte do Gamefication de valor da conta
							if(valTotal > crGame.getFloat(2)){
								dbo.alterarGamefication(dbo, "recompensa", valTotal, 2);
							}
							
							//Responsável pela parte do Gamefication de mesmo itens consumidos
							String antes = listaPedidos.get(0);
							for (String algo : listaPedidos){
								if (antes != algo){
									mesmo = false;
								}
							}
							if(mesmo){
								dbo.alterarGamefication(dbo, "recompensa", 1, 3);
							}
							
							//Responsável pela parte do Gamefication de pagar mais que a metade, nada, todos o mesmo valor.
							crPessoa.moveToFirst();
							Float anterior = crPessoa.getFloat(1);
							for(int index = 0; index < crPessoa.getCount(); index++){
								if( (valTotal/crPessoa.getFloat(1)) <= 2){
									metade = true;
								}
								if(crPessoa.getFloat(1) == 0){
									bolsoFurado = true;
								}
								if(anterior != crPessoa.getFloat(1)){
									partesIguais = false;
								}
								crPessoa.moveToNext();
							}
							if (metade){
								dbo.alterarGamefication(dbo, "recompensa", 1, 4);
							}
							if(bolsoFurado){
								dbo.alterarGamefication(dbo, "recompensa", 1, 5);
							}
							if(partesIguais){
								dbo.alterarGamefication(dbo, "recompensa", 1, 6);
							}
						} catch (Exception e) {
							
						}
					}				
					
					listaPessoas.clear();
					listaPedidos.clear();
					listaAlternativa.clear();
					preencheListaConsumidores(listaPessoas);
					preencheListaItens(listaPedidos);
					dbo.apagarTabelas(dbo);
					total.setText("R$0,00");
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
			
		case R.id.buttonSobreMain:
			Intent sobre = new Intent(MainActivity.this, Sobre.class);
			crItem.close();
			crPessoa.close();
			startActivity(sobre);
			finish();
			break;
		}
    }

    /*Método responsável por preencher o ListView listaItem. Chama o calcularHeightListView.
	  Entrada: ArrayList listaPedidos;
	  Saída: Nenhuma;*/
	private void preencheListaItens(ArrayList<String> parametro) {
    	String[] itens = parametro.toArray(new String[parametro.size()]);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
											MainActivity.this,
											android.R.layout.simple_list_item_1,
											itens);
		listaItem.setAdapter(adapter2);
		calcularHeightListView(listaItem);
    }
    
	/*Método responsável por preencher o ListView listaConsumidor. Chama o calcularHeightListView.
	  Entrada: ArrayList listaPessoas;
	  Saída: Nenhuma;*/
    private void preencheListaConsumidores(ArrayList<String> parametro) {
    	String[] consumidores = parametro.toArray(new String[parametro.size()]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
											MainActivity.this,
											android.R.layout.simple_list_item_1,
											consumidores);
		listaConsumidor.setAdapter(adapter);
		calcularHeightListView(listaConsumidor);		
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
}