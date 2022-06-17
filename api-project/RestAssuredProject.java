import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

import static org.hamcrest.CoreMatchers.equalTo;

public class RestAssuredProject {

    private RequestSpecification requestSpecification;
    private String SSHPublicKey;
    private int id;

    @BeforeClass
    public void setup() {
       
        requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "token ghp_XXXXXXXXXXXXXXXXXXXXXXXXX")
                .setBaseUri("https://api.github.com").build();
    }

    @Test(priority = 1)
    public void addSSHKey() {
        
        File file = new File("src/test/java/addSSHKeyRequestBody.json");

        try {
            
            String requestBody = FileUtils.readFileToString(file, Charset.defaultCharset());

            System.out.println("request body: "+requestBody);

            Response response = given()
                                    .spec(requestSpecification)
                                    .body(requestBody).
                                when()
                                    .post("/user/keys");

            
            id = response.body().path("id");

            response.then()
                    .log().all()
                    .statusCode(equalTo(201));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 2)
    public void getSSHKey() {
      
        Response response = given()
                                .spec(requestSpecification).
                            when()
                                .get("/user/keys");

        
        response.then()
                .log().all()
                .statusCode(equalTo(200));

    }

    @Test(priority = 3)
    public void deleteSSHKey() {
        
        Response response = given()
                                .spec(requestSpecification).
                            when()
                                .pathParam("keyId", id)
                                .delete("/user/keys/{keyId}");

        response.then()
                .log().all()
                .statusCode(equalTo(204));
    }
}