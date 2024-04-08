echo "eval.sh"
echo ""

APRIORI_FILE="A1_G6_t1"
FPGROWTH_FILE="A1_G6_t2"

DATASET="T2_N100_D10K_Corr5.csv T5_N100_D10K_Corr5.csv T10_N1K_D10K_Corr5.csv T10_N100_D1K_Corr5.csv T10_N100_D10K_Corr5.csv T10_N100_D100K_Corr5.csv T20_N100_D10K_Corr5.csv"

echo "1. Compile Apriori File: $APRIORI_FILE.java"
javac $APRIORI_FILE.java
echo "1. Done."
echo ""

echo "2. Compile FP-Growth File: $FPGROWTH_FILE.java"
javac $FPGROWTH_FILE.java
echo "2. Done."
echo ""

echo "3. RUN with Groceries.csv"
echo "RESULT: java $APRIORI_FILE ./database/groceries.csv 0.15"
java $APRIORI_FILE ./database/groceries.csv 0.15
echo ""
echo "RESULT: java $FPGROWTH_FILE ./database/groceries.csv 0.15"
java $FPGROWTH_FILE ./database/groceries.csv 0.15
echo ""

echo "4. RUN with our data"
for DATA in $DATASET; do
    echo "RESULT: java $APRIORI_FILE ./database/$DATA 0.01"
    java $APRIORI_FILE ./database/$DATA 0.01
    echo ""
    echo "RESULT: java $FPGROWTH_FILE ./database/$DATA 0.01"
    java $FPGROWTH_FILE ./database/$DATA 0.01
    echo ""
done