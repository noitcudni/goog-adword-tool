(ns seo-tools.macros)

(defmacro postfix-notation
  [expression]
  (conj (butlast expression) (last expression)))

(defmacro <<< [f & args]
  ; add callback
  (concat '(let [c (cljs.core.async/chan)])
          (list (concat (list f)
                  args
                  (list  '(fn [e, x]
                            (if (or (nil? x)
                                    (undefined? x))
                              (cljs.core.async/close! c)
                              (cljs.core.async/put! c x)))
                        )
                  ))
          (list 'c)
          )
  )
