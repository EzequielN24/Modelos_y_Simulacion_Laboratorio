# queueing-sim-lab

Es un proyecto hecho en Java de simulación de sistemas de colas, orientado a eventos desarrollado con Maven con proposito educativo.

## 🚀 Comenzando

No necesitás tener Maven instalado en tu sistema global para correr este proyecto, ya que incluye **Maven Wrapper**.

## Descripción

Este repositorio contiene la implementación base de un modelo de simulación de colas, incluyendo:

- Entidades y eventos dirigidas por la simulación.
- Estructuras de cola y servidores
- Estrategias de selección de servidor y cola
- Lista de eventos futuros
- Pruebas unitarias para el comportamiento principal

### Prerrequisitos

* **Java JDK 21** o superior instalado.

### Instalación y Compilación

1. Cloná este repositorio:
   ```bash
   git clone https://github.com/dglabella/queueing-sim-lab.git
   cd queueing-sim-lab
   ```

2. Compilá el proyecto usando el Wrapper:
   * **En Linux / macOS:**
     ```bash
     ./mvnw clean package
     ```
   * **En Windows (CMD / PowerShell):**
     ```cmd
     mvnw clean package
     ```

## Estructura principal

```text
src/
  main/java/queueing/sim/       Código fuente de la simulación
  test/java/queueing/sim/       Pruebas unitarias
```

## 🧪 Ejecutando las Pruebas

El proyecto cuenta con pruebas unitarias (`JUnit`) para verificar la consistencia matemática de las distribuciones (Exponencial, Uniforme, Normal, Discreta).

Para ejecutar los tests, corré:
```bash
# En Linux/macOSqueueing/sim
./mvnw test

# En Windows
mvnw test
```

## 🛠️ Tecnologías Utilizadas

* **Java 21**
* **Maven** (gestionado vía Maven Wrapper)
* **VS Code** (Entorno de desarrollo sugerido)

## 📄 Licencia

Este proyecto está bajo la Licencia MIT; podés ver el archivo [LICENSE.md](LICENSE.md) para más detalles.




