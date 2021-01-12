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

(def pbar-panel (JPanel.))

(def label (JLabel. "Choose CSV file to filter..."))

(def font (Font. "Courier", 0, 26))

(def fc (JFileChooser.))

(def removable-fields  ["PatientID" "OrderID" "PatientIdentifier" "Patient Id" "ClinicianID"])

(def fnef (FileNameExtensionFilter. "CSV files", (into-array String ["csv"])))

(def progress-bar (JProgressBar.))

(defn selected-file []
  (.getSelectedFile fc))

(defn selected-file-full-path []
  (when (selected-file) (.getAbsolutePath (.getSelectedFile fc))))

(defn selected-file-name []
  (when (selected-file) (.getDisplayName (.getSelectedFile fc))))

(defn left [str]
  (subs str 0 (- (count str) 4)))

(defn selected-file-base-name []
  (when (selected-file)
    (-> (.getSelectedFile fc)
        .getAbsolutePath
        left)))

(defn selected-file-name-processed []
  (when (selected-file)
    (str (selected-file-base-name) "_processed_.csv")))

(defn -main
  [& args]
  (println "Hello, World! " args)
  (doto frame
    (.setSize 1200 800)
    (.setVisible true)
    (.setContentPane panel)
;    (.setContentPane pbar-panel)
    (.setDefaultCloseOperation WindowConstants/DISPOSE_ON_CLOSE)
;    (.setLayout (BorderLayout.))
    )
;  (.setLayout panel (BoxLayout. panel BoxLayout/Y_AXIS))
;  (.setSize pbar-panel 750 200)
  (.setSize panel 750 600)
;  (.setValue progress-bar 0)
;  (.setStringPainted progress-bar true)
;  (.setVisible progress-bar true)
;  (.add pbar-panel progress-bar BorderLayout/SOUTH)
;  (.setVisible pbar-panel true)
  (.setFont label font)
  (.add panel label)

  (.setFileFilter fc fnef)
  (.setDialogTitle fc "Choose CSV file to filter")
  (.setCurrentDirectory fc (File. (System/getProperty "user.home")))
  (.revalidate panel)
;  (.revalidate pbar-panel)
  (.revalidate label)
;  (.revalidate progress-bar)

  (let [result (.showOpenDialog fc panel)
        file-info (when (= result (JFileChooser/APPROVE_OPTION))
                    (.getSelectedFile fc))
        new-file  (str (selected-file-base-name) "_processed_.csv")]
    (.setText label (str "Processing file: " (selected-file-name)))
    (with-open [reader (io/reader file-info) writer (io/writer new-file)]
      (loop [file file header nil line-num 1]
        )))
  (JOptionPane/showMessageDialog nil (str  "Files processed:\n" (selected-file-name)) "Files processed." JOptionPane/INFORMATION_MESSAGE)
    (.dispose frame)
  )

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

(defn count-lines
  "Takes in File (e.g. from '.getSelectedFile fc') and returns a linecount."
  ([file]
  (when (class file)
    (-> file
        .toPath
        Files/lines
        .count)))
  ([] (count-lines (.getSelectedFile fc))))

(defn find-removables ; TALK
  "Out of 'header' finds fields that match with the 'removable-fields' def.
  Returns numbers of the columns which will be removable. First column is 0."
  [header]
  (for [removable removable-fields
        x (range (count header))
        :when (= (nth header x) removable)]
    x))

(defn remove-fields [data header] ; TALK
  (reduce (fn [accum current] (blank-nth accum current))
          data
          (find-removables header)))

(defn find-header "Finds the first line with over 10 entries. Returns the contents of that, and the number of lines."
  [file]
  (when (class file) ;; Are there actually contents in the file? Is it an opject yet?
      (loop [line-num 0 text (nth (csv/read-csv (io/reader file)) 0) ]
        (if (>= (count text) 10)
          (into (sorted-map) [[:header text] [:line line-num]])
          (recur (inc line-num) (nth (csv/read-csv (io/reader file)) (inc line-num)))))))

(defn copy-csv [from to]
  (with-open [reader (io/reader from)
              writer (io/writer to)]

    ))
(defn massage-data [data]

  )

;(defn read-write
;  [] (read-write (.getSelectedFile fc) )
;  [file] )
(def test-data ["399477778" "9" "REMOVE-ME-CLINICIAN-ID" "Resolution" "Oct 01, 2018 05:52:46 AM" "14433935" "Alaris™ System 8015" "598" "797"
               "Adult ICU" "9.19.1.2" "2.0.0.0" "CHA 031418" "0223ba072-R" "CHA" "9999999" "LVP Module" "" "" "REMOVE-ME-PATIENTID"
               "" "" "Always" "Continuous with Dose Limit" "" "" "" "" "" "" "" "" "" "NORepinephrine" "Unknown"
               "Alert Channel" "" "4.00" "mg" "250" "" "0.016" "mg/mL" "REMOVE-ME-ORDERID" "Continuous infusion" "" "112.5" "mL/h" ""
               "176.5" "No" "" "" "" "" "" "30.00" "mcg/min" "None" "" "" "" "" "1st" "Start" "" "5079"])

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

(comment



  (def stdata ["1" "2" "3" "a" "b" "c"])



                  ;  (nth (csv/read-csv (io/reader (.getSelectedFile fc))) 3)

)
