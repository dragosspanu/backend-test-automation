package qa.automation.graphql;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.automation.utils.GraphqlUtil;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class CountryGraphQLTest {
    private final Logger logger = LoggerFactory.getLogger(CountryGraphQLTest.class);

    @ParameterizedTest
    @CsvFileSource(resources = "/graphql/countries.csv", numLinesToSkip = 1)
    void testGraphQL(String code, String name, String capital, String continent) {
        RestAssured.baseURI = "https://countries.trevorblades.com";
        Map<String, String> variables = new HashMap<>();
        variables.put("code", code);
        String graphqlPayload = GraphqlUtil.prepareGraphqlPayload(variables, "src/test/resources/graphql/countries.graphql");
        given().log().body()
                .contentType(ContentType.JSON)
                .body(graphqlPayload)
                .post("/graphql")
                .then()
                .statusCode(200)
                .body("data.country.name", equalTo(name))
                .body("data.country.capital", equalTo(capital))
                .body("data.country.continent.name", equalTo(continent));
        logger.info("GraphQL request successful for " + code);
    }
}
