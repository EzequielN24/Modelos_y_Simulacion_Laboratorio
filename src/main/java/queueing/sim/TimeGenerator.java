package queueing.sim;

import java.util.random.RandomGenerator;

/** Define un método para generar valores de tiempo para la simulación. */
@FunctionalInterface
public interface TimeGenerator {

  /**
   * Genera un valor de tiempo utilizando el generador de números aleatorios dado.
   *
   * @param randomGenerator el generador de números aleatorios
   * @return el valor de tiempo generado
   */
  double generate(RandomGenerator randomGenerator);
}
