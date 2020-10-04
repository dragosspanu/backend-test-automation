package qa.automation.rest;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
//@SelectPackages("qa.automation.rest")
@SelectClasses({PetStoreApiTest.class, GetPokemonTest.class})
public class RestApiTestSuite {
}