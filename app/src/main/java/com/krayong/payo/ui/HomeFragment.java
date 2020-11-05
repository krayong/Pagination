package com.krayong.payo.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.krayong.payo.R;
import com.krayong.payo.util.RetrofitInterface;
import com.krayong.payo.adapter.HomeItemAdapter;
import com.krayong.payo.listeners.PaginationScrollListener;
import com.krayong.payo.model.UserList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.krayong.payo.util.Connection.checkConnection;

public class HomeFragment extends Fragment implements HomeItemAdapter.HomeItemClickListener {
	private RecyclerView mRecyclerView;
	private ProgressBar mProgressBar;
	
	private ArrayList<UserList.Datum> mHomeItemArrayList = new ArrayList<>();
	private HomeItemAdapter mHomeItemAdapter;
	
	private final String SORT_BY_FIRST_NAME = "By First Name";
	private final String SORT_BY_LAST_NAME = "By Last Name";
	
	private final String ASCENDING_ORDER = "Ascending Order";
	private final String DESCENDING_ORDER = "Descending Order";
	
	private TextView sortText;
	private ImageButton sortOrderButton, sortByButton;
	
	private String sortBy;
	private String sortOrder;
	
	private boolean isLoading = false;
	private int TOTAL_PAGES = 2;
	private int currentPage = 1;
	
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		checkConnection(this);
		
		isLoading = false;
		currentPage = 1;
		mHomeItemArrayList = new ArrayList<>();
		
		mRecyclerView = view.findViewById(R.id.homeRV);
		mProgressBar = view.findViewById(R.id.progressBar);
		
		sortText = view.findViewById(R.id.sortText);
		sortOrderButton = view.findViewById(R.id.sortOrderButton);
		sortByButton = view.findViewById(R.id.sortByButton);
		
		mHomeItemAdapter = new HomeItemAdapter(requireActivity(), mHomeItemArrayList, this);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
		mRecyclerView.setLayoutManager(linearLayoutManager);
		mRecyclerView.setAdapter(mHomeItemAdapter);
		
		mRecyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
			@Override
			protected void loadMoreItems() {
				isLoading = true;
				++currentPage;
				mProgressBar.setVisibility(View.VISIBLE);
				getData(currentPage);
			}
			
			@Override
			public boolean isLastPage() {
				return currentPage == TOTAL_PAGES;
			}
			
