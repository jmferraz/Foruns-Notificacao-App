package com.example.moodleifpe;

import android.app.Activity;
import android.content.Intent;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import retrofit.RestAdapter;
import retrofit.client.Response;


public class LoginActivity extends Activity {

    private TextView usernameEditText;
    private TextView passwordEditText;
    private Button loginButton;

    public static final String ENDPOINT = "http://dead2.ifpe.edu.br/moodle";

    private final IFPEService service;

    private String username;
    private String password;
    private LocalDatabaseHandler localDb;

    public LoginActivity() {
        super();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).build();
        service = restAdapter.create(IFPEService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        localDb = new LocalDatabaseHandler(this);
        if (localDb.getUsername() == null) {
            usernameEditText = (TextView) findViewById(R.id.editTextUsername);
            passwordEditText = (TextView) findViewById(R.id.editTextUsername);
            loginButton = (Button) findViewById(R.id.login_button);
            setOnLoginButtonClickListener();
        } else {
            launchMainActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    private void setOnLoginButtonClickListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    toastLoginFail();
                } else {
                    try {
                        authenticateUser();
                    } catch (IOException e) {
                        toastLoginFail();
                    } catch (Exception e2){
                        toastLoginFail();

                    }
                }
            }
        });
    }

    private void authenticateUser() throws IOException {
        Response response = service.auth(username, password);
        response.getBody().in();
        localDb.insertUser(username, password);
        launchMainActivity();
    }

    private void toastLoginFail() {
        //TODO SUBSTITUIR POR STRING NO ARQUIVO STRINGS.XML
        Toast.makeText(getApplicationContext(), getText(R.string.login_fail), Toast.LENGTH_LONG).show();
    }

    private void launchMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
}
