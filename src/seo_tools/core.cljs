(ns seo-tools.core
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [seo-tools.macros :refer [postfix-notation <<<]]
                   )
  (:require [cljs.nodejs :as nodejs]
            [clojure.string :as s]
            [seo-tools.modules :as m]
            [cljs.core.async :refer [put! chan <! close!]] ))

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

;(defn parse-file-cb [err, data]
  ;(let [lines (rest (s/split data #"\n"))
        ;header-lst (header-order-lst header-map)
        ;rows-cols (map #(s/split % #"\t") lines)]
    ;(println (map #(zipmap header-lst %) rows-cols))
    ;))


;(defmacro <<< [f & args]
  ;`(f ~args))

;(defn <<< [f & args]
  ;(let [c (chan)]
    ;;(println f)
    ;;(apply f (concat args [(fn [e, x]
                             ;;(if (or (nil? x)
                                     ;;(undefined? x))
                               ;;(close! c)
                               ;;(put! c x)))]))
    ;c))

;(defn parse-file-cb [cb-chan]
  ;; returns a function
  ;(fn [err, data]
    ;(let [lines (rest (s/split data #"\n"))
          ;header-lst (header-order-lst header-map)
          ;rows-cols (map #(s/split % #"\t") lines)]
      ;(put! cb-chan (map #(zipmap header-lst %) rows-cols)) )))


(defn parse-file [filename]
  (<<< .readFile fs filename))

;(defn parse-file [filename]
  ;(let [cb-chan (chan)]
    ;(.readFile fs
               ;filename
               ;(parse-file-cb cb-chan))
    ;(go (loop []
          ;(let [content (<! cb-chan)]
            ;(println content))))
    ;))


(defn -main []
  ;(parse-file csv-filename)
  ;(println (postfix-notation (1 1 +)))
  ;(<<< conj '[] 1)
  ;(parse-file csv-filename)
  ;(println (macroexpand '(<<< .readFile fs csv-filename)))
  ;(m/foobar); test a standalone module
  (parse-file csv-filename)
  )

(set! *main-cli-fn* -main)
