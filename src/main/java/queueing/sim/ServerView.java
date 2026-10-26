package queueing.sim;

/** Proporciona una vista de solo lectura de un servidor en un modelo de colas. */
public interface ServerView {

  /**
   * Devuelve el identificador del servidor.
   *
   * @return el identificador del servidor
   */
  int id();

  /**
   * Devuelve si el servidor está actualmente ocupado.
   *
   * @return {@code true} si el servidor está atendiendo a una entidad, {@code false} en caso
   *     contrario
   */
  boolean isBusy();
}
