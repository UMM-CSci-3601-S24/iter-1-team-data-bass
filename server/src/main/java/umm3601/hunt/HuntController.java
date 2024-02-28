package umm3601.hunt;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.mongojack.JacksonMongoCollection;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import umm3601.Controller;

public class HuntController implements Controller {

  private static final String API_HUNTS = "/api/hunts";
  private static final String API_HUNTS_BY_ID = "/api/hunts/{id}";

  static final String HOST_KEY = "hostid";
  static final String TITLE_KEY = "title";
  static final String DESCRIPTION_KEY = "description";
  static final String SORT_ORDER_KEY = "sortorder";

  private final JacksonMongoCollection<Hunt> huntCollection;

 /**
   * Construct a controller for hunts.
   *
   * @param database the database containing hunt data
   */

public HuntController(MongoDatabase database) {
  huntCollection = JacksonMongoCollection.builder().build(
  database,
  "hunts",
  Hunt.class,
  UuidRepresentation.STANDARD);
}

  /**
   * Set the JSON body of the response to be the single hunt
   * specified by the `id` parameter in the request
   *
   * @param ctx a Javalin HTTP context
   */

public void getHunt(Context ctx) {
  String id = ctx.pathParam("id");
  Hunt hunt;
  try {
    hunt = huntCollection.find(eq("_id", new ObjectId(id))).first();

  } catch (IllegalArgumentException e) {
    throw new BadRequestResponse("The requested hunt id wasn't a legal Mongo Object ID.");
  }
  if (hunt == null) {
    throw new NotFoundResponse("The requested hunt was not found");
  } else {
    ctx.json(hunt);
    ctx.status(HttpStatus.OK);
  }
}

 /**
   * Set the JSON body of the response to be a list of all the hunts returned from the database
   * that match any requested filters and ordering
   *
   * @param ctx a Javalin HTTP context
   */

public void getHunts(Context ctx) {
  Bson combinedFilter = constructFilter(ctx); // Not sure if needed.
  Bson sortingOrder = constructSortingOrder(ctx);

  // All three of the find, sort, and into steps happen "in parallel" inside the
  // database system. So MongoDB is going to find the hunts with the specified
  // properties, return those sorted in the specified manner, and put the
  // results into an initially empty ArrayList.
  ArrayList<Hunt> matchingHunts = huntCollection
    .find(combinedFilter)
    .sort(sortingOrder)
    .into(new ArrayList<>());

  // Set the JSON body of the response to be the list of todos returned by the database.
  // According to the Javalin documentation (https://javalin.io/documentation#context),
  // this calls result(jsonString), and also sets content type to json
  ctx.json(matchingHunts);

  // Explicitly set the context status to OK
  ctx.status(HttpStatus.OK);
}


  // *
  // * @param ctx a Javalin HTTP context, which contains the query parameters
  // *    used to construct the filter
  // * @return a Bson filter document that can be used in the `find` method
  // *   to filter the database collection of hunts
  // */

 private Bson constructFilter(Context ctx) {
  List<Bson> filters = new ArrayList<>();
  // starts with an empty list of filer.

  if (ctx.queryParamMap().containsKey(HOST_KEY)) {
    Pattern pattern = Pattern.compile(Pattern.quote(ctx.queryParam(HOST_KEY)), Pattern.CASE_INSENSITIVE);
    filters.add(regex(HOST_KEY, pattern));
    }

  if (ctx.queryParamMap().containsKey(TITLE_KEY)) {
    Pattern pattern = Pattern.compile(Pattern.quote(ctx.queryParam(TITLE_KEY)), Pattern.CASE_INSENSITIVE);
    filters.add(regex(TITLE_KEY, pattern));
    }

  if (ctx.queryParamMap().containsKey(DESCRIPTION_KEY)) {
  Pattern pattern = Pattern.compile(Pattern.quote(ctx.queryParam(DESCRIPTION_KEY)), Pattern.CASE_INSENSITIVE);
  filters.add(regex(DESCRIPTION_KEY, pattern));
    }

  // Combine list of filters into a single filtering document.
  Bson combinedFilter = filters.isEmpty() ? new Document() : and(filters);

  return combinedFilter;
  }

