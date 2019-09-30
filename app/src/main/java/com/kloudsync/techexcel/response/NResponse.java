package com.kloudsync.techexcel.response;

import retrofit2.Response;

public class NResponse<T> {
    private Response<T> response;
    private boolean isNull;

    public NResponse<T> setNull(boolean isNull) {
        this.isNull = isNull;
        return this;
    }

    public boolean isNull() {
        return isNull;
    }

    public Response<T> getResponse() {
        return response;
    }

    public NResponse<T> setResponse(Response<T> response) {
        this.response = response;
        return this;
    }
}
