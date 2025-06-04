package model;
import dao.JugadorDAOimp;
import dao.interfaces.JugadorDAO;


import java.util.List;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.BDDcode;

public class Jugador {
    private int id;
    private String nombre;
    private double saldo;
    private String contrasena;
    private static Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private static final Logger logger = LogManager.getLogger(Jugador.class);

    //Constructores
    public Jugador() {
    }

    public Jugador(int id, String nombre, double saldo) {
        this.id = id;
        this.nombre = nombre;
        this.saldo = saldo;
        this.contrasena = "";
    }

    public Jugador(String nombre, double saldo, String contrasena) {
        this.nombre = nombre;
        this.saldo = saldo;
        this.contrasena = contrasena;
    }
    //--------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------
    // Getters y Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    //--------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------
    /* * Método para registrar un nuevo jugador.
     * Solicita nombre de usuario, contraseña y saldo inicial.
     * Valida los datos ingresados y guarda el jugador en la base de datos.
     */

    public static void RegistrarJugador() {

        System.out.println("Ingrese su nombre de usuario:");
        String nombreUsuario = scanner.next();
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            System.out.println("Nombre de usuario inválido.");
            logger.error("Intento de registro con nombre de usuario inválido.");
            BDDcode.descansar(1);
            return; // Salimos si el nombre es inválido

        }


        System.out.println("Ingrese su contraseña:");
        String contrasena = scanner.next();
        System.out.println("Cuanta plata ingreso al casino?");
        float saldoInicial;

        try {
            String saldoInput = scanner.next().replace(",", ".");
            saldoInicial = Float.parseFloat(saldoInput);
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número válido, use punto para decimales (ej: 100.50)");
            return;
        }

        try {
            JugadorDAO jugadorDAO = new JugadorDAOimp();
            Jugador jugador = new Jugador(nombreUsuario, saldoInicial, contrasena);
            jugadorDAO.guardar(jugador);
            System.out.println("Jugador registrado exitosamente con ID: " + jugador.getId());

        } catch (Exception e) {
            System.err.println("Error al registrar jugador: " + e.getMessage());
            e.printStackTrace();

        }
        logger.info("Jugador registrado: {}", nombreUsuario);
        BDDcode.descansar(1);
        System.out.println("----------Saliendo del area de registro de jugadores----------");
    }

    /* * Método para eliminar un jugador.
     * Solicita el nombre del jugador a eliminar.
     * Busca al jugador en la base de datos y lo elimina si existe.
     */
    public static void eliminarJugador() {

        System.out.println("Ingrese el nombre del jugador a eliminar:");
        String nombreJugador = scanner.next();

        JugadorDAO jugadorDAO = new JugadorDAOimp();
        Jugador jugador = jugadorDAO.buscarPorNombre(nombreJugador);

        if (jugador == null) {
            System.out.println("Jugador no encontrado.");
            return;
        }

        try {
            jugadorDAO.eliminarJugador(nombreJugador);
        } catch (Exception e) {
            logger.error("Error al eliminar jugador '{}': {}", nombreJugador, e.getMessage());
            System.err.println("Error al eliminar jugador: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Jugador eliminado exitosamente: " + nombreJugador);
        logger.info("Jugador eliminado: {}", nombreJugador);
        BDDcode.descansar(1);
        System.out.println("----------Saliendo del area de eliminación de jugadores----------");
    }
    /* * Método para ver la lista de jugadores registrados.
     * Muestra el nombre y saldo de cada jugador en una tabla.
     */

    public static void verJugadores() {
        JugadorDAO jugadorDAO = new JugadorDAOimp();
        List<Jugador> jugadores = jugadorDAO.listarJugadores();

        System.out.println("\n📋 Lista de jugadores:\n");
        System.out.println("╔════════════════════════════════════╗");
        System.out.printf("║ %-20s │ %-10s ║\n", "👤 Nombre", "💰 Saldo");
        System.out.println("╠════════════════════════════════════╣");

        for (Jugador j : jugadores) {
            System.out.printf("║ %-20s │ $%-9.2f ║\n", j.getNombre(), j.getSaldo());
        }

        System.out.println("╚════════════════════════════════════╝\n");
        System.out.println("--------------------------------------------");
        BDDcode.descansar(2);
    }

}
