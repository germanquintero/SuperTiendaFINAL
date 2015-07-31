package com.example.ga.supertienda;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Facturacion extends Fragment {
    // Progress Dialog
    private ProgressDialog pDialog;
    Bundle bundle = new Bundle();

    MediaPlayer reproductor;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> inventarioList;
    ArrayList<HashMap<String, String>> sellList;


    // url to get all products list
    private static String url_facturacion_enviar_datos = "http://basejoseremota.16mb.com/MySQL3/editar_cantidad.php";
    private static String url_facturacion_tomar_datos = "http://basejoseremota.16mb.com/MySQL3/facturacion.php";
    private static String url_editar_ventas ="http://basejoseremota.16mb.com/MySQL3/editar_ventas.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_INVENTARIO = "inventario";
    private static final String TAG_ID = "id";
    private static final String TAG_CODIGO = "codigo";
    private static final String TAG_PRODUCTO = "producto";
    private static final String TAG_PRECIO = "precio";
    private static final String TAG_CANTIDAD = "cantidad";

    String id;
    String codigo,codigo2,codigo_vent;
    String producto,producto_vent;
    String precio,ganancia_vent;
    String cantidad,cantidad2,vendidos_vent, cambio_cliente;

    // products JSONArray
    JSONArray inventario = null;

    ListView lista,lista2;

    String username;
    Button b_buscar,b_agregar, b_fin;
    EditText buscar, et_cantidad,et_efectivo;
    TextView total, tv_cambio1, tv_cambio2;
    int f=10,p, cambio2=0, efectivo=0;
    int success;

    ArrayList<String> arr_cantidad;
    ArrayList<String> arr_codigo;
    ArrayList<String> arr_cantidad_to_rest;
    ArrayList<String> arr_cantidad_final;
    ArrayList<String> arr_ganancia;
    ArrayList<String> arr_producto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_facturacion, container, false);

        b_buscar = (Button) mLinearLayout.findViewById(R.id.b_buscar);
        b_agregar = (Button) mLinearLayout.findViewById(R.id.b_agregar);
        b_fin = (Button) mLinearLayout.findViewById(R.id.b_finalizar);

        buscar = (EditText) mLinearLayout.findViewById(R.id.fact_et_buscar);
        et_cantidad = (EditText) mLinearLayout.findViewById(R.id.fact_et_cantidad);

        total = (TextView) mLinearLayout.findViewById(R.id.tv_total);

        et_efectivo=(EditText)mLinearLayout.findViewById(R.id.et_efectivo);


        b_fin.setEnabled(false);
        b_agregar.setEnabled(false);


        username = this.getArguments().getString("username");


        // Hashmap para el ListView
        inventarioList = new ArrayList<HashMap<String, String>>();
        sellList = new ArrayList<HashMap<String, String>>();

        arr_codigo =new ArrayList<String>();
        arr_cantidad = new ArrayList<String>();
        arr_cantidad_to_rest = new ArrayList<String>();
        arr_cantidad_final = new ArrayList<String>();
        arr_ganancia = new ArrayList<String>();
        arr_producto = new ArrayList<String>();

        lista = (ListView) mLinearLayout.findViewById(R.id.listAllProducts2);
        lista2 = (ListView) mLinearLayout.findViewById(R.id.list_sell_Products);

        b_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_agregar.setEnabled(true);
                if(inventarioList.isEmpty()) {
                }else{
                    inventarioList.remove(0);
                }
                new LoadAllProducts().execute();
                //Toast.makeText(getActivity(), "boton", Toast.LENGTH_SHORT).show();
            }
        });

        b_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_fin.setEnabled(true);
                p=0;
                et_efectivo.setEnabled(true);
                for(HashMap<String, String> hmap : inventarioList)
                {
                    if(TextUtils.isEmpty(et_cantidad.getText())){
                        et_cantidad.setText(Integer.toString(1));
                    }
                    arr_cantidad_to_rest.add(hmap.get(TAG_CANTIDAD));
                    hmap.put(TAG_CANTIDAD, et_cantidad.getText().toString());
                    String precio = hmap.get(TAG_PRECIO);

                    int pp=Integer.parseInt(precio);
                    int can = pp*Integer.parseInt(et_cantidad.getText().toString());
                    hmap.put(TAG_PRECIO, Integer.toString(can));

                }

                if(inventarioList.isEmpty()) {
                    Toast.makeText(getActivity(),getString(R.string.nohayqueagregar), Toast.LENGTH_SHORT).show();
                }else {
                    sellList.add(inventarioList.get(0));

                    for(HashMap<String, String> hmap : sellList)
                    {
                        String precio = hmap.get(TAG_PRECIO);
                        //hmap.put(TAG_CANTIDAD, et_cantidad.getText().toString());
                        //Toast.makeText(getActivity(), "ppp"+tagName, Toast.LENGTH_SHORT).show();
                        p=p + Integer.parseInt(precio);
                    }

                    total.setText(Integer.toString(p));

                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(),
                            sellList,
                            R.layout.row_facturacion,
                            new String[]{
                                    TAG_CODIGO,
                                    TAG_PRODUCTO,
                                    TAG_PRECIO,
                                    TAG_CANTIDAD,
                            },
                            new int[]{
                                    R.id.factu_tv_codigo,
                                    R.id.factu_tv_producto,
                                    R.id.factu_tv_precio,
                                    R.id.factu_tv_cantidad,
                            });
                    lista2.setAdapter(adapter);
                }
            }
        });

        b_fin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                arr_codigo.clear();
                arr_cantidad.clear();

                if (TextUtils.isEmpty(et_efectivo.getText())){
                    efectivo_vacio();
                }

