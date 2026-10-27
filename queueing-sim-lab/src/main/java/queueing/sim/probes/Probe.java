package queueing.sim.probes;

import queueing.sim.EntityView;
import queueing.sim.QueueView;
import queueing.sim.ServerView;

/**
 * Observa eventos durante la ejecución de una simulación.
 *
 * <p>Una sonda puede recopilar, calcular o registrar información sobre una simulación sin controlar
 * su ejecución ni modificar directamente su estado.
 *
 * <p>Todos los métodos de notificación tienen implementaciones vacías por defecto. Una sonda solo
 * necesita sobrescribir aquellas notificaciones que sean relevantes para ella.
 */
public interface Probe {

  /**
   * Notifica que una entidad ha llegado al sistema.
   *
   * @param clock el tiempo de simulación en el que llegó la entidad
   * @param entity la entidad que llegó
   */
  default void entityArrived(double clock, EntityView entity) {}

  /**
   * Notifica que una entidad ha entrado en una cola.
   *
   * @param clock el tiempo de simulación en el que la entidad entró en la cola
   * @param entity la entidad que entró en la cola
   * @param queue la cola en la que entró la entidad
   */
  default void entityEnteredQueue(double clock, EntityView entity, QueueView queue) {}

  /**
   * Notifica que una entidad ha comenzado a recibir servicio.
   *
   * @param clock el tiempo de simulación en el que comenzó el servicio
   * @param entity la entidad que recibe el servicio
   * @param server el servidor que proporciona el servicio
   */
  default void serviceStarted(double clock, EntityView entity, ServerView server) {}

  /**
   * Notifica que una entidad ha partido del sistema.
   *
   * @param clock el tiempo de simulación en el que la entidad partió
   * @param entity la entidad que partió
   */
  default void entityDeparted(double clock, EntityView entity) {}

  /**
   * Notifica que un servidor ha quedado libre.
   *
   * @param clock el tiempo de simulación en el que el servidor quedó libre
   * @param server el servidor que quedó libre
   */
  default void serverBecameIdle(double clock, ServerView server) {}

  /**
   * Notifica que un servidor ha quedado ocupado.
   *
   * <p>Esta notificación representa una transición del servidor de un estado libre a un estado
   * ocupado.
   *
   * @param clock the simulation time at which the server became busy
   * @param server the server that became busy
   */
  default void serverBecameBusy(double clock, ServerView server) {}

  /**
   * Notifica que el tamaño de una cola ha cambiado.
   *
   * @param clock el tiempo de simulación en el que el tamaño de la cola cambió
   * @param queue la cola cuyo tamaño cambió
   */
  default void queueSizeChanged(double clock, QueueView queue) {}

  /**
   * Notifica que la simulación ha comenzado.
   *
   * <p>Esta notificación se envía una vez, antes de que la simulación comience a procesar eventos.
   *
   * @param clock el tiempo de simulación en el que la simulación comenzó
   */
  default void simulationStarted(double clock) {}

  /**
   * Notifica que la simulación ha finalizado.
   *
   * <p>Esta notificación se envía una vez, después de que la simulación ha finalizado el
   * procesamiento de eventos.
   *
   * @param clock el tiempo de simulación en el que la simulación finalizó
   */
  default void simulationFinished(double clock) {}
}
