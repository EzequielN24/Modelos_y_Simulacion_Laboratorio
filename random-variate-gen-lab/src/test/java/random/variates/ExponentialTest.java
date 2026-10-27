package random.variates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ExponentialTest {

  @Test
  void shouldRejectNonPositiveLambda() {
    assertThrows(IllegalArgumentException.class, () -> new Exponential(0.0));

    assertThrows(IllegalArgumentException.class, () -> new Exponential(-1.0));
  }

  @Test
  void shouldGenerateSamplesWithExpectedMean() {
    double lambda = 2.0;

    Distribution<Double> distribution = new Exponential(lambda);

    int samples = 1_000_000;

    double sum = 0.0;

    for (int i = 0; i < samples; i++) {
      sum += distribution.sample();
    }

    double mean = sum / samples;
    double expectedMean = 1.0 / lambda;

    assertEquals(expectedMean, mean, 0.01);
  }

  @Test
  void shouldGenerateAnExpectedSample() {
    assertEquals(
        4.877550458, new Exponential(.2d, new TestRandomGenerator(0.623)).sample(), .000000001);
  }

  private class TestRandomGenerator implements java.util.random.RandomGenerator {

    private final double[] values;
    private int index = 0;

    public TestRandomGenerator(double... values) {
      this.values = values;
    }

    @Override
    public double nextDouble() {
      if (this.index >= this.values.length) {
        throw new IndexOutOfBoundsException("No more values available");
      }
      return values[this.index++];
    }

    @Override
    public long nextLong() {
      if (this.index >= this.values.length) {
        throw new IndexOutOfBoundsException("No more values available");
      }
      return (long) this.values[this.index++];
    }
  }
}
