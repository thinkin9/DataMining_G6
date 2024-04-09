echo "eval.sh"
echo ""

APRIORI_FILE="A1_G6_t1"
FPGROWTH_FILE="A1_G6_t2"

DATASET="T2_N100_D10K_Corr5.csv T5_N100_D10K_Corr5.csv T10_N1K_D10K_Corr5.csv T10_N100_D1K_Corr5.csv T10_N100_D10K_Corr5.csv T10_N100_D100K_Corr5.csv T20_N100_D10K_Corr5.csv"
MINSUP="0.01 0.05 0.1"

echo "1. Compile Apriori File: $APRIORI_FILE.java"
javac $APRIORI_FILE.java
echo "1. Done."
echo ""

echo "2. Compile FP-Growth File: $FPGROWTH_FILE.java"
javac $FPGROWTH_FILE.java
echo "2. Done."
echo ""

echo "3. RUN with Groceries.csv"
echo "java $APRIORI_FILE ./database/groceries.csv 0.15"
echo "java $APRIORI_FILE ./database/groceries.csv 0.15" > result_Apriori.txt
java $APRIORI_FILE ./database/groceries.csv 0.15 >> result_Apriori.txt

echo "java $FPGROWTH_FILE ./database/groceries.csv 0.15"
echo "java $FPGROWTH_FILE ./database/groceries.csv 0.15" > result_FPGrowth.txt
java $FPGROWTH_FILE ./database/groceries.csv 0.15 >> result_FPGrowth.txt
echo ""
echo "3. Done."

echo "4. RUN with our data"
for DATA in $DATASET; do
    echo "java $APRIORI_FILE ./database/$DATA 0.01"
    echo "java $APRIORI_FILE ./database/$DATA 0.01" >> result_Apriori.txt
    java $APRIORI_FILE ./database/$DATA 0.01 >> result_Apriori.txt

    echo "java $FPGROWTH_FILE ./database/$DATA 0.01"
    echo "java $FPGROWTH_FILE ./database/$DATA 0.01" >> result_FPGrowth.txt
    java $FPGROWTH_FILE ./database/$DATA 0.01 >> result_FPGrowth.txt
    echo ""
done
echo "4. Done."

echo "5. RUN with various min_sup"
for _MINSUP in $MINSUP; do
    echo "java $APRIORI_FILE ./database/T10_N100_D10K_Corr5.csv $_MINSUP"
    echo "java $APRIORI_FILE ./database/T10_N100_D10K_Corr5.csv $_MINSUP" >> result_Apriori.txt
    java $APRIORI_FILE ./database/T10_N100_D10K_Corr5.csv $_MINSUP >> result_Apriori.txt
    
    echo "java $FPGROWTH_FILE ./database/T10_N100_D10K_Corr5.csv $_MINSUP"
    echo "java $FPGROWTH_FILE ./database/T10_N100_D10K_Corr5.csv $_MINSUP" >> result_FPGrowth.txt
    java $FPGROWTH_FILE ./database/T10_N100_D10K_Corr5.csv $_MINSUP >> result_FPGrowth.txt
    echo ""
done
echo "5. Done."