Simulador de Procesos
Este proyecto es un Simulador de Procesos desarrollado en Java que simula la gestión de procesos en un sistema operativo, incluyendo la asignación de memoria RAM, ejecución de procesos, cola de espera y un historial de procesos. La aplicación cuenta con una interfaz gráfica (usando Swing) y un gráfico en tiempo real (usando JFreeChart) que muestra el uso de memoria.
Características

Gestión de Procesos: Permite agregar procesos con nombre, RAM requerida y duración. Los códigos de cada proceso se generan automáticamente de forma secuencial (1, 2, 3, ...).
Asignación de Memoria: Simula un sistema con 1 GB (1024 MB) de RAM, asignando memoria a los procesos en ejecución y colocando en cola de espera los procesos que exceden la memoria disponible.
Interfaz Gráfica: Incluye campos para ingresar datos, combo boxes para mostrar procesos en ejecución y en espera, y áreas de texto para el estado actual y el historial de procesos.
Gráfico de Memoria: Muestra el uso de memoria en tiempo real con un eje Y fijo de 0 a 1024 MB.
Control de Procesos: Permite detener procesos seleccionados desde la lista de procesos en ejecución.
Historial de Procesos: Registra todos los procesos creados, mostrando su código, nombre, RAM, duración y estado final.

Requisitos

Java: JDK 8 o superior.
Biblioteca JFreeChart: Necesaria para los gráficos de uso de memoria. (Están incluidas en el repositorio en la carpeta de "Librerias"

Dependencias (si usas Maven)
Agrega las siguientes dependencias al archivo pom.xml:
<dependencies>
    <dependency>
        <groupId>org.jfree</groupId>
        <artifactId>jfreechart</artifactId>
        <version>1.5.3</version>
    </dependency>
</dependencies>

Instalación

Clona el repositorio:
- Recomendación, despues de descargar el código, guardalo en los documentos.
git clone https://github.com/soli7034/SimuladorProcesos
cd <Ruta donde guardaste el proyecto "docuemntos por defecto">


Configura el entorno:

Asegúrate de tener Java instalado (java -version "24" 2025-03-18).
Si usas Maven, asegúrate de que el archivo pom.xml incluya la dependencia de JFreeChart. Luego, ejecuta:mvn clean install




Compila el proyecto:

Si no usas Maven, compila los archivos .java manualmente:javac -cp .:jfreechart-1.5.3.jar:jcommon-1.0.23.jar *.java




Ejecuta la aplicación:

Con Maven:mvn exec:java -Dexec.mainClass="SimuladorProcesos"


Sin Maven:java -cp .:jfreechart-1.5.3.jar:jcommon-1.0.23.jar SimuladorProcesos



Nota: Para entornos Windows, reemplaza : por ; en los comandos de classpath.


Uso

Iniciar la aplicación:

Ejecuta el programa y se abrirá una ventana con la interfaz gráfica.


Agregar un proceso:

Ingresa el nombre, la cantidad de RAM (en MB) y la duración (en segundos) en los campos correspondientes.
Haz clic en "Agregar Proceso".
El código del  proceso se asigna automáticamente (empezando desde 1).
Si hay suficiente RAM (máximo 1024 MB), el proceso se ejecutará; de lo contrario, se añadirá a la cola de espera.


Visualizar procesos:

Los procesos en ejecución se muestran en el combo box "Procesos en Ejecución".
Los procesos en espera se muestran en el combo box "Cola de Espera".
El estado actual (RAM disponible y lista de procesos) se muestra en el área de texto "Estado Actual".
El historial completo de procesos se muestra en el área de texto "Historial de Procesos".


Detener un proceso:

Selecciona un proceso en el combo box "Procesos en Ejecución" y haz clic en "Detener Seleccionado".
El proceso se marcará como TERMINADO, liberará su RAM, y los procesos en espera se verificarán para su ejecución.


Gráfico de memoria:

El gráfico en la parte derecha muestra el uso de memoria en tiempo real, con el eje Y fijo entre 0 y 1024 MB.



Estructura del Proyecto
<tu-repositorio>/
├── SimuladorProcesos.java  # Clase principal con la lógica del simulador y la interfaz gráfica
├── Proceso.java           # Clase que define un proceso (Cód d proceso, nombre, RAM, duración, estado)
├── README.md              # Este archivo
└── pom.xml                # (Opcional) Archivo de configuración de Maven

Contribuir

Haz un fork del repositorio.
Crea una rama para tus cambios (git checkout -b feature/nueva-funcionalidad).
Realiza tus cambios y haz commit (git commit -m "Añadir nueva funcionalidad").
Sube tus cambios (git push origin feature/nueva-funcionalidad).
Crea un Pull Request en GitHub.


Contacto
Si tienes preguntas o sugerencias, contáctame a través de [osolizl@miumg.edu.gt] o abre un issue en el repositorio.
