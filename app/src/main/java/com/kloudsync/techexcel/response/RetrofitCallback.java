package com.kloudsync.techexcel.response;

import com.kloudsync.techexcel.config.AppConfig;

import retrofit2.Call;
import retrofit2.Response;

public abstract class RetrofitCallback<T> implements retrofit2.Callback<NetworkResponse<T>> {

    @Override
    public void onResponse(Call<NetworkResponse<T>> call, Response<NetworkResponse<T>> response) {
        if (response != null && response.isSuccessful()) {
            if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {
                onSuccess(response.body().getRetData());
            } else {
                onFail(response.body().getRetCode(), response.body().ErrorMessage);
            }
        } else {
            onFail(NetworkResponse.NETWORK_ERROR_CODE, "network error");
        }
    }

    @Override
    public void onFailure(Call<NetworkResponse<T>> call, Throwable t) {
        onFail(NetworkResponse.NETWORK_ERROR_CODE, "network error");
    }

    abstract void onSuccess(T response);

    abstract void onFail(int errorCode, String message);
}
