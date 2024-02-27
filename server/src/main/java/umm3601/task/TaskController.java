package umm3601.task;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.mongojack.JacksonMongoCollection;

import com.mongodb.client.MongoDatabase;

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

  public void addNewTask(Context ctx) {
    Task newTask = ctx.bodyValidator(Task.class)
      .check(usr -> usr.description != null
      && usr.description.length() > 0, "Task must have a non-empty task descriptions")
      .check(usr -> usr.position >= 0, "Task's position must be greater than or equal to zero")
      .check(usr -> usr.HuntId != null && usr.HuntId.length() > 0, "Task must have a non-empty Huntid")
      .get();

    // Add the new task to the database
    taskCollection.insertOne(newTask);

    // Set the JSON response to be the `_id` of the newly created task.
    // This gives the client the opportunity to know the ID of the new task,
    // which it can then use to perform further operations (e.g., a GET request
    // to get and display the details of the new task).
    ctx.json(Map.of("id", newTask._id));
    // 201 (`HttpStatus.CREATED`) is the HTTP code for when we successfully
    // create a new resource (a task in this case).
    // See, e.g., https://developer.mozilla.org/en-US/docs/Web/HTTP/Status
    // for a description of the various response codes.
    ctx.status(HttpStatus.CREATED);
  }

/* public boolean markTaskAsDone(@NotNull String taskId) {
  Task task = taskCollection.find(eq("_id", new ObjectId(taskId))).first();
  if (task == null) {
    throw new NotFoundResponse("The requested task was not found");
  }
  task.isDone = true;
  taskCollection.replaceOne(eq("_id", new ObjectId(taskId)), task);
  return true;
} */

  public List<Task> arrangeTasks(List<Task> tasks) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'arrangeTasks'");
  }

/*   public boolean markTaskAsPartiallyDone(String taskId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'markTaskAsPartiallyDone'");
  } */

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

  /**
   * Set the JSON body of the response to be the single task
   * specified by the `id` parameter in the request
   *
   * @param ctx a Javalin HTTP context
   */
  public void getTask(Context ctx) {
    String id = ctx.pathParam("id");
    Task task;

    try {
      task = taskCollection.find(eq("_id", new ObjectId(id))).first();
    } catch (IllegalArgumentException e) {
      throw new BadRequestResponse("The requested task id wasn't a legal Mongo Object ID.");
    }
    if (task == null) {
      throw new NotFoundResponse("The requested task was not found");
    } else {
      ctx.json(task);
      ctx.status(HttpStatus.OK);
    }
  }
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
    Task task = taskCollection.find(eq("_id", new ObjectId(id))).first();
    if (task == null) {
      ctx.status(HttpStatus.NOT_FOUND);
      throw new NotFoundResponse("Task not found");
    }
    taskCollection.findOneAndDelete(eq("_id", new ObjectId(id)));
    ctx.status(HttpStatus.OK);
  }
    /**
   * Set the JSON body of the response to be a list of all the tasks returned from the database
   * that match any requested filters and ordering
   *
   * @param ctx a Javalin HTTP context
   */
  public void getTasks(Context ctx) {
    Bson combinedFilter = constructFilter(ctx);
    Bson sortingOrder = constructSortingOrder(ctx);

    // All three of the find, sort, and into steps happen "in parallel" inside the
    // database system. So MongoDB is going to find the tasks with the specified
    // properties, return those sorted in the specified manner, and put the
    // results into an initially empty ArrayList.
    ArrayList<Task> matchingTasks = taskCollection
      .find(combinedFilter)
      .sort(sortingOrder)
      .into(new ArrayList<>());

    // Set the JSON body of the response to be the list of tasks returned by the database.
    // According to the Javalin documentation (https://javalin.io/documentation#context),
    // this calls result(jsonString), and also sets content type to json
    ctx.json(matchingTasks);

    // Explicitly set the context status to OK
    ctx.status(HttpStatus.OK);
  }
    private Bson constructSortingOrder(Context ctx) {
      // order by the Position key
      return eq(POSITION_KEY, 1);
    }

    private Bson constructFilter(Context ctx) {
      List<Bson> filters = new ArrayList<Bson>();

      if (ctx.queryParamMap().containsKey(POSITION_KEY)) {
        filters.add(eq(POSITION_KEY, ctx.queryParam(POSITION_KEY)));
      }

      if (ctx.queryParamMap().containsKey(HUNTID_KEY)) {
        filters.add(eq(HUNTID_KEY, ctx.queryParam(HUNTID_KEY)));
      }

      return filters.isEmpty() ? new BsonDocument() : new BsonDocument();
    }
}
