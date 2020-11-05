package com.krayong.payo;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.krayong.payo.model.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import static com.krayong.payo.util.Connection.checkConnection;

public class MainActivity extends AppCompatActivity {
	
	private DrawerLayout drawer;
	private AppBarConfiguration mAppBarConfiguration;
	private NavController navController;
	
	@Override
	public void onBackPressed() {
		if (drawer.isDrawerVisible(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.nav_home) {
			finishAffinity();
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		drawer = findViewById(R.id.drawer_layout);
		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.bringToFront();
		
		mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_profile, R.id.nav_signout)
				.setDrawerLayout(drawer)
				.build();
		
		navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(navigationView, navController);
		
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				if (drawer.isDrawerVisible(GravityCompat.START)) {
					drawer.closeDrawer(GravityCompat.START);
				}
				if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != item.getItemId()) {
					switch (item.getItemId()) {
						case R.id.nav_home:
							navController.navigate(R.id.action_nav_profile_to_nav_home);
							return true;
						case R.id.nav_profile:
							navController.navigate(R.id.action_nav_home_to_nav_profile);
							return true;
						case R.id.nav_signout:
							FirebaseAuth.getInstance().signOut();
							navController.navigate(R.id.nav_signout);
							overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
							finishAffinity();
							return true;
					}
				}
				return false;
			}
		});
		
		final TextView fullName = navigationView.getHeaderView(0).findViewById(R.id.fullName);
		final TextView email = navigationView.getHeaderView(0).findViewById(R.id.email);
		final TextView phoneNumber = navigationView.getHeaderView(0).findViewById(R.id.phoneNumber);
		
		checkConnection(this);
		
		FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				User user = snapshot.getValue(User.class);
				if (user != null) {
					fullName.setText(user.getFirstName() + " " + user.getLastName());
					email.setText(user.getEmail());
					phoneNumber.setText(user.getPhoneNumber());
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError error) {
			
			}
		});
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
	}
}