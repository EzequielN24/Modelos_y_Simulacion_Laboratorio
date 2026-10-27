# Random variate gen lab

Este proyecto, con fines educativos, implementa generadores de variables aleatorias.

## 🚀 Comenzando

No necesitás tener Maven instalado en tu sistema global para correr este proyecto, ya que incluye **Maven Wrapper**.

### Prerrequisitos

* **Java JDK 21** o superior instalado.

### Instalación y Compilación

1. Cloná este repositorio:
   ```bash
   git clone https://github.com/dglabella/random-variate-gen-lab.git
   cd random-variate-gen-lab
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

## 🧪 Ejecutando las Pruebas

El proyecto cuenta con pruebas unitarias (`JUnit`) para verificar la consistencia matemática de las distribuciones (Exponencial, Uniforme, Normal, Discreta).

Para ejecutar los tests, corré:
```bash
# En Linux/macOS
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
