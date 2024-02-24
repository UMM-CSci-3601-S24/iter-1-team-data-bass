package umm3601.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskSpec {

  private static final String FAKE_ID_STRING_1 = "fakeIdOne";
  private static final String FAKE_ID_STRING_2 = "fakeIdTwo";

  private Task task1;
  private Task task2;

  @BeforeEach
  void setupEach() {
    task1 = new Task();
    task2 = new Task();
  }

  @Test
  void tasksWithEqualIdAreEqual() {
    task1._id = FAKE_ID_STRING_1;
    task2._id = FAKE_ID_STRING_1;

    assertTrue(task1.equals(task2));
  }

  @Test
  void tasksWithDifferentIdAreNotEqual() {
    task1._id = FAKE_ID_STRING_1;
    task2._id = FAKE_ID_STRING_2;

    assertFalse(task1.equals(task2));
  }

  @Test
  void hashCodesAreBasedOnId() {
    task1._id = FAKE_ID_STRING_1;
    task2._id = FAKE_ID_STRING_1;

    assertTrue(task1.hashCode() == task2.hashCode());
  }

  @Test
  void tasksAreNotEqualToOtherKindsOfThings() {
    task1._id = FAKE_ID_STRING_1;
    // a task is not equal to its id even though id is used for checking equality
    assertFalse(task1.equals(FAKE_ID_STRING_1));
  }
}
