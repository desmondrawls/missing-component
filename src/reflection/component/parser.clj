(ns reflection.component.parser
  (:refer-clojure :exclude [read])
  (:require [com.stuartsierra.component :as component]
            [meta-merge.core :refer [meta-merge]]
            [om.next.server :as om]
            [reflection.component.datomic :as d]))

(defmulti read om/dispatch)
(defmethod read :store/journeys
  [{:keys [datomic] :as env} k _]
  (let [v (->> (d/query datomic
                   '[:find [(pull ?p [*]) ...]
                     :where [?p :input/correlation_identifier]])
               (group-by :input/correlation_identifier)
               (map #(assoc {}
                    :journey/id (key %)
                    :journey/inputs (val %)))
               vec)]
    {:value v}))

(defmethod read :store/users
  [{:keys [datomic] :as env} k _]
  (let [v (->> (d/query datomic
                 '[:find [(pull ?p [*]) ...]
                   :where [?p :input/id]])
            (group-by :input/id)
            (map #(assoc {}
                    :journey/id (key %)
                    :journey/inputs (val %)))
            vec)]
    {:value v}))

(defn parse-query [{:keys [datomic] :as component} query]
  (let [parser (om/parser {:read read})]
    (parser {:datomic datomic} [{:store/journeys [:input/name :input/value :input/time]}])))

(defrecord ParserComponent [_]
  component/Lifecycle
  (start [component]
         component)
  (stop [component]
        component))

(defn parser-component [options]
  (map->ParserComponent options))
