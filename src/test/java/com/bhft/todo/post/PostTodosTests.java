package com.bhft.todo.post;

import com.bhft.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.specs.response.IncorrectDataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static com.todo.generators.TestDataGenerator.generateTestData;

public class PostTodosTests extends BaseTest {


    @Test
    @DisplayName("TC1: Авторизированный юзер может создать TODO с валидными данными")
    public void testCreateTodoWithValidData() {
        Todo newTodo = generateTestData(Todo.class);

        todoRequester.getValidatedRequest().create(newTodo);

        Optional<Todo> createdTodo = Arrays.stream(todoRequester.getValidatedRequest().getAll())
                .filter(todo -> todo.getId() == newTodo.getId())
                .findFirst();

        softly.assertThat(createdTodo)
                .as("Созданная задача не найдена в списке TODO")
                .isPresent();

        createdTodo.ifPresent(todo -> {
            softly.assertThat(todo.getText()).isEqualTo(newTodo.getText());
            softly.assertThat(todo.isCompleted()).isEqualTo(newTodo.isCompleted());
        });
    }

    @Test
    @DisplayName("TC2: Авторизированный юзер может создать TODO с максимальной длиной поля text")
    public void testCreateTodoWithMaxLengthText() {
        String maxLengthText = "A".repeat(255);
        Todo newTodo = new Todo(3, maxLengthText, false);

        todoRequester.getValidatedRequest().create(newTodo);

        Optional<Todo> createdTodo = Arrays.stream(todoRequester.getValidatedRequest().getAll())
                .filter(todo -> todo.getId() == newTodo.getId())
                .findFirst();

        softly.assertThat(createdTodo)
                .as("Созданная задача не найдена в списке TODO")
                .isPresent();

        createdTodo.ifPresent(todo -> {
            softly.assertThat(todo.getText()).isEqualTo(newTodo.getText());
            softly.assertThat(todo.isCompleted()).isEqualTo(newTodo.isCompleted());
        });
    }

    @Test
    @DisplayName("TC3: Авторизированный юзер может создать TODO с минимальной длиной поля text")
    public void testCreateTodoWithMinLengthText() {
        String minLengthText = "A";
        Todo newTodo = new Todo(4, minLengthText, false);

        todoRequester.getValidatedRequest().create(newTodo);

        Optional<Todo> createdTodo = Arrays.stream(todoRequester.getValidatedRequest().getAll())
                .filter(todo -> todo.getId() == newTodo.getId())
                .findFirst();

        softly.assertThat(createdTodo)
                .as("Созданная задача не найдена в списке TODO")
                .isPresent();

        createdTodo.ifPresent(todo -> {
            softly.assertThat(todo.getText()).isEqualTo(newTodo.getText());
            softly.assertThat(todo.isCompleted()).isEqualTo(newTodo.isCompleted());
        });
    }


    @Test
    @DisplayName("TC4: Создание TODO с уже существующим 'id' (если 'id' задается клиентом).")
    public void testCreateTodoWithExistingId() {
        Todo firstTodo = generateTestData(Todo.class);
        createTodo(firstTodo);

        Todo duplicateTodo = new Todo(firstTodo.getId(), "Duplicate Task", true);

        todoRequester.getRequest()
                .create(duplicateTodo)
                .then()
                .spec(new IncorrectDataResponse().sameId());

    }

}
