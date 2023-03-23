(ns recipe-manager-frontend.spec
  (:require [cljs.spec.alpha :as s]))

(s/def ::pos (s/double-in :min 0))

(s/def ::db (s/keys :opt-un [:db/recipes
                             :db/timers]))

(s/def :db/timers (s/map-of any?
                            :timers/timer))

(s/def :timers/timer (s/keys :req [:timer/target-time
                                  :timer/last-time
                                  :timer/status]))

(s/def :timer/target-time ::pos)
(s/def :timer/last-time ::pos)
(s/def :timer/status #{:status/running
                       :status/paused
                       :status/completed})

(s/def :db/recipes (s/map-of keyword? ::base-recipe))

(s/def :recipes/base-recipe (s/keys :req [:recipe/name
                                          :recipe/base-ingredient
                                          :recipe/ingredients
                                          :recipe/parts
                                          :recipe/steps]
                                    :opt [:recipe/notes]))

(s/def :recipe/sub-recipe (s/keys :req [:recipe/name
                                        :recipe/ingredients
                                        :recipe/ingredient-list]
                                  :opt [:recipe/steps]))

(s/def :recipe/id keyword?)

(s/def :recipe/name string?)
(s/def :recipe/base-ingredient (s/keys :req [:ingredient/id
                                             :ingredient/quantity
                                             :ingredient/unit]))

(s/def :recipe/ingredient-list (s/* :ingredient/id))
(s/def :recipe/ingredients (s/map-of :ingredient/id
                                     :recipe/ingredient))

(s/def :recipe/ingredient (s/keys :opt [:ingredient/name
                                        :ingredient/quantity
                                        :ingredient/ratio
                                        :ingredient/notes
                                        :ingredient/options
                                        :ingredient/optional?]))

(s/def :recipe/part-list (s/* :recipe/id))
(s/def :recipe/parts (s/map-of :recipe/id
                               :recipe/sub-recipe))

(s/def :ingredient/id keyword?)
(s/def :ingredient/quantity ::pos)
(s/def :ingredient/unit string?)
(s/def :ingredient/name string?)
(s/def :ingredient/ratio ::pos)
(s/def :ingredient/notes string?)
(s/def :ingredient/optional? boolean?)
(s/def :ingredient/options (s/+ :ingredient/id))
(s/def :ingredient/fixed-qty? boolean?)

(s/def :recipe/steps (s/* :recipe/step))
(s/def :recipe/step (s/keys :req [:step/instructions]
                            :opt [:step/duration]))

(s/def :step/instructions string?)
(s/def :step/duration pos-int?)

(s/def :recipe/notes string?)
