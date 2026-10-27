package queueing.sim;

/** Entidad que participa en la simulacion */
final class Entity implements EntityView {

  /** Id de la entidad */
  private final long id;

  /** Tiempo en que arribo la entidad. */
  private double arrivalTime;

  Entity(long id) {
    this.id = id;
  }

  @Override
  public long id() {
    return this.id;
  }

  double arrivalTime() {
    return this.arrivalTime;
  }

  void arrivalTime(double arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

  @Override
  public String toString() {
    return "Entity[id=" + id() + "]";
  }
}
