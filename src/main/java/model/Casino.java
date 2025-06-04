package model;

import dao.clases.CasinoDAOimp;
import dao.interfaces.CasinoDAO;
import util.BDDcode;
import util.BDDconnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Casino {

    private int id;
    private String nombre;
    private float saldo;
    private static Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private static final Logger logger = LogManager.getLogger(Casino.class);
    //--------------------------------------------------------------------------------------------
    // Getters y setters básicos para acceder y modificar atributos
    public String getNombre() {
        return nombre;
    }
    public int getId() {
        return id;
    }
    public float getSaldo() {
        return saldo;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }
    //--------------------------------------------------------------------------------------------

    public Casino(String nombre, int id, float saldo) {
        this.id = id;

        this.nombre = nombre;

        this.saldo = saldo;

    }

    /**
     * Menú principal para seleccionar opciones relacionadas con casinos
     * Permite agregar, ver, seleccionar, actualizar saldo, eliminar casinos y salir
     * @return el ID del casino seleccionado o -1 si no se selecciona ninguno
     */
    public static int MenuSelecionarCasino() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            int idCasinoSeleccionado = -1;
            // Mostrar menú de opciones
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║        🎰 BIENVENIDO AL CASINO 🎰     ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1 - Agregar Casino                     ║");
            System.out.println("║ 2 - Ver Casinos                        ║");
            System.out.println("║ 3 - Seleccionar Casino                 ║");
            System.out.println("║ 4 - Actualizar Saldo en Casinos        ║");
            System.out.println("║ 5 - Eliminar Casino                    ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 6 - Salir                              ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Por favor, ingrese un número: ");
                scanner.next();
            }
            int opcion2 = scanner.nextInt();
            scanner.nextLine();

            switch (opcion2) {
                case 1:
                    // Agregar un nuevo casino
                    System.out.print("Ingrese el nombre del casino: ");
                    String nombreCasino = scanner.nextLine().trim();

                    float saldoInicial = 0;
                    while (true) {
                        System.out.print("Ingrese el saldo inicial del casino: ");
                        String saldoStr = scanner.nextLine().trim();
                        try {
                            saldoInicial = Float.parseFloat(saldoStr);
                            if (saldoInicial < 0) {
                                logger.warn("Saldo negativo ingresado para nuevo casino: {}", saldoStr);
                                System.out.println("El saldo no puede ser negativo. Intente nuevamente.");
                            } else {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            logger.error("Error de formato en saldo ingresado: {}", saldoStr);
                            System.out.println("Entrada inválida. Por favor, ingrese un número válido para el saldo.");
                        }
                    }

                    CasinoDAO casinoDAOExistente = new CasinoDAOimp();
                    // Verificar si el casino ya existe
                    if (casinoDAOExistente.buscarPorNombre(nombreCasino) != null) {

                        System.out.println("El casino ya existe. Intente con otro nombre.");
                        logger.warn("Intento de registrar casino ya existente: {}", nombreCasino);
                        break;
                    }

                    Casino nuevoCasino = new Casino(nombreCasino, 0, saldoInicial);
                    guardarCasino(nuevoCasino);
                    // Guardar el nuevo casino en la base de datos
                    if (nuevoCasino.getId() > 0) {
                        logger.info("Nuevo casino guardado: {} con saldo {}", nuevoCasino.getNombre(), nuevoCasino.getSaldo());
                        System.out.println("\n" + "╔═══════════════════════════════════════╗");
                        System.out.println("║ ✅ " + "Casino creado con éxito.               ║");
                        System.out.println("╠═══════════════════════════════════════╣");
                        System.out.printf("║ 🆔 ID del casino:       %-15s ║\n", nuevoCasino.getId());
                        System.out.printf("║ 🏛️ Nombre del casino:   %-15s ║\n", nuevoCasino.getNombre());
                        System.out.printf("║ 💰 Saldo del casino:    $%-14.2f ║\n", nuevoCasino.getSaldo());
                        System.out.println("╚═══════════════════════════════════════╝\n");
                        BDDcode.descansar(2);
                    } else {
                        logger.error("Error al guardar el nuevo casino: {}", nombreCasino);
                        System.out.println("Error al guardar el casino.");
                    }

                    continue;
                case 2:
                    // Listar todos los casinos registrados
                    listaCasinos();
                    continue;
                case 3:
                    // Seleccionar un casino existente
                    idCasinoSeleccionado = Casino.seleccionarCasino();
                    if (idCasinoSeleccionado == -1) {
                        System.out.println("⚠️ No se pudo seleccionar un casino. Registre uno primero.");
                        logger.warn("No se pudo seleccionar un casino. No existen aún.");
                        break;
                    }
                    logger.info("Casino seleccionado con ID: {}", idCasinoSeleccionado);
                    System.out.println("Casino seleccionado con ID: " + idCasinoSeleccionado);
                    return idCasinoSeleccionado;
                case 4:
                    // Actualizar el saldo de un casino existente
                    listaCasinos();
                    System.out.print("Ingrese el ID del casino a actualizar: ");
                    int idCasinoActualizar;
                    try {
                        idCasinoActualizar = Integer.parseInt(scanner.nextLine().trim());

                    } catch (NumberFormatException e) {
                        System.out.println("Entrada inválida. Debe ingresar un número.");
                        logger.error("ID inválido ingresado para actualizar casino");
                        continue;
                    }

                    CasinoDAO casinoDAOActualizar = new CasinoDAOimp();
                    Casino casinoActualizar = casinoDAOActualizar.buscarPorId(idCasinoActualizar);

                    if (casinoActualizar == null) {
                        System.out.println("Casino no encontrado.");
                        logger.warn("No se encontró casino con ID: {}", idCasinoActualizar);
                        continue;
                    }

                    System.out.print("Ingrese el nuevo saldo para el casino: ");
                    float nuevoSaldo;

                    try {

                        nuevoSaldo = Float.parseFloat(scanner.nextLine().trim());
                        if (nuevoSaldo < 0) {
                            System.out.println("El saldo no puede ser negativo. Intente nuevamente.");
                            System.out.println("El saldo no puede ser negativo. Intente nuevamente.");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        logger.error("Formato inválido de saldo ingresado");
                        System.out.println("Entrada inválida. Debe ingresar un número válido.");
                        continue;
                    }
                    nuevoSaldo = nuevoSaldo + casinoActualizar.getSaldo();
                    casinoActualizar.setSaldo(nuevoSaldo);
                    casinoDAOActualizar.actualizarSaldo(casinoActualizar);
                    System.out.println("Saldo actualizado correctamente para el casino " + casinoActualizar.getNombre() + ".");
                    continue;
                case 5:
                    // Eliminar un casino existente
                    eliminarCasino();
                    continue;
                case 6:
                    // Salir del programa
                    System.out.println("Saliendo del programa...");
                    BDDcode.salir();
                    break;

                default: {
                    logger.warn("Opción inválida seleccionada en menú de casino: {}", opcion2);
                    System.out.println("Opción inválida. Intente nuevamente.");
                }
            }
            // Si se seleccionó un casino válido, cerramos el scanner y retornamos el ID del casino
            if (idCasinoSeleccionado != -1) {
                scanner.close();
                logger.info("Casino seleccionado con ID: {}", idCasinoSeleccionado);
                return idCasinoSeleccionado;

            }

        }
    }
    /**
     * Muestra la información detallada del casino actualmente seleccionado
     * @param id ID del casino seleccionado
     * @param casinoSeleccionado objeto Casino con los detalles a mostrar
     */

    public static void mostrarCasinoActual(int id, Casino casinoSeleccionado) {

        if (id != -1) {

            System.out.println("╔════════════════════════════════════╗");
            System.out.println("║         💼 Detalles del Casino      ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 🆔 ID:      " + casinoSeleccionado.getId() + "                        ║");
            System.out.println("║ 👤 Nombre:  " + casinoSeleccionado.getNombre() + "                 ║");
            System.out.println("║ 💰 Saldo:   $" + (int) casinoSeleccionado.getSaldo() + "                  ║");
            System.out.println("╚════════════════════════════════════╝");
            logger.info("Detalles del casino actual: ID {}, Nombre {}, Saldo {}", casinoSeleccionado.getId(), casinoSeleccionado.getNombre(), casinoSeleccionado.getSaldo());

        } else {
            System.out.println("No hay un casino seleccionado.");
        }
    }
    /**
     * Guarda un casino usando el DAO después de validar el nombre
     * @param casino objeto Casino a guardar
     */
    public static void guardarCasino(Casino casino) {
        if (casino.getNombre() == null || casino.getNombre().trim().isEmpty()) {
            System.out.println("El nombre del casino no puede estar vacío.");
            logger.error("Intento de guardar casino con nombre vacío");
            return;
        }
        CasinoDAO casinoDAO = new CasinoDAOimp();
        casinoDAO.guardar(casino);

    }
    /**
     * Lista todos los casinos registrados en la base de datos mostrando sus detalles
     */
    public static void listaCasinos() {
        String sql = "SELECT * FROM Casinos";

        try (Connection conn = BDDconnection.Conexion(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n📋 Lista de Casinos Registrados:");

            boolean hayCajeros = false;
            while (rs.next()) {
                hayCajeros = true;
                int id = rs.getInt("id");
                String nombre = rs.getString("Nombre");
                int saldo = rs.getInt("Saldo");

                System.out.println("╔════════════════════════════════════╗");
                System.out.println("║         💼 Detalles del Casino      ║");
                System.out.println("╠════════════════════════════════════╣");
                System.out.printf("║ 🆔 ID:      %-24d ║\n", id);
                System.out.printf("║ 👤 Nombre:  %-24s ║\n", nombre);
                System.out.printf("║ 💰 Saldo:   $%-23d ║\n", saldo);
                System.out.println("╚════════════════════════════════════╝");
                BDDcode.descansar(1);
            }

            if (!hayCajeros) {
                System.out.println("No hay casinos registrados.");
                BDDcode.descansar(1);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar los casinos: " + e.getMessage());
            logger.error("Error al listar los casinos", e);


        }
    }
    /**
     * Permite al usuario seleccionar un casino por su ID
     * @return el ID del casino seleccionado o -1 si no se encuentra
     */
    public static int seleccionarCasino() {
        Scanner scanner = new Scanner(System.in).useDelimiter("\n");
        CasinoDAO casinoDAO = new CasinoDAOimp();
        List<Casino> casinos = casinoDAO.listarCasinos();
        if (casinos.isEmpty()) {
            System.out.println("📋 Lista de Casinos Registrados:");
            System.out.println("No hay casinos registrados.");
            BDDcode.descansar(1);
            return -1;
        }
        listaCasinos();
        System.out.println("Ingrese el ID del Casino donde quiera jugar:");

        int idCajero = 0;

        try {
            idCajero = Integer.parseInt(scanner.next());
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número válido.");
            logger.error("ID de casino inválido ingresado: {}", scanner.next());
            return -1;
        }
        Casino casino = casinoDAO.buscarPorId(idCajero);


        System.out.println("Casino seleccionado: " + casino.getNombre() + ", Saldo: " + casino.getSaldo());
        logger.info("Casino seleccionado con ID: {}, Nombre: {}, Saldo: {}", casino.getId(), casino.getNombre(), casino.getSaldo());
        BDDcode.descansar(1);
        return idCajero;

    }
    /**
     * Elimina un casino por su ID después de confirmación
     */
    public static void eliminarCasino() {
        listaCasinos();
        System.out.println("Ingrese el ID del casino a eliminar:");
        int idCasino;


        try {
            idCasino = Integer.parseInt(scanner.next());


        } catch (NumberFormatException e) {
            logger.error("ID de casino inválido ingresado para eliminación");
            System.out.println("Error: Debe ingresar un número válido.");

            return;
        }

        CasinoDAO casinoDAO = new CasinoDAOimp();
        Casino casino = casinoDAO.buscarPorId(idCasino);

        if (casino == null) {
            System.out.println("Casino no encontrado.");
            BDDcode.descansar(1);
            return;
        }



        try {
            casinoDAO.eliminarCasino(idCasino);
            System.out.println("Casino eliminado exitosamente.");
            logger.info("Casino con ID {} eliminado exitosamente", idCasino);
            BDDcode.descansar(1);
        } catch (Exception e) {
            System.out.println("Error al eliminar el casino: " + e.getMessage());
            logger.error("Error al eliminar el casino con ID: {}", idCasino, e);
            e.printStackTrace();
        }
    }

    /**
     * Deposita una cantidad de dinero al saldo del jugador
     * @param jugador objeto Jugador al que se le depositará el dinero
     * @param monto cantidad a depositar
     */
    public static void depositarDinero(Jugador jugador, float monto) {
        if (monto <= 0) {
            System.out.println("Monto inválido.");
            BDDcode.descansar(1);
            return;
        }
        jugador.setSaldo(jugador.getSaldo() + monto);

        System.out.println("Depósito exitoso. Nuevo saldo: $" + jugador.getSaldo());
        logger.info("Depósito de ${} realizado por el jugador {}", monto, jugador.getNombre());
        BDDcode.descansar(1);

    }
    /**
     * Retira una cantidad de dinero del saldo del jugador
     * @param jugador objeto Jugador del que se retirará el dinero
     * @param monto cantidad a retirar
     */

    public static void retirarDinero(Jugador jugador, float monto) {

        if (monto <= 0 || monto > jugador.getSaldo()) {
            System.out.println("Monto inválido.");
            BDDcode.descansar(1);
            return;
        }

        jugador.setSaldo(jugador.getSaldo() - monto);
        logger.info("Retiro de ${} realizado por el jugador {}", monto, jugador.getNombre());
        System.out.println("Retiro exitoso. Nuevo saldo: $" + jugador.getSaldo());
        BDDcode.descansar(1);
    }

}
