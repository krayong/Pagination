package com.krayong.payo.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserList {
	@SerializedName("data")
	public List<Datum> data = new ArrayList<>();
	
	public class Datum {
		@SerializedName("id")
		public Integer id;
		@SerializedName("email")
		public String email;
		@SerializedName("first_name")
		public String first_name;
		@SerializedName("last_name")
		public String last_name;
		@SerializedName("avatar")
		public String avatar;
	}
}