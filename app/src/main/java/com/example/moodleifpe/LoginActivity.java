package com.example.moodleifpe;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import retrofit.RestAdapter;
import retrofit.client.Response;


public class LoginActivity extends Activity {

    private TextView usernameEditText;
    private TextView passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;

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
            passwordEditText = (TextView) findViewById(R.id.editTextPassword);
            loginButton = (Button) findViewById(R.id.login_button);
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            setOnLoginButtonClickListener();
            Utils.enableCookies();
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
                    new AuthUserTask().execute();
                }
            }
        });
    }

    private void toastLoginFail() {
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

    private class AuthUserTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Response response = service.auth(username, password);
                String indexPage = Utils.responseToString(response);
                if (HtmlExtractor.isAuth(indexPage)) {
                    localDb.insertUser(username, password);
                } else {
                    cancel(true);
                }
            } catch (Exception e) {
                cancel(true);
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.INVISIBLE);
            launchMainActivity();
        }

        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.INVISIBLE);
            toastLoginFail();
        }
    }
}
