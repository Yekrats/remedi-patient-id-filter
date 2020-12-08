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


(def removable-fields
  ["PatientID" "OrderID" "PatientIdentifier"])

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
        (concat $ (nthrest data (inc col)))
        (into [] $))
      data))

(defn count-lines
  "Takes in File (e.g. from '.getSelectedFile fc') and returns a linecount."
  [file]
  (when (class file)
    (-> file
        .toPath
        Files/lines
        .count)))

(def test-header ["GroupID" "ID" "ClinicianID" "AlertType" "LogTime"
                  "InfusionDeviceNumber" "Model" "SequenceID" "SnapshotID"
                  "CareProfile" "InfusionDeviceVersion" "LogVersion"
                  "DatasetName" "DatasetID" "FacilityID" "ModuleNumber" "Module"
                  "AnesthesiaMode" "VolumeInfusion" "PatientID" "DisposableID"
                  "ActionTaken" "LimitCheckMode" "ProgramType" "PumpState"
                  "PatientHeight" "FieldLimit" "Above_Below" "HardSoft" "AlertLimit"
                  "AlertValue" "PlusMinusLimit" "TimesLimit" "DrugName" "TherapyName"
                  "Channel" "DrugQuantifier" "DrugAmount" "DrugUnit" "Diluent"
                  "DrugDoseCalcBasis" "Concentration" "ConcentrationUnit" "OrderID"
                  "ProgrammingType" "InfusionModifier" "Rate" "InfusionRateUnit"
                  "RateCalcBasis" "VolumeToBeInfuse" "AllMode" "InfusionDuration"
                  "PCAMaxLimit" "PCAMaxLimitPeriod" "PCALockoutInterval"
                  "PCADoseQuantifier" "Dose" "DoseUnit" "InfusionDoseCalcBasis"
                  "InitialPatientWeight" "PropPatientWeight" "WeightUnit" "BSA"
                  "Res_1st_2nd" "StartMode" "NonInfusionCause" "TotalRecord"])
                  ;  (nth (csv/read-csv (io/reader (.getSelectedFile fc))) 3)

(defn find-removables [header]
  (for [removable removable-fields
        x (range (count header))
        :when (= (nth header x) removable)]
    x))

(defn remove-fields [data header]
  (reduce (fn [accum current] (blank-nth accum current))
          data
          (find-removables header)))

(comment


(defn copy-csv [from to]
  (with-open [reader (csv/read-csv from)
              writer (csv/write-csv to)]
    (->> (csv/read-csv reader)
         (map #(rest (butlast %)))
         (csv/write-csv writer))))

(reduce
 (fn [accumulator current-item]  ; <-- accumulator is FIRST argument to function
   ...)                          ; <-- your fn definition goes here
 []                              ; <-- initial value for your accumulator
 [:a :b])                        ; <-- collection to operate on

  )
