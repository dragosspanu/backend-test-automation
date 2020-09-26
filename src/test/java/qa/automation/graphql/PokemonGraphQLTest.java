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

class PokemonGraphQLTest {
    private final Logger logger = LoggerFactory.getLogger(PokemonGraphQLTest.class);

    @ParameterizedTest
    @CsvFileSource(resources = "/graphql/pokemons.csv", numLinesToSkip = 1)
    void testGraphQL(String id, String pokemonName, String weight, String height) {
        RestAssured.baseURI = "https://pokeapi-graphiql.herokuapp.com";
        Map<String, String> variables = new HashMap<>();
        variables.put("number", id);
        String graphqlPayload = GraphqlUtil.prepareGraphqlPayload(variables, "src/test/resources/graphql/pokemon.graphql");
        given().log().body()
                .contentType(ContentType.JSON)
                .body(graphqlPayload)
                .post("/graphql")
                .then()
                .statusCode(200)
                .body("data.pokemon.name", equalTo(pokemonName))
                .body("data.pokemon.weight", equalTo(weight))
                .body("data.pokemon.height", equalTo(height));
        logger.info("GraphQL request successful for " + pokemonName);
    }
}
