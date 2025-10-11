(define (domain locks-and-keys)
  (:requirements :strips :typing)
  
  (:types
    casella agent clau porta
  )
  
  (:predicates
    (at ?a - agent ?c - casella)              ; agent està a la casella
    (clau-at ?k - clau ?c - casella)          ; clau està a la casella
    (porta-at ?p - porta ?c - casella)        ; porta està EN aquesta casella
    (te-clau ?k - clau)                       ; algú ha recollit la clau
    (obre ?k - clau ?p - porta)               ; aquesta clau obre aquesta porta
    (porta-oberta ?p - porta)                 ; porta està oberta
    (adjacent ?c1 - casella ?c2 - casella)    ; caselles adjacents
    (es-sortida ?c - casella)                 ; casella és la sortida
    (buida ?c - casella)                      ; casella sense porta (pot entrar-hi lliurement)
  )
  
  ; Moure's a una casella buida
  (:action moure
    :parameters (?a - agent ?origen - casella ?desti - casella)
    :precondition (and
      (at ?a ?origen)
      (adjacent ?origen ?desti)
      (buida ?desti)
    )
    :effect (and
      (at ?a ?desti)
      (not (at ?a ?origen))
    )
  )
  
  ; Moure's i recollir clau (la casella amb clau és buida)
  (:action moure-i-recollir
    :parameters (?a - agent ?origen - casella ?desti - casella ?k - clau)
    :precondition (and
      (at ?a ?origen)
      (adjacent ?origen ?desti)
      (buida ?desti)
      (clau-at ?k ?desti)
    )
    :effect (and
      (at ?a ?desti)
      (not (at ?a ?origen))
      (te-clau ?k)
      (not (clau-at ?k ?desti))
    )
  )
  
  ; Obrir porta i entrar (primera vegada - obre la porta)
  (:action obrir-i-entrar
    :parameters (?a - agent ?origen - casella ?desti - casella ?p - porta ?k - clau)
    :precondition (and
      (at ?a ?origen)
      (adjacent ?origen ?desti)
      (porta-at ?p ?desti)
      (obre ?k ?p)
      (te-clau ?k)
      (not (porta-oberta ?p))
    )
    :effect (and
      (at ?a ?desti)
      (not (at ?a ?origen))
      (porta-oberta ?p)
    )
  )
  
  ; Entrar a porta oberta
  (:action entrar-porta-oberta
    :parameters (?a - agent ?origen - casella ?desti - casella ?p - porta)
    :precondition (and
      (at ?a ?origen)
      (adjacent ?origen ?desti)
      (porta-at ?p ?desti)
      (porta-oberta ?p)
    )
    :effect (and
      (at ?a ?desti)
      (not (at ?a ?origen))
    )
  )
)