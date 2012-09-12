package com.sloy.sevibus.utils;

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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.Twt;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.dataframework.Entity;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class Utils {

	public static String URL_XML = "http://www.infobustussam.com:9005/tussamGO/Resultados?op=ep&ls=%s&st=%d"; // 1.linea
	private static final String URL_SOAP_DINAMICA = "http://www.infobustussam.com:9001/services/dinamica.asmx";
	private static final String BODY_SOAP_TIEMPOS = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetPasoParada xmlns=\"http://tempuri.org/\"><linea>%1s</linea><parada>%2s</parada><status>1</status></GetPasoParada></soap:Body></soap:Envelope>"; // 2.parada
	private static final String BODY_SOAP_BUSES = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetVehiculos xmlns=\"http://tempuri.org/\"><linea>%1s</linea></GetVehiculos></soap:Body></soap:Envelope>";

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
			InputStream is = getTiemposInputStream(linea.getString("nombre"), parada.toString());
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
	
	public static List<BusLocation> getBuses(String linea) throws ServerErrorException{
		List<BusLocation> res = null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try{
			SAXParser parser = factory.newSAXParser();
			BusesHandler handler = new BusesHandler();
			InputStream is = getBusesInputStream(linea);
			parser.parse(is, handler);
			res = handler.getBuses();
		}catch(IllegalArgumentException e){
			Log.e("sevibus", "Error con el InputStream", e);
			throw new ServerErrorException();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		return res;
		
	}

	private static InputStream getTiemposInputStream(String linea, String parada) {
		InputStream res = null;
		try{
			URL url = new URL(URL_SOAP_DINAMICA);
			HttpURLConnection c = (HttpURLConnection)url.openConnection();
			c.setRequestMethod("POST");
			c.setReadTimeout(15 * 1000);
			c.setDoOutput(true);
			// c.setFixedLengthStreamingMode(contentLength)
			c.setUseCaches(false);
			c.setRequestProperty("Content-Type", "text/xml");
			c.connect();

			OutputStreamWriter wr = new OutputStreamWriter(c.getOutputStream());
			String data = String.format(BODY_SOAP_TIEMPOS, linea, parada);
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
	
	private static InputStream getBusesInputStream(String linea) {
		InputStream res = null;
		try{
			URL url = new URL(URL_SOAP_DINAMICA);
			HttpURLConnection c = (HttpURLConnection)url.openConnection();
			c.setRequestMethod("POST");
			c.setReadTimeout(15 * 1000);
			c.setDoOutput(true);
			// c.setFixedLengthStreamingMode(contentLength)
			c.setUseCaches(false);
			c.setRequestProperty("Content-Type", "text/xml");
			c.connect();

			OutputStreamWriter wr = new OutputStreamWriter(c.getOutputStream());
			String data = String.format(BODY_SOAP_BUSES, linea);
			wr.write(data);
			wr.flush();

			res = c.getInputStream();
		}catch(MalformedURLException e){
			Log.e("sevibus", "Error al obtener la fuente de los autobuses", e);
		}catch(IOException e){
			Log.e("sevibus", "Error al obtener la fuente de los autobuses", e);
		}
		return res;

	}
	
	private static Predicate<Twt> isReply = new Predicate<Twt>() {
		@Override
		public boolean apply(Twt status) {
			// Excluye replys y retweets
			return !status.getText().startsWith("@") && !status.getText().startsWith("RT ");
		}
	};
	
	
	
	public static ArrayList<Tweet> getTussamNews() throws TwitterException{
		Twitter tw = TwitterFactory.getSingleton();
		QueryResult res = tw.search(new Query("from:ayto_sevilla tussam"));
		List<Tweet> statuses =res.getTweets(); 
		return Lists.newArrayList(Collections2.filter(statuses, isReply));
	}
	
	public static ArrayList<Status> getSevibusNews() throws TwitterException{
		Twitter tw = TwitterFactory.getSingleton();
		List<Status> statuses = tw.getUserTimeline("SeviBus");
		return Lists.newArrayList(Collections2.filter(statuses, isReply));
	}

	private static String DB_PATH = "/data/data/com.sloy.sevibus/databases/";
	private static String DB_NAME = "sevibus.db";

	static public void createDataBase(Context context) throws IOException {

		boolean dbExist = checkDataBase();
		if(!dbExist){
			try{
				copyDataBase(context);
				Log.i("sevibus", "Regenerada la base de datos");
				Datos.getPrefs(context).edit().putInt(Datos.DB_VERSION, Datos.getAppVersion(context)).commit();
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

		return checkDB != null;
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
