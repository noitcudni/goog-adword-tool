try {
    require("source-map-support").install();
} catch(err) {
}
require("./out.dev/goog/bootstrap/nodejs.js");
require("./out.dev/seo_tools.js");
goog.require("seo_tools.dev");
goog.require("cljs.nodejscli");
