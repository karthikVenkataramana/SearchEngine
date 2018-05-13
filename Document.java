/*
    Author: Karthik Venkataramana Pemmaraju
    Date: 03/06/2017
    Description: Represents memory Image of a document.
*/
package SearchEngine;
import java.util.Hashtable;
class Document implements Comparable < Document > {
    final int id;
    final String name;
    final Hashtable <Word, Weight> word; // Contains list of words, a document has. (KEY - Word, Value - has occured or not).
    Document(int id, String name) {
        this.id = id;
        this.name = name;
        word = new Hashtable < Word, Weight > ();
    }
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Document) {
            Document otherDoc = (Document) other;
            result = (otherDoc.name.equals(this.name));
        }
        return result;
    }
    @Override
    public int hashCode() {
        return (42 * (this.id + 42) + this.name.hashCode()); // Meaning of life is 42 :)
    }
    @Override
    public String toString() {
        return this.name  + " WORD : " + word + "\n";
    }
    @Override
    public int compareTo(Document other) {
        return (this.id - other.id);
    }
    public final void printForwardIndex() { // Pretty prints the Forward Index.
        StringBuilder sb = new StringBuilder();
        word.entrySet().stream().forEach(entry-> sb.append(entry.getKey() + "\n"  + entry.getValue() + "\n"));
        sb.append("\nTOTAL DISTINCT WORDS: " + word.size() + "\n");
        if (word.size() == 0)
            System.out.println("\nDOCUMENT HAS NO WORDS!\n");
        else
            System.out.println(sb);
    }
}