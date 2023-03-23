(ns recipe-manager-frontend.events
  (:require
   [re-frame.core :as re-frame]
   [recipe-manager-frontend.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [recipe-manager-frontend.spec :as specs]
   [recipe-manager-frontend.cofx :as cofx]
   [recipe-manager-frontend.effects]
   [cljs.spec.alpha :as s]))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced
  [_ _]
  {:pre [#(s/valid? ::specs/db db/default-db)]}
  db/default-db))

(re-frame/reg-event-db
 :recipe/select-ingredient
 (fn-traced
  [db [_ path options ingredient-name]]
  (assoc-in db
            [:options/ingredient path]
            (first (filter (fn [option]
                             (= ingredient-name
                                (:ingredient/name option)))
                           options)))))

(re-frame/reg-event-fx
 :timer/start
 [(re-frame/inject-cofx ::cofx/now (js/Date.))]
 (fn-traced
  [{:keys [db now]} [_ id duration]]
  (let [target-time (+ now
                       duration)]
    {:db (assoc-in db
                   [:timers id]
                   {:timer/last-time now
                    :timer/target-time target-time
                    :timer/status :status/running})
     :interval/dispatch {:dispatch [:timer/tick id]
                         :id id
                         :ms 1000}})))

(re-frame/reg-event-fx
 :timer/tick
 [(re-frame/inject-cofx ::cofx/now (js/Date.))]
 (fn-traced
  [{:keys [db now]} [_ id]]
  (let [{:keys [:timer/last-time
                :timer/target-time
                :timer/status]
         :as timer} (get-in db
                            [:timers id])
        since-last (- now last-time)
        next-timer (if (= :status/paused
                          status)
                     (assoc timer
                            :timer/target-time (+ target-time
                                                  since-last)
                            :timer/last-time now)
                     (assoc timer
                            :timer/last-time now))]
    (cond
      (= status :status/completed) {:timer/beep (get db :timer/sound)}
      (< target-time now) {:db (assoc-in db [:timers id :timer/status] :status/completed)}
      :else {:db (assoc-in db [:timers id] next-timer)}))))

(re-frame/reg-event-fx
 :timer/pause
 (fn-traced
  [{:keys [db]} [_ id]]
  {:db (assoc-in db [:timers id :timer/status] :status/paused)
   :dispatch [[:timer/tick id]]}))

(re-frame/reg-event-fx
 :timer/unpause
 (fn-traced
  [{:keys [db]} [_ id]]
  {:db (assoc-in db [:timers id :timer/status] :status/running)
   :dispatch [[:timer/tick id]]}))

(re-frame/reg-event-fx
 :timer/reset
 (fn-traced
  [{:keys [db]} [_ id]]
  {:db (update db :timers #(dissoc % id))
   :interval/clear {:id id}}))

