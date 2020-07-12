package com.guy_gueta.postPc7;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class TokenWorker extends Worker {

    static final String KEY_USER = "username_key";
    static final String KEY_TOKEN = "user_token_key";
    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public TokenWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String name = getInputData().getString(KEY_USER);
        try {
            ServerHolder serverHolder = ServerHolder.getServerHolder();
            Call<Ex7Server.TokenResponse> call = serverHolder.server.getToken(name);
            Response<Ex7Server.TokenResponse> response = call.execute();
            if (response.code() != 200 || !response.isSuccessful()) {
                Data failureData = new Data.Builder().putInt(UserModel.ERROR_KEY, response.code()).build();
                return Result.failure(failureData);
            }
            return success(response);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

    private Result success(Response<Ex7Server.TokenResponse> response)
    {
        Data outputData = new Data.Builder()
                .putString(KEY_TOKEN, "token " + response.body().data)
                .build();
        return Result.success(outputData);
    }
}
