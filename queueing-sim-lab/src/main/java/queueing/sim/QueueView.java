package queueing.sim;

/** Proporciona una vista de solo lectura de una cola en un modelo de colas. */
public interface QueueView {

  /**
   * Retorna el identificador de la cola.
   *
   * @return el identificador de la cola
   */
  int id();

  /**
   * Retorna el número de entidades esperando en la cola.
   *
   * @return el número de entidades esperando en la cola
   */
  int size();
}
