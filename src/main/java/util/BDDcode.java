package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BDDcode {
    private static final Logger logger = LogManager.getLogger(BDDcode.class);
    /* * Método para salir del programa.
     * Cierra la conexión a la base de datos si está abierta.
     * Utiliza System.exit(0) para finalizar la ejecución.
     */
    public static void salir() {
        System.out.println("Saliendo del programa...");

        try {
            if (BDDconnection.Conexion() != null) {
                logger.info("Se está cerrando la conexión a la base de datos.");
                BDDconnection.Conexion().close();

            }
        } catch (SQLException e) {
            logger.error("Error al cerrar la conexión: {}", e.getMessage());
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        } finally {
            logger.info("Conexión a la base de datos cerrada.");
            System.out.println("Conexión cerrada correctamente.");
        }
        System.out.println("Saliendo del programa... ");
        System.exit(0);
    }
    /* * Método para crear las tablas necesarias en la base de datos.
     * Utiliza sentencias SQL CREATE TABLE para crear las tablas Casinos, Jugador y Apuesta.
     * Si las tablas ya existen, no se crean nuevamente.
     */
    public static void crearTablas() {
                    String crearCasino = """
                    CREATE TABLE IF NOT EXISTS Casinos (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(100),
                        saldo DECIMAL(10, 2)
                    );
                """;

                    String crearJugador = """
                CREATE TABLE IF NOT EXISTS Jugador (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100),
                    saldo DECIMAL(10, 2),
                    contrasena VARCHAR(100)
                );
            """;

                    String crearApuesta = """
            CREATE TABLE IF NOT EXISTS Apuesta (
                id INT AUTO_INCREMENT PRIMARY KEY,
                jugador_id INT,
                tipo VARCHAR(20),
                valor VARCHAR(20),
                monto DECIMAL(10, 2),
                gano BOOLEAN,
                fecha TIMESTAMP,
                casino_nombre VARCHAR(100),
                FOREIGN KEY (jugador_id) REFERENCES Jugador(id)
            );
            """;

        try (Statement stmt = BDDconnection.Conexion().createStatement()) {
            stmt.execute(crearCasino);
            stmt.execute(crearJugador);
            stmt.execute(crearApuesta);
            System.out.println("✅ Tablas creadas correctamente.");
        } catch (SQLException e) {
            logger.error("Error al crear las tablas: {}", e.getMessage());
            System.out.println("❌ Error al crear las tablas: " + e.getMessage());
        }
    }

    //--------------------------------------------------------------------------------------------
    /**
     * Método para eliminar todas las tablas de la base de datos.
     * Utiliza sentencias SQL DROP TABLE para eliminar las tablas Apuesta, Jugador y Casinos.
     *
     * @param connection Conexión a la base de datos.
     */
    public static void eliminarTablas(Connection connection) {
        String dropApuesta = "DROP TABLE IF EXISTS Apuesta";
        String dropJugador = "DROP TABLE IF EXISTS Jugador";
        String dropCasino = "DROP TABLE IF EXISTS Casinos";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(dropApuesta);
            stmt.execute(dropJugador);
            stmt.execute(dropCasino);
            System.out.println("✅ Todas las tablas fueron eliminadas correctamente.");
        } catch (SQLException e) {
            logger.error("Error al eliminar las tablas: {}", e.getMessage());
            System.out.println("❌ Error al eliminar las tablas: " + e.getMessage());
        }
    }
    /* * Método para descansar el hilo actual durante un número específico de segundos.
     * Utiliza Thread.sleep() para pausar la ejecución.
     *
     * @param segundos Número de segundos a descansar.
     */
    public static void descansar(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException e) {

            Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
            System.err.println("Error al descansar: " + e.getMessage());
        }
    }


}
