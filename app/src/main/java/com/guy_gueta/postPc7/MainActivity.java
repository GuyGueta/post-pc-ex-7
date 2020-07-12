package com.guy_gueta.postPc7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.bumptech.glide.Glide;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;






public class MainActivity extends AppCompatActivity {
    private UserModel model;
    private TextView myText;
    private ImageView myImage;
    private EditText myNameEdit;
    private Button myNameChange;
    private ProgressBar progressBar;
    private String _token;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dialog.TokenListener listener = getTokenListener();
        setContentView(R.layout.activity_main);
        initItems();
        if (_token == null) {
            model.hasErrorOccurred().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(MainActivity.this,
                                "error,please try again later", duration);
                        toast.show();
                    }
                }
            });
            showDialog(listener);

        }
        else {
            model.setUserToken(_token);
            listener.getToken();
        }


    }

    private void showDialog(Dialog.TokenListener listener)
    {
        Dialog dialog = new Dialog();
        dialog.setTokenListener(listener);
        dialog.show(getSupportFragmentManager(), "Login dialog");
    }

    private void observeUser() {
        model.hasErrorOccurred().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    String msg = getResources().getString(R.string.error_message);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(MainActivity.this, msg, duration);
                    toast.show();
                }
            }
        });
        model.getUserInfo().observe(this, new Observer<Ex7Server.User>() {
            @Override
            public void onChanged(Ex7Server.User user) {
                if (user == null || user.username == null) {
                    return;
                }
                changeView();
                String msg = "Hello %1$s, nice to see you again ";
                String userName;
                if (user.pretty_name == null || user.pretty_name.isEmpty()) {
                    userName = user.username;
                }
                else {
                    userName = user.pretty_name;
                }
                myText.setText(String.format(msg, userName) );
                if (user.image_url != null) {
                    Glide.with(MainActivity.this)
                            .load(ServerHolder.HUJI_URL + user.image_url)
                    .into(myImage);
                }
            }
        });
    }

    public Dialog.TokenListener getTokenListener() {
        return new Dialog.TokenListener() {
            @Override
            public void getToken() {
                progressBar.setVisibility(View.VISIBLE);
                model.getUserToken().observe(MainActivity.this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if (s == null || s.isEmpty()) {
                            return;
                        }
                        _token = s;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(TokenWorker.KEY_TOKEN, s);
                        editor.apply();
                        model.setUserInfo();
                        observeUser();
                    }
                });
            }
        };
    }

    private void initItems()
    {
        model = ViewModelProviders.of(this).get(UserModel.class);
        model.setContext(this);
        myText = findViewById(R.id.myTextView);
        myImage = findViewById(R.id.myPic);
        myNameEdit = findViewById(R.id.myNameEdit);
        myNameChange = findViewById(R.id.myChangeName);
        myNameChange.setOnClickListener(changeName());
        sharedPreferences = getPreferences(MODE_PRIVATE);
        progressBar = findViewById(R.id.progressBar);
        _token = sharedPreferences.getString(TokenWorker.KEY_TOKEN, null);
    }

    public void changeView()
    {
        progressBar.setVisibility(View.GONE);
        myNameChange.setVisibility(View.VISIBLE);
        myNameEdit.setVisibility(View.VISIBLE);
    }

    public View.OnClickListener changeName() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = myNameEdit.getText().toString();
                myNameEdit.setText("");
                if (newName.isEmpty()) {
                    return;
                }
                model.setUserPrettyName(newName);
            }
        };
    }
}
