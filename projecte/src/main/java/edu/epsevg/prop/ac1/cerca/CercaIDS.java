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
        final int n = inicial.getN();
        final int m = inicial.getM();
        final int maxProf = 45; // si puedes, ajusta con una cota mejor

        class Frame {
            Mapa mapa;
            int prof;
            List<Moviment> accions; // buffer reutilizable
            int idx;
            long fp;
            Frame(Mapa mapa, int prof, long fp, List<Moviment> accions) {
                this.mapa = mapa; this.prof = prof; this.fp = fp;
                this.accions = accions; this.idx = 0;
            }
        }
        // Camino actual
        final java.util.ArrayDeque<Long> rutaStack = new java.util.ArrayDeque<>();
        final java.util.HashSet<Long> rutaSet = new java.util.HashSet<>();
        // Buffer de movimientos
        final List<Moviment> camiMovs = new ArrayList<>();
        // Reutiliza lista temporal 
        final java.util.ArrayDeque<List<Moviment>> poolAccions = new java.util.ArrayDeque<>();
        for (int limite = 0; limite <= maxProf; limite++) {
            // reset 
            rutaStack.clear();
            rutaSet.clear();
            camiMovs.clear();
            rc.updateMemoria(rutaSet.size());
            // inicializa pila
            java.util.ArrayDeque<Frame> pila = new java.util.ArrayDeque<>();
            List<Moviment> acc0 = getAccionsBuffer(poolAccions);
            acc0.addAll(inicial.getAccionsPossibles());
            ordenarAccions(acc0); 
            long fp0 = fingerprint(inicial);
            pila.push(new Frame(inicial, 0, fp0, acc0));
            rutaStack.push(fp0);
            rutaSet.add(fp0);
            boolean encontrada = false;

            while (!pila.isEmpty()) {
                Frame fr = pila.peek();
                rc.updateMemoria(rutaSet.size());
                rc.incNodesExplorats();
                if (fr.mapa.esMeta()) {
                    // copiar camino actual
                    rc.cami = new ArrayList<>(camiMovs);
                    encontrada = true;
                    break;
                }
                if (fr.prof == limite) {
                    // no expandir más
                    releaseAccionsBuffer(poolAccions, fr.accions);
                    pila.pop();
                    rutaStack.pop();
                    rutaSet.remove(fr.fp);
                    if (!camiMovs.isEmpty()) camiMovs.remove(camiMovs.size()-1);
                    continue;
                }
                if (fr.idx >= fr.accions.size()) {
                    // sin acciones
                    releaseAccionsBuffer(poolAccions, fr.accions);
                    pila.pop();
                    rutaStack.pop();
                    rutaSet.remove(fr.fp);
                    if (!camiMovs.isEmpty()) camiMovs.remove(camiMovs.size()-1);
                    continue;
                }
                // siguiente acción
                Moviment mov = fr.accions.get(fr.idx++);
                try {
                    Mapa siguiente = fr.mapa.mou(mov);
                    // poda heurística
                    if (!pasaHeuristica(siguiente, fr.prof + 1, limite)) {
                        rc.incNodesTallats();
                        continue;
                    }
                    long fpHijo = fingerprint(siguiente);
                    if (rutaSet.contains(fpHijo)) {
                        rc.incNodesTallats();
                        continue;
                    }
                    List<Moviment> accH = getAccionsBuffer(poolAccions);
                    accH.addAll(siguiente.getAccionsPossibles());
                    ordenarAccions(accH);
                    // avanzar
                    camiMovs.add(mov);
                    pila.push(new Frame(siguiente, fr.prof + 1, fpHijo, accH));
                    rutaStack.push(fpHijo);
                    rutaSet.add(fpHijo);
                } catch (IllegalArgumentException e) {
                    // acción inválida, saltar
                }
            }
            // libera buffers 
            while (!pila.isEmpty()) {
                releaseAccionsBuffer(poolAccions, pila.pop().accions);
            }
            if (encontrada) return;
        }
    }

    // Genera un fingerprint estable y barato del estado
    private long fingerprint(Mapa m) {
        // Ideal: un hash incremental/bitboard; fallback: hashCode() estabilizado
        // Asegúrate de que Mapa.hashCode() y equals() estén bien implementados y sean rápidos
        return (long) m.hashCode();
    }

    // Poda simple admisible: sustituye con una heurística propia si existe
    private boolean pasaHeuristica(Mapa m, int d, int limite) {
        // Si Mapa expone distancia mínima a meta, úsala aquí.
        // Sin heurística, no se poda:
        return true;
    }

    private List<Moviment> getAccionsBuffer(java.util.ArrayDeque<List<Moviment>> pool) {
        List<Moviment> l = pool.pollFirst();
        if (l == null) l = new ArrayList<>(8);
        return l;
        }
    private void releaseAccionsBuffer(java.util.ArrayDeque<List<Moviment>> pool, List<Moviment> l) {
        l.clear();
        pool.offerFirst(l);
    }
    private void ordenarAccions(List<Moviment> acc) {
        // Orden determinista opcional para coherencia del camino y mejor poda
        // Collections.sort(acc); // si Moviment es Comparable
    }
}