else {
                    efectivo = Integer.parseInt(et_efectivo.getText().toString());
                    cambio2 = efectivo - p;
                    cambio_cliente = Integer.toString(cambio2);

                    if (cambio2 >= 0) {
                        check_venta();


                        for (HashMap<String, String> hmap : sellList) {
                            arr_codigo.add(hmap.get(TAG_CODIGO));
                            arr_cantidad.add(hmap.get(TAG_CANTIDAD));
                            arr_ganancia.add(hmap.get(TAG_PRECIO));
                            arr_producto.add(hmap.get(TAG_PRODUCTO));
                        }

                        arr_cantidad_final.clear();
                        int resta;
                        for (int a = 0; a < arr_codigo.size(); a++) {
                            resta = Integer.parseInt(arr_cantidad_to_rest.get(a)) - Integer.parseInt(arr_cantidad.get(a));
                            arr_cantidad_final.add(Integer.toString(resta));
                            //Toast.makeText(getActivity(), arr_cantidad_to_rest.get(a)+" - "+arr_cantidad.get(a)+" = "+Integer.toString(resta), Toast.LENGTH_SHORT).show();
                        }

                        arr_cantidad_to_rest.clear();
                        int q = 0;
                        for (int a = 0; a < arr_codigo.size(); a++) {
                            codigo2 = arr_codigo.get(a);
                            cantidad2 = arr_cantidad_final.get(a);

                            new Edit_prod().execute();
                            esperar(700);
                        }

                        pDialog.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.nuevafactura), Toast.LENGTH_SHORT).show();

                        for (int a = 0; a < arr_codigo.size(); a++) {
                            codigo_vent = arr_codigo.get(a);
                            producto_vent = arr_producto.get(a);
                            ganancia_vent = arr_ganancia.get(a);
                            vendidos_vent = arr_cantidad.get(a);
                            new Edit_ventas().execute();
                            esperar(700);
                        }


                        reproductor = MediaPlayer.create(getActivity(), R.raw.caja_registradora);

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        Facturacion fragment3 = new Facturacion();
                        bundle.putString("username", username);
                        fragment3.setArguments(bundle);
                        fragmentTransaction.replace(android.R.id.content, fragment3).commit();
                        b_fin.setEnabled(false);

                    } else {
                        fail_venta();
                        //
                        // b_fin.setEnabled(false);
                    }
                }
            }

        });
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
            pDialog.setMessage(getString(R.string.cargando_bases));
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

            String code = buscar.getText().toString();
            try {
                List params = new ArrayList();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("buscar", code));

                Log.d("request!", "starting");
                // getting JSON string from URL
                JSONObject json = jParser.makeHttpRequest(url_facturacion_tomar_datos, "POST", params);

                // Check your log cat for JSON reponse
                Log.d(getString(R.string.cargandoproductos), json.toString());


                // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);
                //Toast.makeText(getActivity(), "holi", Toast.LENGTH_SHORT).show();

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    inventario = json.getJSONArray(TAG_INVENTARIO);

                    // looping through All Products
                    //Log.i("ramiro", "produtos.length" + products.length());
                    for (int i = 0; i < inventario.length(); i++) {
                        JSONObject c = inventario.getJSONObject(0);

                        // Storing each json item in variable
                        id = c.getString(TAG_ID);
                        codigo = c.getString(TAG_CODIGO);
                        producto = c.getString(TAG_PRODUCTO);
                        precio = c.getString(TAG_PRECIO);
                        cantidad = c.getString(TAG_CANTIDAD);

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
                    f = 1;
                    Log.d("User Created!", json.toString());
                    return json.getString(TAG_MESSAGE);
                } else if (success==2){
                    f=2;
                }else{
                    f=0;
                    Log.d("Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
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
                            R.layout.row_facturacion,
                            new String[]{
                                    TAG_CODIGO,
                                    TAG_PRODUCTO,
                                    TAG_PRECIO,
                                    TAG_CANTIDAD,
                            },
                            new int[]{
                                    R.id.factu_tv_codigo,
                                    R.id.factu_tv_producto,
                                    R.id.factu_tv_precio,
                                    R.id.factu_tv_cantidad,
                            });
                    // updating listview
                    //setListAdapter(adapter);
                    sus();
                    lista.setAdapter(adapter);

                }
            });
        }
    }

    public void sus () {
        switch (f) {
            case 0:
                Toast.makeText(getActivity(),getString(R.string.codigonoinventario), Toast.LENGTH_SHORT).show();
                break;
            case 1:
                //Toast.makeText(getActivity(), "Se encontro", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getActivity(),getString(R.string.ingresetodos), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    class Edit_prod extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.aproduct));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters
                List params = new ArrayList();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("codigo", codigo2));
                params.add(new BasicNameValuePair("cantidad", cantidad2));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jParser.makeHttpRequest(url_facturacion_enviar_datos, "POST", params);

                // full json response
                Log.d("Registering attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    pDialog.dismiss();
                    Log.d("User Created!", json.toString());
                    //return json.getString(TAG_MESSAGE);
                    return null;
                }else{
                    Log.d("Registering Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(getActivity(), file_url, Toast.LENGTH_LONG).show();
            }
        }
    }

    class Edit_ventas extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.aproduct));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;


            try {
                // Building Parameters
                List params = new ArrayList();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("codigo", codigo_vent));
                params.add(new BasicNameValuePair("producto", producto_vent));
                params.add(new BasicNameValuePair("ganancia", ganancia_vent));
                params.add(new BasicNameValuePair("vendidos", vendidos_vent));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jParser.makeHttpRequest(url_editar_ventas, "POST", params);

                // full json response
                Log.d("Registering attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    pDialog.dismiss();
                    Log.d("User Created!", json.toString());
                    //return json.getString(TAG_MESSAGE);
                    return null;
                }else{
                    Log.d("Registering Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(getActivity(), file_url, Toast.LENGTH_LONG).show();
            }
        }
    }


    void check_venta(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.estadoventa));
        builder.setMessage(getString(R.string.ventaexitosa) + cambio_cliente);
        builder.setPositiveButton("OK",null);
        builder.create();
        builder.show();
        et_efectivo.setEnabled(true);
    }

    void fail_venta()

    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.estadoventa));
        builder.setMessage(getString(R.string.errorventa));
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }



    void  efectivo_vacio()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.estadoventa));
        builder.setMessage(getString(R.string.ingereseefectivo));
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }


    public void esperar (int milisegundos) {
        try {
            Thread.sleep (milisegundos);
        } catch (Exception e) {
            // Mensaje en caso de que falle
        }
    }

    class Edit_clientes extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.aproduct));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;


            try {
                // Building Parameters
                List params = new ArrayList();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("codigo", codigo_vent));
                params.add(new BasicNameValuePair("producto", producto_vent));
                params.add(new BasicNameValuePair("ganancia", ganancia_vent));
                params.add(new BasicNameValuePair("vendidos", vendidos_vent));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jParser.makeHttpRequest(url_editar_ventas, "POST", params);

                // full json response
                Log.d("Registering attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    pDialog.dismiss();
                    Log.d("User Created!", json.toString());
                    //return json.getString(TAG_MESSAGE);
                    return null;
                }else{
                    Log.d("Registering Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(getActivity(), file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}

