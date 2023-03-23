(ns recipe-manager-frontend.cofx
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-cofx
 ::now
 (fn [coeffects _]
   (assoc coeffects :now (system-time))))
