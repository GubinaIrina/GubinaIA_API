package org.example.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.example.model.Order;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class HomeTaskApiTest {


    @BeforeClass
    public void prepare() throws IOException {

        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/store/")
                .addHeader("api_key", System.getProperty("api.key"))
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
        RestAssured.filters(new ResponseLoggingFilter());
    }

    @Test
    public void checkObjectSave() {
        Order order = new Order();
        int id = new Random().nextInt(500000);
        order.setId(id);
        System.getProperties().put("id", order.getId());

        given()
                .body(order)
                .post("/order")
                .then()
                .statusCode(200);

        Order actual =
                given()
                        .pathParam("id", order.getId())
                        .when()
                        .get("/order/{id}")
                        .then()
                        .statusCode(200)
                        .extract().body()
                        .as(Order.class);
        Assert.assertEquals(actual.getId(), order.getId());
    }

    @Test
    public void orderDelete() throws IOException {
        Order order = new Order();
        int id = new Random().nextInt(500000);
        order.setId(id);

        given()
                .body(order)
                .post("/order")
                .then()
                .statusCode(200);

        given()
                .pathParam("id", order.getId())
                .when()
                .delete("/order/{id}")
                .then()
                .statusCode(200);

        given()
                .pathParam("id", order.getId())
                .when()
                .get("/order/{id}")
                .then()
                .statusCode(404);
    }
}
