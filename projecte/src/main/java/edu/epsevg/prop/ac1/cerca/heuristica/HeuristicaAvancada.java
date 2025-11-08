package edu.epsevg.prop.ac1.cerca.heuristica;

import edu.epsevg.prop.ac1.model.Mapa;
import edu.epsevg.prop.ac1.model.Posicio;
import java.util.*;

/**
 * Heurística avanzada ultrarrápida y más informada:
 * - Min distancia agente->clave
 * - + MST (Prim con arrays) entre claves
 * - + min clave->salida
 */
public class HeuristicaAvancada implements Heuristica {

    @Override
    public int h(Mapa estat) {
        if (estat == null) return 0;
        if (estat.esMeta()) return 0;

        List<Posicio> agents = estat.getAgents();
        if (agents == null || agents.isEmpty()) return 0;

        int numAgents = agents.size();
        int[] ax = new int[numAgents];
        int[] ay = new int[numAgents];
        for (int i = 0; i < numAgents; i++) {
            ax[i] = agents.get(i).x;
            ay[i] = agents.get(i).y;
        }

        int n = estat.getN(), m = estat.getM();
        int[] kx = new int[16], ky = new int[16];
        int keyCount = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int c = estat.grid[i][j];
                if (Character.isLowerCase(c)) {
                    if (keyCount >= kx.length) {
                        kx = Arrays.copyOf(kx, kx.length * 2);
                        ky = Arrays.copyOf(ky, ky.length * 2);
                    }
                    kx[keyCount] = i;
                    ky[keyCount] = j;
                    keyCount++;
                }
            }
        }

        if (keyCount == 0) {
            Posicio salida = estat.getSortidaPosicio();
            if (salida == null) return 0;
            int min = Integer.MAX_VALUE;
            for (int a = 0; a < numAgents; a++) {
                int d = Math.abs(ax[a] - salida.x) + Math.abs(ay[a] - salida.y);
                if (d < min) min = d;
            }
            return min == Integer.MAX_VALUE ? 0 : min;
        }

        int minAgentToKey = Integer.MAX_VALUE;
        for (int k = 0; k < keyCount; k++) {
            for (int a = 0; a < numAgents; a++) {
                int d = Math.abs(ax[a] - kx[k]) + Math.abs(ay[a] - ky[k]);
                if (d < minAgentToKey) minAgentToKey = d;
            }
        }

        int mstCost = calcularMST(kx, ky, keyCount);

        int minKeyToExit = 0;
        Posicio salida = estat.getSortidaPosicio();
        if (salida != null) {
            int minKe = Integer.MAX_VALUE;
            for (int k = 0; k < keyCount; k++) {
                int d = Math.abs(kx[k] - salida.x) + Math.abs(ky[k] - salida.y);
                if (d < minKe) minKe = d;
            }
            minKeyToExit = minKe == Integer.MAX_VALUE ? 0 : minKe;
        }

        return Math.max(0, minAgentToKey + mstCost + minKeyToExit);
    }

    private int calcularMST(int[] kx, int[] ky, int n) {
        if (n <= 1) return 0;
        boolean[] used = new boolean[n];
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        used[0] = true;

        for (int i = 1; i < n; i++)
            dist[i] = Math.abs(kx[i] - kx[0]) + Math.abs(ky[i] - ky[0]);

        int cost = 0;
        for (int i = 1; i < n; i++) {
            int best = -1, bestVal = Integer.MAX_VALUE;
            for (int j = 0; j < n; j++)
                if (!used[j] && dist[j] < bestVal) {
                    bestVal = dist[j];
                    best = j;
                }
            if (best == -1) break;
            used[best] = true;
            cost += bestVal;
            for (int j = 0; j < n; j++)
                if (!used[j]) {
                    int d = Math.abs(kx[j] - kx[best]) + Math.abs(ky[j] - ky[best]);
                    if (d < dist[j]) dist[j] = d;
                }
        }
        return cost;
    }
}
