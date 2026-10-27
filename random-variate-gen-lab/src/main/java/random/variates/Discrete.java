package random.variates;

import java.util.Map;
import java.util.random.RandomGenerator;

/**
 * Representa una distribución de probabilidad empírica discreta.
 *
 * <p>Cada valor posible está asociado con una probabilidad que determina cuán probable es que sea
 * muestreado.
 *
 * @param <T> el tipo de valores producidos por esta distribución
 */
public final class Discrete<T> implements Distribution<T> {

  /**
   * Asocia cada valor posible con su probabilidad correspondiente.
   *
   * <p>Las probabilidades deben ser no negativas y sumar 1.
   */
  private final Map<T, Double> table;

  /** El generador de números aleatorios utilizado para producir muestras. */
  private final RandomGenerator randomGenerator;

  /**
   * Crea una distribución empírica discreta utilizando el generador aleatorio por defecto.
   *
   * @param table los valores y sus probabilidades asociadas; no debe ser nula ni vacía, y las
   *     probabilidades deben ser no negativas y sumar uno
   * @throws NullPointerException si {@code table} es {@code null}
   * @throws IllegalArgumentException si {@code table} está vacía o contiene probabilidades
   *     inválidas
   */
  public Discrete(Map<T, Double> table) {
    this(table, RandomGenerator.getDefault());
  }

  /**
   * Crea una distribución empírica discreta utilizando el generador aleatorio especificado.
   *
   * @param table los valores y sus probabilidades asociadas; no debe ser nula ni vacía, y las
   *     probabilidades deben ser no negativas y sumar uno
   * @param randomGenerator el generador de números aleatorios utilizado para generar muestras
   * @throws NullPointerException si {@code table} o {@code randomGenerator} son {@code null}
   * @throws IllegalArgumentException si {@code table} está vacía o contiene probabilidades
   *     inválidas
   */
  public Discrete(Map<T, Double> table, RandomGenerator randomGenerator) {
    if (table == null) {
      throw new NullPointerException("La tabla no puede ser nula.");
    }
    if (randomGenerator == null) {
      throw new NullPointerException("El generador de números aleatorios no puede ser nulo.");
    }
    if (table.isEmpty()) {
      throw new IllegalArgumentException("La tabla de probabilidades no puede estar vacía.");
    }

    double sumaProbabilidades = 0.0;
    for (Map.Entry<T, Double> entry : table.entrySet()) {
      Double prob = entry.getValue();
      if (prob == null || prob < 0.0) {
        throw new IllegalArgumentException("Las probabilidades deben ser no negativas.");
      }
      sumaProbabilidades += prob;
    }

    if (Math.abs(sumaProbabilidades - 1.0) > 1e-6) {
      throw new IllegalArgumentException("La suma de probabilidades debe ser igual a 1 (obtenido: " + sumaProbabilidades + ").");
    }

    this.table = new java.util.LinkedHashMap<>(table);
    this.randomGenerator = randomGenerator;
  }

  @Override
  public T sample() {
    double aleatorio = this.randomGenerator.nextDouble();
    double acumulada = 0.0;
    T ultimoValor = null;
    for (Map.Entry<T, Double> entry : this.table.entrySet()) {
      ultimoValor = entry.getKey();
      acumulada += entry.getValue();
      if (aleatorio <= acumulada) {
        return ultimoValor;
      }
    }
    return ultimoValor;
  }
}
