(ns recipe-manager-frontend.db)

(def default-db
  {:name "name test"
   :timer/sound "/audio/beep-beep.mp3"
   :recipes
   {:yudane-loaf
    {:recipe/name "Whole Wheat Sandwich Loaf"

     :recipe/base-ingredient
     {:ingredient/id :flour
      :ingredient/quantity 450
      :ingredient/unit "g"}

     :recipe/ingredients
     {:flour {:ingredient/name "Whole Wheat Flour"
              :ingredient/ratio (/ 450 450)
              :ingredient/notes ""}
      :water {:ingredient/name "Water"
              :ingredient/ratio (/ 300 450)}
      :instant-yeast {:ingredient/name "Instant Dry Yeast"
                      :ingredient/ratio (/ 5 450)}
      :active-dry-yeast {:ingredient/name "Active Dry Yeast"
                         :ingredient/ratio (/ 6 450)}
      :fresh-yeast {:ingredient/name "Fresh Yeast"
                    :ingredient/ratio (/ 15 450)}
      :sugar {:ingredient/name "Sugar"
              :ingredient/ratio (/ 25 450)}
      :oil {:ingredient/name "Oil"
            :ingredient/ratio (/ 25 450)
            :ingredient/notes "I used olive oil."}
      :egg-yolks {:ingredient/name "Egg Yolks"
                  :ingredient/quantity 2
                  :ingredient/unit ""}
      :salt {:ingredient/name "Salt"
             :ingredient/ratio (/ 8 450)}
      :egg {:ingredient/name "Whole Egg"
            :ingredient/quantity 1
            :ingredient/fixed-qty? true
            :ingredient/unit ""
            :ingredient/notes "For brushing."}}

     :recipe/parts
     {:glaze
      {:recipe/name "Glaze"
       :recipe/ingredients
       {:egg {:ingredient/quantity 1}}
       :recipe/ingredient-list
       [:egg]}
      :yudane
      {:recipe/name "Yudane"
       :recipe/ingredients
       {:flour {:ingredient/quantity (/ 100 450)}
        :water {:ingredient/quantity (/ 150 300)
                :ingredient/notes "Boiling water."}}
       :recipe/ingredient-list
       [:flour
        :water]}
      :main-dough
      {:recipe/name "Main Dough"
       :recipe/ingredients
       {:flour
        {:ingredient/quantity (/ 350 450)}
        :water
        {:ingredient/quantity (/ 150 300)}
        :yeast
        {:ingredient/options [:instant-yeast
                              :active-dry-yeast
                              :fresh-yeast]}
        :sugar
        {:ingredient/optional? true}}
       :recipe/ingredient-list
       [:flour
        :water
        :yeast
        :sugar
        :oil
        :egg-yolks
        :salt]}}

     :recipe/part-list [:yudane :main-dough :glaze]

     :recipe/steps
     [{:step/instructions "Make the yudane. Combine the flour and boiling water. Mix until there is no dry flour left. Leave to cool down completely. If your kitchen is quite warm you can refrigerate the yudane to help control the final dough temperature. You can also make it a day ahead of time and keep it in the fridge until you need it."}
      {:step/instructions "Make the dough. In a large bowl combine all the ingredients except the remaining flour. Mix well. Add the flour and mix to a dough."}
      {:step/instructions "Tip the dough out on the table and knead it for 6 minutes. It will be extremely sticky, so stop every now and then and scrape it together. The stickiness comes from the ingredients used. Egg yolks, sugar, and the oil all make the dough stickier. Combine those with whole wheat flour and you are going to get a stickier than normal dough. If you have a mixer – use it! *Desired dough temperature 25C (77F). If your dough is warmer, then it will ferment more rapidly. If it is cooler, then it will take longer. Adjust proofing time accordingly."
       :step/duration 360000}
      {:step/instructions "Cover and ferment for 1 hour."
       :step/duration 3600000}
      {:step/instructions "Fold."}
      {:step/instructions "Ferment for 1 more hour."
       :step/duration 3600000}
      {:step/instructions "Divide the dough into 4 equal pieces and pre-shape. You can divide it into two if you want or even leave it whole. The design is up to you."}
      {:step/instructions "Leave to rest for 15 minutes."
       :step/duration 900000}
      {:step/instructions "Perform the final shaping and place the dough in a bread tin."}
      {:step/instructions "Cover and final proof for 1.5 hours or until well puffed up. *During the final hour of fermentation preheat your oven to 160C (320F) fan on."
       :step/duration 5400000}
      {:step/instructions "Brush the loaf with egg and bake it for 40 minutes."
       :step/duration 2400000}]

     :recipe/notes
     "The flour I use has a protein content of 13%. If your flour is weaker, then you may need to lower the hydration.

  If you are using active dry yeast, then you may need to let it sit in the water for 10 minutes before adding the other ingredients or else it could take a lot longer to raise the dough.

  If you are curious about why the dough contains oil, egg, and sugar, click the links to learn more about the effects those ingredients have on bread dough."}}})
