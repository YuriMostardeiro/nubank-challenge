(ns nubank-authorizer.Util.transactionUtil
  (:require [nubank-authorizer.domain.account :as acc]
            [nubank-authorizer.services :as serv]))


(defn isAccountInstancied []
  ;- Nenhuma transação deve ser aceita sem uma conta inicializada corretamente: 'conta não inicializada'
  (println "conta não inicializada")
  )

(defn isAccountCardActive []
  ;- Nenhuma transação deve ser aceita quando o cartão não está ativo: 'cartão não ativo'
  (println "cartão não ativo")
  )

(defn isAccountLimitSufficient []
  ;- O valor da transação não deve exceder o limite disponível: 'limite insuficiente'
  (println "limite insuficiente")
  )

(defn isOnTransactionInterval []
  ;- Não deve haver mais de 3 transações em um intervalo de 2 minutos: 'alta frequência-pequeno-intervalo'
  (println "alta frequência-pequeno-intervalo")
  )

(defn isSingleTransaction []
  ;- Não deve haver mais de uma transação semelhante (mesma quantidade e comerciante) em um intervalo de 2 minutos:   'transação dobrada'
  (println "transação dobrada")
  )