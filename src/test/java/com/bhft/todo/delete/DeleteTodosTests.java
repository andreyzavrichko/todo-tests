package com.bhft.todo.delete;

import com.bhft.todo.BaseTest;
import com.todo.annotations.DataPreparationExtension;
import com.todo.annotations.PrepareTodo;
import com.todo.models.Todo;
import com.todo.requests.TodoRequest;
import com.todo.specs.request.RequestSpec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Optional;

@ExtendWith(DataPreparationExtension.class)
public class DeleteTodosTests extends BaseTest {


    @Test
    @PrepareTodo(1)
    @DisplayName("TC1: Авторизированный юзер может удалить todo")
    public void testDeleteExistingTodoWithValidAuth() {
        Optional<Todo> createdTodo = Arrays.stream(todoRequester.getValidatedRequest().readAll(1)).findFirst();

        todoRequester.getValidatedRequest().delete(createdTodo.get().getId());

        softly.assertThat(todoRequester.getValidatedRequest().readAll(1)).hasSize(0);

    }

    @Test
    @PrepareTodo(1)
    @DisplayName("TC2: Неавторизированный юзер не может удалить todo")
    public void testDeleteTodoWithoutAuthHeader() {
        Optional<Todo> createdTodo = Arrays.stream(todoRequester.getValidatedRequest().readAll(1)).findFirst();

        new TodoRequest(RequestSpec.unauthSpec()).delete(createdTodo.get().getId());

        softly.assertThat(todoRequester.getValidatedRequest().readAll(1)).hasSize(1);

    }


    @Test
    @DisplayName("TC3: Авторизованный юзер не может удалить юзера с несуществующим id")
    public void testDeleteNonExistentTodo() {
        todoRequester.getValidatedRequest().deleteNotFound(999);

        todoRequester.getValidatedRequest().getAll();

        softly.assertThat(todoRequester.getValidatedRequest().readAll(1)).hasSize(1);
    }

}
