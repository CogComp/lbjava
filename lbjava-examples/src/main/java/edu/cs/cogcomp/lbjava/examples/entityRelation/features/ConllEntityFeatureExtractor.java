/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.cs.cogcomp.lbjava.examples.entityRelation.features;

import edu.cs.cogcomp.lbjava.examples.entityRelation.datastruct.ConllRawSentence;
import edu.cs.cogcomp.lbjava.examples.entityRelation.datastruct.ConllRawToken;
import edu.cs.cogcomp.lbjava.examples.entityRelation.datastruct.ConllRelation;
import edu.cs.cogcomp.lbjava.examples.entityRelation.reader.GazeteerReader;

import java.util.HashMap;
import java.util.Vector;

public class ConllEntityFeatureExtractor {

    Vector<GazeteerReader> gazets;
    public GazeteerReader locGazet, perGazet;

    public ConllEntityFeatureExtractor() {
        // gazets=new Vector<GazeteerReader>();
    }

    public void addGazets(GazeteerReader g) {
        gazets.add(g);
    }

    public HashMap<String, Double> extractFeatures(ConllRawToken c_t) {
        HashMap<String, Double> features = new HashMap<String, Double>();
        features.put("WORD:" + c_t.phrase, 1.0);
        features.put("POS:" + c_t.POS, 1.0);
        return features;
    }

    public HashMap<String, Double> extractEntityFeatures(ConllRawSentence s, int index,
                                                         boolean isLowerCase) {
        HashMap<String, Double> features = new HashMap<String, Double>();
        ConllRawToken ct = s.sentTokens.elementAt(index);
        features.put("PHRASE:" + ct.getPhrase(isLowerCase), 1.0);
        features.put("LEN:" + ct.getLength(), 1.0);
        String[] allWords = ct.getWords(isLowerCase);
        GazeteerReader g;
        for (int i = 0; i < allWords.length; i++) {
            features.put("WORD:" + allWords[i], 1.0);
        }

        ConllRawToken[] window = s.returnWindow(index, 2, 2);
        int id;
        for (int i = 0; i < window.length; i++) {
            id = window[i].wordId - index;
            // features.put("WINDOW "+id+window[i].getPhrase(isLowerCase),1.0);
            features.put("POS_WINDOW " + id + window[i].POS, 1.0);
        }

        if (containsSubPhrase("ing", ct.getWords(isLowerCase))) {
            features.put("Ing:", 1.0);
        }
        if (containsSubPhrase("ment", ct.getWords(isLowerCase))) {
            features.put("Ment:", 1.0);
        }


        if (locGazet.isContainedIn(ct)) {
            features.put("LOC:", 1.0);
        } else if (perGazet.containsAny(ct)) { // PRECISION OF LOCATION GAZET IS HIGHER WE CAN
                                               // IMPROVE PRECISION OF PERSON USING THAT
            features.put("PER:", 1.0);

        }

        /*
         * for(int i=0;i<gazets.size();i++){ g=gazets.elementAt(i); if(g.testMembership(ct)){
         * features.put("MATCH"+g.listName,1.0); } }
         * 
         * for(int i=0;i<gazets.size();i++){ g=gazets.elementAt(i); if(g.containsAny(ct)){
         * features.put("CONTAINS:"+g.listName,1.0); } }
         */

        features.put("BIAS", 1.0);


        // System.out.println("extracted features= "+features.keySet());
        return features;
    }

    public boolean containsSubPhrase(String canPhrase, String[] phrases) {
        for (String s : phrases) {
            if (s.contains(canPhrase)) {
                return true;
            }
        }
        return false;
    }

