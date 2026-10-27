package random.variates;

/** Represents a probability distribution capable of generating random samples. */
@FunctionalInterface
public interface Distribution<T> {

  /**
   * Generates a random sample from this distribution.
   *
   * @return a random sample from this distribution
   */
  T sample();
}
