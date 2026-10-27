package random.variates;

import java.util.random.RandomGenerator;

/**
 * Representa una distribución de probabilidad exponencial.
 *
 * <p>La distribución está parametrizada por su tasa {@code lambda}.
 */
public final class Exponential implements Distribution<Double> {

  /** El parámetro de tasa de la distribución exponencial; debe ser mayor que cero. */
  private final double lambda;

  /** El generador de números aleatorios utilizado para producir muestras. */
  private final RandomGenerator randomGenerator;

  /**
   * Crea una distribución exponencial con el parámetro de tasa especificado utilizando un generador
   * de números aleatorios por defecto.
   *
   * <p>El parámetro de tasa debe ser estrictamente mayor que cero.
   *
   * @param lambda el parámetro de tasa de la distribución; debe ser mayor que cero
   * @throws IllegalArgumentException si {@code lambda} es menor o igual que cero
   */
  public Exponential(double lambda) {
    this(lambda, RandomGenerator.getDefault());
  }

  /**
   * Crea una distribución exponencial con el parámetro de tasa y el generador de números aleatorios
   * especificados.
   *
   * <p>El parámetro de tasa debe ser estrictamente mayor que cero.
   *
   * @param lambda el parámetro de tasa de la distribución; debe ser mayor que cero
   * @param randomGenerator el generador de números aleatorios utilizado para generar muestras
   * @throws IllegalArgumentException si {@code lambda} es menor o igual que cero
   * @throws NullPointerException si {@code randomGenerator} es {@code null}
   */
  public Exponential(double lambda, RandomGenerator randomGenerator) {
    if (lambda <= 0.0) {
      throw new IllegalArgumentException("El parámetro lambda debe ser mayor que cero.");
    }
    if (randomGenerator == null) {
      throw new NullPointerException("El generador de números aleatorios no puede ser nulo.");
    }
    this.lambda = lambda;
    this.randomGenerator = randomGenerator;
  }

  @Override
  public Double sample() {
    double aleatorio = this.randomGenerator.nextDouble();
    return -Math.log(1.0 - aleatorio) / this.lambda;
  }
}
