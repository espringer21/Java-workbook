package dmit2015.resource;

import dmit2015.restclient.BeerItem;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://github.com/rest-assured/rest-assured
 * https://github.com/rest-assured/rest-assured/wiki/Usage
 * http://www.mastertheboss.com/jboss-frameworks/resteasy/restassured-tutorial
 * https://eclipse-ee4j.github.io/jsonb-api/docs/user-guide.html
 * https://github.com/FasterXML/jackson-databind
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeerItemResourceRestAssuredIT {

    String todoResourceUrl = "http://localhost:8090/restapi/Beer";
    String testDataResourceLocation;

    @Order(1)
    @ParameterizedTest
    @CsvSource(value = {
            "Rocky Mountain Red,Alberta Amber Ale,Alberta Craft Breweries"
    })
    void shouldFindAll(String name,String style,String brand) {
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get(todoResourceUrl)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();
        String jsonBody = response.getBody().asString();

        Jsonb jsonb = JsonbBuilder.create();
        List<BeerItem> queryResultList = jsonb.fromJson(jsonBody, new ArrayList<BeerItem>(){}.getClass().getGenericSuperclass());

        BeerItem firstBeerItem = queryResultList.get(0);
        assertThat(firstBeerItem.getName())
                .isEqualTo(name);
        assertThat(firstBeerItem.getStyle())
                .isEqualTo(style);
        assertThat(firstBeerItem.getBrand()).isEqualTo(brand);

    }

    @Order(2)
    @ParameterizedTest
    @CsvSource(value = {
            "Big Blue Bear,BC Amber,BC Craft"
    })
    void shouldCreate(String name, String style, String brand) {
        BeerItem newBeerItem = new BeerItem();
        newBeerItem.setName(name);
        newBeerItem.setStyle(style);
        newBeerItem.setBrand(brand);

        // Jsonb jsonb = JsonbBuilder.create();
        // String jsonBody = jsonb.toJson(newTodoItem);

        Response response = given()
                .contentType(ContentType.JSON)
                // .body(jsonBody)
                .body(newBeerItem)
                .when()
                .post(todoResourceUrl)
                .then()
                .statusCode(201)
                .extract()
                .response();
        testDataResourceLocation = response.getHeader("location");
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource(value = {
            "Big Blue Bear,BC Amber,BC Craft"
    })
    void shouldFineOne(String name, String style, String brand) {
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get(testDataResourceLocation)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();
        String jsonBody = response.getBody().asString();

        Jsonb jsonb = JsonbBuilder.create();
        BeerItem existingBeerItem = jsonb.fromJson(jsonBody, BeerItem.class);

        assertThat(existingBeerItem.getName())
                .isEqualTo(name);
        assertThat(existingBeerItem.getStyle())
                .isEqualTo(style);
        assertThat(existingBeerItem.getBrand())
                .isEqualTo(brand);
    }

    @Order(4)
    @ParameterizedTest
    @CsvSource(value = {
            "Small Green Bear,YK Amber,YK Craft"
    })
    void shouldUpdate(String name, String style, String brand) {
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get(testDataResourceLocation)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();
        String jsonBody = response.getBody().asString();

        Jsonb jsonb = JsonbBuilder.create();
        BeerItem existingBeerItem = jsonb.fromJson(jsonBody, BeerItem.class);

        existingBeerItem.setName(name);
        existingBeerItem.setStyle(style);
        existingBeerItem.setBrand(brand);

        String jsonTodoItem = jsonb.toJson(existingBeerItem);

        Response putResponse = given()
                .contentType(ContentType.JSON)
                .body(jsonTodoItem)
                .when()
                .put(testDataResourceLocation)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String putResponseJsonBody = putResponse.getBody().asString();
        BeerItem updatedBeerItem = jsonb.fromJson(putResponseJsonBody, BeerItem.class);

        assertThat(existingBeerItem)
                .usingRecursiveComparison()
//                .ignoringFields("updateTime")
                .isEqualTo(updatedBeerItem);
    }

    @Order(5)
    @Test
    void shouldDelete() {
        given()
                .when()
                .delete(testDataResourceLocation)
                .then()
                .statusCode(204);
    }

}