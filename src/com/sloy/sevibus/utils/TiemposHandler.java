package com.sloy.sevibus.utils;



import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketTimeoutException;

public class TiemposHandler {

	
	public static final int ST_OK = 1;
	public static final int ST_ERROR = 2;
	public static final int ST_TIMEOUT = 3;

	private static final String SOAP_ACTION="http://tempuri.org/GetPasoParada";
	private static final  String METHOD_NAME="GetPasoParada";
	private static final  String NAMESPACE="http://tempuri.org/";
	private static final  String URL="http://www.infobustussam.com:9001/services/dinamica.asmx";
	private static final int TIMEOUT = 15000;
	
	public int[] tiempos = new int[2];
	public int[] distancias = new int[2];
	public String nombre = "";
	private int status = 0;
	
	
	public boolean obtenerTiempos(String linea, String parada) throws SocketTimeoutException{
		/* Prepara la llamada SOAP */
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		request.addProperty("linea", linea);
		request.addProperty("parada", parada);
		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		soapEnvelope.dotNet=true;
		soapEnvelope.setOutputSoapObject(request);
		HttpTransportSE aht = new HttpTransportSE(URL,TIMEOUT);
		
		try{
			/* Realiza la llamada */
			aht.call(SOAP_ACTION, soapEnvelope);
			Object resultado = soapEnvelope.getResponse();
//			SoapObject resultado = (SoapObject)res;
			/* Convierte la respuesta a String */
			String respuesta = resultado.toString();
			//Log.d("bus", "respuesta: "+respuesta); //log
			/* Parsea los minutos de la respuesta */
			int i = 0;
			for(String item : respuesta.split("minutos=")){
				if(i>0){
					String[] t = item.split(";");
					tiempos[i-1] = Integer.valueOf(t[0]).intValue();
					//Log.d("bus", "tiempo"+i+" "+t[0]);
				}
				i++;
			}
			/* Parsea los metros de la respuesta */
			i = 0;
			for(String item : respuesta.split("metros=")){
				if(i>0){
					String[] m = item.split(";");
					distancias[i-1] = Integer.valueOf(m[0]).intValue();
				}
				i++;
			}
			/* Parsea el nombre de la parada */
			try{
				String[] a = respuesta.split("ruta=");
				String[] b = a[1].split(";");
				nombre = b[0];
			}catch(Exception e){
				Log.e("sevibus", e.toString(),e);
			}
			
			return true; 
		}catch(SocketTimeoutException  e){
			throw e;
		}catch(Exception e){
			Log.d("sevibus", "No se pudo obtener el tiempo de la línea "+linea);
			status = ST_ERROR;
			tiempos = new int[]{-1,-1};
			distancias = new int[]{-1,-1};
			return false;
		}
	}
	
	public int getStatus(){
		return status;
	}
}