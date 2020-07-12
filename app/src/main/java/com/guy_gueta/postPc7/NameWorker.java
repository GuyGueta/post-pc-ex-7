package com.guy_gueta.postPc7;

import android.content.Context;
import com.google.gson.Gson;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;



public class NameWorker extends Worker {
    static final String PRETTY_NAME_KEY = "pretty_name";

    public NameWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String token = getInputData().getString(TokenWorker.KEY_TOKEN);
        Ex7Server.SetUserPrettyNameRequest request = new Ex7Server.SetUserPrettyNameRequest();
        request.pretty_name = getInputData().getString(PRETTY_NAME_KEY);
        try {
            Call<Ex7Server.UserResponse> call = ServerHolder.getServerHolder().server.setUserPrettyName(token, request);
            Response<Ex7Server.UserResponse> response = call.execute();
            if (response.code() != 200 || !response.isSuccessful()) {
                return Result.failure(new Data.Builder().putInt(UserModel.ERROR_KEY, response.code()).build());
            }
            return success(response);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

    private Result success(Response<Ex7Server.UserResponse> response)
    {
        Gson gson = new Gson();
        String userAsString = gson.toJson(response.body().data);
        Data outputData = new Data.Builder()
                .putString(UserWorker.USER_INFO_KEY, userAsString)
                .build();
        return Result.success(outputData);
    }
}
