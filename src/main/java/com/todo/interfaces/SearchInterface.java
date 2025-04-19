package com.todo.interfaces;

import io.restassured.response.Response;

public interface SearchInterface<T> {
    Response readAll();

    Response readAll(int offset, int limit);
}
