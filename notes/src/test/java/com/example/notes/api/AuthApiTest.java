package com.example.notes.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests for Authentication API endpoints.
 * Tests registration, login, and getting current user info.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthApiTest extends BaseApiTest {

    private static final String TEST_USERNAME = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
    private static final String TEST_EMAIL = TEST_USERNAME + "@example.com";
    private static final String TEST_PASSWORD = "TestPassword123!";

    /**
     * Test user registration with valid credentials.
     * Expects successful registration and JWT token in response.
     */
    @Test
    @Order(1)
    void testRegister_Success() {
        String requestBody = String.format("""
            {
                "username": "%s",
                "email": "%s",
                "password": "%s"
            }
            """, TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);

        given()
                .spec(requestSpec)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.token", notNullValue())
                .body("data.token", not(empty()))
                .time(lessThan(5000L)); // Response time assertion
    }

    /**
     * Test user registration with duplicate username.
     * Expects conflict error response.
     */
    @Test
    @Order(2)
    void testRegister_DuplicateUsername() {
        String requestBody = String.format("""
            {
                "username": "%s",
                "email": "another@example.com",
                "password": "Password123!"
            }
            """, TEST_USERNAME);

        given()
                .spec(requestSpec)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(409)
                .body("success", equalTo(false))
                .body("error.code", equalTo("conflict"));
    }

    /**
     * Test user login with valid credentials.
     * Expects successful login and JWT token.
     */
    @Test
    @Order(3)
    void testLogin_Success() {
        String requestBody = String.format("""
            {
                "username": "%s",
                "password": "%s"
            }
            """, TEST_USERNAME, TEST_PASSWORD);

        String token = given()
                .spec(requestSpec)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.token", notNullValue())
                .extract()
                .path("data.token");

        // Store token for subsequent tests
        setAuthToken(token);
    }

    /**
     * Test user login with invalid password.
     * Expects unauthorized error.
     */
    @Test
    @Order(4)
    void testLogin_InvalidPassword() {
        String requestBody = String.format("""
            {
                "username": "%s",
                "password": "WrongPassword123!"
            }
            """, TEST_USERNAME);

        given()
                .spec(requestSpec)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("error.code", equalTo("invalid_credentials"));
    }

    /**
     * Test user login with non-existent username.
     * Expects unauthorized error.
     */
    @Test
    @Order(5)
    void testLogin_UserNotFound() {
        String requestBody = """
            {
                "username": "nonexistentuser12345",
                "password": "SomePassword123!"
            }
            """;

        given()
                .spec(requestSpec)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("error.code", equalTo("invalid_credentials"));
    }

    /**
     * Test getting current authenticated user info.
     * Expects user details with correct authentication.
     */
    @Test
    @Order(6)
    void testGetCurrentUser_Success() {
        withAuth()
                .when()
                .get("/auth/me")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.username", equalTo(TEST_USERNAME))
                .body("data.email", equalTo(TEST_EMAIL))
                .body("data.id", notNullValue());
    }

    /**
     * Test getting current user info without authentication.
     * Expects unauthorized error.
     */
    @Test
    @Order(7)
    void testGetCurrentUser_Unauthenticated() {
        withoutAuth()
                .when()
                .get("/auth/me")
                .then()
                .statusCode(401);
    }

    /**
     * Test registration with invalid email format.
     * Expects validation error.
     */
    @Test
    @Order(8)
    void testRegister_InvalidEmail() {
        String requestBody = """
            {
                "username": "newuser123",
                "email": "not-an-email",
                "password": "Password123!"
            }
            """;

        given()
                .spec(requestSpec)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("error.code", equalTo("validation_error"));
    }

    /**
     * Test registration with short password.
     * Expects validation error.
     */
    @Test
    @Order(9)
    void testRegister_ShortPassword() {
        String requestBody = """
            {
                "username": "newuser456",
                "email": "newuser@example.com",
                "password": "123"
            }
            """;

        given()
                .spec(requestSpec)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("error.code", equalTo("validation_error"));
    }

    /**
     * Test account lockout after multiple failed login attempts.
     * Expects 429 Too Many Requests after exceeding limit.
     */
    @Test
    @Order(10)
    void testLogin_AccountLocked() {
        String requestBody = """
            {
                "username": "locktestuser",
                "password": "WrongPassword"
            }
            """;

        // Attempt multiple failed logins to trigger lockout
        for (int i = 0; i < 6; i++) {
            given()
                    .spec(requestSpec)
                    .body(requestBody)
                    .contentType(ContentType.JSON)
                    .when()
                    .post("/auth/login");
        }

        // Next attempt should be locked
        given()
                .spec(requestSpec)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(429)
                .body("success", equalTo(false))
                .body("error.code", equalTo("account_locked"));
    }
}
