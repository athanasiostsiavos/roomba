
# Roomba Navigation Service

## Project Overview

This project implements a rest service that simulates a robotic hoover navigating through a room and cleans patches of dirt. The service gets input in JSON format, which contains:

- The dimensions of the room.
- The initial coordinates of the robot.
- The locations of dirt patches.
- A set of driving instructions (N, S, E, W) .

The service outputs the final coordinates of the hoover and the number of patches cleaned.

## Prerequisites

- **Java 11** or later must be installed. Actual this implementation uses Java 21
- **Maven** should be installed and available in your system's `PATH`.

## Installation Instructions

### 1. Clone the repository

Clone the project repository to your local machine using Git:

```bash
git clone https://github.com/athanasiostsiavos/roomba.git
cd roomba
```

### 2. Build the project

You will build the project using Maven.

```bash
mvn clean install
```

If you need to skip the tests during the build, use:

```bash
mvn clean install -DskipTests
```

### 3. Running the Spring Boot Application

Once the project is built, you can run the Spring Boot application directly from the command line:

```bash
mvn spring-boot:run
```

### 4. Running Tests

To run the unit tests included in the project, use:

```bash
mvn test
```

### 5. Sending Requests

Once the Spring Boot application is running, you can send POST requests to the service. The service will be available at `http://localhost:8080/navigate`.

You can test the service using `curl`:

```bash
curl -X POST http://localhost:8080/navigate   -H "Content-Type: application/json"   -d '{
    "roomSize": [5, 5],
    "coords": [1, 2],
    "patches": [[1, 0], [2, 2], [2, 3]],
    "instructions": "NNESEESWNWW"
  }'
```

### 6. Example Output

The service will return the final coordinates of the hoover and the number of patches it cleaned, for example:

```json
{
  "coords": [1, 3],
  "patches": 1
}
```

## Postman Collection

To easily test the API endpoints, import the provided Postman collection into your Postman workspace.

- File: `postman/RoombaService.postman_collection.json`
- Import the collection into Postman via `File > Import` and select the `.json` file.


### Core Classes

1. **RoombaApplication.java**: The entry point for the Spring Boot application.
2. **RoombaController.java**: Handles incoming HTTP requests and returns the final hoover position and number of patches cleaned.
3. **RoombaService.java**: Contains the business logic for calculating the hoover’s movement and tracking how many dirt patches it cleans.
4. **RoombaRequest.java**: Represents the incoming request payload with room size, hoover coordinates, dirt patches, and movement instructions.
5. **RoombaResponse.java**: Represents the response with the final hoover coordinates and number of cleaned patches.
6. **ErrorExceptionHandler.java**: Handles any errors that may occur during the processing of requests.


