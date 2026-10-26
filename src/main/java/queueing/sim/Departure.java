package queueing.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import queueing.sim.probes.Probe;

/**
 * Representa la finalización del servicio para una entidad en un servidor.
 *
 * <p>Al ejecutarse, el evento libera el servidor. Si hay alguna entidad esperando en una cola, la
 * estrategia de selección de cola de salida determina qué cola proporciona la siguiente entidad.
 * Dicha entidad comienza inmediatamente a recibir servicio en el servidor y se programa un nuevo
 * evento de salida.
 */
final class Departure implements Event {

  /** El tiempo de simulacion en el que ocurre la finalizacion del servicio. */
  private final double clock;

  /** La entidad cuya finalizacion del servicio se esta procesando. */
  private final Entity entity;

  /** El servidor cuya finalizacion del servicio se esta procesando. */
  private final Server server;

  /**
   * Crea un evento de finalizacion del servicio.
   *
   * @param clock el tiempo de simulacion en el que ocurre la finalizacion del servicio
   * @param entity la entidad cuya finalizacion del servicio se esta procesando
   * @param server el servidor cuya finalizacion del servicio se esta procesando
   * @throws NullPointerException si {@code entity} o {@code server} es {@code null}
   */
  Departure(double clock, Entity entity, Server server) {
    this.clock = clock;
    this.entity = Objects.requireNonNull(entity);
    this.server = Objects.requireNonNull(server);
  }

  @Override
  public double clock() {
    return this.clock;
  }

  @Override
  public int priority() {
    return 0;
  }

  /**
   * Ejecuta la finalización del servicio.
   *
   * <p>Se completa el servicio de la entidad actual y se libera el servidor. Si hay entidades
   * esperando en alguna cola, la estrategia de selección de cola de salida determina qué cola
   * proporciona la siguiente entidad. Dicha entidad se asigna al mismo servidor y se programa un
   * nuevo evento de salida.
   *
   * @param context el contexto en el que se ejecuta el evento
   */
  @Override
  public void execute(SimulationContext context) {
    InternalSimulationContext internalContext = (InternalSimulationContext) context;

    this.server.release();

    for (Probe probe : internalContext.probes()) {
      probe.entityDeparted(this.clock, this.entity);
    }

    List<QueueView> queues = internalContext.model().queues();
    List<QueueView> queues_con_esperas = new ArrayList<>();
    for (QueueView q : queues) {
      if (q.size() > 0) {
        queues_con_esperas.add(q);
      }
    }

    if (queues_con_esperas.isEmpty()) {
      for (Probe probe : internalContext.probes()) {
        probe.serverBecameIdle(this.clock, this.server);
      }
    } else {
      int queue_id =
          internalContext.strategyBag().departureQueueSelection().select(queues_con_esperas);
      Queue queue_selecionada = internalContext.model().queue(queue_id);
      Entity entidad = queue_selecionada.remove();
      this.server.seize(entidad);

      for (Probe probe : internalContext.probes()) {
        probe.queueSizeChanged(this.clock, queue_selecionada);
        probe.serviceStarted(this.clock, entidad, this.server);
      }

      double tiempo_duracion = this.server.serviceDuration(internalContext.randomGenerator());
      context.schedule(new Departure(this.clock + tiempo_duracion, entidad, this.server));
    }
  }

  @Override
  public String toString() {
    return "Departure[clock=" + this.clock + ", entity=" + this.entity + "]";
  }
}
