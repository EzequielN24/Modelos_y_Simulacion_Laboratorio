package queueing.sim;

import java.util.random.RandomGenerator;

/**
 * Proporciona los servicios y el estado de la simulación disponibles para un evento mientras se
 * está ejecutando.
 */
public interface SimulationContext {

  /**
   * Retorna el generador de numeros aleatorios utilizado por la simulación.
   *
   * @return el generador de numeros aleatorios
   */
  RandomGenerator randomGenerator();

  /**
   * Retorna el modelo de colas que se está simulando.
   *
   * @return el modelo de colas
   */
  QueueingModel model();

  /**
   * Programa un evento para que ocurra durante la simulación.
   *
   * @param event el evento a programar
   * @throws NullPointerException si {@code event} es nulo
   */
  void schedule(Event event);
}
