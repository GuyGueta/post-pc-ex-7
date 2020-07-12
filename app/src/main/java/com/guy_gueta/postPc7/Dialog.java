package com.guy_gueta.postPc7;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class Dialog extends AppCompatDialogFragment {
    private final static String DIALOG_TITLE = "Please enter username: ";
    private EditText usernameInput;
    private UserModel userModel;
    private TokenListener tokenListener;

    public void setTokenListener(TokenListener listener) {
        tokenListener = listener;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        userModel = ViewModelProviders.of(getActivity()).get(UserModel.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        return inflate(inflater, builder);
    }

    private DialogInterface.OnClickListener checkUserName() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userName = usernameInput.getText().toString();
                userModel.setUsername(userName);
                userModel.setUserToken(null);
                tokenListener.getToken();
            }
        };
    }

    public interface TokenListener {
        void getToken();
    }

    private AlertDialog inflate(LayoutInflater inflater, AlertDialog.Builder builder)
    {
        View view = inflater.inflate(R.layout.login_layout, null);
        usernameInput = view.findViewById(R.id.username_input);
        builder.setView(view)
                .setTitle(DIALOG_TITLE)
                .setPositiveButton("ok", checkUserName());
        return builder.create();
    }
}
