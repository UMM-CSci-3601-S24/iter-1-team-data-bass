package umm3601.hunt;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
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
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import io.javalin.json.JavalinJackson;
import io.javalin.validation.BodyValidator;
import io.javalin.validation.ValidationException;

/**
 * Tests the logic of the HuntController
 *
 * @throws IOException
 */
// The tests here include a ton of "magic numbers" (numeric constants).
// It wasn't clear to me that giving all of them names would actually
// help things. The fact that it wasn't obvious what to call some
// of them says a lot. Maybe what this ultimately means is that
// these tests can/should be restructured so the constants (there are
// also a lot of "magic strings" that Checkstyle doesn't actually
// flag as a problem) make more sense.
@SuppressWarnings({ "MagicNumber" })
class HuntControllerSpec {

  // An instance of the controller we're testing that is prepared in
  // `setupEach()`, and then exercised in the various tests below.
  private HuntController huntController;

  // A Mongo object ID that is initialized in `setupEach()` and used
  // in a few of the tests. It isn't used all that often, though,
  // which suggests that maybe we should extract the tests that
  // care about it into their own spec file?
  private ObjectId samsId;

  // The client and database that will be used
  // for all the tests in this spec file.
  private static MongoClient mongoClient;
  private static MongoDatabase db;

  // Used to translate between JSON and POJOs.
  private static JavalinJackson javalinJackson = new JavalinJackson();

  @Mock
  private Context ctx;

  @Captor
  private ArgumentCaptor<ArrayList<Hunt>> huntArrayListCaptor;

  @Captor
  private ArgumentCaptor<Hunt> huntCaptor;

  @Captor
  private ArgumentCaptor<Map<String, String>> mapCaptor;

