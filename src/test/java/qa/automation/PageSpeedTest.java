package qa.automation;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PageSpeedTest {
    static Map<String, Double> websiteScores;
    static Map<String, Integer> websiteErrors;

    @BeforeAll
    public static void initialize() {
        websiteScores = new HashMap<>();
        websiteErrors = new HashMap<>();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/rest/websites.csv")
    public void testWebsite(String website) {
        RestAssured.baseURI = "https://www.googleapis.com";
        Double performanceScore;
        Response response = given().log().uri().
                when().get("/pagespeedonline/v5/runPagespeed?url=" + website).
                then().extract().response();

        if (response.getStatusCode() == 200) {
            performanceScore = Double.valueOf(response.path("lighthouseResult.categories.performance.score").toString());
            websiteScores.put(website, performanceScore);
            assertThat(performanceScore, greaterThanOrEqualTo(0.9));
        } else {
            websiteErrors.put(website, response.getStatusCode());
        }
    }

    @AfterEach
    public void waitAfterTest() {
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void publishResults() {
        LinkedHashMap<String, Double> sortedWebsiteScores = new LinkedHashMap<>();
        websiteScores.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedWebsiteScores.put(x.getKey(), x.getValue()));
        int counter = 1;
        System.out.println("\n--- Websites with a performance score below 100% ---");
        for (Map.Entry<String, Double> entry : sortedWebsiteScores.entrySet()) {
            if (entry.getValue() < 1.0) {
                System.out.println(counter + ". " + entry.getKey() + " : " + (int) (entry.getValue() * 100) + "% score");
                counter++;
            }
        }
        if (counter == 1 && websiteScores.size() != 0)
            System.out.println("All websites had a score of 100%!");

        int counterErrors = 1;
        System.out.println("\n--- Websites with errors ---");
        for (Map.Entry<String, Integer> entry : websiteErrors.entrySet()) {
            System.out.println(counterErrors + ". " + entry.getKey() + " : " + entry.getValue() + " Status Code");
            counterErrors++;
        }
        if (counterErrors == 1)
            System.out.println("All websites returned a '200' status code!\n");
    }
}