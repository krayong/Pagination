package com.krayong.payo.util;

import com.krayong.payo.model.UserList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {
	@GET("/api/users?")
	Call<UserList> doGetUserList(@Query("page") int page);
}
