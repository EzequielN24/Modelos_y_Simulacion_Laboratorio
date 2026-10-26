package queueing.sim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Representa el modelo de colas simulado por una {@link Simulation}.
 *
 * <p>Un modelo de colas consta de una o más colas. Cada cola tiene al menos un servidor asociado.
 * Cada servidor está asociado exactamente a una cola.
 *
 * <p>Las colas y los servidores se crean internamente a partir de los objetos {@link QueueConfig}
 * suministrados.
 */
public final class QueueingModel {

  /** Las colas que componen este modelo */
  private final List<Queue> queues;

  /** Los servidores que componen este modelo */
  private final List<Server> servers;

  /**
   * Crea un modelo de colas a partir de las configuraciones de colas suministradas.
   *
   * <p>Cada {@link QueueConfig} crea una cola. Cada {@link ServerConfig} contenido en una
   * configuración crea un servidor asociado con esa cola.
   *
   * @param queueConfigs the configurations of the queues that compose the model
   * @throws NullPointerException if {@code queueConfigs} is {@code null} or contains {@code null}
   *     elements
   * @throws IllegalArgumentException if {@code queueConfigs} is empty, contains duplicate queue
   *     identifiers, or contains duplicate server identifiers
   */
  public QueueingModel(List<QueueConfig> queueConfigs) {

    Objects.requireNonNull(queueConfigs);

    if (queueConfigs.isEmpty()) {
      throw new IllegalArgumentException("A queueing model must have at least one queue");
    }

    this.queues = createQueues(queueConfigs);

    this.servers = this.queues.stream().flatMap(queue -> queue.servers().stream()).toList();
  }

  /**
   * Devuelve la cola interna con el identificador especificado.
   *
   * <p>Este método tiene visibilidad de paquete porque la implementación interna de {@link Queue}
   * no debe exponerse como parte de la API pública.
   *
   * @param id el identificador de la cola
   * @return la cola con el identificador especificado
   * @throws IllegalArgumentException si no existe ninguna cola con el identificador especificado
   */
  Queue queue(int id) {
    return this.queues.stream()
        .filter(queue -> queue.id() == id)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No queue exists with id: " + id));
  }

  /**
   * Devuelve el servidor interno con el identificador especificado.
   *
   * <p>Este método tiene visibilidad de paquete porque la implementación interna de {@link Server}
   * no debe exponerse como parte de la API pública.
   *
   * @param id el identificador del servidor
   * @return el servidor con el identificador especificado
   * @throws IllegalArgumentException si no existe ningún servidor con el identificador especificado
   */
  Server server(int id) {
    return this.servers.stream()
        .filter(server -> server.id() == id)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No server exists with id: " + id));
  }

  /**
   * Devuelve una vista de solo lectura de todos los servidores en el modelo de colas.
   *
   * @return los servidores en el modelo de colas
   */
  List<ServerView> servers() {
    return List.copyOf(this.servers);
  }

  /**
   * Devuelve vistas de solo lectura de las colas que componen este modelo.
   *
   * <p>Las vistas devueltas exponen únicamente información que los clientes pueden utilizar de
   * forma segura, como el identificador de la cola y el número de entidades en espera.
   *
   * @return una lista no modificable de vistas de colas
   */
  public List<QueueView> queues() {
    return this.queues.stream().map(queue -> (QueueView) queue).toList();
  }

  /**
   * Devuelve vistas de solo lectura de todos los servidores disponibles actualmente.
   *
   * <p>Se considera que un servidor está disponible cuando no está atendiendo ninguna entidad en
   * ese momento.
   *
   * @return una lista no modificable de vistas de servidores disponibles
   */
  public List<ServerView> availableServers() {
    return this.servers.stream()
        .filter(server -> !server.isBusy())
        .map(server -> (ServerView) server)
        .toList();
  }

  /**
   * Devuelve una representación en cadena del estado actual de este modelo de colas.
   *
   * <p>La representación incluye todas las colas en su orden configurado.
   *
   * @return una representación en cadena de este modelo de colas
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("QueueingModel[\n");

    for (int i = 0; i < this.queues.size(); i++) {
      builder.append("  ").append(this.queues.get(i));

      if (i < this.queues.size() - 1) {
        builder.append('\n');
      }
    }

    builder.append("\n]");

    return builder.toString();
  }

  /**
   * Crea las colas y los servidores internos a partir de las configuraciones suministradas.
   *
   * <p>Los identificadores de cola y de servidor deben ser únicos en todo el modelo.
   *
   * @param queueConfigs las configuraciones de las colas
   * @return las colas creadas
   * @throws NullPointerException si una configuración de cola es {@code null} o una configuración
   *     de servidor es {@code null}
   * @throws IllegalArgumentException si los identificadores de cola o de servidor están duplicados
   */
  private static List<Queue> createQueues(List<QueueConfig> queueConfigs) {

    Set<Integer> queueIds = new HashSet<>();
    Set<Integer> serverIds = new HashSet<>();
    List<Queue> queues = new ArrayList<>();

    for (QueueConfig configuration : queueConfigs) {

      Objects.requireNonNull(configuration);

      if (!queueIds.add(configuration.id())) {
        throw new IllegalArgumentException("Queue identifiers must be unique");
      }

      List<Server> servers = new ArrayList<>();

      for (ServerConfig serverConfiguration : configuration.servers()) {

        Objects.requireNonNull(serverConfiguration);

        if (!serverIds.add(serverConfiguration.id())) {
          throw new IllegalArgumentException("Server identifiers must be unique");
        }

        servers.add(
            new Server(serverConfiguration.id(), serverConfiguration.serviceTimeGenerator()));
      }

      queues.add(new Queue(configuration.id(), servers));
    }

    return List.copyOf(queues);
  }
}
