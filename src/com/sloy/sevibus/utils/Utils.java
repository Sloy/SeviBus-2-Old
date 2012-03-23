package com.sloy.sevibus.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Utils {

	public static String URL_XML = "http://www.infobustussam.com:9005/tussamGO/Resultados?op=ep&ls=%s&st=%d"; // 1.linea
	private static final String URL_SOAP = "http://www.infobustussam.com:9001/services/dinamica.asmx";
	private static final String BODY_SOAP = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetPasoParada xmlns=\"http://tempuri.org/\"><linea>%1s</linea><parada>%2s</parada><status>1</status></GetPasoParada></soap:Body></soap:Envelope>"; // 2.parada

	public static String suputamadre(URL url) throws IOException {
		HttpURLConnection c = (HttpURLConnection)url.openConnection();
		c.setRequestMethod("GET");
		c.setReadTimeout(15 * 1000);
		// c.setRequestProperty("Authorization", "Basic " + authStringEnc);
		c.setUseCaches(false);
		c.connect();
		// read the output from the server
		BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null){
			stringBuilder.append(line + "\n");
		}
		String fuck = stringBuilder.toString();
		return fuck;
	}

	public static Integer[][] getTiemposXml(String linea, int parada) {
		Integer[][] res = null;
		try{
			/* Define la fuente del XML */
			URL url = new URL(String.format(URL_XML, linea, parada));
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			InputSource is = new InputSource(br);
			// create the factory
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// create a parser
			SAXParser parser = factory.newSAXParser();
			// create the reader (scanner)
			XMLReader xmlreader = parser.getXMLReader();
			// instantiate our handler
			TiemposHandlerXml th = new TiemposHandlerXml(); // el Handler
			// assign our handler
			xmlreader.setContentHandler(th);
			// perform the synchronous parse
			xmlreader.parse(is);
			// should be done... let's display our results
			res = th.getTiempos();
		}catch(Exception e){
			Log.e("sevibus", "Error parseando la llegada de " + linea + ":" + parada, e);
		}
		return res;
	}

	/**
	 * Obtiene los tiempos de llegada de una línea y parada concretas
	 * 
	 * @param linea
	 *            Entidad de la base de datos representando la línea
	 * @param parada
	 *            Número de la parada
	 * @return
	 * @throws SocketTimeoutException
	 * @throws ServerErrorException si el inputstream falla y devuelve null
	 */
	public static Llegada getTiempos(Entity linea, Integer parada) throws SocketTimeoutException, ServerErrorException {
		Llegada res = new Llegada(linea.getId());
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try{
			SAXParser parser = factory.newSAXParser();
			TiemposHandler handler = new TiemposHandler();
			InputStream is = getInputStream(linea.getString("nombre"), parada.toString());
			parser.parse(is, handler);
			handler.configurarLlegada(res);
		}catch(IllegalArgumentException e){
			Log.e("sevibus", "Error con el InputStream", e);
			throw new ServerErrorException();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return res;
	}

	private static InputStream getInputStream(String linea, String parada) {
		InputStream res = null;
		try{
			URL url = new URL(URL_SOAP);
			HttpURLConnection c = (HttpURLConnection)url.openConnection();
			c.setRequestMethod("POST");
			c.setReadTimeout(15 * 1000);
			c.setDoOutput(true);
			// c.setFixedLengthStreamingMode(contentLength)
			c.setUseCaches(false);
			c.setRequestProperty("Content-Type", "text/xml");
			c.connect();

			OutputStreamWriter wr = new OutputStreamWriter(c.getOutputStream());
			String data = String.format(BODY_SOAP, linea, parada);
			wr.write(data);
			wr.flush();

			res = c.getInputStream();
		}catch(MalformedURLException e){
			Log.e("sevibus", "Error al obtener la fuente de los tiempos", e);
		}catch(IOException e){
			Log.e("sevibus", "Error al obtener la fuente de los tiempos", e);
		}
		return res;

	}

	public static void descargarRelaciones(DataFramework db) throws MalformedURLException, IOException, JSONException {
		String jsonAPelo = Utils.suputamadre(new URL("http://sevibus.appspot.com/getinfo?objeto=linea"));
		JSONArray array = new JSONArray(jsonAPelo); // array con todas las
													// líneas en formato json
		for(int i = 0; i < array.length(); i++){
			JSONObject linea = array.getJSONObject(i); // para cada línea
														// individual
			String nombre = linea.getString("nombre"); // saco el nombre
			Entity lineaEntity = db.getTopEntity("lineas", "nombre = '" + nombre + "'", null); // saco
																								// la
																								// entidad
																								// de
																								// la
																								// línea
			if(lineaEntity == null){// la línea no existe (imposible, porque las
									// estoy leyendo de la misma lista que
									// guardé
				Log.e("sevibus", "Error: no se encontró la línea " + nombre + ", pero se intentó crear una relación con ella");
				continue;
			}
			JSONArray paradas = linea.getJSONArray("paradas"); // cojo el array
																// de paradas en
																// esta línea
			List<Integer> paradasList = Lists.newArrayList();
			for(int j = 0; j < paradas.length(); j++){
				paradasList.add(paradas.getInt(j)); // Me creo una lista de
													// integers que me gustan
													// más
			}
			Collections.sort(paradasList); // me la ordena, que no quiero líos
			for(Integer parada : paradasList){ // para cada parada de esta línea
				Entity paradaEntity = db.getTopEntity("paradas", "numero = " + parada, null); // sacp
																								// la
																								// entidad
																								// de
																								// la
																								// parada
				if(paradaEntity == null){
					Log.e("sevibus", "Error: no se encontró la parada " + parada + ", pero se intentó crear una relación con ella");
					continue;
				}
				// busco si hay ya o no una relación con esta parada y esta
				// línea
				Entity relacion = db.getTopEntity("relaciones", "parada_id = " + paradaEntity.getId() + " AND linea_id = " + lineaEntity.getId(),
						null);
				if(relacion == null){
					// no existe, la creo
					relacion = new Entity("relaciones");
					relacion.setValue("linea_id", lineaEntity.getId());
					relacion.setValue("parada_id", paradaEntity.getId());
					relacion.save();
				}
			}
		}
	}

	public static void descargarParadas() throws MalformedURLException, IOException, JSONException {
		String jsonAPelo = Utils.suputamadre(new URL("http://sevibus.appspot.com/getinfo?objeto=parada"));
		JSONArray array = new JSONArray(jsonAPelo);
		for(int i = 0; i < array.length(); i++){
			JSONObject parada = array.getJSONObject(i);
			int numero = parada.getInt("numero");
			String nombre = parada.getString("nombre");
			String direccion = parada.optString("direccion", "");
			double latitud = parada.optDouble("latitud");
			double longitud = parada.optDouble("longitud");

			Entity e = new Entity("paradas");
			e.setValue("numero", numero);
			e.setValue("nombre", nombre);
			e.setValue("direccion", direccion);
			e.setValue("latitud", latitud);
			e.setValue("longitud", longitud);
			e.save();
		}
	}

	private void descargarLineas() throws MalformedURLException, IOException, JSONException {
		String jsonAPelo = Utils.suputamadre(new URL("http://sevibus.appspot.com/getinfo?objeto=linea"));
		JSONArray array = new JSONArray(jsonAPelo);
		for(int i = 0; i < array.length(); i++){
			JSONObject linea = array.getJSONObject(i);
			String nombre = linea.getString("nombre");
			String trayecto = linea.getString("descripcion");
			boolean circular = linea.optBoolean("circular", false);
			int color = linea.optInt("color", 0);

			Entity e = new Entity("lineas");
			e.setValue("nombre", nombre);
			e.setValue("trayecto", trayecto);
			e.setValue("circular", circular);
			e.setValue("color", color);
			e.save();
		}
	}

	private static String DB_PATH = "/data/data/com.sloy.sevibus/databases/";
	private static String DB_NAME = "sevibus.db";

	static public void createDataBase(Context context) throws IOException {

		boolean dbExist = checkDataBase();
		if(!dbExist){
			try{
				copyDataBase(context);
				Log.i("sevibus", "Regenerada la base de datos");
				Datos.getPrefs().edit().putInt(Datos.DB_VERSION, Datos.getAppVersion()).commit();
			}catch(IOException e){
				throw new Error("Error copying database: " + e.getMessage());
			}
		}

	}

	static public boolean checkDataBase() {

		SQLiteDatabase checkDB = null;
		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		}catch(SQLiteException e){
		}

		if(checkDB != null){
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	static public void copyDataBase(Context context) throws IOException {

		File dir = new File(DB_PATH);
		if(!dir.exists()){
			dir.mkdir();
		}

		InputStream myInput = context.getAssets().open("sevibus.db");

		String outFileName = DB_PATH + DB_NAME;

		OutputStream myOutput = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int length;
		while((length = myInput.read(buffer)) > 0){
			myOutput.write(buffer, 0, length);
		}
		/*
		 * while ((length = myInput2.read(buffer))>0){
		 * myOutput.write(buffer, 0, length);
		 * }
		 * while ((length = myInput3.read(buffer))>0){
		 * myOutput.write(buffer, 0, length);
		 * }
		 */

		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public static boolean isNetworkAvailable(Context ctx) {
		ConnectivityManager connectivity = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivity == null){
			return false;
		}else{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if(info != null){
				for(int i = 0; i < info.length; i++){
					if(info[i].getState() == NetworkInfo.State.CONNECTED){
						return true;
					}
				}
			}
		}
		return false;
	}

}
