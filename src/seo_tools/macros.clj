(ns seo-tools.macros)

(defmacro postfix-notation
  [expression]
  (conj (butlast expression) (last expression)))

(defmacro <<< [f & args]
  ; add callback
  (concat (list f)
          args
          (list  '(fn [e, x]
                    (let [c (cljs.core.async/chan)]
                      (if (or (nil? x)
                              (undefined? x))
                        (cljs.core.async/close! c)
                        (do
                          (cljs.core.async/put! c x)
                          c )))))
          ))
