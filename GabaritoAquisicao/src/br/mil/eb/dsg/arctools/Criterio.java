package br.mil.eb.dsg.arctools;

public class Criterio {

	private String nome;
	private int tipo;
	private double tamanho;
	private int index;
	
	public void setNome(String t){
		nome = t;
	}
	
	public void setTipo(int t){
		tipo = t;
	}
	
	public void setTamanho(double t){
		tamanho = t;
	}
	
	public void setIndex(int i){
		index = i;
	}
	
	public String getNome(){
		return nome;
	}
	
	public int getTipo(){
		return tipo;
	}
	
	public double getTamanho(){
		return tamanho;
	}
	
	public int getIndex(){
		return index;
	}
}
