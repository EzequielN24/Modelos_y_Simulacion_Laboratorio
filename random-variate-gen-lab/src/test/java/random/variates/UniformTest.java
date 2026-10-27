package random.variates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UniformTest {

  @Test
  void shouldRejectInvalidBounds() {
    assertThrows(IllegalArgumentException.class, () -> new Uniform(10.0, 10.0));

    assertThrows(IllegalArgumentException.class, () -> new Uniform(20.0, 10.0));
  }

  @Test
  void shouldGenerateValuesWithinBounds() {
    double a = 10.0;
    double b = 20.0;

    Distribution<Double> distribution = new Uniform(a, b);

    for (int i = 0; i < 10_000; i++) {
      double sample = distribution.sample();

      assertTrue(sample >= a);
      assertTrue(sample < b);
    }
  }

  @Test
  void shouldGenerateSamplesWithExpectedMean() {
    double a = 10.0;
    double b = 20.0;

    Distribution<Double> distribution = new Uniform(a, b);

    int samples = 1_000_000;

    double sum = 0.0;

    for (int i = 0; i < samples; i++) {
      sum += distribution.sample();
    }

    double mean = sum / samples;
    double expectedMean = (a + b) / 2d;

    assertEquals(expectedMean, mean, 0.01);
  }
}
