package com.guy_gueta.postPc7;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class ServerHolder {
    private static ServerHolder serverHolder = null;
    public static String HUJI_URL = "https://hujipostpc2019.pythonanywhere.com";

    synchronized static ServerHolder getServerHolder() {
        if (serverHolder == null) {
            serverHolder = new ServerHolder();
        }
        return serverHolder;
    }

    final Ex7Server server;

    private ServerHolder() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        Retrofit retrofit = buildRetofit(gson);
        server = retrofit.create(Ex7Server.class);
    }

    private Retrofit buildRetofit(Gson gson)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .baseUrl(HUJI_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }
}
