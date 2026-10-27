package queueing.sim;

import java.util.Objects;

/**
 * Describe la configuración de un servidor en un modelo de colas.
 *
 * <p>Un servidor se identifica mediante su {@code id} y está asociado a un generador de tiempos de
 * servicio utilizado para determinar la duración de cada servicio realizado por el servidor.
 *
 * <p>Por ejemplo:
 *
 * <pre>{@code
 * new ServerConfig(
 *     10,
 *     randomGenerator -> 5.0);
 * }</pre>
 *
 * @param id el identificador único del servidor
 * @param serviceTimeGenerator el generador utilizado para generar los tiempos de servicio
 */
public record ServerConfig(int id, TimeGenerator serviceTimeGenerator) {

  /**
   * Crea una configuración de servidor.
   *
   * @throws NullPointerException if {@code serviceTimeGenerator} is {@code null}
   */
  public ServerConfig {
    Objects.requireNonNull(serviceTimeGenerator);
  }
}
