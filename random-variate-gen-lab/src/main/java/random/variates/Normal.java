package random.variates;

import java.util.random.RandomGenerator;

/**
 * Representa una distribución de probabilidad continua normal.
 *
 * <p>
 * La distribución está definida por su media y su desviación estándar.
 */
public final class Normal implements Distribution<Double> {

  /** La media de la distribución. */
  private final double media;

  /** La desviación estándar de la distribución. */
  private final double desviacion_estandar;

  /** El generador de números aleatorios utilizado para producir muestras. */
  private final RandomGenerator randomGenerator;

  /**
   * Crea una distribución normal utilizando el generador aleatorio por defecto.
   *
   * @param media               la media de la distribución
   * @param desviacion_estandar la desviación estándar de la distribución; debe
   *                            ser mayor que
   *                            cero
   * @throws IllegalArgumentException si {@code stdDev} es menor o igual que cero
   */
  public Normal(double media, double desviacion_estandar) {
    this(media, desviacion_estandar, RandomGenerator.getDefault());
  }

  /**
   * Crea una distribución normal utilizando el generador aleatorio especificado.
   *
   * @param media               la media de la distribución
   * @param desviacion_estandar la desviación estándar de la distribución; debe
   *                            ser
   *                            mayor que cero
   * @param randomGenerator     el generador de números aleatorios utilizado para
   *                            generar muestras
   * @throws IllegalArgumentException si {@code stdDev} es menor o igual que cero
   * @throws NullPointerException     si {@code randomGenerator} es {@code null}
   */
  public Normal(double media, double desviacion_estandar, RandomGenerator randomGenerator) {
    if (desviacion_estandar <= 0.0) {
      throw new IllegalArgumentException("La desviación estándar debe ser mayor que cero.");
    }
    if (randomGenerator == null) {
      throw new NullPointerException("El generador de números aleatorios no puede ser nulo.");
    }
    this.media = media;
    this.desviacion_estandar = desviacion_estandar;
    this.randomGenerator = randomGenerator;
  }

  @Override
  public Double sample() {
    double valorEstandar = this.randomGenerator.nextGaussian();
    return this.media + this.desviacion_estandar * valorEstandar;
  }
}
