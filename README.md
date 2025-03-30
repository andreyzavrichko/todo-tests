# Task Overview

The attached image provides a Dockerized application for a straightforward TODO manager supporting CRUD operations. The image can be loaded with Docker and executed as specified.

The task is divided into two main objectives:

1. **Testing**: You are required to write tests to verify the functionality of the application. The application's domain is simple, so you should create test cases based on your understanding. For each route, develop up to five test cases. Any additional test cases that you think of should be listed as suggestions for future testing efforts.

2. **Performance Evaluation**: Focus on the performance of the `POST /todos` endpoint. A detailed graph is not necessary, but you should provide performance measurements and a summary of your findings.

Consider running the application with the environment variable `VERBOSE=1` to enable detailed logging, which could aid in testing (`docker run -e VERBOSE=1`).

## Endpoints Description

The application revolves around managing TODO items, each represented by the following structure:
- `id`: An unsigned 64-bit identifier.
- `text`: Description of the TODO.
- `completed`: Boolean indicating whether the TODO is completed.

### Endpoint Details

- **GET /todos**: Retrieves a JSON list of TODOs.
    - Query parameters:
        - `offset`: Number of TODOs to skip.
        - `limit`: Maximum number of TODOs to return.

- **POST /todos**: Creates a new TODO. This endpoint expects a complete TODO structure in the request body.

- **PUT /todos/:id**: Updates an existing TODO with the provided details.

- **DELETE /todos/:id**: Removes a specified TODO. Requires an `Authorization` header with `Basic` auth credentials (`admin:admin`).

- **/ws**: Establishes a WebSocket connection on port 4242 at the path `/ws` to receive updates about new TODOs.
