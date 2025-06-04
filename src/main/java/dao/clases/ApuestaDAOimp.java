package dao.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.interfaces.ApuestaDAO;
import model.Apuesta;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.BDDconnection;

public class ApuestaDAOimp implements ApuestaDAO {
    // Conexión a la base de datos usada para las operaciones
    private Connection connection;
    // Logger para registrar eventos y errores
    private static final Logger logger = LogManager.getLogger(ApuestaDAOimp.class);
    /**
     * Constructor que recibe la conexión a la base de datos
     * @param connection conexión activa a la base de datos
     */
    public ApuestaDAOimp(Connection connection) {
        this.connection = connection;
    }
    public void guardar(Apuesta apuesta) {
        String sql = "INSERT INTO apuesta (jugador_id, tipo, valor, monto, gano, fecha, casino_nombre) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Establecer parámetros para la consulta SQL
            stmt.setInt(1, apuesta.getJugadorId());
            stmt.setString(2, apuesta.getTipo());
            stmt.setString(3, apuesta.getValor());
            stmt.setDouble(4, apuesta.getMonto());
            stmt.setBoolean(5, apuesta.isGano());
            stmt.setObject(6, apuesta.getFecha());
            stmt.setString(7, apuesta.getCasinoNombre());

            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al guardar la apuesta: " + e.getMessage());

            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------
    /**
     * Obtiene las últimas 10 apuestas registradas en la base de datos
     * @return lista de las últimas 10 apuestas ordenadas por fecha descendente
     * @throws SQLException si ocurre un error en la consulta
     */

    @Override
    public List<Apuesta> obtenerUltimas10Apuestas() throws SQLException {
        List<Apuesta> apuestas = new ArrayList<>();
        String sql = "SELECT jugador_id, tipo, valor, monto, gano, fecha, casino_nombre FROM apuesta ORDER BY fecha DESC LIMIT 10";

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Apuesta apuesta = new Apuesta(rs.getInt("jugador_id"), rs.getString("tipo"), rs.getString("valor"), rs.getDouble("monto"), rs.getBoolean("gano"), rs.getTimestamp("fecha").toLocalDateTime(), rs.getString("casino_nombre"));
                apuestas.add(apuesta);

            }
        } catch (SQLException e) {
            // Loguear error y re-lanzar la excepción para manejarla fuera de este método
            logger.error("Error al obtener las últimas 10 apuestas: " + e.getMessage());
            throw e;
        }
        return apuestas;
    }


}
