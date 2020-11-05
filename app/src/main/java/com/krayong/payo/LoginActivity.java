package com.krayong.payo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.krayong.payo.util.Connection.checkConnection;

public class LoginActivity extends AppCompatActivity {
	private TextInputLayout emailTextInput, passwordTextInput;
	private CheckBox rememberMe;
	
	private SharedPreferences.Editor mEditor;
	private final String SESSIONS = "SESSIONS";
	private final String REMEMBER_ME_ENABLED = "rememberMeEnabled";
	private final String REMEMBER_ME_EMAIL = "rememberMeEmail";
	private final String REMEMBER_ME_PASSWORD = "rememberMePassword";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		checkConnection(this);
		
		findViewById(R.id.createAccountButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Pair[] pairs = new Pair[6];
				pairs[0] = new Pair<>(findViewById(R.id.welcomeBack), "welcome_transition");
				pairs[1] = new Pair<>(findViewById(R.id.createAccountButton), "create_account_transition");
				pairs[2] = new Pair<>(findViewById(R.id.loginButton), "login_transition");
				pairs[3] = new Pair<>(findViewById(R.id.emailTextInput), "email_transition");
				pairs[4] = new Pair<>(findViewById(R.id.passwordTextInput), "password_transition");
				pairs[5] = new Pair<>(findViewById(R.id.backgroundRectangle), "bgrect_transition");
				
				ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
				
				startActivity(new Intent(LoginActivity.this, SignUpActivity.class), activityOptions.toBundle());
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
		
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
			finishAffinity();
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
		
		SharedPreferences sharedPreferences = getSharedPreferences(SESSIONS, MODE_PRIVATE);
		mEditor = sharedPreferences.edit();
		
		emailTextInput = findViewById(R.id.emailTextInput);
		passwordTextInput = findViewById(R.id.passwordTextInput);
		
		emailTextInput.getEditText().addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				emailTextInput.setError(null);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
			}
		});
		
		passwordTextInput.getEditText().addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				passwordTextInput.setError(null);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
			}
		});
		
		rememberMe = findViewById(R.id.rememberMeCheckBox);
		if (sharedPreferences.getBoolean(REMEMBER_ME_ENABLED, false)) {
			emailTextInput.getEditText().setText(sharedPreferences.getString(REMEMBER_ME_EMAIL, ""));
			passwordTextInput.getEditText().setText(sharedPreferences.getString(REMEMBER_ME_PASSWORD, ""));
			rememberMe.setChecked(true);
		}
		
		Button loginButton = findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validateFields()) {
					login();
				}
			}
		});
		
		findViewById(R.id.forgotPasswordButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validateEmail(emailTextInput.getEditText().getText().toString()) != null) {
					Toast.makeText(LoginActivity.this, "Put a valid email associated with an account to change the password", Toast.LENGTH_SHORT).show();
				} else {
					FirebaseAuth.getInstance().sendPasswordResetEmail(emailTextInput.getEditText().getText().toString())
							.addOnCompleteListener(new OnCompleteListener<Void>() {
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									if (task.isSuccessful()) {
										Toast.makeText(LoginActivity.this, "A password reset email has been sent to your email address", Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
										task.getException().printStackTrace();
									}
								}
							});
				}
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (emailTextInput.isErrorEnabled()) {
			emailTextInput.setError(null);
		}
		if (passwordTextInput.isErrorEnabled()) {
			passwordTextInput.setError(null);
		}
	}
	
	private void login() {
		checkConnection(this);
		
		findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
		
		FirebaseAuth.getInstance().signInWithEmailAndPassword(emailTextInput.getEditText().getText().toString(), passwordTextInput.getEditText().getText().toString())
				.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							if (rememberMe.isChecked()) {
								mEditor.putBoolean(REMEMBER_ME_ENABLED, true);
								mEditor.putString(REMEMBER_ME_EMAIL, emailTextInput.getEditText().getText().toString());
								mEditor.putString(REMEMBER_ME_PASSWORD, passwordTextInput.getEditText().getText().toString());
							} else {
								mEditor.remove(REMEMBER_ME_ENABLED);
							}
							mEditor.apply();
							
							startActivity(new Intent(LoginActivity.this, MainActivity.class));
							finishAffinity();
							overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
						} else {
							findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
							Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
							task.getException().printStackTrace();
						}
					}
				});
	}
	
	private boolean validateFields() {
		String emailValidation = validateEmail(emailTextInput.getEditText().getText().toString());
		String passwordValidation = validatePassword(passwordTextInput.getEditText().getText().toString());
		
		if (emailValidation == null && passwordValidation == null) {
			return true;
		}
		
		if (emailValidation != null) {
			emailTextInput.setError(emailValidation);
		}
		
		if (passwordValidation != null) {
			passwordTextInput.setError(passwordValidation);
		}
		
		return false;
	}
	
	public String validateEmail(String email) {
		if (email.trim().length() == 0) {
			return "Email cannot be empty";
		} else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
			return "Invalid Email";
		}
		return null;
	}
	
	public String validatePassword(String password) {
		if (password.trim().length() == 0) {
			return "Passwords cannot be empty";
		} else if (password.trim().length() < 8) {
			return "Password too small. Minimum length is 8";
		} else if (password.trim().length() > 15) {
			return "Password too long. Maximum length is 15";
		} else if (password.trim().contains(" ")) {
			return "Password cannot contain spaces";
		} else if (!(password.trim().contains("@") || password.trim().contains("#") || password.trim().contains("$") || password.trim().contains("%") || password.trim().contains("*") || password.trim().matches("(((?=.*[a-z])(?=.*[A-Z]))|((?=.*[a-z])(?=.*[0-9]))|((?=.*[A-Z])(?=.*[0-9])))"))) {
			return "Password should contain atleast one uppercase character, one number and any of the special characters from (@, #, $, %, *)";
		}
		return null;
	}
}