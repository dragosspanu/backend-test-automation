package qa.automation.pagespeedonline;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class PageSpeedTest {
    static Map<String, Double> websiteScores;
    private static final Logger logger = LoggerFactory.getLogger(PageSpeedTest.class);

    @BeforeAll
    static void initialize() {
        websiteScores = new HashMap<>();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/pagespeedonline/websites.csv")
    void testWebsite(String website) {
        RestAssured.baseURI = "https://www.googleapis.com";
        Double performanceScore;
        Response response = given().log().uri().
                when().get("/pagespeedonline/v5/runPagespeed?url=" + website).
                then().extract().response();
        assertThat(website, response.getStatusCode(), equalTo(200));
        performanceScore = Double.valueOf(response.path("lighthouseResult.categories.performance.score").toString());
        websiteScores.put(website, performanceScore);
        assertThat(performanceScore, greaterThanOrEqualTo(0.9));
    }

    @AfterEach
    void waitAfterTest() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void publishResults() {
        LinkedHashMap<String, Double> sortedWebsiteScores = new LinkedHashMap<>();
        websiteScores.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedWebsiteScores.put(x.getKey(), x.getValue()));
        for (Map.Entry<String, Double> entry : sortedWebsiteScores.entrySet())
            logger.info(entry.getKey() + " : " + (int) (entry.getValue() * 100) + "% score");
    }
}