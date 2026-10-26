package queueing.sim;

import java.util.*;
import java.util.Objects;
import java.util.random.RandomGenerator;

/** Servidor que atiende a los clientes en la cola. */
final class Server implements ServerView {

  /** Id del servidor. */
  private final int id;

  /** Generador de tiempos de servicio. */
  private final TimeGenerator serviceTimeGenerator;

  /** Entidad que esta siendo atendida, o null si el servidor esta libre. */
  private Entity entity;

  /**
   * Crea un servidor.
   *
   * @param id el identificador del servidor
   * @param serviceTimeGenerator the generator used to determine service times
   * @throws NullPointerException if {@code serviceTimeGenerator} is {@code null}
   */
  Server(int id, TimeGenerator serviceTimeGenerator) {
    this.id = id;
    this.serviceTimeGenerator = Objects.requireNonNull(serviceTimeGenerator);
  }

  @Override
  public int id() {
    return this.id;
  }

  /** Retorna si el servidor esta ocupado. */
  @Override
  public boolean isBusy() {
    return this.entity != null;
  }

  /** Genera el tiempo de servicio para el servidor. */
  double serviceDuration(RandomGenerator randomGenerator) {
    return this.serviceTimeGenerator.generate(randomGenerator);
  }

  /** Retorna la entidad que esta siendo atendida. */
  Entity entity() {
    return this.entity;
  }

  /** Se apodera del servidor para la entidad dada, haciendo que el servidor esté ocupado. */
  void seize(Entity entity) {
    Objects.requireNonNull(entity);

    if (this.isBusy()) {
      throw new IllegalStateException("Cannot seize the server because the server is busy");
    }

    this.entity = entity;
  }

  /** Libera el servidor de la entidad que esta siendo atendida. */
  Entity release() {
    if (!this.isBusy()) {
      throw new IllegalStateException("Cannot release the server because the server is idle");
    }

    Entity entity = this.entity;
    this.entity = null;

    return entity;
  }

  @Override
  public String toString() {
    if (this.isBusy()) {
      return "Server[id=" + this.id + ", state=BUSY, entity=" + this.entity + "]";
    }

    return "Server[id=" + this.id + ", state=IDLE]";
  }
}
