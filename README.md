# Infrastructure required: <br/>
1) Java JDK 8 or above <br/>
2) Command prompt <br/>

# Running the Engine: <br/>
COMPILATION: javac -d . *.java <br/>

EXECUTION: java SearchEngine/SearchEngine input/stopwordlist.txt input/ft911 input/topics.txt input/main.qrels 1 10 <br/>

# Sample Output <br/>
        DOCUMENT STATISTICS
***************************************<br/>
WORDS PARSED: 32843<br/>
DOCUMENTS PARSED: 5368<br/>
TIME TAKEN: 6.311 SEC<br/>
OUTPUT WRITTEN TO vsm_output.txt<br/>
***************************************<br/>
<br/>
        EVALUATING PERFORMANCE<br/>
***************************************<br/>
NUMBER OF DOCS REQUESTED, K = 10<br/>
NUMBER OF HIT DOCS: 40<br/>
NUMBER OF HIT DOCS WHICH ARE RELEVANT: 12<br/>
TOTAL NUMBER OF DOCS WHICH ARE RELEVANT: 22<br/>
PRECISION: 30.0 %<br/>
RECALL: 54.54545454545454 %<br/>
***************************************<br/>
![Output] (https://github.com/karthikVenkataramana/SearchEngine/blob/master/output/vsm_output.txt) <br/>
# Command line arguments <br/>
Argument 1: Stop words list (stopwordlist.txt)<br/>
Argument 2: Documents folder (ft911)<br/>
Argument 3: Query file (topics.txt)<br/>
Argument 4: Manual judgement file (main.qrels)<br/>
Argument 5:<br/>
Option = 1 (Consider only title of query in topics.txt)<br/>
Option = 2 (Consider title + Desc)<br/>
Option = 3 (Consider title + Desc + Narrative)<br/>
Option = 4 (Consider title + Narrative)<br/>
Argument 6: Number of documents to be retreived which are sorted in descending order (K).<br/>
             If( k = 0) , prints all docs with score > 0.<br/>

The output folder consists of 4 output files each with different options as described above and retreiving all docs with score > 0.<br/>
For Code explanation and Analysis, see FinalReport.docs<br/>
