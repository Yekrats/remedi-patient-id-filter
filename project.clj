(defproject clojure-remedi-id-filter "2.0.2"
  :description "REMEDI program to remove Patient and Clinician IDs"
  :url "https://catalyzecare.org/remedi"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.2"]
                 [org.clojure/data.csv "1.0.0"]]
  :main ^:skip-aot clojure-remedi-id-filter.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
