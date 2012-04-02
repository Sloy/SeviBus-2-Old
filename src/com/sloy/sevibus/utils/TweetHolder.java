package com.sloy.sevibus.utils;

import com.android.dataframework.Entity;

import twitter4j.Status;

import java.util.Date;

public class TweetHolder implements Comparable<TweetHolder> {
	private Long databaseId = null;
	private long id;
	private String texto;
	private Date fecha;
	private boolean nuevo;

	public TweetHolder(long id, String texto, Date fecha) {
		super();
		this.id = id;
		this.texto = limpiarTweet(texto);
		this.fecha = fecha;
	}

	public TweetHolder(long id, String texto, long fecha) {
		this(id, texto, new Date(fecha));
	}

	public TweetHolder(Entity e) {
		this(e.getLong("id"), e.getString("text"), e.getLong("date"));
		databaseId = e.getId();
	}
	
	public TweetHolder(Status s){
		this(s.getId(),s.getText(),s.getCreatedAt());
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public TweetHolder setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
		return this;
	}

	public Long getDatabaseId() {
		return databaseId;
	}

	public long getId() {
		return id;
	}

	public String getTexto() {
		return texto;
	}

	public Date getFecha() {
		return fecha;
	}

	@Override
	public int compareTo(TweetHolder another) {
		return this.getFecha().compareTo(another.getFecha());
	}
	
	private static String limpiarTweet(String texto){
		texto = texto.replaceAll("\n", "");
		texto = texto.replaceAll("#TUSSAM", "");
		texto = texto.replaceAll("#Sevilla", "");
		return texto;
	}
	

}
