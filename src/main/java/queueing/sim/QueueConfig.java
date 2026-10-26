package queueing.sim;

import java.util.List;

/**
 * Describe la configuración de una cola en un modelo de colas.
 *
 * <p>Una cola se identifica mediante su {@code id} y está asociada a una o más configuraciones de
 * servidor. Cada configuración de servidor define el identificador y el generador de tiempo de
 * servicio de un servidor asociado a la cola.
 *
 * <p>Por ejemplo, una configuración que incluye dos configuraciones de servidor representa una cola
 * con dos servidores:
 *
 * <pre>{@code
 * new QueueConfig(
 *     1,
 *     List.of(
 *         new ServerConfig(
 *             10,
 *             randomGenerator -> 3.0),
 *         new ServerConfig(
 *             20,
 *             randomGenerator -> 5.0)));
 * }</pre>
 *
 * <p>Los objetos {@link Queue} y {@link Server} reales son creados internamente por el marco de
 * simulación a partir de esta configuración.
 *
 * @param id el identificador único de la cola
 * @param servers las configuraciones de servidor asociadas a la cola
 */
public record QueueConfig(int id, List<ServerConfig> servers) {

  /**
   * Crea una configuración de cola.
   *
   * @throws NullPointerException if {@code servers} is {@code null} or contains {@code null}
   *     elements
   * @throws IllegalArgumentException if {@code servers} is empty
   */
  public QueueConfig {

    if (servers == null) {
      throw new NullPointerException("servers must not be null");
    }

    if (servers.isEmpty()) {
      throw new IllegalArgumentException("servers must not be empty");
    }

    servers = List.copyOf(servers);
  }
}
