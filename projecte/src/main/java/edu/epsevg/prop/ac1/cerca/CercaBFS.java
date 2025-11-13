package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.*;

/**
 * Cerca BFS amb o sense LNT segons el paràmetre usarLNT.
 */
public class CercaBFS extends Cerca {

    public CercaBFS(boolean usarLNT) {
        super(usarLNT);
    }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        Queue<Mapa> cola = new LinkedList<>();
        Queue<List<Moviment>> caminos = new LinkedList<>();
        Set<Mapa> visitados = new HashSet<>();

        cola.add(inicial);
        caminos.add(new ArrayList<>());

        rc.updateMemoria(cola.size() + caminos.size() + visitados.size());

        while (!cola.isEmpty()) {
            Mapa actual = cola.poll();
            List<Moviment> caminoActual = caminos.poll();

            rc.updateMemoria(cola.size() + caminos.size() + visitados.size());
            rc.incNodesExplorats();

            // Comprovar si és meta
            if (actual.esMeta()) {
                rc.cami = caminoActual;
                return;
            }

            // --- CONTROL DE CICLES ---
            if (usarLNT) {
                // ✅ VERSIÓ LNT: Control global amb HashSet
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
                            // Moviment invàlid — l'ignorem
                        }
                    }
                }
            } else {
                // 🚫 VERSIÓ SENSE LNT: només controlem repeticions dins del camí actual
                for (Moviment mov : actual.getAccionsPossibles()) {
                    try {
                        Mapa siguiente = actual.mou(mov);
                        // Només afegim si no està ja dins el camí actual
                        if (!estaEnCamino(inicial, caminoActual, siguiente)) {
                            List<Moviment> nuevoCamino = new ArrayList<>(caminoActual);
                            nuevoCamino.add(mov);
                            cola.add(siguiente);
                            caminos.add(nuevoCamino);
                            rc.updateMemoria(cola.size() + caminos.size() + visitados.size());
                        } else {
                            rc.incNodesTallats();
                        }
                    } catch (IllegalArgumentException e) {
                        // Moviment invàlid — l'ignorem
                    }
                }
            }
        }
    }

    /**
     * Comprova si un mapa ja apareix dins del camí actual (control local de cicles).
     * Aquesta funció només s'usa quan usarLNT = false.
     */
    private boolean estaEnCamino(Mapa inicial, List<Moviment> camino, Mapa objectiu) {
        try {
            // Simulem els moviments per reproduir els estats del camí
            Mapa aux = inicial;
            for (Moviment m : camino) {
                aux = aux.mou(m);
                if (aux.equals(objectiu)) {
                    return true;
                }
            }
        } catch (IllegalArgumentException e) {
            // Si algun moviment és invàlid, simplement l’ignorem
        }
        return false;
    }
}