			@Override
			public boolean isLoading() {
				return isLoading;
			}
		});
		
		getData(currentPage);
		
		sortData();
	}
	
	@Override
	public void onLongClick(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
		builder.setTitle("Delete User");
		builder.setMessage("Are you sure you want to delete user " + mHomeItemArrayList.get(position).first_name.trim() + " " + mHomeItemArrayList.get(position).last_name.trim() + "?");
		builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mHomeItemAdapter.notifyItemRemoved(position);
				mHomeItemArrayList.remove(position);
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}
	
	private void getData(int page) {
		checkConnection(this);
		
		String baseURL = "https://reqres.in";
		
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(baseURL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		
		RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
		
		Call<UserList> call = retrofitInterface.doGetUserList(page);
		call.enqueue(new Callback<UserList>() {
			@Override
			public void onResponse(Call<UserList> call, Response<UserList> response) {
				if (response.isSuccessful() && response.body() != null) {
					mProgressBar.setVisibility(View.GONE);
					UserList userList = response.body();
					
					mHomeItemArrayList.addAll(userList.data);
					sort(sortBy, sortOrder.equals(ASCENDING_ORDER));
					
					mHomeItemAdapter = new HomeItemAdapter(requireActivity(), mHomeItemArrayList, HomeFragment.this);
					mRecyclerView.setAdapter(mHomeItemAdapter);
				}
				isLoading = false;
			}
			
			@Override
			public void onFailure(Call<UserList> call, Throwable t) {
				call.cancel();
				isLoading = false;
				Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void sortData() {
		SharedPreferences sortPreferences = requireActivity().getSharedPreferences(FirebaseAuth.getInstance().getCurrentUser().getUid() + "SORTING", MODE_PRIVATE);
		final SharedPreferences.Editor editor = sortPreferences.edit();
		
		if (sortPreferences.getBoolean("SORTING_ORDER_EXISTS", false)) {
			sortBy = sortPreferences.getString("SORTING_BY", SORT_BY_FIRST_NAME);
			sortOrder = sortPreferences.getString("SORTING_ORDER", ASCENDING_ORDER);
		} else {
			editor.putBoolean("SORTING_ORDER_EXISTS", true);
			editor.putString("SORTING_BY", SORT_BY_FIRST_NAME);
			editor.putString("SORTING_ORDER", ASCENDING_ORDER);
			editor.apply();
			sortBy = SORT_BY_FIRST_NAME;
			sortOrder = ASCENDING_ORDER;
		}
		
		sortText.setText(sortBy);
		
		if (sortOrder.equals(ASCENDING_ORDER)) {
			sortOrderButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_downward_arrow));
		} else {
			sortOrderButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_upward_arrow));
		}
		
		sortOrderButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sortOrder.equals(ASCENDING_ORDER)) {
					sortOrder = DESCENDING_ORDER;
					sortOrderButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_upward_arrow));
				} else {
					sortOrder = ASCENDING_ORDER;
					sortOrderButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_downward_arrow));
				}
				editor.putString("SORTING_ORDER", sortOrder).apply();
				sort(sortBy, sortOrder.equals(ASCENDING_ORDER));
			}
		});
		
		sortByButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AlertDialog builder = new AlertDialog.Builder(getContext()).create();
				
				View dialogView = getLayoutInflater().inflate(R.layout.sort_layout, null);
				
				Button okButton = dialogView.findViewById(R.id.sortByOkButton);
				Button cancelButton = dialogView.findViewById(R.id.sortByCancelButton);
				final RadioGroup radioGroup = dialogView.findViewById(R.id.sortByRadioGroup);
				radioGroup.clearCheck();
				
				if (SORT_BY_LAST_NAME.equals(sortBy)) {
					radioGroup.check(R.id.sortByLastName);
				} else {
					radioGroup.check(R.id.sortByFirstName);
				}
				
				cancelButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						builder.dismiss();
					}
				});
				
				okButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (radioGroup.getCheckedRadioButtonId() == R.id.sortByLastName) {
							sortBy = SORT_BY_LAST_NAME;
						} else {
							sortBy = SORT_BY_FIRST_NAME;
						}
						sortText.setText(sortBy);
						editor.putString("SORTING_BY", sortBy).apply();
						sort(sortBy, sortOrder.equals(ASCENDING_ORDER));
						builder.dismiss();
					}
				});
				
				builder.setView(dialogView);
				builder.show();
			}
		});
	}
	
	private void sort(String sortBy, boolean ascending) {
		switch (sortBy) {
			case SORT_BY_FIRST_NAME:
				if (ascending) {
					Collections.sort(mHomeItemArrayList, new Comparator<UserList.Datum>() {
						@Override
						public int compare(UserList.Datum o1, UserList.Datum o2) {
							return o1.first_name.compareTo(o2.first_name);
						}
					});
				} else {
					Collections.sort(mHomeItemArrayList, new Comparator<UserList.Datum>() {
						@Override
						public int compare(UserList.Datum o1, UserList.Datum o2) {
							return o2.first_name.compareTo(o1.first_name);
						}
					});
				}
				break;
			case SORT_BY_LAST_NAME:
				if (ascending) {
					Collections.sort(mHomeItemArrayList, new Comparator<UserList.Datum>() {
						@Override
						public int compare(UserList.Datum o1, UserList.Datum o2) {
							return o1.last_name.compareTo(o2.last_name);
						}
					});
				} else {
					Collections.sort(mHomeItemArrayList, new Comparator<UserList.Datum>() {
						@Override
						public int compare(UserList.Datum o1, UserList.Datum o2) {
							return o2.last_name.compareTo(o1.last_name);
						}
					});
				}
				break;
		}
		
		mHomeItemAdapter.notifyDataSetChanged();
	}
}