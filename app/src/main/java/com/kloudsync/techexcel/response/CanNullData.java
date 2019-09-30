package com.kloudsync.techexcel.response;

public class CanNullData<T> {
    boolean isNull;
    T data;

    public boolean isNull() {
        return isNull;
    }

    public CanNullData setNull(boolean aNull) {
        isNull = aNull;
        return this;


    }

    public T getData() {
        return data;
    }

    public CanNullData<T> setData(T data) {
        this.data = data;
        return this;
    }
}
