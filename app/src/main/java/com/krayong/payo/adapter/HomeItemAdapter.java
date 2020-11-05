package com.krayong.payo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.krayong.payo.R;
import com.krayong.payo.model.UserList;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeItemAdapter extends RecyclerView.Adapter<HomeItemAdapter.HomeItemViewHolder> {
	private Activity mActivity;
	private ArrayList<UserList.Datum> mHomeItemArrayList;
	private HomeItemClickListener mHomeItemClickListener;
	
	public HomeItemAdapter(Activity activity, ArrayList<UserList.Datum> homeItemArrayList, HomeItemClickListener homeItemClickListener) {
		mActivity = activity;
		mHomeItemArrayList = homeItemArrayList;
		mHomeItemClickListener = homeItemClickListener;
	}
	
	@NonNull
	@Override
	public HomeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.home_item, parent, false);
		return new HomeItemViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(@NonNull HomeItemViewHolder holder, int position) {
		UserList.Datum item = mHomeItemArrayList.get(position);
		holder.fullName.setText(item.first_name.trim() + " " + item.last_name.trim());
		holder.email.setText(item.email);
		Glide.with(mActivity)
				.load(item.avatar)
				.placeholder(R.drawable.ic_menu_person)
				.into(holder.avatar);
	}
	
	@Override
	public int getItemCount() {
		return mHomeItemArrayList.size();
	}
	
	public interface HomeItemClickListener {
		void onLongClick(int position);
	}
	
	public class HomeItemViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
		private TextView fullName, email;
		private ImageView avatar;
		
		public HomeItemViewHolder(@NonNull View itemView) {
			super(itemView);
			fullName = itemView.findViewById(R.id.fullName);
			email = itemView.findViewById(R.id.email);
			avatar = itemView.findViewById(R.id.avatarImageView);
			
			itemView.findViewById(R.id.homeItemCard).setOnLongClickListener(this);
		}
		
		@Override
		public boolean onLongClick(View v) {
			mHomeItemClickListener.onLongClick(getAdapterPosition());
			return true;
		}
	}
}
