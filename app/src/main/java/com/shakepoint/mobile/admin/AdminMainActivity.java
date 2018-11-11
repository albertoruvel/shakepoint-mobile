package com.shakepoint.mobile.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.fragment.admin.CreatePartnerFragment;
import com.shakepoint.mobile.fragment.admin.CreateTrainerFragment;
import com.shakepoint.mobile.fragment.admin.ProductsFragment;
import com.shakepoint.mobile.fragment.admin.PromosFragment;
import com.shakepoint.mobile.fragment.admin.VendingsFragment;
import com.shakepoint.mobile.util.SharedUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdminMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.adminFrameLayout)
    FrameLayout frameLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.adminMainActionButton)
    FloatingActionButton actionButton;

    private ActiveFragment currentFragment = ActiveFragment.VENDINGS;
    private VendingsFragment vendingsFragment = VendingsFragment.newInstance();
    private ProductsFragment productsFragment = ProductsFragment.newInstance();
    private CreatePartnerFragment createPartnerFragment = CreatePartnerFragment.newInstance();
    private PromosFragment promosFragment = PromosFragment.newInstance();
    private CreateTrainerFragment trainerFragment = CreateTrainerFragment.newInstance();

    private static final Integer CREATE_PRODUCT = 123;
    private static final Integer CREATE_PROMO = 124;
    private static final Integer CREATE_VENDING = 125;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        //toggle.getDrawerArrowDrawable().setColor(getColor(R.color.colorAccent));
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //set default fragment (vending)
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.adminFrameLayout, vendingsFragment)
                .commit();
    }

    @OnClick(R.id.adminMainActionButton)
    public void onClick() {
        switch(currentFragment) {
            case PRODUCTS:
                startActivityForResult(new Intent(this, AdminCreateProductActivity.class), CREATE_PRODUCT);
                break;
            case VENDINGS:
                startActivityForResult(new Intent(this, AdminCreateVendingActivity.class), CREATE_VENDING);
                break;
            case PROMO:
                startActivityForResult(new Intent(this, AdminCreatePromo.class), CREATE_PROMO);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.admin_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_promos) {
            getSupportActionBar().setTitle("Promociones");
            currentFragment = ActiveFragment.PROMO;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.adminFrameLayout, promosFragment)
                    .commit();
            actionButton.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_products) {
            getSupportActionBar().setTitle("Productos");
            currentFragment = ActiveFragment.PRODUCTS;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.adminFrameLayout, productsFragment)
                    .commit();
            actionButton.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_partners) {
            getSupportActionBar().setTitle("Crear socio");
            currentFragment = ActiveFragment.CREATE_PARTNER;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.adminFrameLayout, createPartnerFragment)
                    .commit();
            actionButton.setVisibility(View.GONE);
        } else if (id == R.id.nav_vendings) {
            getSupportActionBar().setTitle("Vendings");
            currentFragment = ActiveFragment.VENDINGS;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.adminFrameLayout, vendingsFragment)
                    .commit();
            actionButton.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_settings) {
            SharedUtils.clear(this);
            finish();
        } else if (id == R.id.nav_trainer) {
            getSupportActionBar().setTitle("Crear entrenador");
            currentFragment = ActiveFragment.TRAINER;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.adminFrameLayout, trainerFragment)
                    .commit();
            actionButton.setVisibility(View.GONE);
        } else if (id == R.id.nav_flavors) {
            getSupportActionBar().setTitle("Sabores");
            currentFragment = ActiveFragment.FLAVORS;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.adminFrameLayout, )
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private enum ActiveFragment {
        VENDINGS, PRODUCTS, CREATE_PARTNER, PROMO, TRAINER, FLAVORS
    }
}
