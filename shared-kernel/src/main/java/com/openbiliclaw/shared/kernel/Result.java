package com.openbiliclaw.shared.kernel;

public record Result<T>(boolean success, T data, String message) {
    public static <T> Result<T> ok(T data) {
        return new Result<>(true, data, "");
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(false, null, message);
    }
}

