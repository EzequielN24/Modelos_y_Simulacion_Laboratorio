package queueing.sim.strategies;

/**
 * Agrupa las estrategias que definen cómo se toman las decisiones durante la ejecución de una
 * simulación de colas.
 *
 * <p>Las estrategias determinan qué servidor se selecciona cuando puede comenzar un servicio, qué
 * cola recibe una entidad que llega cuando no hay ningún servidor disponible y qué cola suministra
 * la siguiente entidad en espera cuando un servidor queda libre.
 *
 * @param serverSelection estrategia utilizada para seleccionar un servidor entre los disponibles
 * @param arrivalQueueSelection estrategia utilizada para seleccionar una cola para una entidad que
 *     llega cuando no hay ningún servidor disponible
 * @param departureQueueSelection estrategia utilizada para seleccionar la cola de la que se toma
 *     una entidad en espera cuando un servidor queda libre
 */
public record StrategyBag(
    ServerSelectionStrategy serverSelection,
    QueueSelectionStrategy arrivalQueueSelection,
    QueueSelectionStrategy departureQueueSelection) {}
