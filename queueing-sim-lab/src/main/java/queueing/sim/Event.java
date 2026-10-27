package queueing.sim;

/**
 * Representa un evento que puede ocurrir durante una simulación.
 *
 * <p>Los eventos se ordenan según su reloj de simulación y prioridad de ejecución. Cuando se
 * ejecuta un evento, puede interactuar con la simulación a través de un {@link SimulationContext}.
 */
public interface Event {

  /**
   * Devuelve el tiempo de simulación en el que ocurre este evento.
   *
   * @return the event time
   */
  double clock();

  /**
   * Devuelve la prioridad de este evento.
   *
   * <p>Los eventos con valores de prioridad más bajos se ejecutan antes que los eventos con valores
   * de prioridad más altos cuando ocurren al mismo tiempo de simulación.
   *
   * @return la prioridad del evento
   */
  int priority();

  /**
   * Ejecuta este evento.
   *
   * @param context el contexto en el que se ejecuta el evento
   */
  void execute(SimulationContext context);
}
