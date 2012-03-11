package com.sloy.sevibus.utils;


public class Llegada {

	private long lineaID;
	private Bus bus1;
	private Bus bus2;
	
	public Llegada (long lineaID, Bus bus1, Bus bus2){
		this.lineaID = lineaID;
		this.bus1 = bus1;
		this.bus2 = bus2;
	}

	public Bus getBus1() {
		return bus1;
	}

	public Bus getBus2() {
		return bus2;
	}
	
	public long getLineaID(){
		return lineaID;
	}

	public static class Bus {
		private int tiempo;
		private int distancia;

		public Bus(int tiempo, int distancia) {
			this.tiempo = tiempo;
			this.distancia = distancia;
		}

		public int getTiempo() {
			return tiempo;
		}

		public int getDistancia() {
			return distancia;
		}

	}
}