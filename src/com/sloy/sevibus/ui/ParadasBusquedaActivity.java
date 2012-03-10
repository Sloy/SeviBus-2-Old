package com.sloy.sevibus.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.IntentParada;
import com.sloy.sevibus.utils.ParadasAdapter;

import java.util.List;

public class ParadasBusquedaActivity extends SherlockActivity {

	private Button mBtSearch;
	private EditText mTxtBusqueda;
	private ListView mList;
	private ParadasAdapter mAdapter;
	private TextView mEmpty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paradas);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);getSupportActionBar().setDisplayShowTitleEnabled(false);
		
		// Inflate the custom view
		View customNav = LayoutInflater.from(this).inflate(R.layout.paradas_search_actionbar, null);
		// Attach to the action bar
		getSupportActionBar().setCustomView(customNav);
		getSupportActionBar().setDisplayShowCustomEnabled(true);

		mEmpty = (TextView)findViewById(R.id.paradas_vacio);
		mList = (ListView)findViewById(android.R.id.list);
		mTxtBusqueda = (EditText)customNav.findViewById(R.id.paradas_busqueda);
//		mBtSearch = (Button)customNav.findViewById(R.id.paradas_buscar);
//		mBtSearch.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				buscar();
//			}
//		});

		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				startActivity(new IntentParada(ParadasBusquedaActivity.this, mAdapter.getItemId(arg2)));
			}
		});

		mTxtBusqueda.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					//botón buscar del teclado
					buscar(true);
				}
				return true;
			}
		});

		mTxtBusqueda.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				Log.d("sevibus", "onTextChanged");
				// Cuando cambia el texto del cuadro
				buscar(false);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//				Log.d("sevibus", "beforeTextChanged");
			}

			@Override
			public void afterTextChanged(Editable s) {
//				Log.d("sevibus", "aTextChanged");
			}
		});

	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.paradas_search, menu);
		return true;
	}

	private void buscar(boolean quitarTeclado) {
		if(quitarTeclado){
		// quita el teclado
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
 imm.hideSoftInputFromWindow(mTxtBusqueda.getWindowToken(), 0);
		}
		// extrae la consulta
		String query = limpiaConsulta(mTxtBusqueda.getText().toString());

		// hará una consulta u otra dependiendo de si se busca un número o una
		// cadena
		String where = null;
		try{
			Integer numero = Integer.parseInt(query);
			Log.d("sevibus", "Detectado número. Haciendo búsqueda por número de parada");
			where = "numero like '%" + numero + "%'";
		}catch(NumberFormatException e){
			Log.d("sevibus", "Detectado no número. Buscando en nombre y dirección");
			where = "nombre like '%" + query + "%' or direccion like '%" + query + "%'";
		}

		DataFramework db = null;
		try{
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			List<Entity> results = db.getEntityList("paradas", where, "numero", "50");
			if(results.size() > 0){
				mAdapter = new ParadasAdapter(this, results);
				mList.setAdapter(mAdapter);
				mList.setVisibility(View.VISIBLE);
				mEmpty.setVisibility(View.GONE);
			}else{
				mList.setVisibility(View.GONE);
				mEmpty.setVisibility(View.VISIBLE);
			}

		}catch(Exception e){
			Log.e("sevibus", "Error haciendo la búsqueda en la base de datos", e);
		}finally{
			db.close();
		}

	}

	private String limpiaConsulta(String string) {
		String res = string;
		if(res.startsWith(" ")){
			res = limpiaConsulta(string.substring(1, string.length()));
		}
		if(res.endsWith(" ")){
			res = limpiaConsulta(string.substring(0, string.length() - 1));
		}
		return res;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.menu_buscar:
				buscar(true);
				return true;
			case android.R.id.home:
				startActivity(new Intent(this, HomeActivity.class));
				return true;
			default:
				return false;
		}
	}

}
