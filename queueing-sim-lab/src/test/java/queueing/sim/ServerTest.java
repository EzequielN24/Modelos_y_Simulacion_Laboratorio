package queueing.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.random.RandomGenerator;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link Server}. */
class ServerTest {

  @Test
  void serverShouldBeIdleWhenCreated() {
    Server server = new Server(1, rg -> 5.0);

    assertFalse(server.isBusy());
    assertNull(server.entity());
  }

  @Test
  void serverShouldReturnItsId() {
    Server server = new Server(42, rg -> 5.0);

    assertEquals(42, server.id());
  }

  @Test
  void serviceDurationShouldReturnValueGeneratedByServiceTimeGenerator() {
    Server server = new Server(1, rg -> 5.0);

    assertEquals(5.0, server.serviceDuration(RandomGenerator.getDefault()));
  }

  @Test
  void serviceDurationShouldUseProvidedRandomGenerator() {
    RandomGenerator randomGenerator = RandomGenerator.getDefault();
    Server server = new Server(1, rg -> rg.nextDouble(10.0, 20.0));

    double duration = server.serviceDuration(randomGenerator);

    assertTrue(duration >= 10.0);
    assertTrue(duration < 20.0);
  }

  @Test
  void serverShouldRejectNullServiceTimeGenerator() {
    assertThrows(NullPointerException.class, () -> new Server(1, null));
  }

  @Test
  void seizeShouldMakeServerBusy() {
    Server server = new Server(1, rg -> 5.0);
    Entity entity = new Entity(10);

    server.seize(entity);

    assertTrue(server.isBusy());
    assertEquals(entity, server.entity());
  }

  @Test
  void seizeShouldRejectNullEntity() {
    Server server = new Server(1, rg -> 5.0);

    assertThrows(NullPointerException.class, () -> server.seize(null));
  }

  @Test
  void seizeShouldFailWhenServerIsBusy() {
    Server server = new Server(1, rg -> 5.0);

    server.seize(new Entity(10));

    assertThrows(IllegalStateException.class, () -> server.seize(new Entity(20)));
  }

  @Test
  void releaseShouldMakeServerIdle() {
    Server server = new Server(1, rg -> 5.0);
    Entity entity = new Entity(10);

    server.seize(entity);

    Entity servedEntity = server.release();

    assertEquals(entity, servedEntity);
    assertFalse(server.isBusy());
    assertNull(server.entity());
  }

  @Test
  void releaseShouldFailWhenServerIsIdle() {
    Server server = new Server(1, rg -> 5.0);

    assertThrows(IllegalStateException.class, server::release);
  }
}
