package qa.automation.rest;

import io.restassured.RestAssured;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class GetPokemonTest {
    private final Logger logger = LoggerFactory.getLogger(GetPokemonTest.class);

    @ParameterizedTest
    @CsvFileSource(resources = "/rest/pokemons.csv", numLinesToSkip = 1)
    void testPokemonJson(String id, String name, Integer baseExperience, String ability1, String ability2, Integer weight) {
        RestAssured.baseURI = "https://pokeapi.co";
        given().log().uri().
                when().get("/api/v2/pokemon/" + id + "/").
                then().
                statusCode(200).
                body("name", equalTo(name)).
                body("base_experience", equalTo(baseExperience)).
                body("abilities[0].ability.name", equalTo(ability1)).
                body("abilities[1].ability.name", equalTo(ability2)).
                body("weight", equalTo(weight));
        logger.info("Correct info for " + name);
    }
}

