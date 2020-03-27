package qa.automation;

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

public class PokemonGraphQLTest {
    private final Logger logger = LoggerFactory.getLogger(GetPokemonTest.class);

    @ParameterizedTest
    @CsvFileSource(resources = "/graphql/pokemons.csv", numLinesToSkip = 1)
    public void testGraphQL(String pokemonName) {
        RestAssured.baseURI = "https://graphql-pokemon.now.sh";
        Map<String, String> variables = new HashMap<>();
        variables.put("name", pokemonName);
        String graphqlPayload = GraphqlUtil.prepareGraphqlPayload(variables, "src/test/resources/graphql/pokemon.graphql");
        given().log().body()
                .contentType(ContentType.JSON)
                .body(graphqlPayload)
                .post("/graphql")
                .then()
                .statusCode(200)
                .body("data.pokemon.name", equalTo(pokemonName));
        logger.info("GraphQL request successful for " + pokemonName);
    }
}
