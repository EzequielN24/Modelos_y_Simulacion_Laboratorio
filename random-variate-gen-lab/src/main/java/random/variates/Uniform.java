package random.variates;

import java.util.random.RandomGenerator;

/**
 * Representa una distribución de probabilidad continua uniforme.
 *
 * <p>Todos los valores en el intervalo {@code [a, b)} tienen la misma densidad de probabilidad.
 */
public final class Uniform implements Distribution<Double> {

  /** El límite inferior de la distribución. */
  private final double a;

  /** El límite superior de la distribución. */
  private final double b;

  /** El generador de números aleatorios utilizado para producir muestras. */
  private final RandomGenerator randomGenerator;

  /**
   * Crea una distribución uniforme utilizando el generador aleatorio por defecto.
   *
   * @param a el límite inferior de la distribución; debe ser menor que {@code b}
   * @param b el límite superior de la distribución; debe ser mayor que {@code a}
   * @throws IllegalArgumentException si {@code a} es mayor o igual que {@code b}
   */
  public Uniform(double a, double b) {
    this(a, b, RandomGenerator.getDefault());
  }

  /**
   * Crea una distribución uniforme utilizando el generador aleatorio especificado.
   *
   * @param a el límite inferior de la distribución; debe ser menor que {@code b}
   * @param b el límite superior de la distribución; debe ser mayor que {@code a}
   * @param randomGenerator el generador de números aleatorios utilizado para generar muestras
   * @throws IllegalArgumentException si {@code a} es mayor o igual que {@code b}
   * @throws NullPointerException si {@code randomGenerator} es {@code null}
   */
  public Uniform(double a, double b, RandomGenerator randomGenerator) {
    if (a >= b) {
      throw new IllegalArgumentException("El límite inferior 'a' debe ser estrictamente menor que 'b'.");
    }
    if (randomGenerator == null) {
      throw new NullPointerException("El generador de números aleatorios no puede ser nulo.");
    }
    this.a = a;
    this.b = b;
    this.randomGenerator = randomGenerator;
  }

  @Override
  public Double sample() {
    double aleatorio = this.randomGenerator.nextDouble();
    return this.a + (this.b - this.a) * aleatorio;
  }
}
