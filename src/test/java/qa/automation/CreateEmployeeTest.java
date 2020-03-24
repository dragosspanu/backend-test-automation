package qa.automation;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static qa.automation.utils.JsonUtil.buildJsonObject;

public class CreateEmployeeTest {
    private final Logger logger = LoggerFactory.getLogger(GetPokemonTest.class);

    @ParameterizedTest
    @CsvFileSource(resources = "/rest/employees.csv", numLinesToSkip = 1)
    public void testEmployeeCreation(int id, String location) {
        RestAssured.baseURI = "http://dummy.restapiexample.com/api/v1";
        JSONObject payload = buildJsonObject(location);
        given().log().uri()
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/create")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.name", equalTo(payload.get("name")))
                .body("data.salary", equalTo(payload.get("salary")))
                .body("data.age", equalTo(payload.get("age")));
        logger.info("Employee entry created for: " + payload.get("name"));
    }
}
