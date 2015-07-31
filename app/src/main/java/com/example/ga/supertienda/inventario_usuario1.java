package com.example.ga.supertienda;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class inventario_usuario1 extends Fragment {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> inventarioList;
    ArrayList<String> vacios;


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

    String username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_inventario_usuario1, container, false);

        username = this.getArguments().getString("username");
        // Hashmap para el ListView
        inventarioList = new ArrayList<HashMap<String, String>>();
        vacios = new ArrayList<String>();

        // Cargar los productos en el Background Thread
        new LoadAllProducts().execute();
        lista = (ListView) mLinearLayout.findViewById(R.id.listAllProducts);


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
            pDialog.setMessage("Cargando base de datos. Por favor espere...");
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
            try {
                List params = new ArrayList();
                params.add(new BasicNameValuePair("username", username));

                Log.d("request!", "starting");
                // getting JSON string from URL
                JSONObject json = jParser.makeHttpRequest(url_inventario_usuario, "POST", params);
                //JSONObject json = jParser.makeHttpRequest(url_inventario_usuario, "GET", params);

                // Check your log cat for JSON reponse
                Log.d(getString(R.string.cargandoproductos), json.toString());


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

                        if(Integer.parseInt(cantidad)<=10){
                            vacios.add(producto);

                        }

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
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(),
                            inventarioList,
                            R.layout.inventario_usuario2,
                            new String[]{
                                    TAG_CODIGO,
                                    TAG_PRODUCTO,
                                    TAG_PRECIO,
                                    TAG_CANTIDAD,
                            },
                            new int[]{
                                    R.id.inv_user_tv_codigo,
                                    R.id.inv_user_tv_producto,
                                    R.id.inv_user_tv_precio,
                                    R.id.inv_user_tv_cantidad,
                            });
                    // updating listview
                    //setListAdapter(adapter);
                    lista.setAdapter(adapter);

                    if(vacios.isEmpty()){

                    }else{
                        doNotification();
                        vacios.clear();
                    }
                }
            });
        }
    }

    public void doNotification(){
        StringBuilder s =new StringBuilder(20);

        s.setLength(0);

        for(int i=0;i<vacios.size();i++) {
            if(i==0) {
                s.append(vacios.get(i));
            }else {
                s.append(", " + vacios.get(i));
            }
        }

        vacios.clear();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());

        long[] vibrar = {500,500,300,700};
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder.setContentTitle(getString(R.string.pruductosterminacion))
                .setContentText(s)

                .setStyle(new NotificationCompat.BigTextStyle().bigText(s))
                .setTicker(getText(R.string.nollegadiez))
                .setAutoCancel(true)
                .setVibrate(vibrar)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(uri);

        Intent notIntent = new Intent(getActivity(), User_login.class);
        notIntent.putExtra("username", username);

        PendingIntent contIntent = PendingIntent.getActivity(getActivity(), 0, notIntent, 0);

        builder.setContentIntent(contIntent);

        NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(0, builder.build());

    }

}
