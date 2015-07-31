package com.example.ga.supertienda;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class User_login extends ActionBarActivity implements android.support.v7.app.ActionBar.TabListener, ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    String username;
    Bundle bundle = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);

        mViewPager.setOnPageChangeListener(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);

        android.support.v7.app.ActionBar.Tab tab = actionBar.newTab().setText(getResources().getString(R.string.inventario)).setTabListener(this);
        actionBar.addTab(tab);

        tab = actionBar.newTab().setText(getResources().getString(R.string.ventas)).setTabListener(this);
        actionBar.addTab(tab);

       // tab = actionBar.newTab().setText("Clientes").setTabListener(this);
        //actionBar.addTab(tab);
    }

    public class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    username = getIntent().getStringExtra("username");
                    bundle.putString("username", username);
                    inventario_usuario1 frag_inventario = new inventario_usuario1();
                    frag_inventario.setArguments(bundle);

                    return frag_inventario;
                case 1:
                    username = getIntent().getStringExtra("username");
                    bundle.putString("username", username);
                    ventas_usuario frag_ventas = new ventas_usuario();
                    frag_ventas.setArguments(bundle);

                    return frag_ventas;
                //case 2:
                   // Clientes frag_clientes = new Clientes();

                  //  return frag_clientes;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_login, menu);
        return true;
    }

    int frag;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_facturacion) {
            Intent i = new Intent(User_login.this, Datos.class);
            i.putExtra("frag", 3);
            i.putExtra("username", username);
            finish();
            startActivity(i);
            return true;
        }

        if (id == R.id.menu_añadir_productos) {
            Intent i = new Intent(User_login.this, Datos.class);
            i.putExtra("frag", 1);
            i.putExtra("username", username);
           finish();
            startActivity(i);
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
            Intent i = new Intent(User_login.this, Login.class);
            i.putExtra("pref", 5);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        getSupportActionBar().setSelectedNavigationItem(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }
}

/*package com.josevargas.mysql3;


 import android.app.ActionBar;
 import android.app.FragmentManager;
 import android.app.FragmentTransaction;
 import android.content.Intent;
 import android.os.Bundle;
 import android.support.v4.app.Fragment;
 import android.support.v4.app.FragmentActivity;
 import android.support.v4.app.FragmentTabHost;
 import android.support.v4.view.ViewPager;
 import android.support.v7.app.ActionBarActivity;
 import android.util.Log;
 import android.view.Menu;
 import android.view.MenuItem;

 import java.util.ArrayList;


 public class User_login extends ActionBarActivity {

 String username;
 Bundle bundle = new Bundle();
 @Override
 protected void onCreate(Bundle savedInstanceState) {
 super.onCreate(savedInstanceState);
 setContentView(R.layout.activity_user_login);

 username = getIntent().getStringExtra("username");
 bundle.putString("username", username);
 inventario_usuario1 frag_inventario = new inventario_usuario1();
 frag_inventario.setArguments(bundle);

 Clientes frag_clientes = new Clientes();

 ventas_usuario frag_ventas = new ventas_usuario();
 frag_ventas.setArguments(bundle);

 ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();

 fragmentList.add(frag_inventario);
 fragmentList.add(frag_ventas);
 fragmentList.add(frag_clientes);


 MyPageAdapter mSectionsPagerAdapter = new MyPageAdapter(getSupportFragmentManager(), fragmentList);

 ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
 mViewPager.setAdapter(mSectionsPagerAdapter);
 }

 @Override
 public boolean onCreateOptionsMenu(Menu menu) {
 // Inflate the menu; this adds items to the action bar if it is present.
 getMenuInflater().inflate(R.menu.menu_user_login, menu);
 return true;
 }

 int frag;
 @Override
 public boolean onOptionsItemSelected(MenuItem item) {
 // Handle action bar item clicks here. The action bar will
 // automatically handle clicks on the Home/Up button, so long
 // as you specify a parent activity in AndroidManifest.xml.
 int id = item.getItemId();

 if (id == R.id.menu_facturacion) {
 Intent i = new Intent(User_login.this, Datos.class);
 i.putExtra("frag", 3);
 i.putExtra("username", username);
 finish();
 startActivity(i);
 return true;
 }

 if (id == R.id.menu_añadir_productos) {
 Intent i = new Intent(User_login.this, Datos.class);
 i.putExtra("frag", 1);
 i.putExtra("username", username);
 finish();
 startActivity(i);
 return true;
 }



 if (id == R.id.menu_eliminar_productos) {
 Intent i = new Intent(User_login.this, Datos.class);
 i.putExtra("frag", 2);
 i.putExtra("username", username);
 finish();
 startActivity(i);
 return true;
 }

 if (id == R.id.menu_salir) {
 Intent i = new Intent(User_login.this, Login.class);
 i.putExtra("pref", 5);
 startActivity(i);
 finish();
 return true;
 }
 return super.onOptionsItemSelected(item);
 }

 @Override
 public void onBackPressed() {
 finish();
 }



 }


 */
