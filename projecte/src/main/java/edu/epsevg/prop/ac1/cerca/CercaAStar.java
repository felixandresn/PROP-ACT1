package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.cerca.heuristica.Heuristica;
import edu.epsevg.prop.ac1.model.Mapa;
import edu.epsevg.prop.ac1.model.Moviment;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.*;

/**
 * A* rápido y compatible con Node original.
 * - Calcula h y f una sola vez por nodo.
 * - Usa NodeRecord para no recalcular heurística en el comparator.
 * - Mantiene LNT con costes mínimos.
 */
public class CercaAStar extends Cerca {

    private final Heuristica heur;

    private static class NodeRecord {
        final Node node;
        final int h;
        final int f;
        NodeRecord(Node node, int h) {
            this.node = node;
            this.h = h;
            this.f = node.g + h;
        }
    }

    public CercaAStar(boolean usarLNT, Heuristica heur) {
        super(usarLNT);
        this.heur = heur;
    }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        Comparator<NodeRecord> cmp = (a, b) -> {
            if (a.f != b.f) return Integer.compare(a.f, b.f);
            if (a.node.g != b.node.g) return Integer.compare(a.node.g, b.node.g);
            return Integer.compare(a.node.estat.hashCode(), b.node.estat.hashCode());
        };

        PriorityQueue<NodeRecord> frontera = new PriorityQueue<>(cmp);
        Map<Mapa, Integer> lnt = usarLNT ? new HashMap<>() : null;

        int hInicial = Math.max(0, heur.h(inicial));
        Node nodeInicial = new Node(inicial, null, null, 0, 0);
        frontera.add(new NodeRecord(nodeInicial, hInicial));

        if (usarLNT) lnt.put(inicial, 0);

        while (!frontera.isEmpty()) {
            int memoriaActual = frontera.size() + (usarLNT && lnt != null ? lnt.size() : 0);
            rc.updateMemoria(memoriaActual);

            NodeRecord rec = frontera.poll();
            Node actual = rec.node;

            if (usarLNT && lnt != null) {
                Integer mejorCoste = lnt.get(actual.estat);
                if (mejorCoste != null && mejorCoste < actual.g) continue;
            }

            rc.incNodesExplorats();

            if (actual.estat.esMeta()) {
                rc.setCami(reconstruirCami(actual));
                return;
            }

            for (Moviment accio : actual.estat.getAccionsPossibles()) {
                try {
                    Mapa nouEstat = actual.estat.mou(accio);
                    int nouG = actual.g + 1;
                    boolean descartar = false;

                    if (usarLNT && lnt != null) {
                        Integer anterior = lnt.get(nouEstat);
                        if (anterior != null && anterior <= nouG) {
                            descartar = true;
                            rc.incNodesTallats();
                        } else {
                            lnt.put(nouEstat, nouG);
                        }
                    }

                    if (!descartar) {
                        int h = Math.max(0, heur.h(nouEstat));
                        Node nouNode = new Node(nouEstat, actual, accio, actual.depth + 1, nouG);
                        frontera.add(new NodeRecord(nouNode, h));
                    }
                } catch (IllegalArgumentException e) {
                    // movimiento inválido -> ignorar
                }
            }
        }

        rc.setCami(null);
    }

    private List<Moviment> reconstruirCami(Node node) {
        LinkedList<Moviment> cami = new LinkedList<>();
        while (node != null && node.pare != null) {
            cami.addFirst(node.accio);
            node = node.pare;
        }
        return cami;
    }
}
