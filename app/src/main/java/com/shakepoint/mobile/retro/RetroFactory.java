package com.shakepoint.mobile.retro;

import android.content.Context;
import android.util.Log;

import com.shakepoint.mobile.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jose.rubalcaba on 02/12/2018.
 */

public class RetroFactory {
    private static Retrofit retrofit;
    private static final Environment CURRENT_ENV = Environment.DEV;

    private enum Environment {
        LOCAL("http://192.168.0.7:8080/rest/v1/"),
        DEV("http://35.160.133.118/rest/v1/"),
        PRD("https://shakepoint.com.mx/rest/v1/");
        String host;

        Environment(String value) {
            this.host = value;
        }
    }

    public static Retrofit retrofit(Context cxt) {
        if (retrofit == null) {
            switch (CURRENT_ENV) {
                case LOCAL:
                    retrofit = new Retrofit.Builder()
                            .baseUrl(CURRENT_ENV.host)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    break;
                case DEV:
                    retrofit = new Retrofit.Builder()
                            .baseUrl(CURRENT_ENV.host)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    break;
                case PRD:
                    try {
                        okhttp3.OkHttpClient client = getHttpClient(cxt);
                        retrofit = new Retrofit.Builder()
                                .client(client)
                                .baseUrl(CURRENT_ENV.host)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                    } catch (Exception ex) {
                        Log.e("SSLCONFIG", "Could not create ssl config for PRD env");
                    }
                    break;
            }

        }
        return retrofit;
    }

    private static OkHttpClient getHttpClient(Context cxt) throws java.security.cert.CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        //load cas from an input stream
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Certificate cert;
        try (InputStream certStream = cxt.getResources().openRawResource(R.raw.shakepointcommx)) {
            cert = certificateFactory.generateCertificate(certStream);
        }

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", cert);

        //create trust manager
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
        trustManagerFactory.init(keyStore);

        //create context
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustManagerFactory.getTrustManagers(), null);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(context.getSocketFactory(), new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                })
                .build();
        return client;
    }

}
