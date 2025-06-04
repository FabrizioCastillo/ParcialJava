package util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class BDDconnection {

    private static final String DB_FILE = System.getProperty("user.dir") + File.separator + "data" + File.separator + "CasinoDB";
    static String URL = "jdbc:h2:" + DB_FILE;

    private static final String USER = "Admin";
    private static final String PASSWORD = "Admin";
    private static final Logger logger = LogManager.getLogger(BDDconnection.class);
    //--------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------
    /**
     * Método para establecer una conexión a la base de datos H2.
     *
     * @return Connection objeto de conexión a la base de datos.
     */
    public static Connection Conexion() {
        Connection conn = null;
        try {
            //driver de H2
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            if (conn == null) {
                throw new SQLException("No se pudo conectar a la base de datos");
            }
        } catch (ClassNotFoundException e) {
            logger.error("Driver no encontrado: {}", e.getMessage());
            System.err.println("Driver no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos: {}", e.getMessage());
            System.err.println("Error al conectar: " + e.getMessage());
        }
        return conn;
    }
    //--------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------


}
