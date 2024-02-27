package umm3601.task;

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
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinJackson;
import io.javalin.validation.BodyValidator;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

class TaskControllerSpec {

  private TaskController taskController;

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
  private ArgumentCaptor<ArrayList<Task>> taskArrayListCaptor;

  @Captor
  private ArgumentCaptor<Task> taskCaptor;

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
    MongoCollection<Document> taskDocuments = db.getCollection("tasks");
    taskDocuments.drop();
    List<Document> testTasks = new ArrayList<>();
    testTasks.add(
        new Document()
            .append("description", "Take a picture next to a tree.")
            .append("position", 2)
            .append("HuntId", "0256"));
    testTasks.add(
        new Document()
            .append("description", "Take a picture next to Dons.")
            .append("position", 1)
            .append("HuntId", "0256"));

    samsId = new ObjectId();
    Document samTask = new Document()
        .append("_id", samsId)
        .append("description", "Take a picture next to the science building.")
        .append("position", 3)
        .append("HuntId", "0256");

    taskDocuments.insertMany(testTasks);
    taskDocuments.insertOne(samTask);

    taskController = new TaskController(db);
  }

  @Test
  void addsRoutes() {
    Javalin mockServer = mock(Javalin.class);
    taskController.addRoutes(mockServer);
    verify(mockServer, Mockito.atLeast(3)).get(any(), any());
    verify(mockServer, Mockito.atLeastOnce()).post(any(), any());
    verify(mockServer, Mockito.atLeastOnce()).delete(any(), any());
  }

  @Test
  void addTask() throws IOException {
    String testNewTask = """
        {
          "description": "Test Task",
          "position": 25,
          "HuntId": "testers",
        }
        """;
    when(ctx.bodyValidator(Task.class))
        .then(value -> new BodyValidator<Task>(testNewTask, Task.class, javalinJackson));

    taskController.addNewTask(ctx);
    verify(ctx).json(mapCaptor.capture());

    // Our status should be 201, i.e., our new task was successfully created.
    verify(ctx).status(HttpStatus.CREATED);

    // Verify that the task was added to the database with the correct ID
    Document addedTask = db.getCollection("tasks")
        .find(eq("_id", new ObjectId(mapCaptor.getValue().get("id")))).first();

    // Successfully adding the task should return the newly generated, non-empty
    // MongoDB ID for that task.
    assertNotEquals("", addedTask.get("_id"));
    assertEquals("Test Task", addedTask.get("description"));
    assertEquals(25, addedTask.get(TaskController.POSITION_KEY));
    assertEquals("testers", addedTask.get(TaskController.HUNTID_KEY));
  }


  @Test
  void testRemoveTask() throws IOException {
    String testId = samsId.toHexString();
    when(ctx.pathParam("id")).thenReturn(testId);
    assertEquals(1, db.getCollection("tasks").countDocuments(eq("_id", new ObjectId(testId))));
    taskController.deleteTask(ctx);
    verify(ctx).status(HttpStatus.OK);
    assertEquals(0, db.getCollection("tasks").countDocuments(eq("_id", new ObjectId(testId))));
  }

/*   @Test
  void testMarkTaskAsDone() {
    // Arrange
    TaskController taskController = new TaskController(db);
    String taskId = "some-task-id"; // Replace with a valid task ID

    // Act
    boolean isMarkedDone = taskController.markTaskAsDone(taskId);

    // Assert
    assertTrue(isMarkedDone, "Task should be marked as done");
  }
*/

/*   @Test
  void testArrangeTasks() {
    // Arrange
    TaskController taskController = new TaskController(db);
    List<Task> tasks = new ArrayList<>(); // Assuming Task is a class representing a task
    // Add tasks to the list

    // Act
    List<Task> arrangedTasks = taskController.arrangeTasks(tasks);

    // Assert
    assertNotNull(arrangedTasks, "Tasks should be arranged");
    // Add more assertions to verify the order of arrangedTasks
  } */

/*
  @Test
  void testMarkTaskAsPartiallyDone() {
    // Arrange
    TaskController taskController = new TaskController(db);
    String taskId = "some-task-id"; // Replace with a valid task ID

    // Act
    boolean isMarkedPartiallyDone = taskController.markTaskAsPartiallyDone(taskId);

    // Assert
    assertTrue(isMarkedPartiallyDone, "Task should be marked as partially done");
  } */

  @Test
  void canGetAllTasks() throws IOException {
    // When something asks the (mocked) context for the queryParamMap,
    // it will return an empty map (since there are no query params in
    // this case where we want all tasks).
    when(ctx.queryParamMap()).thenReturn(Collections.emptyMap());

    // Now, go ahead and ask the taskController to getTasks
    // (which will, indeed, ask the context for its queryParamMap)
    taskController.getTasks(ctx);

    // We are going to capture an argument to a function, and the type of
    // that argument will be of type ArrayList<Task> (we said so earlier
    // using a Mockito annotation like this):
    // @Captor
    // private ArgumentCaptor<ArrayList<Task>> taskArrayListCaptor;
    // We only want to declare that captor once and let the annotation
    // help us accomplish reassignment of the value for the captor
    // We reset the values of our annotated declarations using the command
    // `MockitoAnnotations.openMocks(this);` in our @BeforeEach

    // Specifically, we want to pay attention to the ArrayList<Task> that
    // is passed as input when ctx.json is called --- what is the argument
    // that was passed? We capture it and can refer to it later.
    verify(ctx).json(taskArrayListCaptor.capture());
    verify(ctx).status(HttpStatus.OK);

    // Check that the database collection holds the same number of documents
    // as the size of the captured List<Task>
    assertEquals(
        db.getCollection("testTasks").countDocuments(),
        taskArrayListCaptor.getValue().size());
  }
}
