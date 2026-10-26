package queueing.sim;

import java.util.*;

/** Cola FIFO asociada a uno o varios servidores. */
final class Queue implements QueueView {

  /** El identificador de esta cola. */
  private final int id;

  /** Las entidades que estan esperando en la cola. */
  private final ArrayDeque<Entity> entities = new ArrayDeque<>();

  /** Los servidores asociados a esta cola. */
  private final List<Server> servers;

  /** Crea una cola asociada a los servidores dados. */
  Queue(int id, List<Server> servers) {
    if (servers.isEmpty()) {
      throw new IllegalArgumentException("servers cannot be empty");
    }
    this.id = id;
    this.servers = List.copyOf(servers);
  }

  /** Retorna si esta cola contiene entidades esperando. */
  boolean isEmpty() {
    return entities.isEmpty();
  }

  /** Agrega una entidad al final de la cola. */
  void add(Entity entity) {
    entities.add(entity);
  }

  /** Retorna la primera entidad en espera sin eliminarla. */
  Entity peek() {
    return entities.peek();
  }

  /** Retorna la primera entidad en espera eliminándola. */
  Entity remove() {
    return entities.remove();
  }

  /** Retorna los servidores asociados a esta cola. */
  List<Server> servers() {
    return servers;
  }

  /** Retorna los servidores asociados a esta cola que están disponibles. */
  List<Server> availableServers() {
    List<Server> availableServers = new ArrayList<>();
    for (Server server : servers) {
      if (!server.isBusy()) {
        availableServers.add(server);
      }
    }
    return availableServers;
  }

  @Override
  public int id() {
    return id;
  }

  @Override
  public int size() {
    return entities.size();
  }

  @Override
  public String toString() {
    return "Queue[id="
        + this.id
        + ", entities="
        + this.entities
        + ", servers="
        + this.servers
        + "]";
  }
}
