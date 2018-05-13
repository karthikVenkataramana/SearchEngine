/*
        Author: Karthik Venkataramana Pemmaraju
        Date: 04/15/2018 and 04/22/2018
        Description:  A complete search engine! 
        Compilation: javac -d . *.java (Required one's are Porter.java, Word.java, Document.java)
        Execution: java SearchEngine/SearchEngine <Name of Stop words file> <Name of folder containing all the files> <query_file> <judgment_file> <Options> <Number of Documents to Retreive>
*/
package SearchEngine;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.time.Instant;
public class SearchEngine {
    final static Hashtable <String, Boolean> stopWords = new Hashtable <String, Boolean> ();    // List of stop words.
    static boolean validText = false;
    static Document currentDocument;    // For parsing files in FT911 folder.
    /*
        SECTION 1: Parsing Stop Words list.    
        @Desc: Reads all the stop words from file and put's them into a Map.
    */
     static void buildStopWords(String fileName) throws Exception {
        Files.readAllLines(Paths.get(fileName))
            .stream()
            .forEach(word -> stopWords.put(word.trim(), true)); // Important! There are spaces in stop words file!.   
    }
    final static Hashtable <String, Document> documentIndex = new Hashtable <String, Document> ();  // Contains list of all documents.
    final static Hashtable <String, Word> wordIndex = new Hashtable <String, Word> ();  // Contains list of all Words.
    /*
        SECTION 2: Parsing the FT911 folder.
        @Desc: Returns true if the text is valid. Also, puts document ID's into File Dictionary.
    */
     static synchronized void addWords(Path path) {
        try {
            Files.readAllLines(path)
                .stream()
                .filter(line -> checkID(line.trim())) // Removing leading or trailing spaces.
                .map(line -> line.replaceAll("\\w*\\d\\w*", "").toLowerCase().split("\\s*[^a-z]\\s*")) // Replace digit words with empty and split on punctuations.
                .flatMap(Arrays::stream) // Convert Stream of Streams into streams.
                .filter(word -> (!word.isEmpty()) && !(stopWords.get(word) != null)) // Eliminate empty words and stop words.
                .map(word -> new Porter().stripAffixes(word)) // Calling Porter Stemmer Algorithm
                .forEach(word -> buildIndexer(word)); // Builds the Index Table
        } catch (IOException e) {};
    }
    /*
        @Desc: Checks if the word is valid or not (If present in between <TEXT> and </TEXT>)
    */
    static boolean checkID(String line) {
        if (line.startsWith("<DOCNO>") && line.endsWith("</DOCNO>")) {
            String fileName = line.replaceAll("[<DOCNO></DOCNO>]", "");
            int currentDocumentId = Integer.parseInt(fileName.substring(6, fileName.length()));
            currentDocument = new Document(currentDocumentId, fileName);
            documentIndex.put(fileName, currentDocument);
        } else if (line.contains("<TEXT>"))
            validText = true;
        else if (line.contains("</TEXT"))
            validText = false;
        return (validText && !(line.equals("<TEXT>") || line.equals("</TEXT>"))); // Return true for all valid Texts, except for tags themselves.
    }    
    /*
        @Desc: Build the Indexer (Both Forward and Reverse). The indexer is built on the fly so no revisiting or reparsing the file is required.
    */
    static synchronized void buildIndexer(String wordName) {
        Word word = wordIndex.get(wordName);
        try{
        if (word == null) { // No Entry for the word.
            word = new Word(wordName);
            wordIndex.put(wordName, word); // First instance of the word.
            word.docFrequency.put(currentDocument, true);  
        } 
        else if (word.docFrequency.get(currentDocument) == null) 
                word.docFrequency.put(currentDocument, true);
        if(currentDocument.word.get(word) == null) // Word exists, but not in current document.
            currentDocument.word.put(word, new Weight()); // Add word to file Dictionary
        else    
            currentDocument.word.get(word).incrementTf();   // tf - d
        } catch(Exception e){}
        word.increment_TermFrequency();     // Total tf
    }
    final static Hashtable <Integer, Document> queryIndex = new Hashtable<Integer ,  Document>();    // List of all queries (Key - query number, Values - Query Doc) 
    static int current_query_number;    // For parsing topics.txt file.
    static String[] known_tags = new String[]{"<title>", "<desc>", "<narr>", "</top>", "<top>"};
    /*
        SECTION 3: Parsing the query (topics.txt)
        @desc: Parse the Query and loads the query_Index data structure.
    */
     static void build_query_index(String filename, int option) throws Exception{
        validText = false;
        Files.readAllLines(Paths.get(filename))
             .stream()
             .filter(line -> !line.trim().isEmpty()) 
             .map(line -> parse_query(line, option)) 
             .map(line -> line.replaceAll("<title>|<desc>|<narr>|</top>|<top>|Description|Narrative|Documents discussing the following issues are relevant", ""))
             .map(line -> line.replaceAll("\\w*\\d\\w*", "").toLowerCase().split("\\s*[^a-z]\\s*")) // Replace digit words with empty and split on punctuations.
             .flatMap(Arrays::stream) // Convert Stream of Streams into streams.
             
             .filter(word -> (!word.isEmpty()) && !(stopWords.get(word) != null) && validText) // Eliminate empty words and stop words.
             .map(word -> new Porter().stripAffixes(word)) // Calling Porter Stemmer Algorithm
             .forEach(word -> {
                Word w = wordIndex.get(word);
                if(w != null){ // If the query term is already present in the collection. (Else, we can just ignore)
                    Document query = queryIndex.get(current_query_number);
                    Weight weight=  query.word.get(w);
                    if(weight == null)
                        weight = new Weight();
                    else
                        weight.incrementTf();
                    query.word.put(w, weight);  // Put word and weight into the document (Query).
                }
             }); // Builds the Index Table
    }
    /*
        @Desc: Sets valid flag depending on the option set. // Option 1 - Only title. 2 - Title + Desc. 3 - Title + Desc + Nar. 4 - Title + Nar.
    */
    static String parse_query(String line, int option){   
        if(line.startsWith("<num>")) {
            current_query_number = Integer.parseInt(line.replaceAll("[^\\d+]", "").trim());
            currentDocument = new Document(current_query_number, String.valueOf(current_query_number));
            queryIndex.put(current_query_number, currentDocument); 
        }
        if(option == 1)
            set_validity(line, known_tags[0], known_tags[1], "", ""); 
        else if(option == 2)
            set_validity(line, known_tags[0] , known_tags[2], "", ""); 
        else if(option == 3)
            set_validity(line, known_tags[0], known_tags[3], "", "");
        else
            set_validity(line, known_tags[0], known_tags[1], known_tags[2], known_tags[3] );
        return line;
    }
    static void set_validity(String line, String start_tag,String end_tag, String additional_tag, String additional_end_tag){
        if(!additional_tag.equals("")){ // option 4 is selected.
            if(line.startsWith(end_tag) || line.startsWith(additional_end_tag))
                validText = false;
            else if(line.startsWith(start_tag) || line.startsWith(additional_tag)){
                validText = true;
            }
        }
        else{   // Option 1 to 3.
            if(line.startsWith(end_tag))
                validText = false;
            else if(line.startsWith(start_tag)){
                validText = true;
            }
        }
    }
    final static HashMap <Document, ArrayList<Document>> manual_entries = new HashMap<Document, ArrayList<Document> >();   // Key - Query Value - List of relavant files. (From main.qrels file)
    /*
        SECTION 4: Parsing the Manual judgement file. (main.qrels file)
    */
    static void parse_manual_entry(String filename){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
                if(line.contains("FT911")){
                String[] tokens = line.split("\\s+");
                    if(tokens[3].equals("1")){  // If it is relevant
                        Document query = queryIndex.get(Integer.parseInt(tokens[0]));  // Query in first token.
                        if(query != null){
                            Document doc = documentIndex.get(tokens[2]);    // Relavant doc in third token.
                            if(manual_entries.get(query) == null){
                                ArrayList<Document> vals = new ArrayList<Document>();
                                vals.add(doc);
                                manual_entries.put(query, vals);
                            }else
                                manual_entries.get(query).add(doc);
                        }
                    }
                }
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    static double sum; // Formula tf-idf / square root of (sum of squares of all tf-idf's in a document)  
    /*
        SECTION 5: Calculating normalized weights for all terms in a document.
    */
    static synchronized void normalized_weight(Document document){
        sum = 0;                                         // Initialize, sum to 0.
        // First, calculate Wtf-idf for each term in Document.
        document.word                                    
                .entrySet()
                .stream()
                .forEach(entry -> Wtf_idf(entry.getKey(), entry.getValue()));
       final double base =  Math.sqrt(sum);        
       // Calculate Normalized score.
       document.word                                   
                .entrySet()
                .stream()
                .forEach(entry -> set_normalized_score(entry.getValue(), base));
    }    
    static void Wtf_idf(Word word, Weight weight){   
        int tf = weight.getTf();  // number of times it occured in a document.
        int N = documentIndex.size();   // Number of documents in the collection.
        int df_t = word.docFrequency.size();    //  Number of documents in which word has occured.
        double tf_idf_score =  tf * Math.log10(N / df_t) ;          // Formula - (tf * log N / df) 
        weight.setW_tf_idf(tf_idf_score);
        sum +=  Math.pow(tf_idf_score, 2); 
    }
    static void set_normalized_score(Weight weight, double base){
        final double tf_idf =  weight.getW_tf_idf();
        weight.setNormalized_score(tf_idf / base);
    }

    final static Map<Document, Double> results = new HashMap<Document, Double>();    // Document and it's similarity score.
    static int number_of_retreived_relavant_docs = 0; 
    static int number_of_retreived_docs = 0;
    /*
        SECTION 6: Calculating cosine similarity from query to all documents in documentIndex.
    */
    static void calculate_cosine_similarity(Document query, int k){
        StringBuffer sb = new StringBuffer();
        List<Map.Entry<Document,Double>> entries;
        // Calculate similarity score with all documents in the collection.
        documentIndex.entrySet()    
                .stream()
                .forEach(entry -> cosine_similarity(entry.getValue(),query));     
        if(k == 0){
            entries = results.entrySet().stream()
                .sorted(Map.Entry.<Document, Double >comparingByValue().reversed()) 
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toList());
        } 
        else{// Get Top K results.
            entries = results.entrySet().stream()
                .sorted(Map.Entry.<Document, Double >comparingByValue().reversed()) 
                .limit(k)
                .collect(Collectors.toList());
        }
        int iteration = 1;
        for(Map.Entry<Document, Double> entry: entries){
            sb.append(query.name +" \t"  + entry.getKey().name + "\t\t" + iteration + "\t"+ entry.getValue() * 100 +"%\n");
            iteration++;
            if(manual_entries.get(query).contains(entry.getKey()))
                number_of_retreived_relavant_docs++;
            number_of_retreived_docs++;
        }
        sb.append("\n**********************************************\n");
        write_to_file(sb.toString(), 2); // Append to file.
    }
    /*
        @Desc: Calculates similarity between two documents.
    */
    static void cosine_similarity(Document document, Document query){
        double csm = 0;
        for(Map.Entry<Word, Weight> q_entry : query.word.entrySet()){   // Key - Word Value - Weight
            Word query_term = q_entry.getKey();
            Weight qtw = q_entry.getValue();
            Weight doc_weight = document.word.get(query_term);
            if(doc_weight != null)
            csm += (qtw.getNormalized_score() * doc_weight.getNormalized_score());     
        }
        results.put(document, csm);
    }
    /*
        SECTION 7: Calculating the metrics (Precision + Recall)
    */
     static void calculate_metrics(int k){
        int number_of_relevant_docs = 0;
        for(Map.Entry<Document, ArrayList<Document>> entry: manual_entries.entrySet()){
            number_of_relevant_docs += entry.getValue().size();
        }
        double precision = (number_of_retreived_relavant_docs / (double) number_of_retreived_docs) * 100; 
        double recall = (number_of_retreived_relavant_docs / (double)  number_of_relevant_docs) * 100;
        System.out.println("\n\tEVALUATING PERFORMANCE\n***************************************\nNUMBER OF DOCS REQUESTED, K = " + k );
        System.out.println("NUMBER OF HIT DOCS: " + number_of_retreived_docs);
        System.out.println("NUMBER OF HIT DOCS WHICH ARE RELEVANT: " + number_of_retreived_relavant_docs);
        System.out.println("TOTAL NUMBER OF DOCS WHICH ARE RELEVANT: " + number_of_relevant_docs);
        System.out.println("PRECISION: " + precision + " %\nRECALL: " + recall+ " %" + "\n***************************************");
    }
    /*
        @desc: option = 1 (No append) option = 2 (append)
    */
    static void write_to_file(String text, int option){
        FileWriter fw;
        try{
            if(option == 1)
            fw = new FileWriter("output/vsm_output.txt");
            else
            fw = new FileWriter("output/vsm_output.txt", true);
            fw.write(text);
            fw.close();
        }catch(Exception e){System.out.println(e);};
    }
    public static void main(String[] args) throws Exception {
        if(args.length < 6){
            System.out.println("Invalid number of arguments");
            return;
        }
        int option = Integer.parseInt(args[4]);
        int k = Integer.parseInt(args[5]);
        long start = Instant.now().toEpochMilli();
        // Parsing files and building the data structures.  
        buildStopWords(args[0]);   
        Files.walk(Paths.get(args[1])).forEach(path -> addWords(path)); // For Each File in the directory, Add words to our data structure words.     
        build_query_index(args[2], option);  // Parse only the title.
        parse_manual_entry(args[3]);    // Build manual entry file.
        write_to_file("QUERY \t DOCUMENT \t RANK \t\t RELEVANCY \t\n", 1);
        // Step 1: Normalize all the words in each document in the collection.
        documentIndex.entrySet()
                .stream()
                .forEach(entry -> normalized_weight(entry.getValue()));  

        // Step 2: Normalize all the words in query collection.
        queryIndex.entrySet()
                .stream()
                .forEach(entry -> normalized_weight(entry.getValue())); // Normalizing the weights in the query term.
        System.out.println(queryIndex);
        // Step 3: Calculate the scores for each query to all Docs.
        queryIndex.entrySet()
                .stream()
                .forEach(entry -> calculate_cosine_similarity(entry.getValue(), k));  // Calculating cosine similarity for each query.
        long end = Instant.now().toEpochMilli();
        System.out.println("\tDOCUMENT STATISTICS\n***************************************\nWORDS PARSED: " + wordIndex.size() + "\nDOCUMENTS PARSED: " + documentIndex.size());
        System.out.println("TIME TAKEN: " + (end - start) / 1000.0 + " SEC\nOUTPUT WRITTEN TO output/vsm_output.txt\n***************************************");
        calculate_metrics(k); 
    }
}