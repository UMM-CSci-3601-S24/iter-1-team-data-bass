package umm3601.hunt;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

import java.util.ArrayList;
import java.util.List;
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

  public void addRoutes(Javalin server) {

    // get the specified Hunt
    server.get(API_HUNTS_BY_ID, this::getHunt);
    // List hunts, filtered using query parameters
    server.get(API_HUNTS, this::getHunts);
  }

}
