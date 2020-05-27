package qa.automation;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("WrapperTypeMayBePrimitive")
public class LighthouseTest {
    static Map<String, Double> websiteScores;

    @BeforeAll
    public static void initialize() {
        websiteScores = new HashMap<>();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/rest/websites.csv")
    public void testWebsite(String website) {
        RestAssured.baseURI = "https://www.googleapis.com";
        Double performanceScore;
        try {
            performanceScore = Double.valueOf(given().log().uri().
                    when().get("/pagespeedonline/v5/runPagespeed?url=" + website).
                    then()
                    .extract()
                    .path("lighthouseResult.categories.performance.score").toString());
        } catch (NullPointerException e) {
            performanceScore = 0.0;
        }
        websiteScores.put(website, performanceScore);
        assertThat(performanceScore, greaterThanOrEqualTo(0.9));
    }

    @AfterAll
    public static void publishResults() {
        int counter = 0;
        System.out.println("\n\n--- RESULTS ---");
        for (Map.Entry<String, Double> entry : websiteScores.entrySet()) {
            if (entry.getValue() < 1.0) {
                System.out.println("This website had a performance score below 100% ---> " + entry.getKey() + " : " + (int) (entry.getValue() * 100) + "% score");
                counter++;
            }
        }
        if (counter == 0)
            System.out.println("All websites had a score of 100%!");
        System.out.println("\n");
    }
}