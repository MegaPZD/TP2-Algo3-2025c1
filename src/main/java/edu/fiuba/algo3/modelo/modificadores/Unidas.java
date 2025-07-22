package edu.fiuba.algo3.modelo.modificadores;

import edu.fiuba.algo3.modelo.cartas.unidades.CartaUnidad;
import edu.fiuba.algo3.modelo.principal.Contexto;
import edu.fiuba.algo3.modelo.secciones.tablero.TipoDeSeccionInvalidaError;
import edu.fiuba.algo3.modelo.secciones.tablero.NoSePuedeEliminarClimaSiNoHayClima;

import java.util.List;

public class Unidas implements Modificador {

    private final Modificador modificador;

    public Unidas(Modificador modificador) {
        this.modificador = modificador;
    }

    @Override
    public String mostrarModificadores() {
        return modificador.mostrarModificadores() + " Unidas";
    }

    @Override
    public void modificar(Contexto contextoModificador) throws TipoDeSeccionInvalidaError {
        try {
            modificador.modificar(contextoModificador);
        } catch (NoSePuedeEliminarClimaSiNoHayClima ignored) {
        }
        List<CartaUnidad> cartasDeSeccion = contextoModificador.getSeccion().getCartas();
        String nombreCarta = contextoModificador.getCarta().getNombre();
        modificarComportamientoDeCartas(nombreCarta, cartasDeSeccion);

    }

    public void retrotraerContexto(Contexto contexto){

        CartaUnidad carta = contexto.getCarta();
        carta.volverValorBase();

        modificador.retrotraerContexto(contexto);

    }

    public void modificarComportamientoDeCartas(String nombreCarta,List<CartaUnidad> cartas) {
        int cantidadUnidas = (int) cartas.stream()
                .filter(carta -> carta.mostrarCarta().contains(nombreCarta))
                .count();

        // Solo modificar si hay 2 o más
        if (cantidadUnidas >= 2) {
            for (CartaUnidad carta : cartas) {
                if (carta.mostrarCarta().contains("Unidas") && (!carta.esLegendaria())) {
                    carta.multiplicarValorBase(cantidadUnidas);
                }
            }
        }
    }

}
