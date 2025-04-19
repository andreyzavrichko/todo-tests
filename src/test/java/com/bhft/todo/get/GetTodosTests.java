package com.bhft.todo.get;


import com.bhft.todo.BaseTest;
import com.todo.annotations.DataPreparationExtension;
import com.todo.annotations.PrepareTodo;
import com.todo.specs.response.IncorrectDataResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

@Epic("TODO Management")
@Feature("Get Todos API")
@ExtendWith(DataPreparationExtension.class)
public class GetTodosTests extends BaseTest {


    @Test
    @PrepareTodo(5)
    @DisplayName("Авторизованный юзер может получить список всех todo")
    public void testGetTodosWithExistingEntries() {
        var createdTodos = todoRequester.getValidatedRequest().readAll(100);

        softly.assertThat(createdTodos).hasSize(5);

    }

    @Test
    @PrepareTodo(5)
    @DisplayName("Авторизованный юзер может получать список todo с учетом offset и limit для пагинации")
    public void testGetTodosWithOffsetAndLimit() {
        var createdTodos = todoRequester.getValidatedRequest().readAll(2, 2);

        softly.assertThat(createdTodos).hasSize(2);
    }

    @Test
    @DisplayName("Передача некорректных значений в offset и limit")
    public void testGetTodosWithInvalidOffsetAndLimit() {
        todoRequester.getRequest().readAll(-1, 2)
                .then().assertThat().spec(IncorrectDataResponse.offsetOtLimitHaveIncorrectValues());


    }

    @Test
    @PrepareTodo(10)
    @DisplayName("Проверка ответа при превышении максимально допустимого значения limit")
    public void testGetTodosWithExcessiveLimit() {
        var paginatedTodos = todoRequester.getValidatedRequest().readAll(1000);
        var allTodos = todoRequester.getValidatedRequest().readAll(20);

        softly.assertThat(Arrays.equals(paginatedTodos, allTodos));

    }
}
