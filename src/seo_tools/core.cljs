(ns seo-tools.core
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [seo-tools.macros :refer [postfix-notation <<<]]
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

;(def csv-filename "/Users/bdcoe/Documents/workspace/seo-tools/chinese_vocab_builder.csv")
(def csv-filename "/Users/bdcoe/Documents/workspace/seo-tools/test.csv")

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


(defn open-file[filename]
  ;(<<< .readFile fs  "utf-8 " filename)
  (<<< .readFile fs filename "utf8")
  )

;(defn parse-file [filename]
  ;(let [cb-chan (chan)]
    ;(.readFile fs
               ;filename
               ;(parse-file-cb cb-chan))
    ;(go (loop []
          ;(let [content (<! cb-chan)]
            ;(println content))))
    ;))

;(defn big-to-little-endian [data-s]
  ;(let [m (map-indexed vector data-s)
        ;even-m (map second (filter #(-> % first even?) m))
        ;odd-m (map second (filter #(-> % first odd?) m))]

    ;(do (println "wtf>>")
        ;(println m)
        ;(s/join (flatten (map list odd-m even-ms))))
    ;)
  ;)


(defn parse-csv [data, h-map, filter-regex]
  (let [lines (rest (s/split data #"\n"))
        header-lst (header-order-lst h-map)
        rows-cols-raw (map #(s/split % #"\t") lines)
        rows-cols (map #(zipmap header-lst %) rows-cols-raw) ]

    ;(println (re-find #"(?i)chinese" (:keyword {:keyword "Chinese foo"})))
    ;(println (type data))


    ;(println (= "Vocab" (s/join (map second (filter #(odd? (first %)) (map-indexed vector (nth (nth rows-cols-raw 0) 0) ))) )))
    ;(pr-str (s/split "Vocab" ""))



    ;(loop [i 0]
      ;(if (< i  (count rows-cols))
        ;(do
          ;;(println (= "vocab builders" (str "" (:keyword (nth rows-cols i)))))
          ;(println (:keyword (nth rows-cols i)))
          ;(println (nth (s/split (:keyword (nth rows-cols i)) "") 1))

          ;(recur (inc i)))
        ;)
      ;)

    ;(println (:keyword (nth rows-cols 2)))
    ;(println (#(:keyword %) (nth rows-cols 0)))

    ;(println (filter #(and (-> % :keyword string?)
                           ;) rows-cols))

    (println (filter #(->> % :keyword (re-find filter-regex) nil? not) rows-cols))
    ;(println rows-cols)

    ;(println (filter #(nil? (re-find filter-regex  (:ad_group %))) rows-cols))
    )
)


(defn -main []
  ;(parse-file csv-filename)
  ;(println (postfix-notation (1 1 +)))
  ;(<<< conj '[] 1)
  ;(parse-file csv-filename)
  ;(println (macroexpand '(<<< .readFile fs csv-filename)))
  ;(m/foobar); test a standalone module

  (go (parse-csv
        (<! (open-file csv-filename))
        header-map
        #"(?i)chinese"))

  ;(.readFile fs csv-filename "utf16le"
             ;(fn [e, d]
               ;(do
                 ;(println (type d))
                 ;(println d)
                 ;(println (map-indexed vector d)))
               ;))
  )


(set! *main-cli-fn* -main)
