(ns seo-tools.core
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [seo-tools.macros :refer [<<<]]
                   )
  (:require [cljs.nodejs :as nodejs]
            [clojure.string :as s]
            [seo-tools.modules :as m]
            [cljs.core.async :refer [put! chan <! close! take!]] ))

(nodejs/enable-util-print!)
(def fs (nodejs/require "fs"))

(def header-map
  {:ad_group  0
   :keyword  1
   :currency  2
   :avg_monthly_searches  3
   :competition  4
   :suggested_bid  5 })

(def csv-filename "/Users/bdcoe/Documents/workspace/seo-tools/chinese_vocab_builder.csv")

(defn header-order-lst [m]
  (vec (for [[header-key, index] m] header-key)))

(defn open-file[filename]
  (<<< .readFile fs filename "utf8"))

(defn sort-by-col-helper [order col-label rows-cols]
  ; order can be either :ascending or :descending
  (cond (= order :ascending) (sort-by col-label #(compare %1 %2) rows-cols)
        (= order :descending) (sort-by col-label #(compare %2 %1) rows-cols)
        :else (throw js/Error "The sorting order can only be :ascending or :descending")))

(defn sort-by-col-ascending [col-label rows-cols]
  (sort-by-col-helper :ascending col-label rows-cols))

(defn sort-by-col-descending [col-label rows-cols]
  (sort-by-col-helper :descending col-label rows-cols))

(defn keyword-filter [filter-regex rows-cols ]
    (filter #(->> % :keyword (re-find filter-regex) nil? not) rows-cols))

(defn parse-col [convert-func col-key rows-cols]
  (map #(assoc % col-key
               (convert-func (col-key %)))
               ;(if (and
                     ;(not (undefined? %))
                     ;(> (.-length %) 0 ))
                   ;(convert-func (col-key %))
                   ;(col-key %)
                 ;))
       rows-cols))


(defn parse-csv [h-map, data]
  (let [lines (rest (s/split data #"\n"))
        header-lst (header-order-lst h-map)
        rows-cols-raw (map #(s/split % #"\t") lines)
        rows-cols (map #(zipmap header-lst %) rows-cols-raw) ]

    (->> rows-cols
         (parse-col js/parseInt :avg_monthly_searches)
         (parse-col js/parseFloat :competition)
         (parse-col js/parseFloat :suggested_bid)
         )
    )
)

(defn range-filter [rows-cols col-key & {:keys [start end]}]
  (let [has-start (-> start nil? not)
        has-end (-> end nil? not) ]
    ;(println (str "has-start " has-start))
    ;(println (str "has-end " has-end))
    (cond (and has-start has-end) (filter #(let [v (col-key %)] and (>= v start) (<= v end)) rows-cols)
           has-start (filter #(let [v (col-key %)] (>= v start)) rows-cols)
           has-end (filter #(let [v (col-key %)] (<= v end)) rows-cols)
           :else (throw js/Error "must supply either :start or :end")
           )
    )
  )

(defn pprint [rows-cols]
  (let [cnt (count rows-cols)]
    (loop [i 0]
      (when (< i cnt)
        (do
          (println (nth rows-cols i))
          (recur (inc i)))
        )
      )
    )
  )


(defn -main []
  ;(go
    ;(->> (<! (open-file csv-filename))
         ;(parse-csv header-map)
         ;(keyword-filter #"(?i)chinese")
         ;(sort-by-col-descending :avg_monthly_searches)
         ;pprint
      ;)
    ;)

  ;(go (println (count (->> (<! (open-file csv-filename)) (parse-csv header-map) (keyword-filter #"(?i)chinese") (sort-by-col-descending :avg_monthly_searches)))))
  ;(go (pprint (range-filter (->> (<! (open-file csv-filename)) (parse-csv header-map) (keyword-filter #"(?i)chinese") (sort-by-col-descending :avg_monthly_searches)) :suggested_bid :end 1.0)))


  ;(go
    ;(pprint
      ;(->>
        ;(->
          ;( ->> (<! (open-file csv-filename))
                ;(parse-csv header-map)
                ;(keyword-filter #"(?i)chinese")

                ;)
          ;(range-filter :suggested_bid :end 1.0)
          ;)
        ;(sort-by-col-descending :avg_monthly_searches)
        ;)
      ;)
    ;)

    (go (let [data (->> (<! (open-file csv-filename))
                        (parse-csv header-map)
                        (keyword-filter #"(?i)chinese")
                        ;(sort-by-col-descending :avg_monthly_searches)
                        )]
          (pprint (sort-by-col-ascending
                    :avg_monthly_searches
                    (range-filter data :suggested_bid :end 1.0)))
          )
        )

  ;(go (pprint
        ;(->
          ;(->> (<! (open-file csv-filename))
               ;(parse-csv header-map)
               ;(keyword-filter #"(?i)chinese")
               ;(sort-by-col-descending :avg_monthly_searches))
          ;(range-filter :suggested_bid :end 1.0)
          ;)
        ;)
      ;)
  )


(set! *main-cli-fn* -main)
