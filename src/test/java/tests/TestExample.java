package tests;

import com.bhft.todo.BaseTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class TestExample extends BaseTest {
    @Test
    void checkStatusCode() {
        given()
                .log().all()
        .when()
                .get("/todos")
        .then()
                .log().all()
                .statusCode(200)
                .body("todos.size()", equalTo(3));
    }
}
