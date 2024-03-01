package umm3601.hunt;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HuntSpec {

  private static final String FAKE_ID_STRING_1 = "fakeIdOne";
  private static final String FAKE_ID_STRING_2 = "fakeIdTwo";

  private Hunt hunt1;
  private Hunt hunt2;

  @BeforeEach
  void setupEach() {
    hunt1 = new Hunt();
    hunt2 = new Hunt();
  }

  @Test
  void huntsWithEqualIdAreEqual() {
    hunt1._id = FAKE_ID_STRING_1;
    hunt2._id = FAKE_ID_STRING_1;

    assertTrue(hunt1.equals(hunt2));
  }

  @Test
  void huntsWithDifferentIdAreNotEqual() {
    hunt1._id = FAKE_ID_STRING_1;
    hunt2._id = FAKE_ID_STRING_2;

    assertFalse(hunt1.equals(hunt2));
  }

  @Test
  void hashCodesAreBasedOnId() {
    hunt1._id = FAKE_ID_STRING_1;
    hunt2._id = FAKE_ID_STRING_1;

    assertTrue(hunt1.hashCode() == hunt2.hashCode());
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  void huntsAreNotEqualToOtherKindsOfThings() {
    hunt1._id = FAKE_ID_STRING_1;
    // a Hunt is not equal to its id even though id is used for checking equality
    assertFalse(hunt1.equals(FAKE_ID_STRING_1));
  }
}
