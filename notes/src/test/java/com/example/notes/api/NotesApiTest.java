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
 * Tests for Notes API endpoints.
 * Tests CRUD operations, filtering, search, and pagination.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotesApiTest extends BaseApiTest {

    private static Long createdNoteId;
    private static final String TEST_USERNAME = "notetestuser_" + UUID.randomUUID().toString().substring(0, 8);
    private static final String TEST_EMAIL = TEST_USERNAME + "@example.com";
    private static final String TEST_PASSWORD = "TestPassword123!";

    /**
     * Setup - create user and get auth token before running note tests.
     */
    @Test
    @Order(1)
    void setupUserAndAuth() {
        // Register user
        String registerBody = String.format("""
            {
                "username": "%s",
                "email": "%s",
                "password": "%s"
            }
            """, TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);

        String token = given()
                .spec(requestSpec)
                .body(registerBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract()
                .path("data.token");

        setAuthToken(token);
    }

    /**
     * Test creating a new note with valid data.
     * Expects successful creation with note details.
     */
    @Test
    @Order(2)
    void testCreateNote_Success() {
        String requestBody = """
            {
                "title": "Test Note Title",
                "content": "This is the content of the test note."
            }
            """;

        createdNoteId = withAuth()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/notes")
                .then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("data.id", notNullValue())
                .body("data.title", equalTo("Test Note Title"))
                .body("data.content", equalTo("This is the content of the test note."))
                .body("data.pinned", equalTo(false))
                .body("data.archived", equalTo(false))
                .body("data.trashed", equalTo(false))
                .extract()
                .path("data.id");
    }

    /**
     * Test creating a note without authentication.
     * Expects unauthorized error.
     */
    @Test
    @Order(3)
    void testCreateNote_Unauthenticated() {
        String requestBody = """
            {
                "title": "Unauthorized Note",
                "content": "This should fail"
            }
            """;

        withoutAuth()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/notes")
                .then()
                .statusCode(401);
    }

    /**
     * Test creating a note with empty title.
     * Expects validation error.
     */
    @Test
    @Order(4)
    void testCreateNote_EmptyTitle() {
        String requestBody = """
            {
                "title": "",
                "content": "Some content"
            }
            """;

        withAuth()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/notes")
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("error.code", equalTo("validation_error"));
    }

    /**
     * Test getting all notes for authenticated user.
     * Expects paginated list of notes.
     */
    @Test
    @Order(5)
    void testGetNotes_Success() {
        withAuth()
                .when()
                .get("/notes")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.content", is(notNullValue()))
                .body("data.number", notNullValue())
                .body("data.size", notNullValue())
                .body("data.totalElements", notNullValue())
                .body("data.totalPages", notNullValue());
    }

    /**
     * Test getting notes without authentication.
     * Expects unauthorized error.
     */
    @Test
    @Order(6)
    void testGetNotes_Unauthenticated() {
        withoutAuth()
                .when()
                .get("/notes")
                .then()
                .statusCode(401);
    }

    /**
     * Test getting a specific note by ID.
     * Expects note details.
     */
    @Test
    @Order(7)
    void testGetNoteById_Success() {
        withAuth()
                .when()
                .get("/notes/" + createdNoteId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.id", equalTo(createdNoteId.intValue()))
                .body("data.title", equalTo("Test Note Title"))
                .body("data.content", equalTo("This is the content of the test note."));
    }

    /**
     * Test getting a non-existent note.
     * Expects not found error.
     */
    @Test
    @Order(8)
    void testGetNoteById_NotFound() {
        withAuth()
                .when()
                .get("/notes/99999")
                .then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("error.code", equalTo("not_found"));
    }

    /**
     * Test updating an existing note.
     * Expects updated note details.
     */
    @Test
    @Order(9)
    void testUpdateNote_Success() {
        String requestBody = """
            {
                "title": "Updated Title",
                "content": "Updated content here"
            }
            """;

        withAuth()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .put("/notes/" + createdNoteId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.title", equalTo("Updated Title"))
                .body("data.content", equalTo("Updated content here"));
    }

    /**
     * Test updating note with pinning.
     * Expects pinned note.
     */
    @Test
    @Order(10)
    void testUpdateNote_Pin() {
        String requestBody = """
            {
                "pinned": true
            }
            """;

        withAuth()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .put("/notes/" + createdNoteId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.pinned", equalTo(true));
    }

    /**
     * Test updating a non-existent note.
     * Expects not found error.
     */
    @Test
    @Order(11)
    void testUpdateNote_NotFound() {
        String requestBody = """
            {
                "title": "Should Fail"
            }
            """;

        withAuth()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .put("/notes/99999")
                .then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("error.code", equalTo("not_found"));
    }

    /**
     * Test deleting an existing note.
     * Expects successful deletion.
     */
    @Test
    @Order(12)
    void testDeleteNote_Success() {
        withAuth()
                .when()
                .delete("/notes/" + createdNoteId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    /**
     * Test deleting a non-existent note.
     * Expects not found error.
     */
    @Test
    @Order(13)
    void testDeleteNote_NotFound() {
        withAuth()
                .when()
                .delete("/notes/99999")
                .then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("error.code", equalTo("not_found"));
    }

    /**
     * Test getting filtered notes - archived.
     * Expects filtered notes list.
     */
    @Test
    @Order(14)
    void testGetNotes_FilterByArchived() {
        withAuth()
                .queryParam("archived", true)
                .when()
                .get("/notes")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.content", is(notNullValue()));
    }

    /**
     * Test getting filtered notes - trashed.
     * Expects filtered notes list.
     */
    @Test
    @Order(15)
    void testGetNotes_FilterByTrashed() {
        withAuth()
                .queryParam("trashed", false)
                .when()
                .get("/notes")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.content", is(notNullValue()));
    }

    /**
     * Test getting filtered notes - pinned.
     * Expects filtered notes list.
     */
    @Test
    @Order(16)
    void testGetNotes_FilterByPinned() {
        withAuth()
                .queryParam("pinned", true)
                .when()
                .get("/notes")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.content", is(notNullValue()));
    }

    /**
     * Test search notes by query.
     * Expects matching notes.
     */
    @Test
    @Order(17)
    void testSearchNotes() {
        // First create a note with searchable content
        String requestBody = """
            {
                "title": "Searchable Note",
                "content": "This note is for searching"
            }
            """;

        withAuth()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/notes")
                .then()
                .statusCode(201);

        // Now search for it
        withAuth()
                .queryParam("q", "Searchable")
                .when()
                .get("/notes")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.content", is(notNullValue()));
    }

    /**
     * Test pagination with custom page size.
     * Expects paginated results.
     */
    @Test
    @Order(18)
    void testPagination() {
        withAuth()
                .queryParam("page", 0)
                .queryParam("size", 5)
                .when()
                .get("/notes")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.size", equalTo(5))
                .body("data.number", equalTo(0));
    }

    /**
     * Test note access control - trying to access another user's note.
     * Expects not found (security: don't leak existence).
     */
    @Test
    @Order(19)
    void testNoteAccessControl() {
        // Create another user and note
        String otherUserBody = """
            {
                "username": "otheruser123",
                "email": "other@example.com",
                "password": "Password123!"
            }
            """;

        String otherToken = given()
                .spec(requestSpec)
                .body(otherUserBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(200)
                .extract()
                .path("data.token");

        // Try to access first user's note with second user's token
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + otherToken)
                .when()
                .get("/notes/" + createdNoteId)
                .then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("error.code", equalTo("not_found"));
    }
}
