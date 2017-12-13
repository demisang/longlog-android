package ru.longlog.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;

import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.longlog.AppApplication;
import ru.longlog.R;
import ru.longlog.models.AuthResponse;
import ru.longlog.models.CheckVersionResponse;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {
    // UI references.
    private EditText mEmailView;
    private EditText mHostView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    /**
     * If true - host is valid
     */
    private boolean apiHostIsCompatible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.email_login_input);
        mHostView = findViewById(R.id.host_login_input);
        mPasswordView = findViewById(R.id.password_login_input);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Fill host from config (or default value)
        mHostView.setText(AppApplication.getBaseUrl());
        mHostView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // New host value - need to check version compatibility
                apiHostIsCompatible = false;
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        final String host = mHostView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } else if (!isHostValid(host)) {
            mHostView.setError(getString(R.string.error_invalid_host));
            focusView = mHostView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            checkApiVersionAndAuth(host, email, password);
        }
    }

    private void checkApiVersionAndAuth(final String host, final String email, final String password) {
        // Get access token response callback
        final Callback<AuthResponse> authCallback = new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                showProgress(false);

                if (response.code() == 200 && response.body() != null) {
                    // Save host value
                    AppApplication.setBaseUrl(host);
                    // Save returned accessToken
                    AppApplication.setAccessToken(response.body().getAccessToken());

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (response.code() == 429) {
                    Toast.makeText(LoginActivity.this, R.string.too_many_requests, Toast.LENGTH_SHORT).show();
                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "An error occurred during networking:\n" + t.getMessage(), Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        };

        // Check API version response callback
        final Callback<CheckVersionResponse> checkVersionCallback = new Callback<CheckVersionResponse>() {
            @Override
            public void onResponse(Call<CheckVersionResponse> call, Response<CheckVersionResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    CheckVersionResponse checkVersionResult = response.body();
                    // If API version is compatible - process auth and get access token
                    if (checkVersionResult.isCompatible()) {
                        if (!checkVersionResult.isLatest()) {
                            // @todo Here can be some user notification about "Server support new API version, update your application for this"
                        }
                        // Mark that this api host is compatible
                        apiHostIsCompatible = true;
                        // Process auth request
                        AppApplication.getApi().accessToken(email, password).enqueue(authCallback);
                    } else {
                        // Incompatible error message
                        String error = getString(R.string.api_version_incompatible, AppApplication.getAppApiVersion(),
                                TextUtils.join(", ", checkVersionResult.getSupportedVersions()));
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                        showProgress(false);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, R.string.check_version_error, Toast.LENGTH_LONG).show();
                    showProgress(false);
                }
            }

            @Override
            public void onFailure(Call<CheckVersionResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "An error occurred during networking:\n" + t.getMessage(), Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        };

        // For first check api version compatibility
        if (!apiHostIsCompatible) {
            AppApplication.getApi().checkVersion(AppApplication.getAppApiVersion()).enqueue(checkVersionCallback);
        } else {
            // Process auth request if host already checked
            AppApplication.getApi().accessToken(email, password).enqueue(authCallback);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    private boolean isHostValid(String host) {
        return Patterns.WEB_URL.matcher(host).matches();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Login page doesn't have menu
        return true;
    }
}

