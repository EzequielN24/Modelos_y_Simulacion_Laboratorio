package queueing.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link FutureEventList}. */
class FutureEventListTest {

  @Test
  void peekShouldReturnEventWithLowestClockWithoutRemovingIt() {
    FutureEventList fel = new FutureEventList();
    Event event1 = createEvent(10.0, 1);
    Event event2 = createEvent(5.0, 1);

    fel.insert(event1);
    fel.insert(event2);

    assertEquals(event2, fel.peek());
    assertEquals(event2, fel.peek());
    assertEquals(event2, fel.imminent());
    assertEquals(event1, fel.imminent());
  }

  @Test
  void imminentShouldReturnEventWithLowestClock() {
    FutureEventList fel = new FutureEventList();
    Event event1 = createEvent(10.0, 1);
    Event event2 = createEvent(5.0, 1);
    Event event3 = createEvent(15.0, 1);

    fel.insert(event1);
    fel.insert(event2);
    fel.insert(event3);

    assertEquals(event2, fel.imminent());
    assertEquals(event1, fel.imminent());
    assertEquals(event3, fel.imminent());
  }

  @Test
  void imminentShouldUsePriorityWhenClocksAreEqual() {
    FutureEventList fel = new FutureEventList();
    Event event1 = createEvent(10.0, 3);
    Event event2 = createEvent(10.0, 1);
    Event event3 = createEvent(10.0, 2);

    fel.insert(event1);
    fel.insert(event2);
    fel.insert(event3);

    assertEquals(event2, fel.imminent());
    assertEquals(event3, fel.imminent());
    assertEquals(event1, fel.imminent());
  }

  @Test
  void eventsWithEqualClockAndPriorityShouldPreserveInsertionOrder() {
    FutureEventList fel = new FutureEventList();
    Event event1 = createEvent(10.0, 1);
    Event event2 = createEvent(10.0, 1);
    Event event3 = createEvent(10.0, 1);

    fel.insert(event1);
    fel.insert(event2);
    fel.insert(event3);

    assertEquals(event1, fel.imminent());
    assertEquals(event2, fel.imminent());
    assertEquals(event3, fel.imminent());
  }

  @Test
  void imminentShouldRemoveTheReturnedEvent() {
    FutureEventList fel = new FutureEventList();
    Event event1 = createEvent(10.0, 1);
    Event event2 = createEvent(20.0, 1);

    fel.insert(event1);
    fel.insert(event2);

    assertEquals(event1, fel.imminent());
    assertEquals(event2, fel.imminent());
  }

  @Test
  void imminentShouldFailWhenFutureEventListIsEmpty() {
    FutureEventList fel = new FutureEventList();

    assertThrows(IllegalStateException.class, fel::imminent);
  }

  private Event createEvent(double clock, int priority) {
    return new Event() {

      @Override
      public double clock() {
        return clock;
      }

      @Override
      public int priority() {
        return priority;
      }

      @Override
      public void execute(SimulationContext context) {
        // No behavior is required for testing the Future Event List.
      }
    };
  }
}
