package com.guy_gueta.postPc7;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.gson.Gson;

import java.util.List;
import java.util.UUID;

public class UserModel extends ViewModel {
    public final static String ERROR_KEY = "worker_error";

    private String modelUserName;
    private Context modelContext;
    private MutableLiveData<String> modelUserToken = new MutableLiveData<>("");
    private MutableLiveData<Ex7Server.User> modelUserInfo = new MutableLiveData<>(new Ex7Server.User());
    private MutableLiveData<Boolean> hasErrorOccurred = new MutableLiveData<>(false);



    void setUserToken(String token) {
        if (token != null) {
            modelUserToken.postValue(token);
            return;
        }
        Data inputData = new Data.Builder().putString(TokenWorker.KEY_USER, modelUserName).build();
        UUID workTag = UUID.randomUUID();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        WorkRequest request = getRequest(inputData, constraints, workTag, 0);
        WorkManager manager = WorkManager.getInstance(modelContext);
        manager.enqueue(request);
        manager.getWorkInfosByTagLiveData(workTag.toString()).observe((AppCompatActivity) modelContext, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty()) {
                    return;
                }
                WorkInfo info = workInfos.get(0);
                checkWorkForError(info);
                String token = info.getOutputData().getString(TokenWorker.KEY_TOKEN);
                modelUserToken.postValue(token);
            }
        });
    }

    void setUserInfo() {
        Data inputData = new Data.Builder().putString(TokenWorker.KEY_TOKEN, modelUserToken.getValue()).build();
        UUID workTag = UUID.randomUUID();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        WorkRequest request = getRequest(inputData, constraints, workTag, 1);
        WorkManager manager = WorkManager.getInstance(modelContext);
        manager.enqueue(request);
        manager.getWorkInfosByTagLiveData(workTag.toString()).observe((AppCompatActivity) modelContext, userWorkObserver());
    }

    void setUserPrettyName(String prettyName) {
        Data inputData = new Data.Builder().putString(TokenWorker.KEY_TOKEN, modelUserToken.getValue())
                .putString(NameWorker.PRETTY_NAME_KEY, prettyName).build();
        UUID workTag = UUID.randomUUID();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        WorkRequest request = getRequest(inputData, constraints, workTag, 2);
        WorkManager manager = WorkManager.getInstance(modelContext);
        manager.enqueue(request);
        manager.getWorkInfosByTagLiveData(workTag.toString()).observe((AppCompatActivity) modelContext, userWorkObserver());
    }

    private Observer<List<WorkInfo>> userWorkObserver() {
        return new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty()) {
                    return;
                }
                WorkInfo info = workInfos.get(0);
                checkWorkForError(info);
                Gson gson = new Gson();
                String userJson = info.getOutputData().getString(UserWorker.USER_INFO_KEY);
                Ex7Server.User user = gson.fromJson(userJson, Ex7Server.User.class);
                modelUserInfo.postValue(user);
            }
        };
    }

    private void checkWorkForError(WorkInfo info) {
        int errorCode = info.getOutputData().getInt(ERROR_KEY, -1);
        hasErrorOccurred.postValue(errorCode != -1);
    }

    private WorkRequest getRequest(Data inputData,Constraints constraints,UUID workTag, int worker)
    {
        OneTimeWorkRequest.Builder builder;
        switch (worker)
        {
            case 0:
                builder = new OneTimeWorkRequest.Builder(TokenWorker.class);
                break;
            case 1:
                builder = new OneTimeWorkRequest.Builder(UserWorker.class);
                break;
            case 2:
                builder = new OneTimeWorkRequest.Builder(NameWorker.class);
                break;
            default: // not gonna reach here
                builder  = new OneTimeWorkRequest.Builder(NameWorker.class);
                break;

        }
        builder.setConstraints(constraints);
        builder.setInputData(inputData);
        builder.addTag(workTag.toString());
        return builder.build();
    }

    void setContext(Context context) {
        modelContext = context;
    }

    void setUsername(String username) {
        modelUserName = username;
    }

    LiveData<String> getUserToken () {
        return modelUserToken;
    }

    LiveData<Ex7Server.User> getUserInfo() {
        return modelUserInfo;
    }

    LiveData<Boolean> hasErrorOccurred() {
        return hasErrorOccurred;
    }
}
