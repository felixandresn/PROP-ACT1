package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.ArrayList;
import java.util.List;


public class CercaBFS extends Cerca {
    public CercaBFS(boolean usarLNT) { super(usarLNT); }

    @Override
public void ferCerca(Mapa inicial, ResultatCerca rc) {
    java.util.Queue<Mapa> cola = new java.util.LinkedList<>();
    java.util.Queue<java.util.List<Moviment>> caminos = new java.util.LinkedList<>();
    java.util.Set<Mapa> visitados = new java.util.HashSet<>();
    cola.add(inicial);
    caminos.add(new java.util.ArrayList<>());

    rc.updateMemoria(cola.size() + caminos.size() + visitados.size());

    while (!cola.isEmpty()) {
        Mapa actual = cola.poll();
        List<Moviment> caminoActual = caminos.poll();

        rc.updateMemoria(cola.size() + caminos.size() + visitados.size());
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
                        cola.add(siguiente);
                        caminos.add(nuevoCamino);
                        rc.updateMemoria(cola.size() + caminos.size() + visitados.size());
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
