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
 * Tests for Tags API endpoints.
 * Tests tag CRUD operations and note-tag associations.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TagsApiTest extends BaseApiTest {

    private static Long createdTagId;
    private static Long testNoteId;
    private static String authToken;

    private static final String TEST_USERNAME = "tagtestuser_" + UUID.randomUUID().toString().substring(0, 8);
    private static final String TEST_EMAIL = TEST_USERNAME + "@example.com";
    private static final String TEST_PASSWORD = "TestPassword123!";

    /**
     * Setup - create user, auth token, and a test note.
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

        authToken = given()
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

        setAuthToken(authToken);

        // Create a test note
        String noteBody = """
            {
                "title": "Note for Tag Test",
                "content": "Testing tags"
            }
            """;

        testNoteId = withAuth()
                .body(noteBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/notes")
                .then()
                .statusCode(201)
                .extract()
                .path("data.id");
    }

    /**
     * Test creating a new tag.
     * Expects successful tag creation.
     */
    @Test
    @Order(2)
    void testCreateTag_Success() {
        String requestBody = """
            {
                "name": "Important"
            }
            """;

        createdTagId = withAuth()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/tags")
                .then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("data.id", notNullValue())
                .body("data.name", equalTo("Important"))
                .extract()
                .path("data.id");
    }

    /**
     * Test creating a tag without authentication.
     * Expects unauthorized error.
     */
    @Test
    @Order(3)
    void testCreateTag_Unauthenticated() {
        String requestBody = """
            {
                "name": "Should Fail"
            }
            """;

        withoutAuth()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/tags")
                .then()
                .statusCode(401);
    }

    /**
     * Test creating a duplicate tag (same name).
     * Expects conflict error.
     */
    @Test
    @Order(4)
    void testCreateTag_Duplicate() {
        String requestBody = """
            {
                "name": "Important"
            }
            """;

        withAuth()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/tags")
                .then()
                .statusCode(409)
                .body("success", equalTo(false))
                .body("error.code", equalTo("conflict"));
    }

    /**
     * Test getting all tags for authenticated user.
     * Expects list of tags.
     */
    @Test
    @Order(5)
    void testGetTags_Success() {
        withAuth()
                .when()
                .get("/tags")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", is(notNullValue()));
    }

    /**
     * Test getting tags without authentication.
     * Expects unauthorized error.
     */
    @Test
    @Order(6)
    void testGetTags_Unauthenticated() {
        withoutAuth()
                .when()
                .get("/tags")
                .then()
                .statusCode(401);
    }

    /**
     * Test getting a specific tag by ID.
     * Expects tag details.
     */
    @Test
    @Order(7)
    void testGetTagById_Success() {
        withAuth()
                .when()
                .get("/tags/" + createdTagId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.id", equalTo(createdTagId.intValue()))
                .body("data.name", equalTo("Important"));
    }

    /**
     * Test getting a non-existent tag.
     * Expects not found error.
     */
    @Test
    @Order(8)
    void testGetTagById_NotFound() {
        withAuth()
                .when()
                .get("/tags/99999")
                .then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("error.code", equalTo("not_found"));
    }

    /**
     * Test adding a tag to a note.
     * Expects note with tag association.
     */
    @Test
    @Order(9)
    void testAddTagToNote_Success() {
        withAuth()
                .when()
                .put("/notes/" + testNoteId + "/tags/" + createdTagId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.id", equalTo(testNoteId.intValue()));
    }

    /**
     * Test adding a tag to a trashed note.
     * Expects error - cannot tag trashed note.
     */
    @Test
    @Order(10)
    void testAddTagToNote_TrashedNote() {
        // First trash the note
        String trashBody = """
            {
                "trashed": true
            }
            """;

        withAuth()
                .body(trashBody)
                .contentType(ContentType.JSON)
                .when()
                .put("/notes/" + testNoteId)
                .then()
                .statusCode(200);

        // Now try to add tag - should fail
        String requestBody = """
            {
                "tagIds": [1]
            }
            """;

        withAuth()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .put("/notes/" + testNoteId + "/tags")
                .then()
                .statusCode(400)
                .body("success", equalTo(false));
    }

    /**
     * Test removing a tag from a note.
     * Expects note without tag.
     */
    @Test
    @Order(11)
    void testRemoveTagFromNote_Success() {
        // First restore the note
        String restoreBody = """
            {
                "trashed": false
            }
            """;

        withAuth()
                .body(restoreBody)
                .contentType(ContentType.JSON)
                .when()
                .put("/notes/" + testNoteId)
                .then()
                .statusCode(200);

        // Now remove tag
        withAuth()
                .when()
                .delete("/notes/" + testNoteId + "/tags/" + createdTagId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    /**
     * Test setting tags on a note (replace all).
     * Expects note with new tag set.
     */
    @Test
    @Order(12)
    void testSetTagsOnNote_Success() {
        // First create another tag
        String newTagBody = """
            {
                "name": "Work"
            }
            """;

        Long newTagId = withAuth()
                .body(newTagBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/tags")
                .then()
                .statusCode(201)
                .extract()
                .path("data.id");

        // Now set tags on note
        String requestBody = String.format("""
            {
                "tagIds": [%d, %d]
            }
            """, createdTagId, newTagId);

        withAuth()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .put("/notes/" + testNoteId + "/tags")
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    /**
     * Test getting notes by tag ID.
     * Expects filtered notes.
     */
    @Test
    @Order(13)
    void testGetNotesByTag() {
        withAuth()
                .queryParam("tagIds", createdTagId)
                .when()
                .get("/notes")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.content", is(notNullValue()));
    }

    /**
     * Test deleting a tag.
     * Expects successful deletion.
     */
    @Test
    @Order(14)
    void testDeleteTag_Success() {
        // Create a tag to delete
        String tagBody = """
            {
                "name": "ToBeDeleted"
            }
            """;

        Long tagToDelete = withAuth()
                .body(tagBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/tags")
                .then()
                .statusCode(201)
                .extract()
                .path("data.id");

        // Delete it
        withAuth()
                .when()
                .delete("/tags/" + tagToDelete)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    /**
     * Test deleting a non-existent tag.
     * Expects not found error.
     */
    @Test
    @Order(15)
    void testDeleteTag_NotFound() {
        withAuth()
                .when()
                .delete("/tags/99999")
                .then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("error.code", equalTo("not_found"));
    }

    /**
     * Test creating tag with empty name.
     * Expects validation error.
     */
    @Test
    @Order(16)
    void testCreateTag_EmptyName() {
        String requestBody = """
            {
                "name": ""
            }
            """;

        withAuth()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/tags")
                .then()
                .statusCode(400)
                .body("success", equalTo(false));
    }

    /**
     * Test tag access control - trying to access another user's tag.
     * Expects not found (security).
     */
    @Test
    @Order(17)
    void testTagAccessControl() {
        // Create another user
        String otherUserBody = """
            {
                "username": "othertagusertest",
                "email": "othertag@example.com",
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

        // Try to access first user's tag with second user's token
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + otherToken)
                .when()
                .get("/tags/" + createdTagId)
                .then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("error.code", equalTo("not_found"));
    }
}
