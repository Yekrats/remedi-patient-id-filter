(ns clojure-remedi-id-filter.core
  (:gen-class)
  (:import (javax.swing JFrame JPanel JLabel JFileChooser WindowConstants
                        JOptionPane JProgressBar BoxLayout)
           (java.awt Font Component BorderLayout)
           (java.io File)
           (java.nio.file Files)
           (javax.swing.filechooser FileNameExtensionFilter))

  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]

            [clojure.string :as str]))

(def frame (JFrame. "REMEDI Patient ID Filter 2.0"))

(def panel (JPanel.))

(def label-panel (JPanel.))

(def label (JLabel. "Choose CSV file to filter..."))

(def font (Font. "Courier", 0, 26))

(def fc (JFileChooser.))

(def removable-fields  ["PatientID" "OrderID" "PatientIdentifier" "Patient Id" "ClinicianID"])

(defn left-minus-4 "Returns string of all but the last 4 characters, to remove the base extension."
  [str]
  (subs str 0 (- (count str) 4)))

;  (def fnef (FileNameExtensionFilter. "CSV files", (into-array String ["csv"])))

(def JFC-approve (JFileChooser/APPROVE_OPTION))

(defn blank-nth "Blanks data in a particular column ('col'). First column is zero.
                  Inputs the data; outputs the same data with the one column blanked."
  [data col]
  (if (< col (count data))
      (as-> data $
        (take col $)
        (into [] $)
        (conj $ "")
        (concat $ (nthrest data (inc col)))
        (into [] $))
      data))

(defn find-removables
  "Out of 'header' finds fields that match with the 'removable-fields' def.
  Returns numbers of the columns which will be removable. First column is 0."
  [header]
  (for [removable removable-fields
        x (range (count header))
        :when (= (nth header x) removable)]
        x))

(defn remove-fields [data header]
  (reduce (fn [accum current] (blank-nth accum current))
          data
          (find-removables header)))


(defn eval-line "Evaluates line, whether it is header, preheader, or data, and outputs the same line back proper appropriate formatting"
  [data header]
  (if (or (< (count data) 10) (seq (find-removables data)))
    data
    (remove-fields data header))
  )


(defn -main
  []
  (.setSize frame 1200 800)
  (.setVisible frame true)
  (.setContentPane frame panel)
  (.setDefaultCloseOperation frame WindowConstants/DISPOSE_ON_CLOSE)
  (.setSize panel 750 600)
  (.setFont label font)
  (.add panel label)
  (println (System/getProperty "user.home"))
  (.setCurrentDirectory fc (File. (System/getProperty "user.home")))
  (.revalidate panel)
  (.revalidate label)
  (let [result (.showOpenDialog fc panel)
        approved? (= result JFC-approve)
        old-file-object (when approved?
                    (.getSelectedFile fc))
        old-file (when approved? (.getAbsolutePath old-file-object))
        new-file (when approved? (str (left-minus-4 old-file) "_processed_.csv"))
        old-file-short-name (when approved? (.getDisplayName old-file-object))
        header-info (when approved?
                      (loop [line-num 0 text (nth (csv/read-csv (io/reader old-file)) 0 )]
                        (if (or (>= (count text) 10) (> line-num 10))
                                text
                                (recur (inc line-num) (nth (csv/read-csv (io/reader old-file)) (inc line-num))))))]

    (when approved?
      (.setText label (str "Processing file: " old-file-short-name ))

      (with-open [reader (io/reader old-file) writer (io/writer new-file)]
                                 (as-> (csv/read-csv reader) $
                                   (map #(eval-line % header-info) $)
                                   (csv/write-csv writer $)))
      (JOptionPane/showMessageDialog nil (str  "File processed:\n" old-file-short-name)
                                     "Files processed." JOptionPane/INFORMATION_MESSAGE)))
; END OF LET BLOCK.

  (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
  (.dispose frame)
  ;(System/exit 0)
  )
