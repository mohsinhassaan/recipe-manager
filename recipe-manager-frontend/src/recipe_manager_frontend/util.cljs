(ns recipe-manager-frontend.util
  (:require
   [recipe-manager-frontend.db :as db]))

(defn- scale [quantity batch-size]
  (if (pos? batch-size)
    (* batch-size quantity)
    quantity))

(defn base-quantity
  [recipe & [batch-size]]
  (let [quantity (get-in recipe [:recipe/base-ingredient
                                 :ingredient/quantity])]
    (scale quantity batch-size)))

(defn base-unit
  [recipe]
  (get-in recipe [:recipe/base-ingredient :ingredient/unit]))

(defn part-ids
  [recipe]
  (get recipe :recipe/part-list))

(defn- merge-ingredients [a b]
  (merge-with + a b))

(defn ingredients
  [recipe]
  (let [parent-ingredients (get recipe :recipe/ingredient-list)
        children (get recipe :recipe/parts)
        child-ingredients (map #(hash-map (key %1)
                                          (ingredients (val %1)))
                               children)]
    {:ingredient-ids parent-ingredients
     :sub-recipes (apply merge child-ingredients)}))

(defn flat-ingredients
  [recipe]
  (let [parent-ingredients (get recipe :recipe/ingredient-list)
        children (get recipe :recipe/parts)
        child-ingredients (map #(flat-ingredients (val %1))
                               children)]
    (->> (apply concat parent-ingredients
                child-ingredients)
         distinct)))

(defn ingredient
  [recipe part-key ingredient-key & [batches]]
  (let [base-quantity (base-quantity recipe)
        base-unit (base-unit recipe)
        recipe-ingredient (get-in recipe
                                  [:recipe/ingredients ingredient-key])
        part-ingredient (get-in recipe
                                [:recipe/parts part-key
                                 :recipe/ingredients ingredient-key]
                                {:ingredient/quantity 1})
        new-quantity (cond
                       (:ingredient/ratio
                        recipe-ingredient) (* base-quantity
                                              (:ingredient/ratio
                                               recipe-ingredient)
                                              (:ingredient/quantity
                                               part-ingredient))
                       (:ingredient/quantity
                        recipe-ingredient) (* (:ingredient/quantity
                                               recipe-ingredient)
                                              (:ingredient/quantity
                                               part-ingredient)))
        scaled-quantity (if (or (:ingredient/fixed-qty? recipe-ingredient)
                                (:ingredient/fixed-qty? part-ingredient))
                          new-quantity
                          (scale new-quantity batches))
        merged-ingredient (-> (merge recipe-ingredient part-ingredient)
                              (assoc :ingredient/quantity scaled-quantity)
                              (assoc :ingredient/unit
                                     (or (:ingredient/unit part-ingredient)
                                         (:ingredient/unit recipe-ingredient)
                                         base-unit)))]
    (if-let [options (:ingredient/options merged-ingredient)]
      (assoc merged-ingredient
             :ingredient/options
             (map #(ingredient recipe part-key % batches)
                  options))
      merged-ingredient)))

(defn ms->hhmmss
  [ms]
  (-> (js/Date. ms)
      .toISOString
      (.substring 11 19)))
