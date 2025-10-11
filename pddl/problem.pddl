(define (problem mapa-a)
  (:domain locks-and-keys)
  
  ; ============================================================================
  ; NOMENCLATURA DE CASELLES:
  ;   fXcY significa: Fila X, Columna Y
  ;   Origen (0,0) = esquina superior esquerra del mapa
  ;   
  ; MAPA COMPLET (8x5):
  ;   Col:    0 1 2 3 4 5 6 7
  ;   Fila 0: # # # # # # # #
  ;   Fila 1: # · 2 · · · # #
  ;   Fila 2: # a · · · # @ #
  ;   Fila 3: # · · · 1 · A #
  ;   Fila 4: # # # # # # # #
  ;
  ; On:
  ;   # = paret (no modelada)
  ;   · = espai buit
  ;   2 = agent2 (f1c2)
  ;   1 = agent1 (f3c4)
  ;   a = clau-a (f2c1)
  ;   A = porta-A (f3c6)
  ;   @ = sortida (f2c6)
  ; ============================================================================
  
  (:objects
    agent1 agent2 - agent
    
    ; Caselles transitables (sense paredes)
    ; Fila 1: columnes 1,2,3,4,5
    f1c1 f1c2 f1c3 f1c4 f1c5
    
    ; Fila 2: columnes 1,2,3,4,6 (la columna 5 és paret)
    f2c1 f2c2 f2c3 f2c4 f2c6
    
    ; Fila 3: columnes 1,2,3,4,5,6
    f3c1 f3c2 f3c3 f3c4 f3c5 f3c6 - casella
    
    clau-a - clau
    porta-A - porta
  )
  
  (:init
    ; ===== POSICIONS INICIALS =====
    (at agent2 f1c2)  ; Agent '2' a fila 1, columna 2
    (at agent1 f3c4)  ; Agent '1' a fila 3, columna 4
    
    ; ===== OBJECTES =====
    (clau-at clau-a f2c1)    ; Clau 'a' a fila 2, columna 1
    (porta-at porta-A f3c6)  ; Porta 'A' a fila 3, columna 6
    
    ; ===== RELACIONS =====
    (obre clau-a porta-A)    ; La clau 'a' obre la porta 'A'
    (es-sortida f2c6)        ; Sortida '@' a fila 2, columna 6
    
    ; ===== CASELLES BUIDES (sense porta) =====
    ; Fila 1
    (buida f1c1) (buida f1c2) (buida f1c3) (buida f1c4) (buida f1c5)
    
    ; Fila 2
    (buida f2c1) (buida f2c2) (buida f2c3) (buida f2c4) (buida f2c6)
    
    ; Fila 3
    (buida f3c1) (buida f3c2) (buida f3c3) (buida f3c4) (buida f3c5)
    ; f3c6 NO és buida (conté la porta-A)
    
    ; ===== ADJACÈNCIES HORIZONTALS =====
    ; Fila 1: f1c1 <-> f1c2 <-> f1c3 <-> f1c4 <-> f1c5
    (adjacent f1c1 f1c2) (adjacent f1c2 f1c1)
    (adjacent f1c2 f1c3) (adjacent f1c3 f1c2)
    (adjacent f1c3 f1c4) (adjacent f1c4 f1c3)
    (adjacent f1c4 f1c5) (adjacent f1c5 f1c4)
    
    ; Fila 2: f2c1 <-> f2c2 <-> f2c3 <-> f2c4    f2c6
    ;                                          (paret a c5, no connecta)
    (adjacent f2c1 f2c2) (adjacent f2c2 f2c1)
    (adjacent f2c2 f2c3) (adjacent f2c3 f2c2)
    (adjacent f2c3 f2c4) (adjacent f2c4 f2c3)
    ; NO hi ha adjacent(f2c4, f2c6) perquè hi ha paret a columna 5
    
    ; Fila 3: f3c1 <-> f3c2 <-> f3c3 <-> f3c4 <-> f3c5 <-> f3c6
    (adjacent f3c1 f3c2) (adjacent f3c2 f3c1)
    (adjacent f3c2 f3c3) (adjacent f3c3 f3c2)
    (adjacent f3c3 f3c4) (adjacent f3c4 f3c3)
    (adjacent f3c4 f3c5) (adjacent f3c5 f3c4)
    (adjacent f3c5 f3c6) (adjacent f3c6 f3c5)
    
    ; ===== ADJACÈNCIES VERTICALS =====
    ; Columna 1: f1c1 <-> f2c1 <-> f3c1
    (adjacent f1c1 f2c1) (adjacent f2c1 f1c1)
    (adjacent f2c1 f3c1) (adjacent f3c1 f2c1)
    
    ; Columna 2: f1c2 <-> f2c2 <-> f3c2
    (adjacent f1c2 f2c2) (adjacent f2c2 f1c2)
    (adjacent f2c2 f3c2) (adjacent f3c2 f2c2)
    
    ; Columna 3: f1c3 <-> f2c3 <-> f3c3
    (adjacent f1c3 f2c3) (adjacent f2c3 f1c3)
    (adjacent f2c3 f3c3) (adjacent f3c3 f2c3)
    
    ; Columna 4: f1c4 <-> f2c4 <-> f3c4
    (adjacent f1c4 f2c4) (adjacent f2c4 f1c4)
    (adjacent f2c4 f3c4) (adjacent f3c4 f2c4)
    
    ; Columna 5: f1c5 <-> f3c5 (no existeix f2c5, és paret)
    (adjacent f1c5 f3c5) (adjacent f3c5 f1c5)
    
    ; Columna 6: f2c6 <-> f3c6
    (adjacent f2c6 f3c6) (adjacent f3c6 f2c6)
  )
  
  (:goal
    (or
      (at agent1 f2c6)  ; Agent1 arriba a la sortida
      (at agent2 f2c6)  ; Agent2 arriba a la sortida
    )
  )
)