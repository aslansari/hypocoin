package com.aslansari.hypocoin.viewmodel;

public final class Resource<T> {

    private final DataStatus status;
    private final T t;
    private final Throwable throwable;

    private Resource(DataStatus status, T t, Throwable throwable) {
        this.status = status;
        this.t = t;
        this.throwable = throwable;
    }

    public static <T> Resource<T> error(T t, Throwable throwable) {
        return new Resource<>(DataStatus.ERROR, t, throwable);
    }

    public static <T> Resource<T> loading(T t) {
        return new Resource<>(DataStatus.LOADING, t, null);
    }

    public static <T> Resource<T> complete(T t) {
        return new Resource<>(DataStatus.COMPLETE, t, null);
    }

    public DataStatus getStatus() {
        return status;
    }

    public boolean isLoading() {
        return status == DataStatus.LOADING;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public T getValue() {
        return t;
    }
}
