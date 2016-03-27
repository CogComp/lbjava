package edu.illinois.cs.cogcomp.lbjava.algo_test;

import edu.illinois.cs.cogcomp.lbjava.parse.Parser;

/**
 * Parser, parse example from <code>AlgoDataSet</code>
 *
 * @author Yiming Jiang
 */
public class AlgoParser implements Parser{

    // an instance of AlgoDataSet
    private AlgoDataSet dataSet;

    /* indices are inclusive */
    public int startIndex = 0;
    public int endIndex = 0;

    private int currentIndex = 0;

    /**
     * Constructor
     * @param d dataset
     * @param isTraining is Training flag; true -> training set, false -> testing set
     */
    public AlgoParser(AlgoDataSet d, boolean isTraining) {
        dataSet = d;
        if (isTraining) {
            endIndex = d.getNumberOfTrainingExamples() - 1;
        }
        else {
            startIndex = d.getNumberOfTrainingExamples();
            endIndex = d.getTotalNumberOfExamples() - 1;
            currentIndex = startIndex;
        }
    }

    /**
     * Iterator, find next example in data set
     * @return next example
     */
    public Object next() {
        if (currentIndex < (endIndex + 1)) {
            AlgoData data = new AlgoData(dataSet.getFeatures()[currentIndex],
                    dataSet.getLabels()[currentIndex]);
            currentIndex ++;
            return data;
        }
        return null;
    }

    /**
     * Close the parser
     */
    public void close() {

    }

    /**
     * Forget and reset the current index
     */
    public void reset() {
        currentIndex = startIndex;
    }
}

