(ns clojure-remedi-id-filter.core
  (:gen-class)
  (:import (javax.swing JFrame JPanel JLabel JFileChooser)
           (java.awt Font)
           (java.io File)
           (java.nio.file Files))
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            ))

(def frame (JFrame. "REMEDI Patient ID Filter 2.0"))

(def panel (JPanel.))

(def label (JLabel. "Choose CSV file to filter..."))

(def font (Font. "Courier", 0, 26))

(def fc (JFileChooser.))

(defn copy-csv [from to]
  (with-open [reader (csv/read-csv from)
              writer (csv/write-csv to)]
    (->> (csv/read-csv reader)
         (map #(rest (butlast %)))
         (csv/write-csv writer))))

(defn -main
  [& args]
  (println "Hello, World! " args)
  (doto frame
    (.setSize 800 800)
    (.setVisible true)
    (.setContentPane panel))
  (.setFont label font)
  (.add panel label)
  (.revalidate label)
  (.setDialogTitle fc "Choose CSV file to filter")
  (.setCurrentDirectory fc (File. (System/getProperty "user.home")))

;; if (result == JFileChooser.APPROVE_OPTION) {
;;    File selectedFile = fileChooser.getSelectedFile();
;;    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
;; } \\ Java code.

  (let [result (.showOpenDialog fc panel)
        file (if (= result (JFileChooser/APPROVE_OPTION))
                (.getSelectedFile fc)
                nil)]
    (.setText label (str "Processing file: " (.getDisplayName (.getSelectedFile fc))))
    (println (class file))
    ;(println
    ; (when file
    ;   (-> fc
    ;       csv/read-csv
    ;       first)))
    )

  )

(def testdata ["399477778" "9" "" "Resolution" "Oct 01, 2018 05:52:46 AM" "14433935" "Alaris™ System 8015" "598" "797"
               "Adult ICU" "9.19.1.2" "2.0.0.0" "CHA 031418" "0223ba072-R" "CHA" "14439581" "LVP Module" "" "" "Unknown"
               "" "" "Always" "Continuous with Dose Limit" "" "" "" "" "" "" "" "" "" "NORepinephrine" "Unknown"
               "Alert Channel" "" "4.00" "mg" "250" "" "0.016" "mg/mL" "" "Continuous infusion" "" "112.5" "mL/h" ""
               "176.5" "No" "" "" "" "" "" "30.00" "mcg/min" "None" "" "" "" "" "1st" "Start" "" "5079"])

(def stdata ["1" "2" "3" "a" "b" "c"])

(defn blank-nth "Blanks data in a particular column ('col') and any other optional columns ('cols'). First column is zero."
  [data col]
  (if (< col (count data))
      (as-> data $
        (take col $)
        (into [] $)
        (conj $ "")
        (concat $ (nthrest stdata (inc col)))
        (into [] $))
      data))

(defn count-lines
  "Takes in File (like from '.getSelectedFile fc') and outputs a linecount."
  [file]
  (when (class file)
    (-> file
        .toPath
        Files/lines
        .count)))
