package br.ufla.rachandoaconta;

import java.util.ArrayList;
import br.ufla.rachandoaconta.DataBase.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseOperations extends SQLiteOpenHelper {
	public static final int database_version = 1;
	public String CREATE_QUERY = "CREATE TABLE IF NOT EXISTS "+Pessoa.TABLENAME+"("+
			Pessoa.NOME+" Text, "+Pessoa.VALOR+" REAL, "+Pessoa.SITUACAO+" INTEGER);";
	public String CREATE_QUERY2 = "CREATE TABLE IF NOT EXISTS "+Item.TABLENAME+"("+
			Item.NOME+" Text, "+Item.VALOR+" REAL, "+Item.QUANTIDADE+" REAL, "+Item.JAYSON+" TEXT);";
	public String CREATE_QUERY3 = "CREATE TABLE IF NOT EXISTS "+Game.TABLENAME+"("+Game.NOME+" Text, "+
			Game.QUANTIDADE+" INTEGER, "+Game.VALOR+" REAL, "+Game.DIVERSO1+" INTEGER, "+
			Game.DIVERSO2+" INTEGER, "+Game.DIVERSO3+" INTEGER, "+Game.DIVERSO4+" INTEGER);";

	public DataBaseOperations(Context context) {
		super(context, DataBase.DATABASENAME, null, database_version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_QUERY);
		db.execSQL(CREATE_QUERY2);
		db.execSQL(CREATE_QUERY3);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	
	/*Cadastra uma pessoa na tabela*/
	public void cadastrarPessoa(DataBaseOperations dob, String nome){
		SQLiteDatabase SQ = dob.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(Pessoa.NOME, nome);
		cv.put(Pessoa.VALOR, 0);
		cv.put(Pessoa.SITUACAO, 0);
		SQ.insert(Pessoa.TABLENAME, null, cv);
	}
	
	/*Recupera a tabela pessoa*/
	public Cursor recuperarPessoa(DataBaseOperations dop){
		SQLiteDatabase SQ = dop.getReadableDatabase();
		String[] colunas = {Pessoa.NOME, Pessoa.VALOR, Pessoa.SITUACAO};
		Cursor CR = SQ.query(Pessoa.TABLENAME, colunas, null, null, null, null, null);
		return CR;
	}
	
	/*Recupera apenas uma pessoa da tabela de acordo com o parâmetro recebido*/
	public Cursor recuperarIndividuo(DataBaseOperations dop, String parametro){
		SQLiteDatabase SQ = dop.getReadableDatabase();
		String[] colunas = {Pessoa.NOME, Pessoa.VALOR, Pessoa.SITUACAO};
		String[] ref = {parametro};
		String selecao = Pessoa.NOME+" = ?";
		Cursor CR = SQ.query(Pessoa.TABLENAME, colunas, selecao, ref, null, null, null);
		return CR;
	}
	
	/*Atualiza se a pessoa já pagou a conta*/
	public void situacaoPessoa(DataBaseOperations dop, String referencia, int valor){
		SQLiteDatabase SQ = dop.getReadableDatabase();
		String[] ref = {referencia};
		String selecao = Pessoa.NOME+" = ?";
		ContentValues cv = new ContentValues();
		cv.put(Pessoa.SITUACAO, valor);
		SQ.update(Pessoa.TABLENAME, cv, selecao, ref);
	}
	
	/*Atualiza o valor gasto pela pessoa até o momento*/
	public void atualizarValorPessoa(DataBaseOperations dob, String referenciaNome, float valorAsalvar){
		String[] ref = {referenciaNome};
		SQLiteDatabase SQ = dob.getWritableDatabase();
		String selecao = Pessoa.NOME+" = ?";
		ContentValues cv = new ContentValues();
		cv.put(Pessoa.VALOR, valorAsalvar);
		SQ.update(Pessoa.TABLENAME, cv, selecao, ref);
	}
	
	/*Cadastra um pedido na tabela item*/
	public void cadastrarPedido(DataBaseOperations dob, String nome, String valor, String quantidade, ArrayList<String> lista){
		StringBuilder construindo = new StringBuilder();
		//Pega apenas o nome da pessoa em cada item da ArrayLista e junta tudo em uma
		//StringBuilder separando os nomes com vígula
		for(int i = 0; i < lista.size(); i++){
			String[] separando = new String[2];
			separando = lista.get(i).split("\n");
			construindo.append(separando[0]);
			if(i != lista.size()-1){
				construindo.append(",");
			}
		}
		String array = construindo.toString();
		SQLiteDatabase SQ = dob.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(Item.NOME, nome);
		cv.put(Item.VALOR, Double.parseDouble(valor));
		cv.put(Item.QUANTIDADE, Double.parseDouble(quantidade));
		cv.put(Item.JAYSON, array);
		SQ.insert(Item.TABLENAME, null, cv);
	}
	
	/*Recupera a tabela item*/
	public Cursor recuperarPedido(DataBaseOperations dop){
		SQLiteDatabase SQ = dop.getReadableDatabase();
		String[] colunas = {Item.NOME, Item.VALOR, Item.QUANTIDADE, Item.JAYSON};
		Cursor CR = SQ.query(Item.TABLENAME, colunas, null, null, null, null, null);
		return CR;
	}
	
	/*Altera a quantidade do item na tabela de acordo com a referencia recebida*/
	public void atualizarPedido(DataBaseOperations dop, String referencia1, String referencia2, int quantidade){
		SQLiteDatabase SQ = dop.getReadableDatabase();
		String[] ref = {referencia1, referencia2};
		String selecao = Item.NOME+" = ?"+" AND "+Item.JAYSON+ " = ?";
		ContentValues cv = new ContentValues();
		cv.put(Item.QUANTIDADE, quantidade);
		SQ.update(Item.TABLENAME, cv, selecao, ref);
	}
	
	/*Apaga um item independente da quantidade da tabela*/
	public void apagarPedido(DataBaseOperations dop, String referencia1, String referencia2){
		SQLiteDatabase SQ = dop.getWritableDatabase();
		String[] ref = {referencia1, referencia2};
		String selecao = Item.NOME+" = ?"+" AND "+Item.JAYSON+ " = ?";
		SQ.delete(Item.TABLENAME, selecao, ref);
	}
	
	/*Apaga as duas tabelas por completo*/
	public void apagarTabelas(DataBaseOperations dop){
		SQLiteDatabase SQ = dop.getWritableDatabase();
		SQ.delete(Pessoa.TABLENAME, null, null);
		SQ.delete(Item.TABLENAME, null, null);
	}
	
	public void cadastrargamefication(DataBaseOperations dop, String referencia){
		SQLiteDatabase SQ = dop.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(Game.NOME, referencia);
		cv.put(Game.QUANTIDADE, 0);
		cv.put(Game.VALOR, 0);
		cv.put(Game.DIVERSO1, 0);
		cv.put(Game.DIVERSO2, 0);
		cv.put(Game.DIVERSO3, 0);
		cv.put(Game.DIVERSO4, 0);
		SQ.insert(Game.TABLENAME, null, cv);
		Log.d("DEBUG DBO", "cadastrou: "+referencia);
	}
	
	public Cursor recuperarGamefication(DataBaseOperations dop, String referencia){
		SQLiteDatabase SQ = dop.getReadableDatabase();
		String[] ref = {referencia};
		String selecao = Game.NOME+" = ?";
		String[] colunas = {Game.NOME, Game.QUANTIDADE, Game.VALOR, Game.DIVERSO1, Game.DIVERSO2, Game.DIVERSO3, Game.DIVERSO4};
		Cursor CR = SQ.query(Game.TABLENAME, colunas, selecao, ref, null, null, null);
		return CR;
	}
	
	public void alterarGamefication(DataBaseOperations dop, String referencia1, float referencia2, int decisao){
		SQLiteDatabase SQ = dop.getWritableDatabase();
		String[] ref = {referencia1};
		String selecao = Game.NOME+" = ?";
		ContentValues cv = new ContentValues();
		switch (decisao) {
		case 1:
			cv.put(Game.QUANTIDADE, referencia2);
			SQ.update(Game.TABLENAME, cv, selecao, ref);
			Log.d("DEBUG DBO","Passe pelo case 1");
			break;
		case 2:
			cv.put(Game.VALOR, referencia2);
			SQ.update(Game.TABLENAME, cv, selecao, ref);
			Log.d("DEBUG DBO","Passe pelo case 2");
			break;
		case 3:
			cv.put(Game.DIVERSO1, referencia2);
			SQ.update(Game.TABLENAME, cv, selecao, ref);
			Log.d("DEBUG DBO","Passe pelo case 3");
			break;
		case 4:
			cv.put(Game.DIVERSO2, referencia2);
			SQ.update(Game.TABLENAME, cv, selecao, ref);
			Log.d("DEBUG DBO","Passe pelo case 4");
			break;
		case 5:
			cv.put(Game.DIVERSO3, referencia2);
			SQ.update(Game.TABLENAME, cv, selecao, ref);
			Log.d("DEBUG DBO","Passe pelo case 5");
			break;
		case 6:
			cv.put(Game.DIVERSO4, referencia2);
			SQ.update(Game.TABLENAME, cv, selecao, ref);
			Log.d("DEBUG DBO","Passe pelo case 6");
			break;
		}
	}
}
