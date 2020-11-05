package com.krayong.payo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.krayong.payo.model.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.krayong.payo.util.Connection.checkConnection;

public class SignUpActivity extends AppCompatActivity {
	
	private TextInputLayout firstNameTextInput, lastNameTextInput, emailTextInput, phoneNumberTextInput, addressTextInput, passwordTextInput, confirmPasswordTextInput;
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		checkConnection(this);
		
		findViewById(R.id.backArrow).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		firstNameTextInput = findViewById(R.id.firstNameTextInput);
		firstNameTextInput.getEditText().addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				firstNameTextInput.setError(null);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
			}
		});
		
		lastNameTextInput = findViewById(R.id.lastNameTextInput);
		lastNameTextInput.getEditText().addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				lastNameTextInput.setError(null);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
			}
		});
		
		emailTextInput = findViewById(R.id.emailTextInput);
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
		
		phoneNumberTextInput = findViewById(R.id.phoneNumberTextInput);
		phoneNumberTextInput.getEditText().addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				phoneNumberTextInput.setError(null);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
			}
		});
		
		addressTextInput = findViewById(R.id.addressTextInput);
		addressTextInput.getEditText().addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				addressTextInput.setError(null);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
			}
		});
		
		passwordTextInput = findViewById(R.id.passwordTextInput);
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
		
		confirmPasswordTextInput = findViewById(R.id.confirmPasswordTextInput);
		confirmPasswordTextInput.getEditText().addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				confirmPasswordTextInput.setError(null);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
			}
		});
		
		findViewById(R.id.createAccountButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validateFields()) {
					createAccount();
				}
			}
		});
	}
	
	private void createAccount() {
		checkConnection(this);
		
		findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
		
		FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailTextInput.getEditText().getText().toString(), passwordTextInput.getEditText().getText().toString())
				.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							storeUserDetails();
						} else {
							findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
							Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
						}
					}
				});
	}
	
	private void storeUserDetails() {
		final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
		
		final User user = new User(firstNameTextInput.getEditText().getText().toString().trim(),
				lastNameTextInput.getEditText().getText().toString().trim(),
				emailTextInput.getEditText().getText().toString().trim(),
				phoneNumberTextInput.getEditText().getText().toString().trim(),
				addressTextInput.getEditText().getText().toString().trim());
		
		FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(user);
		
		startActivity(new Intent(SignUpActivity.this, MainActivity.class));
		finishAffinity();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	private boolean validateFields() {
		String firstName = firstNameTextInput.getEditText().getText().toString();
		String firstNameValidation = validateName(firstName);
		
		String lastName = lastNameTextInput.getEditText().getText().toString();
		String lastNameValidation = validateName(lastName);
		
		String email = emailTextInput.getEditText().getText().toString();
		String emailValidation = validateEmail(email);
		
		String phoneNumber = phoneNumberTextInput.getEditText().getText().toString();
		String phoneNumberValidation = validatePhoneNumber(phoneNumber);
		
		String address = addressTextInput.getEditText().getText().toString();
		String addressValidation = validateAddress(address);
		
		String passwordValidation = validatePassword(passwordTextInput.getEditText().getText().toString(), confirmPasswordTextInput.getEditText().getText().toString());
		
		String confirmPasswordValidation = validateConfirmPassword(confirmPasswordTextInput.getEditText().getText().toString(), passwordTextInput.getEditText().getText().toString());
		
		if (firstNameValidation == null && lastNameValidation == null && emailValidation == null && phoneNumberValidation == null && addressValidation == null && passwordValidation == null && confirmPasswordValidation == null) {
			return true;
		}
		
		if (firstNameValidation != null) {
			firstNameTextInput.setError(firstNameValidation);
		}
		
		if (lastNameValidation != null) {
			lastNameTextInput.setError(lastNameValidation);
		}
		
		if (emailValidation != null) {
			emailTextInput.setError(emailValidation);
		}
		
		if (phoneNumberValidation != null) {
			phoneNumberTextInput.setError(phoneNumberValidation);
		}
		
		if (addressValidation != null) {
			addressTextInput.setError(addressValidation);
		}
		
		if (passwordValidation != null) {
			passwordTextInput.setError(passwordValidation);
		}
		
		if (confirmPasswordValidation != null) {
			confirmPasswordTextInput.setError(confirmPasswordValidation);
		}
		
		return false;
	}
	
	private String validateName(String name) {
		if (name.trim().length() == 0) {
			return "Name cannot be empty";
		} else if (name.trim().matches("^[0-9]+$")) {
			return "Name cannot have numbers in it";
		} else if (!name.trim().matches("^[a-zA-Z][a-zA-Z ]++$")) {
			return "Invalid Name";
		}
		return null;
	}
	
	private String validateEmail(String email) {
		if (email.trim().length() == 0) {
			return "Email cannot be empty";
		} else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
			return "Invalid Email";
		}
		return null;
	}
	
	private String validatePhoneNumber(String phoneNumber) {
		if (phoneNumber.trim().length() == 0) {
			return "Phone number cannot be empty";
		} else if (!Patterns.PHONE.matcher(phoneNumber.trim()).matches()) {
			return "Invalid Phone Number";
		}
		return null;
	}
	
	private String validateAddress(String address) {
		if (address.trim().length() == 0) {
			return "Address cannot be empty";
		} else if (address.trim().length() < 5) {
			return "Address too small";
		}
		return null;
	}
	
	private String validatePassword(String password, String confirmPassword) {
		if (password.trim().length() == 0) {
			return "Passwords cannot be empty";
		} else if (confirmPassword.trim().length() == 0) {
			return "Passwords cannot be empty";
		} else if (!confirmPassword.trim().matches(password.trim())) {
			return "Passwords do not match";
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
	
	private String validateConfirmPassword(String confirmPassword, String password) {
		if (confirmPassword.trim().length() == 0) {
			return "Passwords cannot be empty";
		} else if (password.trim().length() == 0) {
			return "Passwords cannot be empty";
		} else if (!confirmPassword.trim().matches(password.trim())) {
			return "Passwords do not match";
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