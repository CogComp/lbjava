package edu.illinois.cs.cogcomp.lbjava.examples.entityRelation;

import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import edu.illinois.cs.cogcomp.lbjava.examples.entityRelation.datastruct.ConllRawSentence;
import edu.illinois.cs.cogcomp.lbjava.examples.entityRelation.datastruct.ConllRawToken;
import edu.illinois.cs.cogcomp.lbjava.examples.entityRelation.datastruct.ConllRelation;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by haowu on 2/9/15.
 * Modified by kordjams and khashab2
 */
public class Conll04_RelationReaderNew implements Parser {

    public Vector<ConllRawToken> instances;
    public Vector<ConllRawSentence> sentences;
    public Vector<ConllRelation> relations;
    public String type;

    public String[] entityLabels, relLabels;
    private int currentInstanceId;
    private int currentTokenId;
    private int currentPairId;
    private int currentSentenceId;

    /**
     * This constructor reads the CONLL data.
     * @param fileLocation
     * @param readerType: Characterizes whether the Next() functions runs on the "relations" or on the "entities".
     *                  Possible values are "token" (for entity instances)
     *                  and "pair" (for relation instances)
     */
    // TODO: add independent setter for "readerType", rather than reading the data multiple times in the LBJ file
    public Conll04_RelationReaderNew(String fileLocation, String readerType) {
        boolean verbose = false;
        instances = new Vector<ConllRawToken>();
        relations = new Vector<ConllRelation>();
        sentences = new Vector<ConllRawSentence>();
        entityLabels = new String[0];
        relLabels = new String[0];
        type = readerType;

        List<String> lines = null;
        try {
            lines = LineIO.read(fileLocation);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String line;
        String[] tokens;

        ConllRawToken token = new ConllRawToken();
        ConllRelation relation;
        int currSentId = 0;
        boolean sentEnd = false;
        ConllRawSentence sent = new ConllRawSentence(currSentId);

        ArrayList<String> entityAll = new ArrayList<String>();
        ArrayList<String> relationAll = new ArrayList<String>();

        int sentindex = 0;
        while (sentindex < lines.size()) {
            line = lines.get(sentindex);
            sentindex++;

            if( verbose )
                System.out.println(sentindex + " " + line);
            if (line.isEmpty()) {
                sentEnd = true;
                continue;
            }
            tokens = line.split("\t|\n");
            int tokenLength = tokens.length;
            if (tokenLength == 3) {
                relation = new ConllRelation();
                relation.sentId = currSentId;
                relation.wordId1 = Integer.parseInt(tokens[0]);
                relation.wordId2 = Integer.parseInt(tokens[1]);
                relation.relType = tokens[2];
                relations.add(relation);
                if( verbose )
                    System.out.println("WORD1:"+relation.s.sentTokens.elementAt(relation.wordId1).phrase);
                sent.addRelations(relation);
				sentences.elementAt(sentences.size()-1).addRelations(relation);
                if (!relationAll.contains(tokens[2])) {
                    relationAll.add(tokens[2]);
                }
            } else {
                if( verbose )
                    System.out.println("tokens[1]="+tokens[1]+"done");
                if (sentEnd) {
                    {
                        sentences.add(sent);
                        if( verbose ) {
                            if (currSentId < 700)
                                System.out.println("sid:" + currSentId);
                            else System.out.println("sid:" + (currSentId + 51));
                            for (int ind = 0; ind < sent.sentTokens.size(); ind++)
                                System.out.print(sent.sentTokens.get(ind).phrase + " ");
                            System.out.println();
                        }
                        currSentId++;
                    }
                    sent = new ConllRawSentence(currSentId);
                }

                token = new ConllRawToken();

                token.entType = tokens[1];
                token.sentId = currSentId;
                token.wordId = Integer.parseInt(tokens[2]);
                token.setPOS(tokens[4]);
                token.setPhrase(tokens[5]);

                sent.addTokens(token);
                if (!tokens[1].trim().equals("O")) {
                    instances.add(token);
                    sent.setCurrentTokenAsEntity();
                    if (!entityAll.contains(tokens[1])) {
                        entityAll.add(tokens[1]);
                    }
                }

                sentEnd = false;
            }
        }

        entityLabels = entityAll.toArray(entityLabels);
        relLabels = relationAll.toArray(relLabels);

        for (int counter = 0; counter < relations.size(); counter++) {
            int sindex = relations.elementAt(counter).sentId;
            relations.elementAt(counter).s.sentTokens.addAll(0, sentences.elementAt(sindex).sentTokens);
            relations.elementAt(counter).e1 = sentences.elementAt(sindex).sentTokens.elementAt(relations.elementAt(counter).wordId1);
            relations.elementAt(counter).e2 = sentences.elementAt(sindex).sentTokens.elementAt(relations.elementAt(counter).wordId2);
        }
    }

    public void printData() {
        System.out.println("printing total " + sentences.size() + " sentences");
        for (int i = 0; i < sentences.size(); i++) {
            sentences.elementAt(i).printEntities();
            sentences.elementAt(i).printRelations();
        }
        System.out.println("printing total " + instances.size() + " instances");
        for (int i = 0; i < instances.size(); i++) {
            instances.elementAt(i).printInstance();
        }
        System.out.println("printing total " + relations.size() + " relations");
        for (int i = 0; i < relations.size(); i++) {
            relations.elementAt(i).printRelation();
            System.out.println("WORD1:" + relations.elementAt(i).s.sentTokens.elementAt(relations.elementAt(i).wordId1).phrase);
            System.out.println("WORD2:" + relations.elementAt(i).s.sentTokens.elementAt(relations.elementAt(i).wordId2).phrase);
        }
    }

    public void close() {
        // nothing
    }

    public Object next() {

        if (type.equals("Token")) {
            if (currentTokenId < instances.size()) {
                ConllRawToken file = instances.get(currentTokenId++);
                return file;//Document(file, label);
            } else
                return null;
        }
        if (type.equals("Pair")) {
            if (currentPairId < relations.size()) {
                ConllRelation file = relations.get(currentPairId++);
                file.e1.setRelation(file);
                file.e2.setRelation(file);
                return file;
            } else
                return null;
        }
        return null;
    }

    public void reset() {
        if (type.equals("Pair"))
            currentPairId = 0;
        if (type.equals("Token"))
            currentTokenId = 0;
    }

    public void setId(int i) {
        if (type.equals("Pair"))
            currentPairId = i;
        if (type.equals("Token"))
            currentTokenId = i;
    }

    public Object[] get(String x, ConllRelation r) {
        Object[] a = null;
        if (x.equalsIgnoreCase("ConllRawToken")) {
            a = getTokens(r);
        }
        return a;
    }

    public ConllRawToken[] getTokens(ConllRelation r) {
        ConllRawToken[] a = new ConllRawToken[2];
        a[0] = r.e1;
        a[1] = r.e2;
        return a;
    }

    public static ConllRawToken PersonCandidate(ConllRelation t) {
        return t.e1;
    }

    public static ConllRawToken OrgCandidate(ConllRelation t) {
        return t.e2;
    }

    /**
     * Testing the reader
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Start reading tokens ... ");
        Conll04_RelationReaderNew cr = new Conll04_RelationReaderNew("data/conll04.corp", "Token");
//        Conll04_RelationReaderNew cr = new Conll04_RelationReaderNew("data/conll04_test.corp", "Token");
//        cr.printData();

        ConllRawToken tok = (ConllRawToken) cr.next();
        while( tok != null ) {
            System.out.println(tok);
            tok = (ConllRawToken) cr.next();
        }
    }
}