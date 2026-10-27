package random.variates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NormalTest {

  @Test
  void shouldRejectNonPositiveStandardDeviation() {
    assertThrows(IllegalArgumentException.class, () -> new Normal(0.0, 0.0));

    assertThrows(IllegalArgumentException.class, () -> new Normal(0.0, -1.0));
  }

  @Test
  void shouldGenerateSamplesWithExpectedMean() {
    double expectedMean = 10.0;
    double standardDeviation = 2.0;

    Distribution<Double> distribution = new Normal(expectedMean, standardDeviation);

    int samples = 1_000_000;
    double sum = 0.0;

    for (int i = 0; i < samples; i++) {
      sum += distribution.sample();
    }

    double mean = sum / samples;

    assertEquals(expectedMean, mean, 0.01);
  }

  @Test
  void shouldGenerateSamplesWithExpectedStandardDeviation() {
    double mean = 10.0;
    double expectedStandardDeviation = 2.0;

    Distribution<Double> distribution = new Normal(mean, expectedStandardDeviation);

    int samples = 1_000_000;

    double sum = 0.0;
    double sumOfSquaredDifferences = 0.0;

    for (int i = 0; i < samples; i++) {
      double sample = distribution.sample();

      sum += sample;
    }

    double sampleMean = sum / samples;

    for (int i = 0; i < samples; i++) {
      double sample = distribution.sample();

      double difference = sample - sampleMean;

      sumOfSquaredDifferences += difference * difference;
    }

    double standardDeviation = Math.sqrt(sumOfSquaredDifferences / samples);

    assertEquals(expectedStandardDeviation, standardDeviation, 0.01);
  }

  @Test
  void shouldGenerateSamplesWithinExpectedStandardDeviationRanges() {
    double mean = 10.0;
    double standardDeviation = 2.0;

    Distribution<Double> distribution = new Normal(mean, standardDeviation);

    int samples = 1_000_000;

    int withinOneStdDev = 0;
    int withinTwoStdDev = 0;
    int withinThreeStdDev = 0;

    for (int i = 0; i < samples; i++) {
      double sample = distribution.sample();

      double distanceFromMean = Math.abs(sample - mean);

      if (distanceFromMean <= standardDeviation) {
        withinOneStdDev++;
      }

      if (distanceFromMean <= 2.0 * standardDeviation) {
        withinTwoStdDev++;
      }

      if (distanceFromMean <= 3.0 * standardDeviation) {
        withinThreeStdDev++;
      }
    }

    double oneStdDevProbability = (double) withinOneStdDev / samples;

    double twoStdDevProbability = (double) withinTwoStdDev / samples;

    double threeStdDevProbability = (double) withinThreeStdDev / samples;

    assertEquals(0.6827, oneStdDevProbability, 0.01);

    assertEquals(0.9545, twoStdDevProbability, 0.01);

    assertEquals(0.9973, threeStdDevProbability, 0.01);
  }
}
