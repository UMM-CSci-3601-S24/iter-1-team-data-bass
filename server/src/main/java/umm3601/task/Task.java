package umm3601.task;

import org.mongojack.Id;
import org.mongojack.ObjectId;

@SuppressWarnings({"VisibilityModifier"})
public class Task {

  @ObjectId @Id

  @SuppressWarnings({"TaskDescription"})
  public String _id;
  public String description;
  public int position;
  public String Huntid;
public boolean isDone;

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Task)) {
      return false;
    }
    Task other = (Task) obj;
    return _id.equals(other._id);
  }

  @Override
  public int hashCode() {
    // Equal tasks will hash the same.
    return _id.hashCode();
  }

  public Object getId() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getId'");
  }

}
