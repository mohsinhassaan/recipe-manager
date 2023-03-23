(ns recipe-manager-frontend.subs
  (:require
   [re-frame.core :as re-frame]
   [recipe-manager-frontend.util :as util]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::recipes
 (fn [db]
   (keys (:recipes db))))

(re-frame/reg-sub
 ::recipe
 (fn [db [_ recipe-key]]
   (get-in db [:recipes recipe-key])))

(re-frame/reg-sub
 ::parts
 (fn [[_ recipe-key]]
   (re-frame/subscribe [::recipe recipe-key]))

 (fn [recipe _]
   (:recipe/part-list recipe)))

(re-frame/reg-sub
 ::part
 (fn [[_ recipe-key _]]
   (re-frame/subscribe [::recipe recipe-key]))

 (fn [recipe [_ _ part-key]]
   (get-in recipe [:recipe/parts part-key])))

(re-frame/reg-sub
 ::ingredients

 (fn [[_ recipe-key part-key]]
   (re-frame/subscribe [::part recipe-key part-key]))

 (fn [part _]
   (util/flat-ingredients part)))

(re-frame/reg-sub
 ::ingredient

 (fn [[_ recipe-key _ _]]
   (re-frame/subscribe [::recipe recipe-key]))

 (fn [recipe [_ _ part-key ingredient-key]]
   (util/ingredient recipe part-key ingredient-key)))

(re-frame/reg-sub
 ::steps
 (fn [[_ recipe-key _ _]]
   (re-frame/subscribe [::recipe recipe-key]))

 (fn [recipe _]
   (get recipe :recipe/steps)))

(re-frame/reg-sub
 ::selected-ingredient
 (fn [db [_ path options]]
   (get-in db [:options/ingredient path] (first options))))

(re-frame/reg-sub
 ::timer
 (fn [db [_ id]]
   (get-in db [:timers id])))

(re-frame/reg-sub
 :timer/remaining
 (fn [[_ id _]]
   (re-frame/subscribe [::timer id]))
 (fn [timer [_ _ duration]]
   (let [ms (if timer
              (- (:timer/target-time timer)
                 (:timer/last-time timer))
              duration)]
     (util/ms->hhmmss ms))))

(re-frame/reg-sub
 :timer/status
 (fn [[_ id]]
   (re-frame/subscribe [::timer id]))
 (fn [timer _]
   (get timer :timer/status)))
