package com.example.ga.supertienda;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ventas_usuario extends Fragment {

    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> inventarioList;


    // url to get all products list
    private static String url_ventas_usuario = "http://basejoseremota.16mb.com/MySQL3/ventas_usuario.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_VENTAS = "ventas";
    private static final String TAG_ID = "id";
    private static final String TAG_CODIGO = "codigo";
    private static final String TAG_PRODUCTO = "producto";
    private static final String TAG_GANANCIA = "ganancia";
    private static final String TAG_VENDIDOS = "vendidos";

    // products JSONArray
    JSONArray inventario = null;

    ListView lista;
    TextView gananciav;

    String username;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_ventas_usuario, container, false);

        username = this.getArguments().getString("username");
        // Hashmap para el ListView
        inventarioList = new ArrayList<HashMap<String, String>>();

        // Cargar los productos en el Background Thread
        new LoadAllProducts().execute();
        lista = (ListView) mLinearLayout.findViewById(R.id.listAllProducts);
        gananciav = (TextView) mLinearLayout.findViewById(R.id.tv_gananciav);


        return mLinearLayout;
    }




    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Antes de empezar el background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.cargando_bases));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * obteniendo todos los productos
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            //
            //String username = "u";
            int ganancia_total=0;
            try {
                List params = new ArrayList();
                params.add(new BasicNameValuePair("username", username));

                Log.d("request!", "starting");
                // getting JSON string from URL
                JSONObject json = jParser.makeHttpRequest(url_ventas_usuario, "POST", params);
                //JSONObject json = jParser.makeHttpRequest(url_inventario_usuario, "GET", params);

                // Check your log cat for JSON reponse
                Log.d(getString(R.string.cargandoproductos), json.toString());


                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    inventario = json.getJSONArray(TAG_VENTAS);

                    // looping through All Products
                    //Log.i("ramiro", "produtos.length" + products.length());
                    for (int i = 0; i < inventario.length(); i++) {
                        JSONObject c = inventario.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String codigo = c.getString(TAG_CODIGO);
                        String producto = c.getString(TAG_PRODUCTO);
                        String precio = c.getString(TAG_GANANCIA);
                        String cantidad = c.getString(TAG_VENDIDOS);

                        // creating new HashMap
                        HashMap map = new HashMap();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_CODIGO, codigo);
                        map.put(TAG_PRODUCTO, producto);
                        map.put(TAG_GANANCIA, precio);
                        map.put(TAG_VENDIDOS, cantidad);


                        inventarioList.add(map);
                    }
                    for(HashMap<String, String> hmap : inventarioList)
                    {

                        String ganancia1 = hmap.get(TAG_GANANCIA);

                        int pp=Integer.parseInt(ganancia1);
                        ganancia_total = pp + ganancia_total;
                        gananciav.setText("$"+Integer.toString(ganancia_total));

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
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(),
                            inventarioList,
                            R.layout.ventas_usuario,
                            new String[]{
                                    TAG_CODIGO,
                                    TAG_PRODUCTO,
                                    TAG_GANANCIA,
                                    TAG_VENDIDOS,
                            },
                            new int[]{
                                    R.id.vent_user_tv_codigo,
                                    R.id.vent_user_tv_producto,
                                    R.id.vent_user_tv_ganancia,
                                    R.id.vent_user_tv_vendidos,
                            });
                    // updating listview
                    //setListAdapter(adapter);
                    lista.setAdapter(adapter);
                }
            });
        }
    }
}
