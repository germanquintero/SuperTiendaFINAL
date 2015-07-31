package com.example.ga.supertienda;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Inventario_usuario extends ActionBarActivity {


    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> inventarioList;


    // url to get all products list
    private static String url_inventario_usuario = "http://basejoseremota.16mb.com/MySQL3/inventario_usuario.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_INVENTARIO = "inventario";
    private static final String TAG_ID = "id";
    private static final String TAG_CODIGO = "codigo";
    private static final String TAG_PRODUCTO = "producto";
    private static final String TAG_PRECIO = "precio";
    private static final String TAG_CANTIDAD = "cantidad";

    // products JSONArray
    JSONArray inventario = null;

    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario_usuario);

        // Hashmap para el ListView
        inventarioList = new ArrayList<HashMap<String, String>>();

        // Cargar los productos en el Background Thread
        new LoadAllProducts().execute();
        lista = (ListView) findViewById(R.id.listAllProducts);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Antes de empezar el background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Inventario_usuario.this);
            pDialog.setMessage("Cargando comercios. Por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * obteniendo todos los productos
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            String username = getIntent().getStringExtra("username");
            //String username = "1";
            try {
                List params = new ArrayList();
                params.add(new BasicNameValuePair("username", username));

                Log.d("request!", "starting");
                // getting JSON string from URL
                JSONObject json = jParser.makeHttpRequest(url_inventario_usuario, "POST", params);
                //JSONObject json = jParser.makeHttpRequest(url_inventario_usuario, "GET", params);

                // Check your log cat for JSON reponse
                Log.d("Cargando productos: ", json.toString());


                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    inventario = json.getJSONArray(TAG_INVENTARIO);

                    // looping through All Products
                    //Log.i("ramiro", "produtos.length" + products.length());
                    for (int i = 0; i < inventario.length(); i++) {
                        JSONObject c = inventario.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String codigo = c.getString(TAG_CODIGO);
                        String producto = c.getString(TAG_PRODUCTO);
                        String precio = c.getString(TAG_PRECIO);
                        String cantidad = c.getString(TAG_CANTIDAD);

                        // creating new HashMap
                        HashMap map = new HashMap();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_CODIGO, codigo);
                        map.put(TAG_PRODUCTO, producto);
                        map.put(TAG_PRECIO, precio);
                        map.put(TAG_CANTIDAD, cantidad);


                        inventarioList.add(map);
                    }
                    return json.getString(TAG_SUCCESS);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // Toast.makeText(Inventario_usuario.this, "No se pudo conectar", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            Inventario_usuario.this,
                            inventarioList,
                            R.layout.inventario_usuario2,
                            new String[] {
                                    TAG_ID,
                                    TAG_CODIGO,
                                    TAG_PRODUCTO,
                                    TAG_PRECIO,
                                    TAG_CANTIDAD,
                            },
                            new int[] {
                                    R.id.inv_user_tv_id,
                                    R.id.inv_user_tv_codigo,
                                    R.id.inv_user_tv_producto,
                                    R.id.inv_user_tv_precio,
                                    R.id.inv_user_tv_cantidad,
                            });
                    // updating listview
                    //setListAdapter(adapter);
                    lista.setAdapter(adapter);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventario_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Inventario_usuario.this, Login.class);
        finish();
        startActivity(i);

    }
}