    public HashMap<String, Double> extractRelationFeatures(ConllRawSentence s, int index1,
            int index2, boolean isLowerCase) {
        HashMap<String, Double> features = new HashMap<String, Double>();
        ConllRawToken ct1 = s.sentTokens.elementAt(index1);
        ConllRawToken ct2 = s.sentTokens.elementAt(index2);
        String lPrefix = "L", rPrefix = "R";


        // features.put("PHRASE:"+ct1.getPhrase(isLowerCase), 1.0);
        // features.put("PHRASE:"+ct2.getPhrase(isLowerCase), 1.0);

        // features.put(lPrefix+"ENTITYTYPE:"+ct1.entType,1.0);
        // features.put(rPrefix+"ENTITYTYPE:"+ct2.entType,1.0);

        features.put(lPrefix + "PHRASE:" + ct1.getPhrase(isLowerCase), 1.0);
        features.put(rPrefix + "PHRASE:" + ct2.getPhrase(isLowerCase), 1.0);
        features.put(lPrefix + "P_LEN:" + ct1.splitWords.length, 1.0);
        features.put(rPrefix + "P_LEN:" + ct2.splitWords.length, 1.0);

        int id;

        ConllRawToken[] window1 = s.returnWindow(index1, 2, 2);
        ConllRawToken[] window2 = s.returnWindow(index2, 2, 2);

        for (int i = 0; i < window1.length; i++) {
            id = window1[i].wordId - index1;
            // features.put(lPrefix+"WINDOW"+id+":"+window1[i].getPhrase(isLowerCase),1.0);
            features.put(lPrefix + "POS_WINDOW" + id + ":" + window1[i].POS, 1.0);
        }

        for (int i = 0; i < window2.length; i++) {
            id = window2[i].wordId - index2;
            // features.put(rPrefix+"WINDOW"+id+":"+window2[i].getPhrase(isLowerCase),1.0);
            features.put(rPrefix + "POS_WINDOW" + id + ":" + window2[i].POS, 1.0);
        }

        if (locGazet.isContainedIn(ct1)) {
            features.put(lPrefix + "LOC:", 1.0);
        } else if (perGazet.containsAny(ct1)) { // PRECISION OF LOCATION GAZET IS HIGHER WE CAN
                                                // IMPROVE PRECISION OF PERSON USING THAT
            features.put(lPrefix + "PER:", 1.0);

        }

        if (locGazet.isContainedIn(ct2)) {
            features.put(rPrefix + "LOC:", 1.0);
        } else if (perGazet.containsAny(ct2)) { // PRECISION OF LOCATION GAZET IS HIGHER WE CAN
                                                // IMPROVE PRECISION OF PERSON USING THAT
            features.put(rPrefix + "PER:", 1.0);

        }

        if (containsSubPhrase("ing", ct1.getWords(isLowerCase))) {
            features.put(lPrefix + "-ing", 1.0);
        }
        if (containsSubPhrase("ment", ct1.getWords(isLowerCase))) {
            features.put(lPrefix + "-ment", 1.0);
        }

        if (containsSubPhrase("ing", ct2.getWords(isLowerCase))) {
            features.put(rPrefix + "-ing", 1.0);
        }
        if (containsSubPhrase("ment", ct2.getWords(isLowerCase))) {
            features.put(rPrefix + "-ment", 1.0);
        }

        // features.put("REL_ENT_LEN:"+(Math.abs(index2-index1)-1), 1.0);
        features.put("REL_ENT_LEN:", (double) (Math.abs(index2 - index1) - 1));
        // features.put("REL_LEN:"+(Math.abs(index2-index1)+ct1.splitWords.length+ct2.splitWords.length-1),
        // 1.0);
        // features.put("REL_LEN:",(double)
        // (Math.abs(index2-index1)+ct1.splitWords.length+ct2.splitWords.length-1));

        features.put("BIAS", 1.0);
        // System.out.println("extracted features= "+features.keySet());
        /*
         * features.put("WORD1:"+ct1.phrase,1.0); features.put("WORD2:"+ct2.phrase,1.0);
         * features.put("POS1:"+ct1.POS,1.0); features.put("POS2:"+ct2.POS,1.0);
         */
        return features;
    }

    public HashMap<String, Double> extractFeatures(ConllRawToken ct1, ConllRawToken ct2,
            ConllRawSentence s) {
        HashMap<String, Double> features = new HashMap<String, Double>();
        features.put("WORD1:" + ct1.phrase, 1.0);
        features.put("WORD2:" + ct2.phrase, 1.0);
        features.put("POS1:" + ct1.POS, 1.0);
        features.put("POS2:" + ct2.POS, 1.0);
        return features;
    }


    public HashMap<String, Double> extractFeatures(ConllRelation c, ConllRawSentence s) {
        HashMap<String, Double> features = new HashMap<String, Double>();
        features.put("WORD1:" + s.sentTokens.elementAt(c.wordId1).phrase, 1.0);
        features.put("WORD2:" + s.sentTokens.elementAt(c.wordId2).phrase, 1.0);

        // features.put("POS1:"+s.sentTokens.elementAt(c.wordId1).POS,1.0);
        // features.put("POS2:"+s.sentTokens.elementAt(c.wordId1).POS,1.0);
        return features;
    }
}
