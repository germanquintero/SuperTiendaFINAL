package com.example.ga.supertienda;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;


public class Datos extends ActionBarActivity {

    Bundle bundle = new Bundle();
    int frag=1;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);

        username = getIntent().getStringExtra("username");

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        frag = getIntent().getIntExtra("frag",0);
        switch (frag){
            case 1:
                Anadir_producto fragment1 = new Anadir_producto();
                bundle.putString("username", username);
                fragment1.setArguments(bundle);
                fragmentTransaction.replace(android.R.id.content, fragment1).commit();
                break;
            case 2:
                Eliminar_productos fragment2 = new Eliminar_productos();
                bundle.putString("username", username);
                fragment2.setArguments(bundle);
                fragmentTransaction.replace(android.R.id.content, fragment2).commit();
                break;
            case 3:
                Facturacion fragment3 = new Facturacion();
                bundle.putString("username", username);
                fragment3.setArguments(bundle);
                fragmentTransaction.replace(android.R.id.content, fragment3).commit();
                break;
            default:
                break;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_datos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        int id = item.getItemId();

        if (id == R.id.menu_facturacion) {
            Facturacion fragment3 = new Facturacion();
            bundle.putString("username", username);
            fragment3.setArguments(bundle);
            fragmentTransaction.replace(android.R.id.content, fragment3).commit();
            return true;
        }

        if (id == R.id.menu_añadir_productos) {
            Anadir_producto fragment1 = new Anadir_producto();
            bundle.putString("username", username);
            fragment1.setArguments(bundle);
            fragmentTransaction.replace(android.R.id.content, fragment1).commit();
            return true;
        }
        if (id == R.id.about1) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.about))
                    .setMessage(getResources().getString(R.string.sAboutInfo))
                    .setNeutralButton(getResources().getString(R.string.sOk), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            return true;
        }

        if (id == R.id.menu_salir) {
            Intent i = new Intent(Datos.this, Login.class);
            i.putExtra("pref", 5);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

   @Override
    public void onBackPressed() {
        Intent i = new Intent(Datos.this, Login.class);
        //i.putExtra("username", username);
        startActivity(i);
        finish();
    }
}
