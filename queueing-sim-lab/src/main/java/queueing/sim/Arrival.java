package queueing.sim;

import java.util.List;
import java.util.Objects;
import queueing.sim.probes.Probe;

/**
 * Representa la llegada de una entidad al sistema
 *
 * <p>Cuando se ejecuta, el evento busca servidores disponibles en todo el sistema de colas. Si un
 * servidor está disponible, la estrategia de selección de servidores determina qué servidor recibe
 * la entidad que llega. De lo contrario, la estrategia de selección de colas de llegada determina
 * qué cola recibe la entidad.
 *
 * <p>El evento siempre agenda la próxima llegada.
 */
final class Arrival implements Event {

  /** El tiempo de simulación en el que ocurre esta llegada. */
  private final double clock;

  /** La entidad que llega al sistema. */
  private final Entity entity;

  /** Genera el tiempo hasta la próxima llegada. */
  private final TimeGenerator interarrivalTimeGenerator;

  /** Genera la próxima entidad que llegará. */
  private final EntityGenerator entityGenerator;

  /**
   * Crea un evento de llegada.
   *
   * @param clock el tiempo de simulación en el que ocurre la llegada
   * @param entity la entidad que llega al sistema
   * @param interarrivalTimeGenerator el generador utilizado para determinar el siguiente tiempo
   *     entre llegadas
   * @param entityGenerator el generador utilizado para crear la siguiente entidad
   * @throws NullPointerException si algún parámetro de objeto es {@code null}
   */
  Arrival(
      double clock,
      Entity entity,
      TimeGenerator interarrivalTimeGenerator,
      EntityGenerator entityGenerator) {
    this.clock = clock;
    this.entity = Objects.requireNonNull(entity);
    this.interarrivalTimeGenerator = Objects.requireNonNull(interarrivalTimeGenerator);
    this.entityGenerator = Objects.requireNonNull(entityGenerator);
  }

  @Override
  public double clock() {
    return this.clock;
  }

  @Override
  public int priority() {
    return 1;
  }

  /**
   * Ejecuta la llegada.
   *
   * <p>Si al menos un servidor está disponible, la estrategia de selección de servidores determina
   * qué servidor recibe la entidad que llega. El servidor seleccionado se ocupa, comienza a atender
   * a la entidad y se programa un evento de salida.
   *
   * <p>Si no hay ningún servidor disponible, la estrategia de selección de colas de llegada
   * determina qué cola recibe la entidad.
   *
   * <p>La próxima llegada siempre se programa.
   *
   * @param context el contexto de simulación
   */
  @Override
  public void execute(SimulationContext context) {

    InternalSimulationContext internalContext = (InternalSimulationContext) context;

    this.entity.arrivalTime(this.clock);

    for (Probe probe : internalContext.probes()) {
      probe.entityArrived(this.clock, this.entity);
    }

    List<ServerView> serverDisponibles = internalContext.model().availableServers();

    if (serverDisponibles.isEmpty()) {
      List<QueueView> cola = internalContext.model().queues();
      int cola_seleccionada = internalContext.strategyBag().arrivalQueueSelection().select(cola);
      internalContext.model().queue(cola_seleccionada).add(this.entity);

      for (Probe probe : internalContext.probes()) {
        probe.entityEnteredQueue(
            this.clock, this.entity, internalContext.model().queue(cola_seleccionada));
        probe.queueSizeChanged(this.clock, internalContext.model().queue(cola_seleccionada));
      }

    } else {
      int server_seleccionado =
          internalContext.strategyBag().serverSelection().select(serverDisponibles);
      internalContext.model().server(server_seleccionado).seize(this.entity);
      double duracion_servicio =
          internalContext
              .model()
              .server(server_seleccionado)
              .serviceDuration(internalContext.randomGenerator());
      context.schedule(
          new Departure(
              this.clock + duracion_servicio,
              this.entity,
              internalContext.model().server(server_seleccionado)));

      for (Probe probe : internalContext.probes()) {
        probe.serverBecameBusy(this.clock, internalContext.model().server(server_seleccionado));
        probe.serviceStarted(
            this.clock, this.entity, internalContext.model().server(server_seleccionado));
      }
    }

    Entity proxima_entidad = this.entityGenerator.generate();
    double tiempo_proxima_llegada =
        this.interarrivalTimeGenerator.generate(internalContext.randomGenerator());
    context.schedule(
        new Arrival(
            this.clock + tiempo_proxima_llegada,
            proxima_entidad,
            this.interarrivalTimeGenerator,
            this.entityGenerator));
  }

  @Override
  public String toString() {
    return "Arrival[clock=" + this.clock + ", entity=" + this.entity + "]";
  }
}
