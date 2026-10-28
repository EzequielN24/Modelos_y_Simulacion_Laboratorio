package queueing.sim.generators;

import java.util.random.RandomGenerator;
import queueing.sim.TimeGenerator;
import random.variates.Exponential;

/**
 * Generador de tiempos entre arribos dependiente del horario del día (proceso
 * de Poisson por tramos).
 *
 * <p>
 * El día tiene 1.440 minutos (24 horas * 60 minutos):
 * <ul>
 * <li>Horario pico mañana: de 9:00 a 13:00 hs (minutos 540 a 780). Exponencial
 * con media mu = 9 min.</li>
 * <li>Horario pico noche: de 20:00 a 23:00 hs (minutos 1200 a 1380).
 * Exponencial con media mu = 9 min.</li>
 * <li>Horario normal (resto del día): Exponencial con media mu = 15 min.</li>
 * </ul>
 */
public final class ArribosVuelosHorarioPicoGenerator implements TimeGenerator {

  private double relojActual = 0.0;

  /**
   * Actualiza el reloj de simulación actual para determinar en qué franja horaria
   * se encuentra.
   */
  public void actualizarReloj(double reloj) {
    this.relojActual = reloj;
  }

  /**
   * Genera el siguiente intervalo de arribo.
   *
   * @param randomGenerator generador de números aleatorios
   * @return intervalo de tiempo hasta el próximo arribo (minutos)
   */
  @Override
  public double generate(RandomGenerator randomGenerator) {
    // Minuto del día actual (0 a 1439.999...)
    double minutoDelDia = this.relojActual % 1440.0;
    if (minutoDelDia < 0.0) {
      minutoDelDia += 1440.0;
    }

    // Horarios pico:
    // 9:00 a 13:00 hs => [9*60, 13*60) = [540, 780)
    // 20:00 a 23:00 hs => [20*60, 23*60) = [1200, 1380)
    boolean esHorarioPico = (minutoDelDia >= 540.0 && minutoDelDia < 780.0)
        || (minutoDelDia >= 1200.0 && minutoDelDia < 1380.0);

    double media = esHorarioPico ? 9.0 : 15.0;
    // Exponential de random-variate-gen se parametriza con lambda (tasa = 1.0 /
    // media)
    double lambda = 1.0 / media;
    double intervalo = new Exponential(lambda, randomGenerator).sample();

    // Actualizamos el reloj de forma estimada para el próximo arribo
    this.relojActual += intervalo;
    return intervalo;
  }
}
