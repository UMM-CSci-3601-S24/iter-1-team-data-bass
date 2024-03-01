package umm3601.hunt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.json.JavalinJackson;
import org.bson.Document;
import com.mongodb.client.model.Filters;

class HuntControllerSpec {

  private HuntController huntController;

  private ObjectId batesId;

  private static MongoClient mongoClient;
  private static MongoDatabase db;

  private static JavalinJackson javalinJackson = new JavalinJackson();

  @Mock
  private Context ctx;

  // @Captor
  // private ArgumentCaptor<ArrayList<Hunt>> huntCaptor;

  @Captor
  private ArgumentCaptor<Hunt> huntCaptor;

  @Captor
  private ArgumentCaptor<Map<String, String>> mapCaptor;

  @BeforeAll
  static void setUpAll() {
    String mongoAddr = System.getenv().getOrDefault("MONGO_ADDR", "localhost");

    mongoClient = MongoClients.create(
      MongoClientSettings.builder()
        .applyToClusterSettings(builder ->
          builder.hosts(Arrays.asList(new ServerAddress(mongoAddr))))
        .build());
    db = mongoClient.getDatabase("test");
  }

  @AfterAll
  static void tearDownAll() {
    db.drop();
    mongoClient.close();
  }

  @BeforeEach
  void setUpEach() throws IOException {

    MockitoAnnotations.openMocks(this);
    MongoCollection<Document> huntDocuments = db.getCollection("hunts");
    huntDocuments.drop();
    List<Document> testHunts = new ArrayList<>();
        testHunts.add(
          new Document()
            .append("_id", "65d8f8d6384ab865a9acad5f")
            .append("name", "Tom Jones")
            .append("description", "A hunt for the Morris bear")
            .append("ownerId", "65d8f8d62b531fa812f0f498"));
        testHunts.add(
          new Document()
            .append("_id", "65d8f8d60d7e0dd1270982f2")
            .append("name", "Jim Jimson")
            .append("description", "A hunt for Atlantis")
            .append("ownerId", "65d8f8d6e1283e6b2acd7503"));
        testHunts.add(
          new Document()
            .append("_id", "65d8f8d631438ba61b3e7888")
            .append("name", "Wilby Wonka")
            .append("description", "A hunt for Nerd's Gummy Clusters")
            .append("ownerId", "65d8f8d690ea8ecf129e14c1"));

        batesId = new ObjectId();
        Document batesHunt = new Document()
          .append("_id", batesId)
          .append("name", "Bates")
          .append("description", "A hunt for the Bates rock")
          .append("ownerId", "1234");

        huntDocuments.insertMany(testHunts);
        huntDocuments.insertOne(batesHunt);

        huntController = new HuntController(db);
  }

  @Test
  void addsRoutes() {
    Javalin mockServer = mock(Javalin.class);
    huntController.addRoutes(mockServer);
    verify(mockServer, Mockito.atLeast(1)).get(any(), any());
    verify(mockServer, Mockito.atLeastOnce()).post(any(), any());
    verify(mockServer, Mockito.atLeastOnce()).delete(any(), any());
  }

  @Test
  void testCreateHunt() throws IOException {
    String testNewHunt = "{"
      + "\"name\": \"Bates\","
      + "\"description\": \"A hunt for the Bates rock\","
      + "\"ownerId\": \"1234\""
      + "}";

    ctx.body();
    huntController.createHunt(ctx);

    verify(ctx).status(201);
    verify(ctx).json(huntCaptor.capture());

    ObjectId id = huntCaptor.getValue()._id;
    String name = huntCaptor.getValue().name;
    String description = huntCaptor.getValue().description;
    String ownerId = huntCaptor.getValue().ownerId;

    assertEquals("Bates", name, "Name is wrong");
    assertEquals("A hunt for the Bates rock", description, "Description is wrong");
    assertEquals("1234", ownerId, "Owner ID is wrong");
  }

  @Test
  void testRemoveHunt() throws IOException {
    String testId = batesId.toHexString();
    when(ctx.pathParam("id")).thenReturn(testId);
    assertEquals(1, db.getCollection("hunts").countDocuments(Filters.eq("_id", new ObjectId(testId))));
    huntController.removeHunt(ctx);
    verify(ctx).status(200);
    assertEquals(0, db.getCollection("hunts").countDocuments(Filters.eq("_id", new ObjectId(testId))));
  }

}
