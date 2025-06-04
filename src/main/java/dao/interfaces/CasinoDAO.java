package dao.interfaces;

import model.Casino;

import java.util.List;

public interface CasinoDAO {

    void guardar(Casino casino);

    Casino buscarPorId(int id);

    void actualizarSaldo(Casino casino);

    Casino buscarPorNombre(String nombre);

    void eliminarCasino(int id);

    List<Casino> listarCasinos();



}
