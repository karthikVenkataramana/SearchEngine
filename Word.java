/*
    Author: Karthik Venkataramana Pemmaraju
    Date: 03/06/2017
    Description: Represents run time mapping of a Word.
*/
package SearchEngine;
import java.util.*;
public class Word implements Comparable < Word > {
    final String name;
    final Hashtable < Document, Boolean> docFrequency; // Key - File ID, Value - count (List of Documents in which this word is present).
    int tf_total; // Total  Term- Frequency. (Number of times , a word has occured in all documents).
   
    Word(String name) {
        this.name = name;
        docFrequency = new Hashtable < Document, Boolean> ();
        tf_total = 0;
    }
    protected final String getName() {
        return this.name;
    } 
    protected final int get_TermFrequency(){
        return this.tf_total;
    } 
    protected final void increment_TermFrequency() {
        this.tf_total++;
    } 
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Word) {
            Word otherWord = (Word) other;
            result = (otherWord.name.equals(this.name));
        }
        return result;
    }
    @Override
    public int hashCode() {
        return 42 + this.name.hashCode();
    }
    @Override
    public int compareTo(Word other) {
        return this.name.compareTo(other.name);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(); 
        // this.docFrequency.entrySet().stream().forEach(entry -> sb.append("DOC NAME: " + entry.getKey() + "\tCOUNT: " + entry.getValue() + "\n"));
        sb.append("\nWORD: " +this.name.toUpperCase());
        return sb.toString();
    }
}