  /**
   * Sets up (the connection to the) DB once; that connection and DB will
   * then be (re)used for all the tests, and closed in the `teardown()`
   * method. It's somewhat expensive to establish a connection to the
   * database, and there are usually limits to how many connections
   * a database will support at once. Limiting ourselves to a single
   * connection that will be shared across all the tests in this spec
   * file helps both speed things up and reduce the load on the DB
   * engine.
   */
  @BeforeAll
  static void setupAll() {
    String mongoAddr = System.getenv().getOrDefault("MONGO_ADDR", "localhost");

    mongoClient = MongoClients.create(
        MongoClientSettings.builder()
            .applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress(mongoAddr))))
            .build());
    db = mongoClient.getDatabase("test");
  }

  @AfterAll
  static void teardown() {
    db.drop();
    mongoClient.close();
  }

  @BeforeEach
  void setupEach() throws IOException {
    // Reset our mock context and argument captor (declared with Mockito
    // annotations @Mock and @Captor)
    MockitoAnnotations.openMocks(this);

    // Setup database
    MongoCollection<Document> huntDocuments = db.getCollection("hunts");
    huntDocuments.drop();
    List<Document> testHunts = new ArrayList<>();
    testHunts.add(
        new Document()
            .append("title", "Chris")
            .append("hostid", "Joe")
            .append("description", "UMM")
            .append("task", "chris@this.that")
            // .append("role", "admin")
            // .append("avatar", "https://gravatar.com/avatar/8c9616d6cc5de638ea6920fb5d65fc6c?d=identicon"
            );
    samsId = new ObjectId();
    Document sam = new Document()
        .append("_id", samsId)
        .append("title", "Sam")
        .append("hostid", "Joe")
        .append("description", "OHMNET")
        .append("task", "sam@frogs.com");
        // .append("role", "viewer")
        // .append("avatar", "https://gravatar.com/avatar/08b7610b558a4cbbd20ae99072801f4d?d=identicon"


    huntDocuments.insertMany(testHunts);
    huntDocuments.insertOne(sam);

    huntController = new HuntController(db);
  }

  @Test
  void addsRoutes() {
    Javalin mockServer = mock(Javalin.class);
    huntController.addRoutes(mockServer);
    verify(mockServer, Mockito.atLeast(2)).get(any(), any());
    verify(mockServer, Mockito.atLeastOnce()).post(any(), any());
    verify(mockServer, Mockito.atLeastOnce()).delete(any(), any());
  }

  @Test
  void canGetAllHunts() throws IOException {
    // When something asks the (mocked) context for the queryParamMap,
    // it will return an empty map (since there are no query params in
    // this case where we want all hunts).
    when(ctx.queryParamMap()).thenReturn(Collections.emptyMap());

    // Now, go ahead and ask the HuntController to getUsers
    // (which will, indeed, ask the context for its queryParamMap)
    huntController.getHunts(ctx);

    // We are going to capture an argument to a function, and the type of
    // that argument will be of type ArrayList<Hunt> (we said so earlier
    // using a Mockito annotation like this):
    // @Captor
    // private ArgumentCaptor<ArrayList<Hunt>> userArrayListCaptor;
    // We only want to declare that captor once and let the annotation
    // help us accomplish reassignment of the value for the captor
    // We reset the values of our annotated declarations using the command
    // `MockitoAnnotations.openMocks(this);` in our @BeforeEach

    // Specifically, we want to pay attention to the ArrayList<Hunt> that
    // is passed as input when ctx.json is called --- what is the argument
    // that was passed? We capture it and can refer to it later.
    verify(ctx).json(huntArrayListCaptor.capture());
    verify(ctx).status(HttpStatus.OK);

    // Check that the database collection holds the same number of documents
    // as the size of the captured List<Hunt>
    assertEquals(
        db.getCollection("hunts").countDocuments(),
        huntArrayListCaptor.getValue().size());
  }

  @Test
  void getHuntWithExistentId() throws IOException {
    String id = samsId.toHexString();
    when(ctx.pathParam("id")).thenReturn(id);

    huntController.getHunt(ctx);

    verify(ctx).json(huntCaptor.capture());
    verify(ctx).status(HttpStatus.OK);
    assertEquals("Sam", huntCaptor.getValue().title);
    assertEquals(samsId.toHexString(), huntCaptor.getValue()._id);
  }

  @Test
  void getHuntWithBadId() throws IOException {
    when(ctx.pathParam("id")).thenReturn("bad");

    Throwable exception = assertThrows(BadRequestResponse.class, () -> {
      huntController.getHunt(ctx);
    });

    assertEquals("The requested hunt id wasn't a legal Mongo Object ID.", exception.getMessage());
  }

  @Test
  void getHuntWithNonexistentId() throws IOException {
    String id = "588935f5c668650dc77df581";
    when(ctx.pathParam("id")).thenReturn(id);

    Throwable exception = assertThrows(NotFoundResponse.class, () -> {
      huntController.getHunt(ctx);
    });

    assertEquals("The requested hunt was not found", exception.getMessage());
  }

  @Test
  void addHunt() throws IOException {
    String testNewHunt = """
        {
          "title": "Test Hunt",
          "hostid": "25",
          "description": "testers",
          "task": "test@example.com"
        }
        """;
    when(ctx.bodyValidator(Hunt.class))
        .then(value -> new BodyValidator<Hunt>(testNewHunt, Hunt.class, javalinJackson));

    huntController.addNewHunt(ctx);
    verify(ctx).json(mapCaptor.capture());

    // Our status should be 201, i.e., our new Hunt was successfully created.
    verify(ctx).status(HttpStatus.CREATED);

    // Verify that the Hunt was added to the database with the correct ID
    Document addedHunt = db.getCollection("hunts")
        .find(eq("_id", new ObjectId(mapCaptor.getValue().get("id")))).first();

    // Successfully adding the Hunt should return the newly generated, non-empty
    // MongoDB ID for that Hunt.
    assertNotEquals("", addedHunt.get("_id"));
    assertEquals("Test Hunt", addedHunt.get("title"));
    assertEquals("25", addedHunt.get(HuntController.HOST_KEY));
    assertEquals("testers", addedHunt.get(HuntController.DESCRIPTION_KEY));
    // assertEquals("test@example.com", addedHunt.get("email"));
    assertEquals("test@example.com", addedHunt.get(HuntController.TASK_KEY));
    // assertNotNull(addedHunt.get("avatar"));
  }

  @Test
  void addNullTitleHunt() throws IOException {
    String testNewHunt = """
        {
          "hostid": 25,
          "description": "testers",
          "task": "test@example.com"
        }
        """;
    when(ctx.bodyValidator(Hunt.class))
        .then(value -> new BodyValidator<Hunt>(testNewHunt, Hunt.class, javalinJackson));

    assertThrows(ValidationException.class, () -> {
      huntController.addNewHunt(ctx);
    });
  }

  @Test
  void addInvalidTitleHunt() throws IOException {
    String testNewHunt = """
        {
          "title": "",
          "hostid": "25",
          "description": "testers",
          "task": "test@example.com"

        }
        """;
    when(ctx.bodyValidator(Hunt.class))
        .then(value -> new BodyValidator<Hunt>(testNewHunt, Hunt.class, javalinJackson));

    assertThrows(ValidationException.class, () -> {
      huntController.addNewHunt(ctx);
    });
  }

  @Test
  void addNullTaskHunt() throws IOException {
    String testNewHunt = """
        {
          "title": "Test Hunt",
          "hostid": 25,
          "description": "viewer"
        }
        """;
    when(ctx.bodyValidator(Hunt.class))
        .then(value -> new BodyValidator<Hunt>(testNewHunt, Hunt.class, javalinJackson));

    assertThrows(ValidationException.class, () -> {
      huntController.addNewHunt(ctx);
    });
  }


  @Test
  void addNullDescriptionHunt() throws IOException {
    String testNewHunt = """
        {
          "title": "Test Hunt",
          "hostid": 25,
          "task": "viewer"
        }
        """;
    when(ctx.bodyValidator(Hunt.class))
        .then(value -> new BodyValidator<Hunt>(testNewHunt, Hunt.class, javalinJackson));

    assertThrows(ValidationException.class, () -> {
      huntController.addNewHunt(ctx);
    });
  }

  @Test
  void addInvalidTaskHunt() throws IOException {
    String testNewHunt = """
        {
          "title": "joe",
          "hostid": "joe",
          "task": "",
          "description": "test@example.com"
        }
        """;
    when(ctx.bodyValidator(Hunt.class))
        .then(value -> new BodyValidator<Hunt>(testNewHunt, Hunt.class, javalinJackson));

    assertThrows(ValidationException.class, () -> {
      huntController.addNewHunt(ctx);
    });
  }

  @Test
  void deleteFoundHunt() throws IOException {
    String testID = samsId.toHexString();
    when(ctx.pathParam("id")).thenReturn(testID);

    // Hunt exists before deletion
    assertEquals(1, db.getCollection("hunts").countDocuments(eq("_id", new ObjectId(testID))));

    huntController.deleteHunt(ctx);

    verify(ctx).status(HttpStatus.OK);

    // Hunt is no longer in the database
    assertEquals(0, db.getCollection("hunts").countDocuments(eq("_id", new ObjectId(testID))));
  }

  @Test
  void tryToDeleteNotFoundHunt() throws IOException {
    String testID = samsId.toHexString();
    when(ctx.pathParam("id")).thenReturn(testID);

    huntController.deleteHunt(ctx);
    // Hunt is no longer in the database
    assertEquals(0, db.getCollection("hunts").countDocuments(eq("_id", new ObjectId(testID))));

    assertThrows(NotFoundResponse.class, () -> {
      huntController.deleteHunt(ctx);
    });

    verify(ctx).status(HttpStatus.NOT_FOUND);

    // Hunt is still not in the database
    assertEquals(0, db.getCollection("hunts").countDocuments(eq("_id", new ObjectId(testID))));
  }

  @Test
  void constructFilterWithHostKey() {
    when(ctx.queryParamMap()).thenReturn(Map.of(HuntController.HOST_KEY, List.of("testHost")));
    when(ctx.queryParam(HuntController.HOST_KEY)).thenReturn("testHost");
    Bson filter = huntController.constructFilter(ctx);
    assertNotNull(filter);
  }

  @Test
  void constructFilterWithTitleKey() {
    when(ctx.queryParamMap()).thenReturn(Map.of(HuntController.TITLE_KEY, List.of("testHost")));
    when(ctx.queryParam(HuntController.TITLE_KEY)).thenReturn("testHost");
    Bson filter = huntController.constructFilter(ctx);
    assertNotNull(filter);
  }

  @Test
  void constructFilterWithDescriptionKey() {
    when(ctx.queryParamMap()).thenReturn(Map.of(HuntController.DESCRIPTION_KEY, List.of("testHost")));
    when(ctx.queryParam(HuntController.DESCRIPTION_KEY)).thenReturn("testHost");
    Bson filter = huntController.constructFilter(ctx);
    assertNotNull(filter);
  }

  @Test
  void constructFilterWithTaskKey() {
    when(ctx.queryParamMap()).thenReturn(Map.of(HuntController.TASK_KEY, List.of("testHost")));
    when(ctx.queryParam(HuntController.TASK_KEY)).thenReturn("testHost");
    Bson filter = huntController.constructFilter(ctx);
    assertNotNull(filter);
  }
}
