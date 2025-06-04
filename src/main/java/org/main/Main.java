package org.main;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import dao.clases.ApuestaDAOimp;
import dao.clases.CasinoDAOimp;
import dao.interfaces.ApuestaDAO;
import dao.interfaces.CasinoDAO;
import model.Apuesta;
import model.Casino;
import model.Jugador;
import model.Ruleta;
import util.BDDcode;
import util.BDDconnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {
    // Logger para registrar eventos, errores e información del sistema
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws SQLException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Bienvenido al Casino ");
        // Preguntar si desea eliminar registros existentes en la base de datos
        System.out.println("Desea Eliminar los registros? (S/N)");

        String respuesta = scanner.nextLine().trim().toUpperCase();

        if (respuesta.equals("S")) {
            logger.warn("Eliminando registros de la base de datos");
            BDDcode.eliminarTablas(BDDconnection.Conexion());
        } else if (respuesta.equals("N")) {
            logger.info("Decidió mantener los registros existentes");
        } else {
            logger.error("Respuesta no válida: {}", respuesta);
            BDDcode.salir();
        }
        // Conectar a la base de datos y crear tablas si es necesario
        logger.info("Conectando a la base de datos y creando tablas si es necesario");
        BDDconnection.Conexion();
        BDDcode.crearTablas();
        BDDcode.descansar(1);

        // Mostrar el menú de selección de casino
        CasinoDAO casinoDAO = new CasinoDAOimp();
        int selecion = Casino.MenuSelecionarCasino();

        int opcion;
        // Bucle principal del programa para mostrar menú y ejecutar opciones
        do {
            System.out.println("\n🎮╔═══════════════════════════════════════════════╗");
            System.out.println("   ║               MENÚ PRINCIPAL 🎲              ║");
            System.out.println("   ╠═══════════════════════════════════════════════╣");
            System.out.println("   ║   1️⃣  - Iniciar sesión                       ║");
            System.out.println("   ║   2️⃣  - Registrar jugador                    ║");
            System.out.println("   ║   3️⃣  - Ver jugadores                        ║");
            System.out.println("   ║   4️⃣  - Eliminar jugador                     ║");
            System.out.println("   ║   5️⃣  - Ver casino                           ║");
            System.out.println("   ║   6️⃣  - Ver Ultimas Apuestas                 ║");
            System.out.println("   ║   7️⃣  - Cambiar de casino                    ║");
            System.out.println("   ╠═══════════════════════════════════════════════╣");
            System.out.println("   ║   8️⃣  - Salir 🚪                             ║");
            System.out.println("   ╚═══════════════════════════════════════════════╝");
            System.out.print("👉 Seleccione una opción: ");

            while (!scanner.hasNextInt()) {

                System.out.print("Por favor, ingrese un número: ");
                scanner.next();
            }

            opcion = scanner.nextInt();
            scanner.nextLine();
            logger.info("Opción seleccionada: {}", opcion);
            switch (opcion) {
                case 1:
                    // Iniciar el juego de ruleta con el casino seleccionado
                    logger.info("Iniciando juego de ruleta para el casino ID {}", selecion);
                    Ruleta.Jugar(selecion);
                    break;
                case 2:
                    // Registrar un nuevo jugador
                    Jugador.RegistrarJugador();
                    continue;
                case 3:
                    // Ver la lista de jugadores registrados
                    Jugador.verJugadores();
                    break;
                case 4:
                    // Eliminar un jugador existente
                    Jugador.eliminarJugador();
                    break;
                case 5:
                    // Mostrar información del casino actual
                    Casino.mostrarCasinoActual(selecion, casinoDAO.buscarPorId(selecion));
                    break;
                case 6:
                    // Ver las últimas 10 apuestas realizadas
                    ApuestaDAO apuestaDAO = new ApuestaDAOimp(BDDconnection.Conexion());
                    List<Apuesta> ultimasApuestas = apuestaDAO.obtenerUltimas10Apuestas();

                    System.out.println("Últimas 10 apuestas:");
                    for (Apuesta apuesta : ultimasApuestas) {
                        System.out.println(
                                "Jugador ID: " + apuesta.getJugadorId()
                                        + " | Tipo: " + apuesta.getTipo()
                                        + " | Valor: " + apuesta.getValor()
                                        + " | Monto: " + apuesta.getMonto()
                                        + " | Ganó: " + (apuesta.isGano() ? "Sí" : "No")
                                        + " | Fecha: " + apuesta.getFecha()
                                        + " | Casino: " + apuesta.getCasinoNombre()
                        );
                    }
                    BDDcode.descansar(2);
                    break;
                case 7:
                    // Cambiar de casino
                    logger.info("Cambio de casino solicitado");
                    System.out.println("Cambiando de casino...");
                    Casino.listaCasinos();
                    System.out.println("Seleccione el ID del casino al que desea cambiar (0 para salir): ");
                    while (!scanner.hasNextInt()) {
                        System.out.print("Por favor, ingrese un número válido: ");
                        scanner.next();
                    }
                    selecion = scanner.nextInt();
                    scanner.nextLine();

                    if (selecion == 0) {
                        logger.info("Usuario eligió salir sin cambiar de casino");
                        System.out.println("No se seleccionó ningún casino. Saliendo del programa.");
                        BDDcode.salir();
                    }
                    logger.info("Casino cambiado. Nuevo ID: {}, Nombre: {}", selecion, casinoDAO.buscarPorId(selecion).getNombre());

                    System.out.println("Casino cambiado exitosamente.");
                    continue;

                case 8:
                    // Salir del programa
                    logger.info("Usuario salió del programa");
                    System.out.println("¡Gracias por jugar! Hasta luego. 👋");
                    BDDcode.salir();
                    break;

                default:
                    logger.warn("Opción inválida ingresada: {}", opcion);
                    System.out.println("Opción inválida. Intente nuevamente.");
                    break;
            }

        } while (opcion != 8);
        logger.info("Fin del programa");

    }
}