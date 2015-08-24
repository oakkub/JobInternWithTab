package com.example.oakkub.jobintern.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.oakkub.jobintern.Activities.TabMainActivity;
import com.example.oakkub.jobintern.Network.Retrofit.RestClient;
import com.example.oakkub.jobintern.Objects.CheckServerStatus;
import com.example.oakkub.jobintern.Objects.User;
import com.example.oakkub.jobintern.R;
import com.example.oakkub.jobintern.UI.ProgressDialog.MyProgressDialog;
import com.example.oakkub.jobintern.Utilities.UtilString;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends Fragment implements TextWatcher {

    private final int INPUT_MINIMUM = 3;

    @Bind(R.id.usernameEditText)
    EditText usernameEditText;
    @Bind(R.id.passwordEditText)
    EditText passwordEditText;
    @Bind(R.id.loginButton)
    Button loginButton;
    private ProgressDialog progressDialog;

    private SharedPreferences sharedPreferences;

    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_login, viewGroup, false);

        ButterKnife.bind(this, rootView);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // check if user is login
        if(sharedPreferences.contains(UtilString.PREF_USERNAME)) {
            goToMainScreen();
        }

        usernameEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);

        progressDialog = new MyProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        return rootView;
    }

    private void goToMainScreen() {

        Intent goToMainScreenIntent = new Intent(getActivity(), TabMainActivity.class);
        goToMainScreenIntent.putExtra(UtilString.PREF_USERNAME,
                sharedPreferences.getString(UtilString.PREF_USERNAME, ""));

        if(goToMainScreenIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            startActivity(goToMainScreenIntent);
            getActivity().finish();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void afterTextChanged(Editable editable) {

        if(usernameEditText.getText().toString().length() < INPUT_MINIMUM) usernameEditText.setError("Username must have at least " + INPUT_MINIMUM + " characters");
        if(passwordEditText.getText().toString().length() < INPUT_MINIMUM) passwordEditText.setError("Password must have at least " + INPUT_MINIMUM + " characters");

    }

    @OnClick(R.id.loginButton)
    void login() {

        progressDialog.show();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                login();
            }
        }, LOGIN_DELAYED);*/

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(username.length() < INPUT_MINIMUM || password.length() < INPUT_MINIMUM) {

            progressDialog.dismiss();

            Snackbar.make(rootView, "Username and password must have at least " + INPUT_MINIMUM + " characters.", Snackbar.LENGTH_LONG).show();
            return;
        }

        final User user = new User(username, password);

        RestClient.getInstance(getActivity()).getApiService().hasUser(user.getUsername(), user.getPassword(), new Callback<CheckServerStatus>() {
            @Override
            public void success(CheckServerStatus checkServerStatus, Response response) {

                progressDialog.dismiss();

                if(checkServerStatus.isProgressOK()) {

                    Intent loginIntent = new Intent(getActivity(), TabMainActivity.class);
                    loginIntent.putExtra("username", user.getUsername());

                    if(loginIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                        startActivity(loginIntent);
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
                    }

                } else {

                    Snackbar.make(rootView, "Username or password are incorrect.", Snackbar.LENGTH_LONG).show();

                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("LOGIN ERROR", error.getMessage());

                progressDialog.dismiss();

                Snackbar.make(rootView, "Cannot login, Please try again.", Snackbar.LENGTH_LONG).show();
            }
        });


    }

}
