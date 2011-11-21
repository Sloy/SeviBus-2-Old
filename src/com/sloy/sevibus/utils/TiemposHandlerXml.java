/*
 * W. F. Ableson
 * fableson@msiservices.com
 */
package com.sloy.sevibus.utils;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TiemposHandlerXml extends DefaultHandler {

	private Integer[][] resultado = new Integer[2][2];
	private int index = 0;

	/**
	 * Devuelve un mapa con la información de tiempos de esta parada/línea
	 * 
	 * @return Lista de paradas
	 */
	public Integer[][] getTiempos() {
		return resultado;
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		try{
			/* Para las estimaciones */
			if(localName.equals("estimacion")){
				String tiempo = atts.getValue("t");
				String distancia = atts.getValue("m");
				resultado[index][0] = tiempo.equals("") ? -1 : new Integer(tiempo);
				resultado[index][1] = distancia.equals("") ? -1 : new Integer(distancia);
				index++;
			}
		}catch(Exception e){
			Log.e("sevibus", "Error parseando llegada", e);
		}
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
	}

	@Override
	public void characters(char ch[], int start, int length) {
	}

}