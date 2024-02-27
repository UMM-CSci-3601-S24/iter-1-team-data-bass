package umm3601.task;

import org.mongojack.Id;
import org.mongojack.ObjectId;

@SuppressWarnings({ "VisibilityModifier" })
public class Task {

  @ObjectId @Id
  @SuppressWarnings({ "MemberName" })
  public String _id;
  public String huntId;
  public String taskDescription;

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
    return _id.hashCode();

  }
}
