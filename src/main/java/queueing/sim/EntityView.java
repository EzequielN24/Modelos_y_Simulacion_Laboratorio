package queueing.sim;

/**
 * Proporciona una vista de solo lectura de una entidad en un modelo de colas.
 *
 * @author
 */
public interface EntityView {

  /**
   * Retorna el identificador de la entidad.
   *
   * @return el identificador de la entidad
   */
  long id();
}
