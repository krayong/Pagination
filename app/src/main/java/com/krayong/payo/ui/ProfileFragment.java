package com.krayong.payo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.krayong.payo.R;
import com.krayong.payo.model.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.krayong.payo.util.Connection.checkConnection;

public class ProfileFragment extends Fragment {
	
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_profile, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		checkConnection(this);
		
		final ProgressBar progressBar = view.findViewById(R.id.progressBar);
		
		progressBar.setVisibility(View.VISIBLE);
		
		final TextInputLayout firstNameTextInput = view.findViewById(R.id.firstNameTextInput);
		final TextInputLayout lastNameTextInput = view.findViewById(R.id.lastNameTextInput);
		final TextInputLayout emailTextInput = view.findViewById(R.id.emailTextInput);
		final TextInputLayout phoneNumberTextInput = view.findViewById(R.id.phoneNumberTextInput);
		final TextInputLayout addressTextInput = view.findViewById(R.id.addressTextInput);
		
		FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				User user = snapshot.getValue(User.class);
				if (user != null) {
					firstNameTextInput.getEditText().setText(user.getFirstName());
					lastNameTextInput.getEditText().setText(user.getLastName());
					emailTextInput.getEditText().setText(user.getEmail());
					phoneNumberTextInput.getEditText().setText(user.getPhoneNumber());
					addressTextInput.getEditText().setText(user.getAddress());
				}
				
				progressBar.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError error) {
			
			}
		});
	}
}