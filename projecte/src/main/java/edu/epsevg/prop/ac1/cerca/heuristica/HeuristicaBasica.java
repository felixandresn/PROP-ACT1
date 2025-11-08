package edu.epsevg.prop.ac1.cerca.heuristica;

import edu.epsevg.prop.ac1.model.Mapa;
import edu.epsevg.prop.ac1.model.Posicio;
import java.util.List;

/**
 * Heurística básica optimizada (Manhattan a la clave más cercana o a la salida).
 */
public class HeuristicaBasica implements Heuristica {

    @Override
    public int h(Mapa estat) {
        if (estat == null) return 0;
        if (estat.esMeta()) return 0;

        List<Posicio> agents = estat.getAgents();
        if (agents == null || agents.isEmpty()) return 0;

        int[] ax = new int[agents.size()];
        int[] ay = new int[agents.size()];
        for (int i = 0; i < agents.size(); i++) {
            ax[i] = agents.get(i).x;
            ay[i] = agents.get(i).y;
        }

        int n = estat.getN(), m = estat.getM();
        int minDistClave = Integer.MAX_VALUE;
        boolean hayClaves = false;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int cell = estat.grid[i][j];
                if (Character.isLowerCase(cell)) {
                    hayClaves = true;
                    for (int a = 0; a < ax.length; a++) {
                        int d = Math.abs(ax[a] - i) + Math.abs(ay[a] - j);
                        if (d < minDistClave) minDistClave = d;
                    }
                }
            }
        }

        if (hayClaves) return minDistClave == Integer.MAX_VALUE ? 0 : minDistClave;

        Posicio salida = estat.getSortidaPosicio();
        if (salida == null) return 0;
        int minDistSalida = Integer.MAX_VALUE;
        for (int a = 0; a < ax.length; a++) {
            int d = Math.abs(ax[a] - salida.x) + Math.abs(ay[a] - salida.y);
            if (d < minDistSalida) minDistSalida = d;
        }
        return minDistSalida == Integer.MAX_VALUE ? 0 : minDistSalida;
    }
}
