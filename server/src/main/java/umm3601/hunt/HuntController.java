package umm3601.hunt;

import static org.mockito.ArgumentMatchers.eq;

import org.bson.UuidRepresentation;
import org.bson.types.ObjectId;
import org.mongojack.JacksonMongoCollection;

import com.mongodb.client.MongoDatabase;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import umm3601.Controller;

public class HuntController implements Controller {

  private static final String API_HUNTS = "/api/hunts";
  private static final String API_HUNT_BY_ID = "/api/hunts/{id}";
  static final String NAME_KEY = "name";
  static final String DESCRIPTION_KEY = "description";
  static final String OWNERID_KEY = "ownerId";

  private final JacksonMongoCollection<Hunt> huntCollection;

  /**
   * Construct a controller for hunts.
   *
   * @param database the database containing hunt data
   */
  public HuntController(MongoDatabase database) {
    huntCollection = JacksonMongoCollection.builder().build(
        database,
        "hunt",
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

  @Override
  public void addRoutes(Javalin server) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'addRoutes'");
  }


}
