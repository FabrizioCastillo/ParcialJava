package dao.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dao.interfaces.CasinoDAO;
import model.Casino;
import util.BDDconnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CasinoDAOimp implements CasinoDAO {
    private static final Logger logger = LogManager.getLogger(CasinoDAOimp.class);
    //--------------------------------------------------------------------------------------------
    /**
     * Guarda un nuevo Casino en la base de datos.
     *
     * @param casino El Casino a guardar.
     */
    @Override
    public void guardar(Casino casino) {
        String sql = "INSERT INTO Casinos (Nombre, Saldo) VALUES (?, ?)";
        // Consulta SQL para insertar un nuevo Casino
        try (Connection conn = BDDconnection.Conexion(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, casino.getNombre());
            pstmt.setFloat(2, casino.getSaldo());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {

                logger.error("No se pudo insertar el Casino: " + casino.getNombre());
                throw new SQLException("No se pudo insertar el Casino");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    casino.setId(generatedKeys.getInt(1));
                    logger.info("Casino guardado con ID: " + casino.getId());
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al guardar Casino: " + e.getMessage());
        }

    }

    //--------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------
    /**
     * Busca un Casino por su nombre.
     *
     * @param nombre El nombre del Casino a buscar.
     * @return El Casino encontrado o null si no existe.
     */
    @Override
    public Casino buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM Casinos WHERE Nombre = ?";
        // Consulta SQL para buscar un Casino por su nombre
        try (Connection conn = BDDconnection.Conexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("Id");
                float saldo = rs.getFloat("Saldo");
                return new Casino(nombre, id, saldo);
            }

        } catch (SQLException e) {

            logger.error("Error al buscar Casino por nombre: " + e.getMessage());
            System.out.println("Error al buscar Casino por nombre: " + e.getMessage());
        }
        return null;
    }

    //--------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------
    /**
     * Busca un Casino por su ID.
     *
     * @param id El ID del Casino a buscar.
     * @return El Casino encontrado o null si no existe.
     */
    @Override
    public Casino buscarPorId(int id) {
        String sql = "SELECT * FROM Casinos WHERE Id = ?";
        // Consulta SQL para buscar un Casino por su ID
        try (Connection conn = BDDconnection.Conexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("Nombre");
                float saldo = rs.getFloat("Saldo");
                return new Casino(nombre, id, saldo);

            }

        } catch (SQLException e) {
            logger.error("Error al buscar Casino: " + e.getMessage());
            System.out.println("Error al buscar Casino: " + e.getMessage());
        }
        return null;
    }

    //--------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------
    /**
     * Actualiza el saldo de un Casino.
     *
     * @param casino El Casino con el nuevo saldo.
     */
    @Override
    public void actualizarSaldo(Casino casino) {
        String sql = "UPDATE Casinos SET Saldo = ? WHERE id = ?";
        // Consulta SQL para actualizar el saldo de un Casino
        try (Connection conn = BDDconnection.Conexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setFloat(1, casino.getSaldo());
            pstmt.setInt(2, casino.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error al actualizar saldo del Casino: " + e.getMessage());
            System.out.println("Error al actualizar saldo del Casino: " + e.getMessage());
        }
    }
    //--------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------
    /**
     * Elimina un Casino por su ID.
     *
     * @param id El ID del Casino a eliminar.
     */
    @Override
    public void eliminarCasino(int id) {
        String sql = "DELETE FROM Casinos WHERE Id = ?";
        // Consulta SQL para eliminar un Casino por su ID
        try (Connection conn = BDDconnection.Conexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error al eliminar Casino: " + e.getMessage());
            System.out.println("Error al eliminar Casino: " + e.getMessage());
        }
        logger.info("Casino con ID {} eliminado exitosamente.", id);


    }
    //--------------------------------------------------------------------------------------------

    //--------------------------------------------------------------------------------------------
    /**
     * Lista todos los Casinos.
     *
     * @return Una lista de Casinos.
     */
    @Override
    public List<Casino> listarCasinos() {
        List<Casino> casinos = new ArrayList<>();
        String sql = "SELECT * FROM casinos";
        // Consulta SQL para obtener todos los casinos
        try (Connection conn = BDDconnection.Conexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Casino casino = new Casino(
                        rs.getString("nombre"),
                        rs.getInt("id"),
                        rs.getFloat("saldo")
                );
                casinos.add(casino);
            }
        } catch (SQLException e) {
            logger.error("Error al listar Casinos: " + e.getMessage());
            e.printStackTrace();
        }

        return casinos;
    }



}
