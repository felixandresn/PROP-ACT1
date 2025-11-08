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
            // System.out.println("Probando profundidad: " + profMax);
            
            Map<Mapa, Integer> lnt = usarLNT ? new HashMap<>() : null;
            List<Moviment> resultado = dfsLimitadoLNT(inicial, profMax, lnt, rc, 0);
            
            if (resultado != null) {
                rc.setCami(resultado);
                return;
            }
        }
        
        rc.setCami(null);
    }
    
    private List<Moviment> dfsLimitadoLNT(Mapa actual, int profMax, Map<Mapa, Integer> lnt, ResultatCerca rc, int profundidad) {
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
            // Si ya visitamos este estado con igual o menor profundidad, podar
            if (lnt.containsKey(actual)) {
                int profAnterior = lnt.get(actual);
                if (profAnterior <= profundidad) {
                    rc.incNodesTallats();
                    return null;
                }
            }
            // Registrar/actualizar con la profundidad actual (menor es mejor)
            lnt.put(actual, profundidad);
        }
        
        // Expandir
        List<Moviment> accions = actual.getAccionsPossibles();
        for (Moviment mov : accions) {
            try {
                Mapa siguiente = actual.mou(mov);
                
                // Control de ciclos sin LNT (solo en rama actual)
                boolean esRepetit = false;
                if (!usarLNT) {
                    // Verificar si ya está en el camino actual (más complejo, necesitaríamos llevar el camino)
                    // Por simplicidad, en modo sin LNT confiamos en que el espacio de estados es manejable
                }
                
                if (!esRepetit) {
                    List<Moviment> resultado = dfsLimitadoLNT(siguiente, profMax, lnt, rc, profundidad + 1);
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
}