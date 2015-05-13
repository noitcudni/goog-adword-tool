(ns seo-tools.dev
  (:require [figwheel.client]
            [seo-tools.core]))

(defn -main []
  (figwheel.client/start)
  (seo-tools.core/-main))

(set! *main-cli-fn* -main)
