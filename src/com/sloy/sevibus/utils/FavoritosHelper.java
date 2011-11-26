package com.sloy.sevibus.utils;

public class FavoritosHelper {

	private int paradaID;
	private int lineaID;
	private String descripcion;
	private int orden;
	private int usada;
	
	public FavoritosHelper(int parada){
		paradaID=parada;
	}

	public int getParadaID() {
		return paradaID;
	}

	public void setParadaID(int paradaID) {
		this.paradaID = paradaID;
	}

	public int getLineaID() {
		return lineaID;
	}

	public void setLineaID(int lineaID) {
		this.lineaID = lineaID;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public int getUsada() {
		return usada;
	}

	public void setUsada(int usada) {
		this.usada = usada;
	}
	
	
}
