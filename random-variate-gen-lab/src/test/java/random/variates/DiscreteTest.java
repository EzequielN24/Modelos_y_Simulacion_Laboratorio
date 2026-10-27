package random.variates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DiscreteTest {

  @Test
  void shouldRejectEmptyTable() {
    assertThrows(IllegalArgumentException.class, () -> new Discrete<>(Map.of()));
  }

  @Test
  void shouldRejectNegativeProbabilities() {
    Map<String, Double> table =
        Map.of(
            "A", 0.5,
            "B", -0.5,
            "C", 1.0);

    assertThrows(IllegalArgumentException.class, () -> new Discrete<>(table));
  }

  @Test
  void shouldRejectProbabilitiesThatDoNotSumToOne() {
    Map<String, Double> table =
        Map.of(
            "A", 0.2,
            "B", 0.3,
            "C", 0.2);

    assertThrows(IllegalArgumentException.class, () -> new Discrete<>(table));
  }

  @Test
  void shouldGenerateValuesFromTheTable() {
    Map<String, Double> table =
        Map.of(
            "A", 0.2,
            "B", 0.5,
            "C", 0.3);

    Distribution<String> distribution = new Discrete<>(table);

    for (int i = 0; i < 10_000; i++) {
      String sample = distribution.sample();

      assertTrue(table.containsKey(sample));
    }
  }

  @Test
  void shouldGenerateValuesAccordingToTheirProbabilities() {
    Map<String, Double> table =
        Map.of(
            "A", 0.2,
            "B", 0.5,
            "C", 0.3);

    Distribution<String> distribution = new Discrete<>(table);

    int samples = 1_000_000;

    Map<String, Integer> counts = new HashMap<>();

    for (int i = 0; i < samples; i++) {
      String sample = distribution.sample();

      counts.merge(sample, 1, Integer::sum);
    }

    double aProbability = counts.getOrDefault("A", 0) / (double) samples;

    double bProbability = counts.getOrDefault("B", 0) / (double) samples;

    double cProbability = counts.getOrDefault("C", 0) / (double) samples;

    assertEquals(0.2, aProbability, 0.01);
    assertEquals(0.5, bProbability, 0.01);
    assertEquals(0.3, cProbability, 0.01);
  }
}
