package edu.epsevg.prop.ac1.cerca;


import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CercaIDS extends Cerca {
    public CercaIDS(boolean usarLNT) { super(usarLNT); }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        int profundidad = 0;
        int n = inicial.getN();
        int m = inicial.getM();
        int maxProfundidad = n * m;

        boolean encontrada = false;
        while (profundidad <= maxProfundidad && !encontrada) {
            List<Moviment> camino = new ArrayList<>();
            Set<Mapa> rama = new HashSet<>();
            rc.updateMemoria(rama.size()); // Actualiza antes de empezar nueva iteración (opcional)
            if (dfsLimit(inicial, rc, profundidad, 0, rama, camino)) {
                rc.cami = new ArrayList<>(camino);
                encontrada = true;
            }
            profundidad++;
        }
    }

    private boolean dfsLimit(Mapa mapa, ResultatCerca rc, int limite, int actual, Set<Mapa> rama, List<Moviment> camino) {
        rc.updateMemoria(rama.size());

        if (rama.contains(mapa)) {
            rc.incNodesTallats();
            return false;
        }
        rc.incNodesExplorats();

        if (mapa.esMeta()) return true;
        if (actual >= limite) return false;

        rama.add(mapa);
        for (Moviment mov : mapa.getAccionsPossibles()) {
            try {
                Mapa siguiente = mapa.mou(mov);
                camino.add(mov);
                boolean exito = dfsLimit(siguiente, rc, limite, actual + 1, rama, camino);
                if (exito) return true;
                camino.remove(camino.size() - 1);
            } catch (IllegalArgumentException e) {
                // ignora movimiento
            }
        }
        rama.remove(mapa);
        return false;
    }








}
