package queueing.sim.probes;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;
import queueing.sim.EntityView;
import queueing.sim.ServerView;
import random.variates.Normal;

/**
 * Sonda que registra el desgaste y la durabilidad de suelo de cada pista (servidor).
 *
 * <p>Inicialmente cada pista cuenta con una durabilidad configurable (por defecto 3.000 unidades).
 * En cada inicio de servicio / aterrizaje, la pista sufre un desgaste muestreado de una distribución
 * Normal(media = 5, desviacion = 1).
 */
public final class RunwayDurabilityProbe implements Probe {

  private final double durabilidadInicial;
  private final Map<Integer, Double> durabilidadPistas = new HashMap<>();
  private final Normal generadorDesgaste;

  /**
   * Crea una sonda de durabilidad con durabilidad inicial y distribución de desgaste Normal(5, 1).
   *
   * @param durabilidadInicial valor inicial de durabilidad (ej. 3000)
   * @param rng generador de números aleatorios para el desgaste
   */
  public RunwayDurabilityProbe(double durabilidadInicial, RandomGenerator rng) {
    this.durabilidadInicial = durabilidadInicial;
    this.generadorDesgaste = new Normal(5.0, 1.0, rng);
  }

  @Override
  public void serverBecameIdle(double clock, ServerView server) {
    this.durabilidadPistas.putIfAbsent(server.id(), this.durabilidadInicial);
  }

  @Override
  public void serviceStarted(double clock, EntityView entity, ServerView server) {
    this.durabilidadPistas.putIfAbsent(server.id(), this.durabilidadInicial);
    double desgaste = this.generadorDesgaste.sample();
    double actual = this.durabilidadPistas.get(server.id());
    this.durabilidadPistas.put(server.id(), actual - desgaste);
  }

  /**
   * Devuelve la durabilidad actual/final de la pista especificada.
   *
   * @param serverId id del servidor / pista
   * @return la durabilidad de suelo de la pista
   */
  public double durabilidad(int serverId) {
    return this.durabilidadPistas.getOrDefault(serverId, this.durabilidadInicial);
  }

  /**
   * Devuelve el mapa completo con las durabilidades de todas las pistas.
   *
   * @return mapa de serverId a durabilidad
   */
  public Map<Integer, Double> durabilidades() {
    return Map.copyOf(this.durabilidadPistas);
  }
}
