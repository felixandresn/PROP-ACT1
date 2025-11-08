package edu.epsevg.prop.ac1.cerca;
 
import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class CercaDFS extends Cerca {
    public CercaDFS(boolean usarLNT) { super(usarLNT); }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        Stack<Mapa> pila = new Stack<>();
        Stack<List<Moviment>> caminos = new Stack<>();
        Set<Mapa> visitados = new HashSet<>();
        pila.push(inicial);
        caminos.push(new ArrayList<>()); // Camino vacío para el estado inicial

        // Actualiza memoria inicial
        rc.updateMemoria(pila.size() + caminos.size() + visitados.size());

        while (!pila.isEmpty()) {
            Mapa actual = pila.pop();
            List<Moviment> caminoActual = caminos.pop();

            rc.updateMemoria(pila.size() + caminos.size() + visitados.size());
            rc.incNodesExplorats();

            if (actual.esMeta()) {
                rc.cami = caminoActual;
                return;
            }
            if (!visitados.contains(actual)) {
                visitados.add(actual);
                for (Moviment mov : actual.getAccionsPossibles()) {
                    try {
                        Mapa siguiente = actual.mou(mov);
                        if (!visitados.contains(siguiente)) {
                            List<Moviment> nuevoCamino = new ArrayList<>(caminoActual);
                            nuevoCamino.add(mov);
                            pila.push(siguiente);
                            caminos.push(nuevoCamino);
                            rc.updateMemoria(pila.size() + caminos.size() + visitados.size());
                        } else {
                            rc.incNodesTallats();
                        }
                    } catch (IllegalArgumentException e) {
                        // ignora movimiento
                    }
                }
            }
        }
    }


}
