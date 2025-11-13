package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.*;

public class CercaIDS extends Cerca {
    private static final int MAX_PROFUNDITAT = 50;
    
    public CercaIDS(boolean usarLNT) {
        super(usarLNT);
    }
    
    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        // IDS con control de profundidad iterativa
        for (int profMax = 0; profMax <= MAX_PROFUNDITAT; profMax++) {
            Map<Mapa, Integer> lnt = usarLNT ? new HashMap<>() : null;
            List<Moviment> resultado = dfsLimitadoLNT(inicial, profMax, lnt, rc, 0, new ArrayList<>(), inicial);
            
            if (resultado != null) {
                rc.setCami(resultado);
                return;
            }
        }
        rc.setCami(null);
    }
    
    private List<Moviment> dfsLimitadoLNT(
        Mapa actual, int profMax, Map<Mapa, Integer> lnt, 
        ResultatCerca rc, int profundidad, 
        List<Moviment> caminoActual, Mapa inicial
    ) {
        // Actualizar memoria
        int memoriaActual = (lnt != null ? lnt.size() : 0);
        rc.updateMemoria(memoriaActual);
        rc.incNodesExplorats();
        
        // Comprobar meta
        if (actual.esMeta()) {
            return new ArrayList<>();
        }
        
        // Límite de profundidad alcanzado
        if (profundidad >= profMax) {
            return null;
        }
        
        // Control de ciclos con LNT
        if (usarLNT && lnt != null) {
            if (lnt.containsKey(actual)) {
                int profAnterior = lnt.get(actual);
                if (profAnterior <= profundidad) {
                    rc.incNodesTallats();
                    return null;
                }
            }
            lnt.put(actual, profundidad);
        }

        // Expandir
        for (Moviment mov : actual.getAccionsPossibles()) {
            try {
                Mapa siguiente = actual.mou(mov);
                
                boolean esRepetit = false;
                if (!usarLNT) {
                    // Control de ciclos local, igual que en el DFS
                    esRepetit = estaEnCamino(inicial, caminoActual, siguiente);
                }
                
                if (!esRepetit) {
                    List<Moviment> nuevoCamino = new ArrayList<>(caminoActual);
                    nuevoCamino.add(mov);
                    List<Moviment> resultado = dfsLimitadoLNT(siguiente, profMax, lnt, rc, profundidad + 1, nuevoCamino, inicial);
                    if (resultado != null) {
                        resultado.add(0, mov);
                        return resultado;
                    }
                } else {
                    rc.incNodesTallats();
                }
            } catch (IllegalArgumentException e) {
                // Movimiento inválido
            }
        }
        return null;
    }

    /** Control local de ciclos sin LNT */
    private boolean estaEnCamino(Mapa inicial, List<Moviment> camino, Mapa objectiu) {
        try {
            Mapa aux = inicial;
            for (Moviment m : camino) {
                aux = aux.mou(m);
                if (aux.equals(objectiu)) return true;
            }
        } catch (IllegalArgumentException e) {
            // ignorar
        }
        return false;
    }
}
