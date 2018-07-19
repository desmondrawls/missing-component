(ns reflection.component.datomic
  (:require [datomic.api :as d]
            [com.stuartsierra.component :as component]
            [meta-merge.core :refer [meta-merge]]
            [clojure.java.io :as io]
            [reflection.util :as u])
  (:import datomic.Util))

(defprotocol UniqueName
  (query*         [this q params])
  (pull           [this pattern eid])
  (transact*      [this transaction])
  (resolve-tempid [this tempids tempid]))

(defn tempid [part]
  (d/tempid part))

(defn transact [component transaction]
  (transact* component transaction))

(defrecord DatomicDataSource [uri schema initial-data connection]
  component/Lifecycle
  (start [component]
         (let [c (d/connect uri)]
           (assoc component :connection c)))
  (stop [component]
        (assoc component :connection nil))

  UniqueName
  (query* [{:keys [connection] :as component} q params]
          (let [db (d/db connection)]
            (apply d/q q db params)))

  (pull [{:keys [connection] :as component} pattern eid]
        (let [db (d/db connection)]
          (d/pull db pattern eid)))

  (transact* [{:keys [connection] :as component} transaction]
             @(d/transact connection transaction))

  (resolve-tempid [{:keys [connection] :as component} tempids tempid]
                  (let [db (d/db connection)]
                    (d/resolve-tempid db tempids tempid))))

(defn datomic-component [options]
  (let [uri (System/getenv "RECORDING_STUDIO_TRANSACTOR_URL")]
    (map->DatomicDataSource
      (meta-merge options {:uri uri}))))

(defn query [component q & params]
  (query* component q params))
