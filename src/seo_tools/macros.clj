(ns seo-tools.macros)

(defmacro postfix-notation
  [expression]
  (conj (butlast expression) (last expression)))

(defmacro <<< [f & args]
  ; add callback
  `(let [c# (cljs.core.async/chan)]
     (do
       (~f ~@args
           (fn [e# x#]
             (if (nil? x#)
               (cljs.core.async/close! c#)
               (cljs.core.async/put! c# x#)
               )
             ))
       c#
       )
     )
  )
