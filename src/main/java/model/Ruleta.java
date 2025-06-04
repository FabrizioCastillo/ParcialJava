package model;

import dao.JugadorDAOimp;
import dao.clases.ApuestaDAOimp;
import dao.clases.CasinoDAOimp;
import dao.interfaces.ApuestaDAO;
import dao.interfaces.CasinoDAO;
import dao.interfaces.JugadorDAO;
import util.BDDcode;
import util.BDDconnection;


import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Ruleta {
    private Jugador jugador;
    private Casino casino;
    private JugadorDAO jugadorDAO;
    private CasinoDAO casinoDAO;
    private ApuestaDAO apuestaDAO;
    private static Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    static Logger logger = LogManager.getLogger(Ruleta.class);

    //--------------------------------------------------------------------------------------------
    public Ruleta(Jugador jugador, Casino casino, JugadorDAO jugadorDAO, CasinoDAO casinoDAO) {
        this.jugador = jugador;
        this.casino = casino;
        this.jugadorDAO = jugadorDAO;
        this.casinoDAO = casinoDAO;
        this.apuestaDAO = new ApuestaDAOimp(BDDconnection.Conexion());
    }

    //--------------------------------------------------------------------------------------------
    /* * Simula una apuesta a un número específico en la ruleta.
     * Descuenta el monto apostado, genera un número aleatorio,
     * evalúa si el jugador ganó, actualiza los saldos y guarda la apuesta en la base de datos.
     */
    public void tirarNumero(int numeroApostado, float monto) {
        jugador.setSaldo(jugador.getSaldo() - monto);
        System.out.println("ID del jugador antes de apostar: " + jugador.getId());

        int bola = (int) (Math.random() * 37);
        System.out.println("La bola dio: " + bola);

        boolean gano = bola == numeroApostado;

        if (gano) {
            System.out.println("¡Felicidades! Has ganado.");
            casino.setSaldo(casino.getSaldo() - monto * 35);
            jugador.setSaldo(jugador.getSaldo() + monto * 36);
            System.out.println("Ganaste: " + (monto * 36));
            BDDcode.descansar(2);
        } else {
            System.out.println("Perdiste la apuesta.");
            casino.setSaldo(casino.getSaldo() + monto);
            System.out.println("Perdiste: " + monto);
            BDDcode.descansar(2);
        }
        try {
            logger.info("Apuesta realizada por el jugador: " + jugador.getNombre() + ", Número apostado: " + numeroApostado + ", Monto: " + monto);
        } catch (Exception e) {
            logger.error("Error al registrar la apuesta: ", e);
        }
        try {
            Apuesta apuesta = new Apuesta(
                    jugador.getId(),
                    "numero",
                    String.valueOf(numeroApostado),
                    monto,
                    gano,
                    LocalDateTime.now(),
                    casino.getNombre()
            );
            apuestaDAO.guardar(apuesta);

            System.out.println("Saldo actual: " + jugador.getSaldo());
            casinoDAO.actualizarSaldo(casino);
            jugadorDAO.actualizarSaldo(jugador);

        } catch (Exception e) {

            logger.error("Error al guardar la apuesta numero: ", e);
        }
    }

    //--------------------------------------------------------------------------------------------
    /* * Simula una apuesta a color
        * Descuenta el monto, genera el número, evalúa si ganó,
        * actualiza saldos y guarda la apuesta en la base de datos.
     */
    private static final Set<Integer> ROJOS = Set.of(
            1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36
    );

    public void tirarColor(int color, float monto) {
        int bola = (int) (Math.random() * 37);
        boolean esRojo = ROJOS.contains(bola);

        jugador.setSaldo(jugador.getSaldo() - monto);

        System.out.println("La bola dio: " + bola);

        if ((esRojo && color == 0) || (!esRojo && color == 1)) {
            casino.setSaldo(casino.getSaldo() - monto);
            jugador.setSaldo(jugador.getSaldo() + monto * 2);
            //se descuenta
            System.out.println("¡Felicidades! Has ganado.");
            System.out.println("Ganaste: " + (monto * 2));
            BDDcode.descansar(2);

        } else {
            System.out.println("Perdiste la apuesta.");
            casino.setSaldo(casino.getSaldo() + monto);
            System.out.println("Perdiste: " + monto);
            BDDcode.descansar(2);
        }

        boolean gano = (esRojo && color == 0) || (!esRojo && color == 1);
        String colorApostado = (color == 0) ? "rojo" : "negro";
        //Se guarda la apuesta en la base de datos
        try {
            Apuesta apuesta = new Apuesta(
                    jugador.getId(),
                    "color",
                    colorApostado,
                    monto,
                    gano,
                    LocalDateTime.now(),
                    casino.getNombre()
            );
            apuestaDAO.guardar(apuesta);

            System.out.println("Saldo actual: " + jugador.getSaldo());
            casinoDAO.actualizarSaldo(casino);
            jugadorDAO.actualizarSaldo(jugador);

        } catch (Exception e) {

            logger.error("Error al guardar la apuesta color: ", e);
        }

    }

    //--------------------------------------------------------------------------------------------
    /*  * Simula una apuesta a par o impar en la ruleta.
     * Descuenta el monto, genera el número, evalúa si ganó,
     * actualiza saldos y guarda la apuesta en la base de datos.
     */
    public void tirarParImpar(int eleccion, float monto) {
        jugador.setSaldo(jugador.getSaldo() - monto);
        int bola = (int) (Math.random() * 37);
        System.out.println("La bola dio: " + bola);

        boolean gano = (bola != 0) && (
                (bola % 2 == 0 && eleccion == 0) ||
                        (bola % 2 != 0 && eleccion == 1)
        );

        if (gano) {
            jugador.setSaldo(jugador.getSaldo() + monto * 2);
            casino.setSaldo(casino.getSaldo() - monto);
            System.out.println("¡Felicidades! Has ganado.");
            System.out.println("Ganaste: " + (monto * 2));
            BDDcode.descansar(2);
        } else {
            System.out.println("Perdiste la apuesta.");
            casino.setSaldo(casino.getSaldo() + monto);
            System.out.println("Perdiste: " + monto);
            BDDcode.descansar(2);
        }

        String valorApostado = (eleccion == 0) ? "par" : "impar";
        // Se guarda la apuesta en la base de datos
        try {
            Apuesta apuesta = new Apuesta(
                    jugador.getId(),
                    "par/impar",
                    valorApostado,
                    monto,
                    gano,
                    LocalDateTime.now(),
                    casino.getNombre()
            );
            apuestaDAO.guardar(apuesta);

            System.out.println("Saldo actual: " + jugador.getSaldo());
            BDDcode.descansar(1);
            // Actualizar los saldos en la base de datos
            casinoDAO.actualizarSaldo(casino);
            jugadorDAO.actualizarSaldo(jugador);

        } catch (Exception e) {

            logger.error("Error al guardar la apuesta par/impar: ", e);
        }
    }

    //--------------------------------------------------------------------------------------------
    /*     * Simula una apuesta a una docena (1ra, 2da o 3ra) en la ruleta.
     * Descuenta el monto, genera el número, evalúa si ganó,
     * actualiza saldos y guarda la apuesta en la base de datos.
     */
    public void tirarDocena(int docena, float monto) {
        jugador.setSaldo(jugador.getSaldo() - monto);
        int bola = (int) (Math.random() * 37);

        System.out.println("Docena: " + docena + ", Bola: " + bola);

        boolean gano = (bola >= 1 && bola <= 12 && docena == 1) ||
                (bola >= 13 && bola <= 24 && docena == 2) ||
                (bola >= 25 && bola <= 36 && docena == 3);

        if (gano) {
            jugador.setSaldo(jugador.getSaldo() + monto * 3);
            casino.setSaldo(casino.getSaldo() - monto * 2);
            System.out.println("¡Felicidades! Has ganado.");
            System.out.println("Ganaste: " + (monto * 3));
            BDDcode.descansar(2);
        } else {
            System.out.println("Perdiste la apuesta.");
            casino.setSaldo(casino.getSaldo() + monto);
            System.out.println("Perdiste: " + monto);
            BDDcode.descansar(2);
        }
        String valorApostado = switch (docena) {
            case 1 -> "1ra";
            case 2 -> "2da";
            case 3 -> "3ra";
            default -> "desconocido";
        };

        // Se guarda la apuesta en la base de datos
        try {
            Apuesta apuesta = new Apuesta(
                    jugador.getId(),
                    "docena",
                    valorApostado,
                    monto,
                    gano,
                    LocalDateTime.now(),
                    casino.getNombre()
            );
            apuestaDAO.guardar(apuesta);

            System.out.println("Saldo actual: " + jugador.getSaldo());
            BDDcode.descansar(1);
            // Actualizar los saldos en la base de datos
            casinoDAO.actualizarSaldo(casino);
            jugadorDAO.actualizarSaldo(jugador);

        } catch (Exception e) {

            logger.error("Error al guardar la apuesta docena: ", e);
        }
    }

    //--------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------
    /*     * Simula una apuesta a una columna (1, 2 o 3) en la ruleta.
     * Descuenta el monto, genera el número, evalúa si ganó,
     * actualiza saldos y guarda la apuesta en la base de datos.
     */
    public void tirarColumna(int columna, float monto) {
        jugador.setSaldo(jugador.getSaldo() - monto);
        int bola = (int) (Math.random() * 37);

        System.out.println("La bola cayó en el número: " + bola);

        String nombreColumna;
        int resto = bola % 3;

        if (bola == 0) {
            nombreColumna = "Sin columna";
            System.out.println("El número 0 no pertenece a ninguna columna.");
        } else if (resto == 1) {
            nombreColumna = "Columna 1";
        } else if (resto == 2) {
            nombreColumna = "Columna 2";
        } else {
            nombreColumna = "Columna 3";
        }

        System.out.println("Resultado real: " + nombreColumna);

        boolean gano = (bola % 3 == 1 && columna == 1) ||
                (bola % 3 == 2 && columna == 2) ||
                (bola % 3 == 0 && bola != 0 && columna == 3);

        if (gano) {
            jugador.setSaldo(jugador.getSaldo() + monto * 3);
            casino.setSaldo(casino.getSaldo() - monto * 2);
            System.out.println("¡Felicidades! Has ganado.");
            System.out.println("Ganaste: " + (monto * 3));
            BDDcode.descansar(2);
        } else {
            System.out.println("Perdiste la apuesta.");
            casino.setSaldo(casino.getSaldo() + monto);
            System.out.println("Perdiste: " + monto);
            BDDcode.descansar(2);
        }

        String valorApostado = "Columna " + columna;
        // Se guarda la apuesta en la base de datos
        try {
            Apuesta apuesta = new Apuesta(
                    jugador.getId(),
                    "Columna",
                    valorApostado,
                    monto,
                    gano,
                    LocalDateTime.now(),
                    casino.getNombre()
            );
            apuestaDAO.guardar(apuesta);

            System.out.println("Saldo actual: " + jugador.getSaldo());
            BDDcode.descansar(1);
            // Actualizar los saldos en la base de datos
            casinoDAO.actualizarSaldo(casino);
            jugadorDAO.actualizarSaldo(jugador);

        } catch (Exception e) {

            logger.error("Error al guardar la apuesta columna: ", e);
        }
    }
    /* * Método para iniciar el juego de ruleta.
     * Permite al jugador iniciar sesión, seleccionar un casino y realizar apuestas.
     * Muestra un menú con opciones de apuestas y acceso al cajero.
     */

    public static void Jugar(int idCajeroSeleccionado) {
        // Inicio de sesión del jugador
        System.out.println("Ingrese su nombre de usuario:");
        String nombreUsuario = scanner.next();
        System.out.println("Ingrese su contraseña:");
        String contrasena = scanner.next();

        JugadorDAO jugadorDAO = new JugadorDAOimp();
        Jugador jugador = jugadorDAO.buscarPorNombre(nombreUsuario);

        if (jugador == null || !jugador.getContrasena().equals(contrasena)) {
            System.out.println("Usuario o contraseña incorrectos. Por favor, inténtelo de nuevo.");
            BDDcode.descansar(2);
            return;
        }

        System.out.println("✅ Inicio de sesión exitoso. Bienvenido, " + jugador.getNombre());
        BDDcode.descansar(2);

        int opcion;

        CasinoDAO casinoDAO = new CasinoDAOimp();
        // Carga el casino por el ID del cajero seleccionado
        Casino casino = casinoDAO.buscarPorId(idCajeroSeleccionado);
        System.out.println("Casino seleccionado: " + casino.getNombre() + ", Saldo: " + casino.getSaldo());
        logger.info("Jugador '{}' ha iniciado sesión en el casino '{}'", jugador.getNombre(), casino.getNombre());


        Ruleta ruleta = new Ruleta(jugador, casino, jugadorDAO, casinoDAO);
        // Menú principal de juego

        do {
            System.out.println("\n🎰╔══════════════════════════════════════╗");
            System.out.printf("   ║           MODO RULETA 🎲            ║\n");
            System.out.println("   ╠══════════════════════════════════════╣");
            System.out.printf("   ║ 💰 Saldo actual: $%-20.2f ║\n", jugador.getSaldo());
            System.out.println("   ╠══════════════════════════════════════╣");
            System.out.println("   ║ 1️⃣  - Jugar Número                  ║");
            System.out.println("   ║ 2️⃣  - Jugar Color                   ║");
            System.out.println("   ║ 3️⃣  - Jugar Par o Impar            ║");
            System.out.println("   ║ 4️⃣  - Jugar Docena                 ║");
            System.out.println("   ║ 5️⃣  - Jugar Columna                ║");
            System.out.println("   ║ 6️⃣  - Ver Casino                    ║");
            System.out.println("   ║ 7️⃣  - Ingresar al cajero 💳        ║");
            System.out.println("   ╠══════════════════════════════════════╣");
            System.out.println("   ║ 8️⃣  - Salir (Cerrar Sesion)🚪       ║");
            System.out.println("   ╚══════════════════════════════════════╝");
            System.out.print(" Seleccione una opción: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Por favor, ingrese un número válido: ");
                BDDcode.descansar(1);
                scanner.next();
            }
            opcion = scanner.nextInt();

            float monto;

            switch (opcion) {
                case 1 -> {
                    System.out.print("Ingrese el número (0-36): ");
                    int numero = scanner.nextInt();
                    System.out.print("Monto a apostar: ");
                    monto = scanner.nextFloat();

                    if (numero < 0 || numero > 36 || monto < 0 || jugador.getSaldo() < monto) {
                        System.out.println("Entrada inválida.");
                        BDDcode.descansar(1);
                        continue;
                    }
                    ruleta.tirarNumero(numero, monto);

                }
                case 2 -> {
                    System.out.print("Color (0 = Rojo, 1 = Negro): ");
                    int color = scanner.nextInt();

                    System.out.print("Monto: ");
                    monto = scanner.nextFloat();
                    if (color < 0 || color > 1 || monto < 0 || jugador.getSaldo() < monto) {
                        System.out.println("Entrada inválida.");
                        BDDcode.descansar(1);
                        continue;
                    }
                    ruleta.tirarColor(color, monto);

                }
                case 3 -> {
                    System.out.print("Par(0) o Impar(1): ");
                    int eleccion = scanner.nextInt();
                    System.out.print("Monto: ");
                    monto = scanner.nextFloat();

                    if (eleccion < 0 || eleccion > 1 || monto < 0 || jugador.getSaldo() < monto) {
                        System.out.println("Entrada inválida.");
                        BDDcode.descansar(1);
                        continue;
                    }
                    ruleta.tirarParImpar(eleccion, monto);

                }
                case 4 -> {
                    System.out.print("Docena (1, 2 o 3): ");
                    int docena = scanner.nextInt();
                    System.out.print("Monto: ");
                    monto = scanner.nextFloat();

                    if (docena < 1 || docena > 3 || monto < 0 || jugador.getSaldo() < monto) {
                        System.out.println("Entrada inválida.");
                        BDDcode.descansar(1);
                        continue;
                    }
                    ruleta.tirarDocena(docena, monto);
                }
                case 5 -> {
                    System.out.print("Columna (1, 2 o 3): ");
                    int columna = scanner.nextInt();
                    System.out.print("Monto: ");
                    monto = scanner.nextFloat();

                    if (columna < 1 || columna > 3 || monto < 0 || jugador.getSaldo() < monto) {
                        System.out.println("Entrada inválida.");
                        BDDcode.descansar(1);
                        continue;
                    }
                    ruleta.tirarColumna(columna, monto);
                }
                case 6 -> {
                    // Mostrar información del casino
                    System.out.println("\n🏛️╔══════════════════════════════════════════════╗");
                    System.out.printf("   ║  Detalles del Casino: %-20s ║\n", casino.getNombre());
                    System.out.println("   ╠══════════════════════════════════════════════╣");
                    System.out.printf("   ║ 🆔 ID: %-30d ║\n", casino.getId());
                    System.out.printf("   ║ 💰 Saldo: $%-25.2f ║\n", casino.getSaldo());
                    System.out.println("   ╚══════════════════════════════════════════════╝");
                    System.out.println("\nApuestas recientes:");
                    BDDcode.descansar(2);
                    continue;

                }
                case 7 -> {
                    // Acceso al cajero
                    System.out.println("\n🏧╔════════════════════════════════════════════════╗");
                    System.out.printf("   ║   Bienvenido al cajero de %-20s ║\n", casino.getNombre());
                    System.out.println("   ╠════════════════════════════════════════════════╣");
                    System.out.printf("   ║ 💰 Saldo actual del jugador: $%-16.2f ║\n", jugador.getSaldo());
                    System.out.println("   ╠════════════════════════════════════════════════╣");
                    System.out.println("   ║ 1️⃣  - Retirar plata                           ║");
                    System.out.println("   ║ 2️⃣  - Depositar plata                         ║");
                    System.out.println("   ║ 3️⃣  - Salir del cajero 🚪                     ║");
                    System.out.println("   ╚════════════════════════════════════════════════╝");
                    System.out.print("Seleccione una opción: ");

                    int opcionCajero = scanner.nextInt();
                    switch (opcionCajero) {
                        case 1 -> {
                            System.out.print("Ingrese el monto a retirar: ");
                            float retirar = scanner.nextFloat();
                            Casino.retirarDinero(jugador, retirar);

                        }
                        case 2 -> {
                            System.out.print("Ingrese el monto a depositar: ");
                            float depositar = scanner.nextFloat();
                            Casino.depositarDinero(jugador, depositar);
                        }
                        case 3 -> System.out.println("Saliendo del cajero...");
                        default -> System.out.println("Opción inválida.");
                    }

                }

                case 8 -> {
                    // Salir y guardar saldos
                    jugadorDAO.actualizarSaldo(jugador);
                    casinoDAO.actualizarSaldo(casino);
                    System.out.println("Gracias por jugar, " + jugador.getNombre() + ". ¡Hasta luego!");
                    BDDcode.descansar(1);
                    return;

                }
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 9);// Salir y guardar saldos

    }


}
