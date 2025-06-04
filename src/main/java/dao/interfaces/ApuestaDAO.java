package dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import model.Apuesta;

public interface ApuestaDAO {

    void guardar(Apuesta apuesta);

    List<Apuesta> obtenerUltimas10Apuestas() throws SQLException;

}
