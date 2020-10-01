package qa.automation.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static qa.automation.utils.JsonUtil.buildJsonObject;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PetStoreApiTest {
    private final Logger logger = LoggerFactory.getLogger(PetStoreApiTest.class);
    private static JSONObject petCreatedJson;
    private static JSONObject petUpdatedJson;

    @BeforeAll
    static void initialize() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        petCreatedJson = buildJsonObject("src/test/resources/rest/pet_created.json");
        petUpdatedJson = buildJsonObject("src/test/resources/rest/pet_updated.json");
    }

    @Test
    @Order(1)
    void testPetCreation() {
        given().log().uri()
                .contentType(ContentType.JSON)
                .body(petCreatedJson)
                .post("/pet")
                .then()
                .statusCode(200).extract().response()
                .then().body("name", equalTo(petCreatedJson.get("name")));
        logger.info("Pet entry with id " + petCreatedJson.get("id") + " and name " + petCreatedJson.get("name") + " successfully CREATED");
    }

    @Test
    @Order(2)
    void testPetReturnCreated() {
        given().log().uri()
                .get("/pet/" + petCreatedJson.get("id"))
                .then()
                .statusCode(200)
                .body("id", equalTo(((Long) petCreatedJson.get("id")).intValue()))
                .body("category.id", equalTo(((Long) (((JSONObject) petCreatedJson.get("category")).get("id"))).intValue()))
                .body("category.name", equalTo(((JSONObject) petCreatedJson.get("category")).get("name")))
                .body("name", equalTo(petCreatedJson.get("name")))
                .body("photoUrls", equalTo(petCreatedJson.get("photoUrls")))
                .body("status", equalTo(petCreatedJson.get("status")));
        logger.info("Correct info returned for CREATED pet with id " + petCreatedJson.get("id") + " and name " + petCreatedJson.get("name"));
    }

    @Test
    @Order(3)
    void testPetUpdate() {
        given().log().uri()
                .contentType(ContentType.JSON)
                .body(petUpdatedJson)
                .put("/pet")
                .then()
                .statusCode(200).extract().response()
                .then().body("status", equalTo(petUpdatedJson.get("status")));
        logger.info("Pet entry with id " + petUpdatedJson.get("id") + " and name " + petUpdatedJson.get("name") + " successfully UPDATED");
    }

    @Test
    @Order(4)
    void testPetReturnUpdated() {
        given().log().uri()
                .get("/pet/" + petUpdatedJson.get("id"))
                .then()
                .statusCode(200)
                .body("id", equalTo(((Long) petUpdatedJson.get("id")).intValue()))
                .body("category.id", equalTo(((Long) (((JSONObject) petUpdatedJson.get("category")).get("id"))).intValue()))
                .body("category.name", equalTo(((JSONObject) petUpdatedJson.get("category")).get("name")))
                .body("name", equalTo(petUpdatedJson.get("name")))
                .body("photoUrls", equalTo(petUpdatedJson.get("photoUrls")))
                .body("status", equalTo(petUpdatedJson.get("status")));
        logger.info("Correct info returned for UPDATED pet with id " + petUpdatedJson.get("id") + " and name " + petUpdatedJson.get("name"));
    }

    @Test
    @Order(5)
    void testPetDeletion() {
        given().log().uri()
                .contentType(ContentType.JSON)
                .delete("/pet/" + petUpdatedJson.get("id"))
                .then()
                .statusCode(200).extract().response()
                .then().body("message", equalTo((petUpdatedJson.get("id")).toString()));
        logger.info("Pet entry with id " + petUpdatedJson.get("id") + " and name " + petUpdatedJson.get("name") + " successfully DELETED");
    }

    @Test
    @Order(6)
    void testPetReturnDeleted() {
        given().log().uri()
                .get("/pet/" + petUpdatedJson.get("id"))
                .then()
                .statusCode(404)
                .body("message", equalTo("Pet not found"));
        logger.info("Correct info returned for DELETED pet with id " + petUpdatedJson.get("id") + " and name " + petUpdatedJson.get("name"));
    }
}