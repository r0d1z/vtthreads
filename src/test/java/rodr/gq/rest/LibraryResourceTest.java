package rodr.gq.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isA;

@QuarkusTest
public class LibraryResourceTest {

    @Test
    public void testGetBookDetails() {
        given()
          .when().get("/library/book/12345")
          .then()
             .statusCode(200)
             .body("isbn", is("12345"))
             .body("title", is("The Java Virtual Machine"))
             .body("reviews.size()", greaterThan(0))
             .body("stock", greaterThan(-1))
             .body("price", isA(Number.class));
    }

    @Test
    public void testGetUnknownBookDetails() {
        given()
          .when().get("/library/book/99999")
          .then()
             .statusCode(200)
             .body("isbn", is("99999"))
             .body("title", is("Unknown"));
    }
}
