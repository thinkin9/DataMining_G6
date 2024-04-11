1. Submission Files
   * README.md (this file)
   * A1_G6_t1.java (Apriori)
   * A1_G6_t2.java (FP-Growth)
   * DataGeneration.ipynb (jupyter file for Data Generation) (with Google Colab)
   * A1_G6_report.pdf (Report)
   * ./database (database archive)
      * T10_N100_D10K_Corr5.csv (base)  
      * T2_N100_D10K_Corr5.csv (for T) 
      * T5_N100_D10K_Corr5.csv (for T)
      * T15_N100_D10K_Corr5.csv (for T)
      * T20_N100_D10K_Corr5.csv (for T)
      * T10_N100_D1K_Corr5.csv (for D)
      * T10_N100_D20K_Corr5.csv (for D)
      * T10_N100_D50K_Corr5.csv (for D)
      * T10_N100_D100K_Corr5.csv (for D)
2. Our Github Repository: [link](https://github.com/thinkin9/DataMining_G6)
3. Command
   * javac A1_G6_t1.java
   * javac A1_G6_t2.java
   * java A1_G6_t1 ./database/groceries.csv 0.15
   * java A1_G6_t2 ./database/groceries.csv 0.15   
4. To automatic evaluation for our datasets, we implemented shell script
   * chmod +x eval.sh
   * ./eval.sh
5. Results
    * result_Apriori.txt
    * result_FPGrowth.txt
6. Performance (based on Execution time, visualized with Origin)
   * Apriori
     ![image](https://github.com/thinkin9/DataMining_G6/assets/53069520/c440df2c-18bd-4735-93e5-43e2780138b0)
   * FP-Growth
     ![image](https://github.com/thinkin9/DataMining_G6/assets/53069520/2ff1365b-e7a2-4a1e-9079-0c3d7ce085ae)
