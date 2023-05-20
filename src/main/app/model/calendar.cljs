(ns app.model.calendar
  (:require
   [taoensso.timbre :as log]
   [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
   [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))

(defn event-path
  "Normalized path to an event entity or field in Fulcro state-map"
  ([id field] [:event/id id field])
  ([id] [:event/id id]))

(defn insert-event*
  "Insert a user into the correct table of the Fulcro state-map database."
  [state-map {:keys [:event/id] :as event}]
  (assoc-in state-map (event-path id) event))

(defmutation upsert-event
  "Client Mutation: Upsert a user (full-stack. see CLJ version for server-side)."
  [{:keys [:event/id :event/title] :as params}]
  (action [{:keys [state]}]
          (log/info "Upsert event action")
          (swap! state (fn [s]
                         (-> s
                             (insert-event* params)
                             (targeting/integrate-ident* [:event/id id] :append [:all-events])))))
  (ok-action [env]
             (log/info "OK action"))
  (error-action [env]
                (log/info "Error action"))

  #_(remote [env]
            (-> env
                (m/returning 'app.ui.root/User)
                (m/with-target (targeting/append-to [:all-accounts])))))

