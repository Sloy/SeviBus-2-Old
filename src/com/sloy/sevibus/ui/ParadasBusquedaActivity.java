package com.sloy.sevibus.ui;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.IntentParada;
import com.sloy.sevibus.utils.ParadasAdapter;

import java.util.List;

public class ParadasBusquedaActivity extends SherlockActivity  {

	private Button mBtSearch;
	private EditText mTxtBusqueda;
	private ListView mList;
	private ParadasAdapter mAdapter;
	private TextView mEmpty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paradas);

		mEmpty = (TextView)findViewById(R.id.paradas_vacio);
		mList = (ListView)findViewById(android.R.id.list);
		mTxtBusqueda = (EditText)findViewById(R.id.paradas_busqueda);
		mBtSearch = (Button)findViewById(R.id.paradas_buscar);
		mBtSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				buscar();
			}
		});

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
					buscar();
				}
				return true;
			}
		});

		/*
		 * mTxtBusqueda.addTextChangedListener(new TextWatcher() {
		 * @Override
		 * public void onTextChanged(CharSequence s, int start, int before, int
		 * count) {
		 * Log.d("sevibus", "onTextChanged");
		 * buscar();
		 * }
		 * @Override
		 * public void beforeTextChanged(CharSequence s, int start, int count,
		 * int after) {
		 * Log.d("sevibus", "beforeTextChanged");
		 * }
		 * @Override
		 * public void afterTextChanged(Editable s) {
		 * Log.d("sevibus", "aTextChanged");
		 * }
		 * });
		 */
	}

	private void buscar() {
		// quita el teclado
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mTxtBusqueda.getWindowToken(), 0);

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

}
