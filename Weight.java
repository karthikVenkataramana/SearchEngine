 /*
    Author: Karthik Venkataramana Pemmaraju
    Date: 04 /15/2017
    Description: Represents details of a word in document.
*/
package SearchEngine;
import java.util.*;
public class Weight{
    int tf; // Term frequency in the document.
    double W_tf_idf; // tf - idf score.
    double normalized_score;    // Eucilidean  normal form.
    Weight(){
        tf = 1;
    }
    /**
     * @param tf the tf to set
     */
    public void setTf(int tf) {
        this.tf = tf;
    }

    /**
     * @return the tf
     */
    public int getTf() {
        return tf;
    }

    public void incrementTf(){
        this.tf++;
    }
    /**
     * @param normalized_score the normalized_score to set
     */
    protected void setNormalized_score(double normalized_score) {
        this.normalized_score = normalized_score;
    }
    /**
     * @return the normalized_score
     */
    public double getNormalized_score() {
        return normalized_score;
    }

    /**
     * @param w_tf_idf the w_tf_idf to set
     */
    public void setW_tf_idf(double w_tf_idf) {
        this.W_tf_idf = w_tf_idf;
    }

    /**
     * @return the w_tf_idf
     */
    public double getW_tf_idf() {
        return W_tf_idf;
    } 

    @Override
    public String toString(){
        return "TF: " + this.tf + "\tTF-IDF: " + this.W_tf_idf + "\tNormalized Score: " + this.normalized_score;
    }
}