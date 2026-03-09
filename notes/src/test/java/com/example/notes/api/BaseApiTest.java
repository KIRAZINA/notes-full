package com.example.notes.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;

/**
 * Base class for REST API tests using RestAssured.
 * Provides common configuration and helper methods.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public abstract class BaseApiTest {

    @LocalServerPort
    private int port;

    protected static String authToken;
    protected static Long testUserId;

    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    /**
     * Sets up RestAssured configuration before all tests.
     */
    @BeforeAll
    static void setupRestAssured() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /**
     * Sets up request and response specifications before each test.
     */
    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/api";
        RestAssured.port = port;

        // Build request specification with common settings
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        // Build response specification with common expectations
        responseSpec = new ResponseSpecBuilder()
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Helper method to set up authentication for subsequent requests.
     * 
     * @param token the JWT token to use for authentication
     */
    protected void setAuthToken(String token) {
        authToken = token;
    }

    /**
     * Helper method to get request specification with auth header.
     * 
     * @return RequestSpecification with Authorization header
     */
    protected RequestSpecification withAuth() {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + authToken);
    }

    /**
     * Helper method to get request specification without auth (for public endpoints).
     * 
     * @return RequestSpecification without Authorization header
     */
    protected RequestSpecification withoutAuth() {
        return given().spec(requestSpec);
    }
}
