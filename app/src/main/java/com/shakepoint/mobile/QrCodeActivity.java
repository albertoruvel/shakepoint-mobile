package com.shakepoint.mobile;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QrCodeActivity extends AppCompatActivity {

    public static final String QR_CODE_URL = "qrCodeUrl";
    private static final int WRITE_REQUEST = 1123;

    @BindView(R.id.qrCodeImage)
    ImageView qrCode;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        final String qrCodeUrl = getIntent().getStringExtra(QR_CODE_URL);
        ButterKnife.bind(this);
        Picasso.with(this).load(qrCodeUrl).into(qrCode);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Settings.System.canWrite(this)) {
            //change brightness
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 200);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("La aplicación necesita permiso para modificar el brillo de tu pantalla, deseas continuar?")
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //intent.setData(Uri.parse("package: " + getPackageName()));
                            dialog.dismiss();
                            startActivityForResult(intent, WRITE_REQUEST);
                            Toast.makeText(QrCodeActivity.this, "Da permisos a Shakepoint y vuelve a ingresar a ver la compra", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .create().show();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_REQUEST && Settings.System.canWrite(this)){
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 500);
        }
    }

}
