package br.ufla.rachandoaconta;

import android.provider.BaseColumns;

public class DataBase {

	public static final String DATABASENAME = "aplicativo2";
	
public DataBase(){}
	
	public static abstract class Pessoa implements BaseColumns{
		public static final String NOME = "nome";
		public static final String VALOR = "valor";
		public static final String SITUACAO = "situacao";
		public static final String TABLENAME = "pessoa";
	}
	
	public static abstract class Item implements BaseColumns{
		public static final String NOME = "nome";
		public static final String VALOR = "valor";
		public static final String JAYSON = "jayson";
		public static final String QUANTIDADE = "quantidade";
		public static final String TABLENAME = "item";
	}
	
	public static abstract class Game implements BaseColumns{
		public static final String NOME = "descobertas";
		public static final String QUANTIDADE = "quantidade";
		public static final String VALOR = "valor";
		public static final String DIVERSO1 = "diverso1";
		public static final String DIVERSO2 = "diverso2";
		public static final String DIVERSO3 = "diverso3";
		public static final String DIVERSO4 = "diverso4";
		public static final String TABLENAME = "gamefication";
	}
}
