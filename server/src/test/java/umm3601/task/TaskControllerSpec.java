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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

  // testing adding a new task.
  @Test
  void addTask() throws IOException {
    String testNewTask = """
        {
          "description": "test description",
          "position": 1,
          "HuntId": testHuntId
        }
        """;
    when(ctx.bodyValidator(Task.class))
        .then(value -> new BodyValidator<>(testNewTask, Task.class, javalinJackson));

    taskController.addNewTask(ctx);
    verify(ctx).json(mapCaptor.capture());
    verify(ctx).status(HttpStatus.CREATED);

    Document addedTask = db.getCollection("task")
        .find(eq("description", "test description")).first();

    assertNotEquals("", addedTask.get("_id"));
    assertEquals("description", addedTask.get("test description"));
    assertEquals("position", addedTask.get("1"));
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

  @Test
  void testMarkTaskAsDone() {
    // Arrange
    TaskController taskController = new TaskController(null);
    String taskId = "some-task-id"; // Replace with a valid task ID

    // Act
    boolean isMarkedDone = taskController.markTaskAsDone(taskId);

    // Assert
    assertTrue(isMarkedDone, "Task should be marked as done");
  }

  @Test
  void testArrangeTasks() {
    // Arrange
    TaskController taskController = new TaskController(null);
    List<Task> tasks = new ArrayList<>(); // Assuming Task is a class representing a task
    // Add tasks to the list

    // Act
    List<Task> arrangedTasks = taskController.arrangeTasks(tasks);

    // Assert
    assertNotNull(arrangedTasks, "Tasks should be arranged");
    // Add more assertions to verify the order of arrangedTasks
  }

  @Test
  void testMarkTaskAsPartiallyDone() {
    // Arrange
    TaskController taskController = new TaskController(null);
    String taskId = "some-task-id"; // Replace with a valid task ID

    // Act
    boolean isMarkedPartiallyDone = taskController.markTaskAsPartiallyDone(taskId);

    // Assert
    assertTrue(isMarkedPartiallyDone, "Task should be marked as partially done");
  }

  @Test
  void testGetTask() {
    // Arrange
    TaskController taskController = new TaskController(null);
    String taskId = "some-task-id"; // Replace with a valid task ID

    // Act
    Task task = taskController.getTask(ctx);

    // Assert
    assertNotNull(task, "Task should not be null");
    assertEquals(taskId, task.getId(), "Task ID should match the requested ID");
  }
}
