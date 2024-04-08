1. chmod +x eval.sh
2. ./eval.sh > result.txt   
    APRIORI_FILE=   
        A1_G6_t1   
    FPGROWTH_FILE=   
        A1_G6_t2   
    DATASET=   
        T2_N100_D10K_Corr5.csv   
        T5_N100_D10K_Corr5.csv   
        T10_N1K_D10K_Corr5.csv   
        T10_N100_D1K_Corr5.csv   
        T10_N100_D10K_Corr5.csv   
        T10_N100_D100K_Corr5.csv   
        T20_N100_D10K_Corr5.csv   
    Compilation   
        javac A1_G6_t1.java    
        javac A1_G6_t2.java    
    RUN   
        java A1_G6_t1 ./database/groceries.csv 0.15   
        java A1_G6_t2 ./database/groceries.csv 0.15