package umm3601.task;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.mongojack.JacksonMongoCollection;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import umm3601.Controller;

public class TaskController implements Controller {

  private static final String API_TASKS = "/api/tasks";
  private static final String API_TASK_BY_ID = "/api/tasks/{id}";
  static final String DESCRIPTION_KEY = "description";
  static final String POSITION_KEY = "position";
  static final String HUNTID_KEY = "huntId";

  private final JacksonMongoCollection<Task> taskCollection;

  /**
   * Construct a controller for tasks.
   *
   * @param database the database containing task data
   */
  public TaskController(MongoDatabase database) {
    taskCollection = JacksonMongoCollection.builder().build(
        database,
        "task",
        Task.class,
        UuidRepresentation.STANDARD);
  }

  /**
   * Set the JSON body of the response to be a list of all the tasks returned from the database
   * that match any requested filters and ordering
   *
   * @param ctx a Javalin HTTP context
   */
  public void getTasks(Context ctx) {
    System.err.println(ctx);
    System.err.println(Task.class);

    // Explicitly set the context status to OK
    ctx.status(HttpStatus.OK);
  }

  void addNewTask(Context ctx) {
    Task newTask = ctx.bodyValidator(Task.class)
      .check((Task task) -> task.description != null && task.description.length() > 0, "Description must not be null or empty")
      .check((Task task) -> task.position >= 0, "Position must be greater than or equal to 0")
      .check((Task task) -> task.Huntid != null && task.Huntid.length() > 0, "HuntId must not be null or empty")
      .get();

    taskCollection.insertOne(newTask);

    ctx.json(Map.of("id", newTask._id));

    ctx.status(HttpStatus.CREATED);
  }

public boolean markTaskAsDone(@NotNull String taskId) {
  Task task = taskCollection.find(eq("_id", new ObjectId(taskId))).first();
  if (task == null) {
    throw new NotFoundResponse("The requested task was not found");
  }
  task.isDone = true;
  taskCollection.replaceOne(eq("_id", new ObjectId(taskId)), task);
  return true;
}

  public List<Task> arrangeTasks(List<Task> tasks) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'arrangeTasks'");
  }

  public boolean markTaskAsPartiallyDone(String taskId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'markTaskAsPartiallyDone'");
  }

  /**
   * Setup routes for the `task` collection endpoints.
   *
   * These endpoints are:
   * - `GET /api/tasks/:id`
   * - Get the specified task
   * - `GET /api/tasks?age=NUMBER&company=STRING&name=STRING`
   * - List tasks, filtered using query parameters
   * - `age`, `company`, and `name` are optional query parameters
   * - `GET /api/tasksByCompany`
   * - Get task names and IDs, possibly filtered, grouped by company
   * - `DELETE /api/tasks/:id`
   * - Delete the specified task
   * - `POST /api/tasks`
   * - Create a new task
   * - The task info is in the JSON body of the HTTP request
   *
   * GROUPS SHOULD CREATE THEIR OWN CONTROLLERS THAT IMPLEMENT THE
   * `Controller` INTERFACE FOR WHATEVER DATA THEY'RE WORKING WITH.
   * You'll then implement the `addRoutes` method for that controller,
   * which will set up the routes for that data. The `Server#setupRoutes`
   * method will then call `addRoutes` for each controller, which will
   * add the routes for that controller's data.
   *
   * @param server         The Javalin server instance
   * @param taskController The controller that handles the task endpoints
   */

  public void addRoutes(Javalin server) {
    // Get the specified task
    server.get(API_TASK_BY_ID, this::getTask);

    // Add new task with the task info being in the JSON body
    // of the HTTP request
    server.put(API_TASKS, ctx -> addNewTask(ctx));

    // Delete the specified task
    server.delete(API_TASK_BY_ID, ctx -> deleteTask(ctx));
  }

  public void deleteTask(Context ctx) {
    String id = ctx.pathParam("id");
    DeleteResult deleteResult = taskCollection.deleteOne(eq("_id", new ObjectId(id)));
    if (deleteResult.getDeletedCount() != 1) {
      ctx.status(HttpStatus.NOT_FOUND);
      throw new NotFoundResponse(
        "Was unable to find this task");
    }
    ctx.status(HttpStatus.OK);
  }
}
