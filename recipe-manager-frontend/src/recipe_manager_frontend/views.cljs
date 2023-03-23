(ns recipe-manager-frontend.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [recipe-manager-frontend.subs :as subs]
   ["@mui/joy" :refer [Typography Tooltip
                       List ListItem ListItemButton
                       Table Box
                       Card Sheet
                       Select Option
                       Button IconButton]]
   ["@mui/icons-material" :refer [Close]]
   [recipe-manager-frontend.events :as events]))

(defn timer
  [path duration]
  (let [display @(re-frame/subscribe [:timer/remaining path duration])
        status @(re-frame/subscribe [:timer/status path])]
    [:> ListItem
     {:endAction
      (when (#{:status/running :status/paused}
             status)
        (reagent/as-element [:> IconButton {:onClick #(re-frame/dispatch [:timer/reset
                                                                          path])
                                            :color "danger"
                                            :variant "solid"}
                             [:> Close]]))}
     [:> ListItemButton (case status
                          :status/running {:onClick #(re-frame/dispatch [:timer/pause
                                                                         path])
                                           :color "success"
                                           :variant "soft"}
                          :status/paused {:onClick #(re-frame/dispatch [:timer/unpause
                                                                        path])
                                          :color  "warning"
                                          :variant "soft"}
                          :status/completed {:onClick #(re-frame/dispatch [:timer/reset
                                                                           path])
                                             :color "danger"
                                             :variant "soft"}
                          {:onClick #(re-frame/dispatch [:timer/start
                                                         path
                                                         duration])
                           :color "primary"})
      display]]))

(defn step [path step-data]
  (let [[_ idx] path]
    [:> List
     [:> ListItem
      [:div
       [:> Typography {:level "h5"}
        (inc idx)]
       [:> Typography
        (:step/instructions step-data)]]]
     (when-let [duration (:step/duration step-data)]
       [timer path duration])]))

(defn steps [path]
  (let [[recipe-key] path
        steps @(re-frame/subscribe [::subs/steps recipe-key])]
    [:> List
     (map-indexed (fn [idx step-data]
                    ^{:key step-data}
                    [:> ListItem
                     [step (conj path idx) step-data]])
                  steps)]))

(defn ingredient-quantity [ingredient-data]
  (str (:ingredient/quantity ingredient-data)
       " "
       (:ingredient/unit ingredient-data)))

(defn ingredient-selector [path options selected-ingredient]
  [:> Select {:size "sm"
              :defaultValue
              (:ingredient/name selected-ingredient)
              :onChange #(re-frame/dispatch [:recipe/select-ingredient
                                             path
                                             options
                                             %2])}
   (for [option options]
     ^{:key option}
     [:> Option
      {:value (:ingredient/name option)}
      (:ingredient/name option)])])

(defn ingredient [path]
  (let [[recipe-key part-key ingredient-key] path
        ingredient-data @(re-frame/subscribe [::subs/ingredient
                                              recipe-key
                                              part-key
                                              ingredient-key])
        options (:ingredient/options ingredient-data)
        selected-ingredient (if options
                              @(re-frame/subscribe [::subs/selected-ingredient
                                                    path
                                                    options])
                              ingredient-data)]
    [:tr
     [:td
      (if options
        [ingredient-selector path options selected-ingredient]
        [:> Typography (:ingredient/name ingredient-data)])]
     [:td
      [ingredient-quantity selected-ingredient]]
     [:td
      [:> Typography (:ingredient/notes selected-ingredient)]]]))

(defn ingredient-table [path]
  (let [[recipe-key part-key] path
        ingredients @(re-frame/subscribe [::subs/ingredients
                                          recipe-key
                                          part-key])]
    [:> Table {:aria-label "ingredient-table"}
     [:thead
      [:tr
       [:th "Ingredient"]
       [:th "Quantity"]
       [:th "Notes"]]]
     [:tbody
      (for [ingredient-key ingredients]
        ^{:key ingredient-key}
        [ingredient (conj path ingredient-key)])]]))

(defn part [path]
  (let [[recipe-key part-key] path
        part-data @(re-frame/subscribe [::subs/part recipe-key part-key])]
    [:> ListItem
     [:div
      [:> Typography {:level "h5"}
       (:recipe/name part-data)]
      [ingredient-table [recipe-key part-key]]]]))

(defn part-list [path]
  (let [[recipe-key] path
        parts @(re-frame/subscribe [::subs/parts
                                    recipe-key])]
    [:> List {:aria-labelledby "part-list"}
     (for [part-key parts]
       ^{:key part-key}
       [part (conj path part-key)])]))

(defn recipe [path]
  (let [[recipe-key] path
        recipe-data @(re-frame/subscribe [::subs/recipe recipe-key])]
    [:> ListItem
     [:div
      [:> Typography {:level "h4"}
       (:recipe/name recipe-data)]
      [part-list path]
      [steps path]
      [:> Typography
       (:recipe/notes recipe-data)]]]))

(defn recipe-list []
  (when-let [recipes @(re-frame/subscribe [::subs/recipes])]
    [:> List
     (for [recipe-key recipes]
       ^{:key recipe-key}
       [recipe [recipe-key]])]))

(defn main-panel []
  [:div
   [recipe-list]])
