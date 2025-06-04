package dao.interfaces;

import java.util.List;

import model.Jugador;

public interface JugadorDAO {

    List<Jugador> listarJugadores();

    void guardar(Jugador jugador);

    Jugador buscarPorNombre(String nombre);

    void actualizarSaldo(Jugador jugador);

    void eliminarJugador(String nombre);
}