    /**
   * Construct a Bson sorting document to use in the `sort` method based on the
   * query parameters from the context.
   *
   * This checks for the presence of the `sortby` and `sortorder` query
   * parameters and constructs a sorting document that will sort hunts by
   * the specified field in the specified order. If the `sortby` query
   * parameter is not present, it defaults to "name". If the `sortorder`
   * query parameter is not present, it defaults to "asc".
   *
   * @param ctx a Javalin HTTP context, which contains the query parameters
   *   used to construct the sorting order
   * @return a Bson sorting document that can be used in the `sort` method
   *  to sort the database collection of hunts
   */

   private Bson constructSortingOrder(Context ctx) {
    // Sort the results. Use the `sortby` query param (default "name")
    // as the field to sort by, and the query param `sortorder` (default
    // "asc") to specify the sort order.
    String sortBy = Objects.requireNonNullElse(ctx.queryParam("sortby"), "title");
    String sortOrder = Objects.requireNonNullElse(ctx.queryParam("sortorder"), "asc");
    Bson sortingOrder = sortOrder.equals("desc") ?  Sorts.descending(sortBy) : Sorts.ascending(sortBy);
    return sortingOrder;
  }

  /**
   *
   * @param server // The javalin server instance
   * @param HuntController The controller handles the hunt endpoints
   *
   */



  public void addNewHunt(Context ctx) {
    /*
     * The follow chain of statements uses the Javalin validator system
     * to verify that instance of `User` provided in this context is
     * a "legal" user. It checks the following things (in order):
     *    - The user has a value for the name (`usr.name != null`)
     *    - The user name is not blank (`usr.name.length > 0`)
     *    - The provided email is valid (matches EMAIL_REGEX)
     *    - The provided age is > 0
     *    - The provided age is < REASONABLE_AGE_LIMIT
     *    - The provided role is valid (one of "admin", "editor", or "viewer")
     *    - A non-blank company is provided
     * If any of these checks fail, the Javalin system will throw a
     * `BadRequestResponse` with an appropriate error message.
     */
    Hunt newHunt = ctx.bodyValidator(Hunt.class)
      .check(usr -> usr.title != null && usr.title.length() > 0, "User must have a non-empty user name")
      // .check(usr -> usr.email.matches(EMAIL_REGEX), "User must have a legal email")
      // .check(usr -> usr.age > 0, "User's age must be greater than zero")
      // .check(usr -> usr.age < REASONABLE_AGE_LIMIT, "User's age must be less than " + REASONABLE_AGE_LIMIT)
      // .check(usr -> usr.role.matches(ROLE_REGEX), "User must have a legal user role")
      // .check(usr -> usr.company != null && usr.company.length() > 0, "User must have a non-empty company name")
      .get();


    // Add the new user to the database
    huntCollection.insertOne(newHunt);

    // Set the JSON response to be the `_id` of the newly created user.
    // This gives the client the opportunity to know the ID of the new user,
    // which it can then use to perform further operations (e.g., a GET request
    // to get and display the details of the new user).
    ctx.json(Map.of("id", newHunt._id));
    // 201 (`HttpStatus.CREATED`) is the HTTP code for when we successfully
    // create a new resource (a user in this case).
    // See, e.g., https://developer.mozilla.org/en-US/docs/Web/HTTP/Status
    // for a description of the various response codes.
    ctx.status(HttpStatus.CREATED);
  }

    /**
   * Utility function to generate the md5 hash for a given string
   *
   * @param str the string to generate a md5 for
   */
  public String md5(String str) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] hashInBytes = md.digest(str.toLowerCase().getBytes(StandardCharsets.UTF_8));

    StringBuilder result = new StringBuilder();
    for (byte b : hashInBytes) {
      result.append(String.format("%02x", b));
    }
    return result.toString();
  }

  public void addRoutes(Javalin server) {

    // get the specified Hunt
    server.get(API_HUNTS_BY_ID, this::getHunt);
    // List hunts, filtered using query parameters
    server.get(API_HUNTS, this::getHunts);

    server.post(API_HUNTS, this::addNewHunt);
  }

}
