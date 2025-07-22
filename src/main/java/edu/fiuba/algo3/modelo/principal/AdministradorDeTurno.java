package edu.fiuba.algo3.modelo.principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.fiuba.algo3.modelo.cartas.unidades.CartaUnidad;
import edu.fiuba.algo3.modelo.secciones.tablero.Seccion;
import edu.fiuba.algo3.modelo.secciones.tablero.Tablero;

public class AdministradorDeTurno {
    private final List<Jugador> jugadores;
    private final List<Ronda> rondas = new ArrayList<>();
    private int indiceActual;
    private int ciclos = 0;

    public AdministradorDeTurno(List<Jugador> jugadores) {
        this.jugadores = jugadores;
        for (int i = 0; i < jugadores.size(); i++) {
            jugadores.get(i).setOrdenDeJuego(i);
        }
    }

    public void indiceActual(int indice) {
        this.indiceActual = indice;
    }

    public void tirarMoneda() {
        this.indiceActual = new Random().nextInt(2);
    }

    public int actual() {
        return this.indiceActual;
    }

    public Jugador jugadorActual() {
        return jugadores.get(indiceActual);
    }

    public void siguiente() {
        indiceActual = 1 - indiceActual;
    }

    public void finalizarRonda(Tablero tablero) {

        for (Seccion seccion : tablero.obtenerSeccionesDelJugador(0)) {
            List<CartaUnidad> cartas = new ArrayList<>(seccion.getCartas());
            for (CartaUnidad carta : cartas) {
                Contexto contexto = new Contexto(tablero, seccion, carta, jugadores.get(0));
                carta.retrotraerModificacion(contexto);
            }
        }

        // Luego retrotraer contexto para el jugador 1
        for (Seccion seccion : tablero.obtenerSeccionesDelJugador(1)) {
            List<CartaUnidad> cartas = new ArrayList<>(seccion.getCartas());
            for (CartaUnidad carta : cartas) {
                Contexto contexto = new Contexto(tablero, seccion, carta, jugadores.get(1));
                carta.retrotraerModificacion(contexto);
            }
        }

        List<CartaUnidad> cartasDel1 = tablero.removerCartasDeJugador(0);
        List<CartaUnidad> cartasDel2 = tablero.removerCartasDeJugador(1);

        jugadores.get(0).agregarCartasAlDescarte(new ArrayList<>(cartasDel1));
        jugadores.get(1).agregarCartasAlDescarte(new ArrayList<>(cartasDel2));
        ciclos++;
    }

    private void agregarRonda() {
        rondas.add(new Ronda());
    }

    public void actualizarRonda(int puntaje) {
        if (rondas.size() <= ciclos) {
            agregarRonda();
        }
        Ronda rondaActual = rondas.get(ciclos);
        Jugador jugadorActual = jugadores.get(indiceActual);
        rondaActual.agregarPuntajeJugador(jugadorActual.getNombre(), puntaje);
    }

    public String mostrarGanador() {
        String ganador = "empate";
        int contadorJ1 = 0;
        int contadorJ2 = 0;

        String nombreJugador1 = jugadores.get(0).getNombre();
        String nombreJugador2 = jugadores.get(1).getNombre();

        for (Ronda ronda : rondas) {
            if (ronda == null) continue;
            String ganadorRonda = ronda.getGanadorRonda();

            if (ganadorRonda.equals(nombreJugador1)) {
                contadorJ1++;
            } else if (ganadorRonda.equals(nombreJugador2)) {
                contadorJ2++;
            }
        }

        if (contadorJ1 > contadorJ2) {
            ganador = nombreJugador1;
        } else if (contadorJ2 > contadorJ1) {
            ganador = nombreJugador2;
        }

        return ganador;
    }

    public boolean juegoTerminado() {
        if (ciclos < 2) {
            return false;
        } else return !mostrarGanador().equals("empate") || ciclos == 3;
    }

    public List<Map<String, Integer>> getPuntosPorRonda() {
        List<Map<String, Integer>> puntosPorRonda = new ArrayList<>();
        for (Ronda ronda : rondas) {
            if (ronda != null) {
                puntosPorRonda.add(ronda.getPuntajeJugadores());
            }
        }
        return puntosPorRonda;
    }

    public int getNumeroRondaActual() {
        // ciclos es el número de rondas finalizadas, la ronda actual es ciclos+1
        return ciclos + 1;
    }
}