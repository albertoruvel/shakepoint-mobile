package com.shakepoint.mobile;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shakepoint.mobile.adapter.admin.PromosAdapter;
import com.shakepoint.mobile.data.res.admin.Promotion;
import com.shakepoint.mobile.decorator.SpaceDividerItemDecorator;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.TrainerClient;
import com.shakepoint.mobile.util.SharedUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignedPromosActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.trainerPromosMessage)
    TextView message;

    @BindView(R.id.trainerPromosProgressBar)
    ProgressBar progressBar;

    @BindView(R.id.trainerPromosRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.trainerPromosRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private TrainerClient trainerClient;
    private PromosAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_promos);
        ButterKnife.bind(this);
        trainerClient = RetroFactory.retrofit(this).create(TrainerClient.class);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Snackbar.make(coordinatorLayout, "Puedes compartir alguna promoción con personas a las que ")
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPromotions();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpaceDividerItemDecorator());
        getPromotions();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getPromotions() {
        trainerClient.getTrainerPromoCodes(SharedUtils.getAuthenticationToken(this))
                .enqueue(new Callback<List<Promotion>>() {
                    @Override
                    public void onResponse(Call<List<Promotion>> call, Response<List<Promotion>> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body().isEmpty()) {
                                    showMessage("No tienes promociones asignadas");
                                } else {
                                    adapter = new PromosAdapter(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final int position = recyclerView.getChildAdapterPosition(v);
                                            Promotion promotion = adapter.getPromo(position);
                                            try{
                                                Intent intent = new Intent();
                                                intent.setAction(Intent.ACTION_SEND);
                                                final String message = String.format(getString(R.string.whatsapp_message), promotion.getCode());
                                                intent.putExtra(Intent.EXTRA_TEXT, message);
                                                intent.setType("text/plain");
                                                intent.setPackage("com.whatsapp");
                                                startActivity(intent);
                                            }catch(Exception ex) {

                                            }
                                        }
                                    }, response.body());
                                    recyclerView.setAdapter(adapter);
                                    showList();
                                }
                                break;
                            case 500:
                                showMessage(getString(R.string.request_error));
                                break;
                            case 401:
                                Toast.makeText(AssignedPromosActivity.this, "Ha ocurrido un error inesperado", Toast.LENGTH_LONG).show();
                                SharedUtils.clear(AssignedPromosActivity.this);
                                finishAffinity();
                                break;
                        }
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<List<Promotion>> call, Throwable t) {
                        showMessage(getString(R.string.request_error));
                    }
                });
    }

    private void showList() {
        recyclerView.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showMessage(String msg) {
        message.setText(msg);
        recyclerView.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
