package edu.epsevg.prop.ac1.cerca;
 
import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class CercaDFS extends Cerca {
    private static final int MAX_PROFUNDITAT = 50; // Límit DFS
    
    public CercaDFS(boolean usarLNT) { 
        super(usarLNT); 
    }
    
    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        Stack<Mapa> pila = new Stack<>();
        Stack<List<Moviment>> caminos = new Stack<>();
        Set<Mapa> visitados = new HashSet<>();
        
        pila.push(inicial);
        caminos.push(new ArrayList<>()); // Camino estado inicial
        
        rc.updateMemoria(pila.size() + caminos.size() + visitados.size());
        
        while (!pila.isEmpty()) {
            Mapa actual = pila.pop();
            List<Moviment> caminoActual = caminos.pop();
            
            rc.updateMemoria(pila.size() + caminos.size() + visitados.size());
            rc.incNodesExplorats();
            
            // Meta trobada
            if (actual.esMeta()) {
                rc.cami = caminoActual;
                return;
            }
            
            // Límit de profunditat
            if (caminoActual.size() >= MAX_PROFUNDITAT) {
                continue;
            }

            // --- CONTROL DE CICLES ---
            if (usarLNT) {
                // ✅ VERSIÓ LNT: control global amb HashSet (no tocat)
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
            } else {
                // 🚫 VERSIÓ SENSE LNT: només controlem repeticions dins del camí actual
                for (Moviment mov : actual.getAccionsPossibles()) {
                    try {
                        Mapa siguiente = actual.mou(mov);
                        // només afegim si no està dins el camí actual
                        if (!estaEnCamino(inicial, caminoActual, siguiente)) {
                            List<Moviment> nuevoCamino = new ArrayList<>(caminoActual);
                            nuevoCamino.add(mov);
                            pila.push(siguiente);
                            caminos.push(nuevoCamino);
                            rc.updateMemoria(pila.size() + caminos.size() + visitados.size());
                        } else {
                            rc.incNodesTallats();
                        }
                    } catch (IllegalArgumentException e) {
                        // ignora movimiento invàlid
                    }
                }
            }
        }
    }

    /**
     * Comprova si un mapa ja apareix dins el camí actual (control local de cicles).
     * Aquesta funció només s'usa quan usarLNT = false.
     */
    private boolean estaEnCamino(Mapa inicial, List<Moviment> camino, Mapa objectiu) {
        try {
            Mapa aux = inicial;
            for (Moviment m : camino) {
                aux = aux.mou(m);
                if (aux.equals(objectiu)) {
                    return true;
                }
            }
        } catch (IllegalArgumentException e) {
            // Si algun moviment és invàlid, simplement el saltem
        }
        return false;
    }
}
