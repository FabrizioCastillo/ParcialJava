package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dao.interfaces.JugadorDAO;
import model.Jugador;
import util.BDDconnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JugadorDAOimp implements JugadorDAO {
    private static final Logger logger = LogManager.getLogger(JugadorDAOimp.class);
    //--------------------------------------------------------------------------------------------
    /**
     * Elimina un jugador y todas sus apuestas asociadas de la base de datos.
     *
     * @param nombre El nombre del jugador a eliminar.
     */
    @Override
    public void eliminarJugador(String nombre) {
        try (Connection conn = BDDconnection.Conexion()) {
            String buscarId = "SELECT id FROM jugador WHERE nombre = ?";
            PreparedStatement buscarStmt = conn.prepareStatement(buscarId);
            buscarStmt.setString(1, nombre);
            ResultSet rs = buscarStmt.executeQuery();

            if (rs.next()) {
                int jugadorId = rs.getInt("id");

                String eliminarApuestas = "DELETE FROM apuesta WHERE jugador_id = ?";
                PreparedStatement eliminarApuestasStmt = conn.prepareStatement(eliminarApuestas);
                eliminarApuestasStmt.setInt(1, jugadorId);
                eliminarApuestasStmt.executeUpdate();

                String eliminarJugador = "DELETE FROM jugador WHERE id = ?";
                PreparedStatement eliminarJugadorStmt = conn.prepareStatement(eliminarJugador);
                eliminarJugadorStmt.setInt(1, jugadorId);
                eliminarJugadorStmt.executeUpdate();

                System.out.println("✅Jugador y sus apuestas eliminados correctamente.");
            } else {
                System.out.println("🔴Jugador no encontrado.");
            }

        } catch (SQLException e) {
            logger.error("Error al eliminar jugador: " + nombre, e);
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------------------------
    /**
     * Guarda un nuevo jugador en la base de datos.
     *
     * @param jugador El objeto Jugador a guardar.
     */
    @Override
    public void guardar(Jugador jugador) {
        String sql = "INSERT INTO jugador (nombre, saldo, contrasena) VALUES (?, ?, ?)";

        try (Connection conn = BDDconnection.Conexion(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, jugador.getNombre());
            stmt.setDouble(2, jugador.getSaldo());
            stmt.setString(3, jugador.getContrasena());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    jugador.setId(idGenerado);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al guardar jugador: " + jugador.getNombre(), e);
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------------------------
    /**
     * Busca un jugador por su nombre en la base de datos.
     *
     * @param nombre El nombre del jugador a buscar.
     * @return El objeto Jugador encontrado o null si no existe.
     */
    @Override
    public Jugador buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM jugador WHERE nombre = ?";
        try (Connection conn = BDDconnection.Conexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Jugador jugador = new Jugador();
                jugador.setId(rs.getInt("id"));
                jugador.setNombre(rs.getString("nombre"));
                jugador.setSaldo(rs.getDouble("saldo"));
                jugador.setContrasena(rs.getString("contrasena"));
                return jugador;
            }
        } catch (SQLException e) {
        logger.error("Error al buscar jugador por nombre: " + nombre, e);
            e.printStackTrace();
        }
        return null;
    }
    //--------------------------------------------------------------------------------------------
    /* * Método para listar todos los jugadores registrados en la base de datos.
     * @return Lista de objetos Jugador con los datos de cada jugador.
     */
    @Override
    public List<Jugador> listarJugadores() {
        List<Jugador> jugadores = new ArrayList<>();
        try (Connection conn = BDDconnection.Conexion()) {
            String sql = "SELECT * FROM jugador";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Jugador jugador = new Jugador(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("saldo"));
                jugador.setContrasena(rs.getString("contrasena"));
                jugadores.add(jugador);
            }

        } catch (SQLException e) {
        logger.error("Error al listar jugadores", e);
            e.printStackTrace();
        }
        return jugadores;
    }
//--------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------
    /**
     * Actualiza el saldo de un jugador en la base de datos.
     *
     * @param jugador El objeto Jugador con el nuevo saldo.
     */
@Override
    public void actualizarSaldo(Jugador jugador) {
        try (Connection conn = BDDconnection.Conexion()) {
            String sql = "UPDATE jugador SET saldo = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, jugador.getSaldo());
            stmt.setInt(2, jugador.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
        logger.error("Error al actualizar saldo del jugador: " + jugador.getNombre(), e);
            e.printStackTrace();
        }
    }
}
