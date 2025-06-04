package model;

import java.time.LocalDateTime;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Apuesta {

    private int id;
    private int jugadorId;
    private String tipo;
    private String valor;
    private double monto;
    private boolean gano;
    private LocalDateTime fecha;
    private String casinoNombre;
    private static Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    //--------------------------------------------------------------------------------------------
    // Get y set
    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }

    public boolean isGano() {
        return gano;
    }

    public String getValor() {
        return valor;
    }

    public int getJugadorId() {
        return jugadorId;
    }

    public String getCasinoNombre() {
        return casinoNombre;
    }
//--------------------------------------------------------------------------------------------
    //constructor por defecto
    public Apuesta(int jugadorId, String tipo, String valor, double monto, boolean gano, LocalDateTime fecha, String casinoNombre) {
        this.jugadorId = jugadorId;
        this.tipo = tipo;
        this.valor = valor;
        this.monto = monto;
        this.gano = gano;
        this.fecha = fecha;
        this.casinoNombre = casinoNombre;

    }


}
