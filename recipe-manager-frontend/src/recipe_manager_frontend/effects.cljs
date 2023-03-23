(ns recipe-manager-frontend.effects
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]))

(s/def ::dispatch sequential?)
(s/def ::ms int?)
(s/def ::id any?)
(s/def ::dispatch-interval-args (s/keys :req-un [::dispatch ::ms ::id]))

(def registered-keys (atom nil))

(re-frame/reg-fx
 :interval/dispatch
 (fn [{:keys [:dispatch :ms :id] :as config}]
   (s/assert ::dispatch-interval-args config)
   (let [interval-id (js/setInterval #(re-frame/dispatch dispatch) ms)]
     (swap! registered-keys assoc id interval-id))))

(re-frame/reg-fx
 :interval/clear
 (fn [{:keys [:id]}]
   (when-let [interval-id (get @registered-keys id)]
     (js/clearInterval interval-id)
     (swap! registered-keys dissoc id))))

(re-frame/reg-fx
 :timer/beep
 (fn [sound]
   (let [audio (js/Audio. sound)]
     (.play audio))))
