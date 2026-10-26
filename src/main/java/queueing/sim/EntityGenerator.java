package queueing.sim;

/** Interfaz para generar entidades */
@FunctionalInterface
interface EntityGenerator {
  /** Genera una nueva entidad */
  Entity generate();
}
