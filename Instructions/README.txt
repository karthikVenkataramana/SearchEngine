For Code explanation and Analysis, see FinalReport.docs

###### COMMAND LINE ARGS ########
Argument 1: Stop words list (stopwordlist.txt)
Argument 2: Documents folder (ft911)
Argument 3: Query file (topics.txt)
Argument 4: Manual judgement file (main.qrels)
Argument 5:
Option = 1 (Consider only title of query in topics.txt)
Option = 2 (Consider title + Desc)
Option = 3 (Conside title + Desc + Narrative)
Argument 6: Number of documents to be retreived which are sorted in descending order (K).
             If( k = 0) , prints all docs with score > 0.

###### RUNNING THE PROGRAM ######
COMPILATION: javac -d . *.java

EXECUTION: java SearchEngine/SearchEngine input/stopwordlist.txt input/ft911 input/topics.txt input/main.qrels 1 10

####### SAMPLE OUTPUT #########
        DOCUMENT STATISTICS
***************************************
WORDS PARSED: 32843
DOCUMENTS PARSED: 5368
TIME TAKEN: 6.311 SEC
OUTPUT WRITTEN TO vsm_output.txt
***************************************

        EVALUATING PERFORMANCE
***************************************
NUMBER OF DOCS REQUESTED, K = 10
NUMBER OF HIT DOCS: 40
NUMBER OF HIT DOCS WHICH ARE RELEVANT: 12
TOTAL NUMBER OF DOCS WHICH ARE RELEVANT: 22
PRECISION: 30.0 %
RECALL: 54.54545454545454 %
***************************************
