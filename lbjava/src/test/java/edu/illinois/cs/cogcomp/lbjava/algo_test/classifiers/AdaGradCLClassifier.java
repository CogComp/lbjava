package edu.illinois.cs.cogcomp.lbjava.algo_test.classifiers;

import edu.illinois.cs.cogcomp.lbjava.algo_test.AlgoData;
import edu.illinois.cs.cogcomp.lbjava.algo_test.AlgoDiscreteLabel;
import edu.illinois.cs.cogcomp.lbjava.algo_test.AlgoRealFeatures;
import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.lbjava.io.IOUtilities;
import edu.illinois.cs.cogcomp.lbjava.learn.*;
import edu.illinois.cs.cogcomp.lbjava.parse.*;

/**
 * This is the classifier, extended AdaGradCL, which is AdaGrad for classification
 */
public class AdaGradCLClassifier extends AdaGradCL
{
    private static void loadInstance()
    {
        if (instance == null) instance = new AdaGradCLClassifier(true);
    }

    public static Parser getParser() { return null; }
    public static Parser getTestParser() { return null; }

    public static boolean isTraining;
    public static AdaGradCLClassifier instance;

    public static AdaGradCLClassifier getInstance()
    {
        loadInstance();
        return instance;
    }

    private AdaGradCLClassifier(boolean b)
    {
        super(new Parameters());
        containingPackage = "";
        name = "AdaGradCLClassifier";
        setEncoding(null);

        /* set the labeler to Algo Discrete Label */
        setLabeler(new AlgoDiscreteLabel());

        /* set the extractor to Algo Real Features */
        setExtractor(new AlgoRealFeatures());
        isClone = false;
    }

    public static TestingMetric getTestingMetric() { return null; }

    private boolean isClone;

    public void unclone() { isClone = false; }

    public AdaGradCLClassifier()
    {
        super("AdaGradCLClassifier");
        isClone = true;
    }

    public AdaGradCLClassifier(String modelPath, String lexiconPath) { this(new Parameters(), modelPath, lexiconPath); }

    public AdaGradCLClassifier(Parameters p, String modelPath, String lexiconPath) {
        super(p);
        try {
            lcFilePath = new java.net.URL("file:" + modelPath);
            lexFilePath = new java.net.URL("file:" + lexiconPath);
        }
        catch (Exception e) {
            System.err.println("ERROR: Can't create model or lexicon URL: " + e);
            e.printStackTrace();
            System.exit(1);
        }

        if (new java.io.File(modelPath).exists()) {
            readModel(lcFilePath);
            readLexiconOnDemand(lexFilePath);
        }
        else if (IOUtilities.existsInClasspath(AdaGradCLClassifier.class, modelPath)) {
            readModel(IOUtilities.loadFromClasspath(AdaGradCLClassifier.class, modelPath));
            readLexiconOnDemand(IOUtilities.loadFromClasspath(AdaGradCLClassifier.class, lexiconPath));
        }
        else {
            containingPackage = "";
            name = "AdaGradCLClassifier";
            setLabeler(new AlgoDiscreteLabel());
            setExtractor(new AlgoRealFeatures());
        }

        isClone = false;
    }

    public String getInputType() { return "AlgoData"; }
    public String getOutputType() { return "discrete"; }

    public void learn(Object example)
    {
        if (isClone)
        {
            if (!(example instanceof AlgoData || example instanceof Object[]))
            {
                String type = example == null ? "null" : example.getClass().getName();
                System.err.println("Classifier 'AdaGradCLClassifier(AlgoData)' defined on line 18 of CL.lbj received '" + type + "' as input.");
                new Exception().printStackTrace();
                System.exit(1);
            }

            loadInstance();
            instance.learn(example);
            return;
        }

        if (example instanceof Object[])
        {
            Object[] a = (Object[]) example;
            if (a[0] instanceof int[])
            {
                super.learn((int[]) a[0], (double[]) a[1], (int[]) a[2], (double[]) a[3]);
                return;
            }
        }

        super.learn(example);
    }

    public void learn(Object[] examples)
    {
        if (isClone)
        {
            if (!(examples instanceof AlgoData[] || examples instanceof Object[][]))
            {
                String type = examples == null ? "null" : examples.getClass().getName();
                System.err.println("Classifier 'AdaGradCLClassifier(AlgoData)' defined on line 18 of CL.lbj received '" + type + "' as input.");
                new Exception().printStackTrace();
                System.exit(1);
            }

            loadInstance();
            instance.learn(examples);
            return;
        }

        super.learn(examples);
    }

    public Feature featureValue(Object __example)
    {
        if (isClone)
        {
            if (!(__example instanceof AlgoData || __example instanceof Object[]))
            {
                String type = __example == null ? "null" : __example.getClass().getName();
                System.err.println("Classifier 'AdaGradCLClassifier(AlgoData)' defined on line 18 of CL.lbj received '" + type + "' as input.");
                new Exception().printStackTrace();
                System.exit(1);
            }

            loadInstance();
            return instance.featureValue(__example);
        }

        if (__example instanceof Object[])
        {
            Object[] a = (Object[]) __example;
            if (a[0] instanceof int[])
                return super.featureValue((int[]) a[0], (double[]) a[1]);
        }

        Feature __result;
        __result = super.featureValue(__example);
        return __result;
    }

    public FeatureVector classify(Object __example)
    {
        return new FeatureVector(featureValue(__example));
    }

    public String discreteValue(Object __example)
    {
        return featureValue(__example).getStringValue();
    }

    public FeatureVector[] classify(Object[] examples)
    {
        if (isClone)
        {
            if (!(examples instanceof AlgoData[] || examples instanceof Object[][]))
            {
                String type = examples == null ? "null" : examples.getClass().getName();
                System.err.println("Classifier 'AdaGradCLClassifier(AlgoData)' defined on line 18 of CL.lbj received '" + type + "' as input.");
                new Exception().printStackTrace();
                System.exit(1);
            }

            loadInstance();
            return instance.classify(examples);
        }

        FeatureVector[] result = super.classify(examples);
        return result;
    }

    public static void main(String[] args)
    {
        String testParserName = null;
        String testFile = null;
        Parser testParser = getTestParser();

        try
        {
            if (!args[0].equals("null"))
                testParserName = args[0];
            if (args.length > 1) testFile = args[1];

            if (testParserName == null && testParser == null)
            {
                System.err.println("The \"testFrom\" clause was not used in the learning classifier expression that");
                System.err.println("generated this classifier, so a parser and input file must be specified.\n");
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            System.err.println("usage: AdaGradCLClassifier \\");
            System.err.println("           <parser> <input file> [<null label> [<null label> ...]]\n");
            System.err.println("     * <parser> must be the fully qualified class name of a Parser, or \"null\"");
            System.err.println("       to use the default as specified by the \"testFrom\" clause.");
            System.err.println("     * <input file> is the relative or absolute path of a file, or \"null\" to");
            System.err.println("       use the parser arguments specified by the \"testFrom\" clause.  <input");
            System.err.println("       file> can also be non-\"null\" when <parser> is \"null\" (when the parser");
            System.err.println("       specified by the \"testFrom\" clause has a single string argument");
            System.err.println("       constructor) to use an alternate file.");
            System.err.println("     * A <null label> is a label (or prediction) that should not count towards");
            System.err.println("       overall precision and recall assessments.");
            System.exit(1);
        }

        if (testParserName == null && testFile != null && !testFile.equals("null"))
            testParserName = testParser.getClass().getName();
        if (testParserName != null)
            testParser = edu.illinois.cs.cogcomp.lbjava.util.ClassUtils.getParser(testParserName, new Class[]{ String.class }, new String[]{ testFile });
        AdaGradCLClassifier classifier = new AdaGradCLClassifier();
        TestDiscrete tester = new TestDiscrete();
        for (int i = 2; i < args.length; ++i)
            tester.addNull(args[i]);
        TestDiscrete.testDiscrete(tester, classifier, classifier.getLabeler(), testParser, true, 0);
    }

    public int hashCode() { return "AdaGradCLClassifier".hashCode(); }
    public boolean equals(Object o) { return o instanceof AdaGradCLClassifier; }

    public void write(java.io.PrintStream a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.write(a0);
            return;
        }

        super.write(a0);
    }

    public void learn(int[] a0, double[] a1, int[] a2, double[] a3)
    {
        if (isClone)
        {
            loadInstance();
            instance.learn(a0, a1, a2, a3);
            return;
        }

        super.learn(a0, a1, a2, a3);
    }

    public edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector classify(int[] a0, double[] a1)
    {
        if (isClone)
        {
            loadInstance();
            return instance.classify(a0, a1);
        }

        return super.classify(a0, a1);
    }

    public edu.illinois.cs.cogcomp.lbjava.classify.Feature featureValue(int[] a0, double[] a1)
    {
        if (isClone)
        {
            loadInstance();
            return instance.featureValue(a0, a1);
        }

        return super.featureValue(a0, a1);
    }

    public java.lang.String discreteValue(int[] a0, double[] a1)
    {
        if (isClone)
        {
            loadInstance();
            return instance.discreteValue(a0, a1);
        }

        return super.discreteValue(a0, a1);
    }

    public double score(int[] a0, double[] a1)
    {
        if (isClone)
        {
            loadInstance();
            return instance.score(a0, a1);
        }

        return super.score(a0, a1);
    }

    public double score(java.lang.Object a0)
    {
        if (isClone)
        {
            loadInstance();
            return instance.score(a0);
        }

        return super.score(a0);
    }

    public edu.illinois.cs.cogcomp.lbjava.classify.ScoreSet scores(int[] a0, double[] a1)
    {
        if (isClone)
        {
            loadInstance();
            return instance.scores(a0, a1);
        }

        return super.scores(a0, a1);
    }

    public void setParameters(AdaGradCL.Parameters a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.setParameters(a0);
            return;
        }

        super.setParameters(a0);
    }

    public double[] getWeightVector()
    {
        if (isClone)
        {
            loadInstance();
            return instance.getWeightVector();
        }

        return super.getWeightVector();
    }

    public java.lang.String getLossFunction()
    {
        if (isClone)
        {
            loadInstance();
            return instance.getLossFunction();
        }

        return super.getLossFunction();
    }

    public double getConstantLearningRate()
    {
        if (isClone)
        {
            loadInstance();
            return instance.getConstantLearningRate();
        }

        return super.getConstantLearningRate();
    }

    public void setLabeler(edu.illinois.cs.cogcomp.lbjava.classify.Classifier a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.setLabeler(a0);
            return;
        }

        super.setLabeler(a0);
    }

    public void write(java.lang.String a0, java.lang.String a1)
    {
        if (isClone)
        {
            loadInstance();
            instance.write(a0, a1);
            return;
        }

        super.write(a0, a1);
    }

    public void write(edu.illinois.cs.cogcomp.lbjava.util.ExceptionlessOutputStream a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.write(a0);
            return;
        }

        super.write(a0);
    }

    public void read(java.lang.String a0, java.lang.String a1)
    {
        if (isClone)
        {
            loadInstance();
            instance.read(a0, a1);
            return;
        }

        super.read(a0, a1);
    }

    public void read(edu.illinois.cs.cogcomp.lbjava.util.ExceptionlessInputStream a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.read(a0);
            return;
        }

        super.read(a0);
    }

    public void save()
    {
        if (isClone)
        {
            loadInstance();
            instance.save();
            return;
        }

        super.save();
    }

    public edu.illinois.cs.cogcomp.lbjava.learn.Learner.Parameters getParameters()
    {
        if (isClone)
        {
            loadInstance();
            return instance.getParameters();
        }

        return super.getParameters();
    }

    public void initialize(int a0, int a1)
    {
        if (isClone)
        {
            loadInstance();
            instance.initialize(a0, a1);
            return;
        }

        super.initialize(a0, a1);
    }

    public void learn(edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.learn(a0);
            return;
        }

        super.learn(a0);
    }

    public void learn(edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector[] a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.learn(a0);
            return;
        }

        super.learn(a0);
    }

    public edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector[] classify(edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector[] a0)
    {
        if (isClone)
        {
            loadInstance();
            return instance.classify(a0);
        }

        return super.classify(a0);
    }

    public edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector classify(edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector a0)
    {
        if (isClone)
        {
            loadInstance();
            return instance.classify(a0);
        }

        return super.classify(a0);
    }

    public edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector[] classify(java.lang.Object[][] a0)
    {
        if (isClone)
        {
            loadInstance();
            return instance.classify(a0);
        }

        return super.classify(a0);
    }

    public edu.illinois.cs.cogcomp.lbjava.classify.Feature featureValue(edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector a0)
    {
        if (isClone)
        {
            loadInstance();
            return instance.featureValue(a0);
        }

        return super.featureValue(a0);
    }

    public double realValue(edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector a0)
    {
        if (isClone)
        {
            loadInstance();
            return instance.realValue(a0);
        }

        return super.realValue(a0);
    }

    public double realValue(int[] a0, double[] a1)
    {
        if (isClone)
        {
            loadInstance();
            return instance.realValue(a0, a1);
        }

        return super.realValue(a0, a1);
    }

    public java.lang.String discreteValue(edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector a0)
    {
        if (isClone)
        {
            loadInstance();
            return instance.discreteValue(a0);
        }

        return super.discreteValue(a0);
    }

    public edu.illinois.cs.cogcomp.lbjava.classify.ScoreSet scores(java.lang.Object a0)
    {
        if (isClone)
        {
            loadInstance();
            return instance.scores(a0);
        }

        return super.scores(a0);
    }

    public edu.illinois.cs.cogcomp.lbjava.classify.ScoreSet scores(edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector a0)
    {
        if (isClone)
        {
            loadInstance();
            return instance.scores(a0);
        }

        return super.scores(a0);
    }

    public void setParameters(edu.illinois.cs.cogcomp.lbjava.learn.Learner.Parameters a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.setParameters(a0);
            return;
        }

        super.setParameters(a0);
    }

    public java.lang.Object[] getExampleArray(java.lang.Object a0, boolean a1)
    {
        if (isClone)
        {
            loadInstance();
            return instance.getExampleArray(a0, a1);
        }

        return super.getExampleArray(a0, a1);
    }

    public java.lang.Object[] getExampleArray(java.lang.Object a0)
    {
        if (isClone)
        {
            loadInstance();
            return instance.getExampleArray(a0);
        }

        return super.getExampleArray(a0);
    }

    public void forget()
    {
        if (isClone)
        {
            loadInstance();
            instance.forget();
            return;
        }

        super.forget();
    }

    public void readLexiconOnDemand(java.net.URL a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.readLexiconOnDemand(a0);
            return;
        }

        super.readLexiconOnDemand(a0);
    }

    public void readLexiconOnDemand(java.lang.String a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.readLexiconOnDemand(a0);
            return;
        }

        super.readLexiconOnDemand(a0);
    }

    public edu.illinois.cs.cogcomp.lbjava.classify.Classifier getLabeler()
    {
        if (isClone)
        {
            loadInstance();
            return instance.getLabeler();
        }

        return super.getLabeler();
    }

    public void setExtractor(edu.illinois.cs.cogcomp.lbjava.classify.Classifier a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.setExtractor(a0);
            return;
        }

        super.setExtractor(a0);
    }

    public edu.illinois.cs.cogcomp.lbjava.classify.Classifier getExtractor()
    {
        if (isClone)
        {
            loadInstance();
            return instance.getExtractor();
        }

        return super.getExtractor();
    }

    public void setLexicon(edu.illinois.cs.cogcomp.lbjava.learn.Lexicon a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.setLexicon(a0);
            return;
        }

        super.setLexicon(a0);
    }

    public edu.illinois.cs.cogcomp.lbjava.learn.Lexicon getLexicon()
    {
        if (isClone)
        {
            loadInstance();
            return instance.getLexicon();
        }

        return super.getLexicon();
    }

    public void setLabelLexicon(edu.illinois.cs.cogcomp.lbjava.learn.Lexicon a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.setLabelLexicon(a0);
            return;
        }

        super.setLabelLexicon(a0);
    }

    public edu.illinois.cs.cogcomp.lbjava.learn.Lexicon getLabelLexicon()
    {
        if (isClone)
        {
            loadInstance();
            return instance.getLabelLexicon();
        }

        return super.getLabelLexicon();
    }

    public void setEncoding(java.lang.String a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.setEncoding(a0);
            return;
        }

        super.setEncoding(a0);
    }

    public void setModelLocation(java.net.URL a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.setModelLocation(a0);
            return;
        }

        super.setModelLocation(a0);
    }

    public void setModelLocation(java.lang.String a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.setModelLocation(a0);
            return;
        }

        super.setModelLocation(a0);
    }

    public java.net.URL getModelLocation()
    {
        if (isClone)
        {
            loadInstance();
            return instance.getModelLocation();
        }

        return super.getModelLocation();
    }

    public void setLexiconLocation(java.net.URL a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.setLexiconLocation(a0);
            return;
        }

        super.setLexiconLocation(a0);
    }

    public void setLexiconLocation(java.lang.String a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.setLexiconLocation(a0);
            return;
        }

        super.setLexiconLocation(a0);
    }

    public java.net.URL getLexiconLocation()
    {
        if (isClone)
        {
            loadInstance();
            return instance.getLexiconLocation();
        }

        return super.getLexiconLocation();
    }

    public void countFeatures(edu.illinois.cs.cogcomp.lbjava.learn.Lexicon.CountPolicy a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.countFeatures(a0);
            return;
        }

        super.countFeatures(a0);
    }

    public edu.illinois.cs.cogcomp.lbjava.learn.Lexicon getLexiconDiscardCounts()
    {
        if (isClone)
        {
            loadInstance();
            return instance.getLexiconDiscardCounts();
        }

        return super.getLexiconDiscardCounts();
    }

    public edu.illinois.cs.cogcomp.lbjava.learn.Learner emptyClone()
    {
        if (isClone)
        {
            loadInstance();
            return instance.emptyClone();
        }

        return super.emptyClone();
    }

    public void doneLearning()
    {
        if (isClone)
        {
            loadInstance();
            instance.doneLearning();
            return;
        }

        super.doneLearning();
    }

    public void doneWithRound()
    {
        if (isClone)
        {
            loadInstance();
            instance.doneWithRound();
            return;
        }

        super.doneWithRound();
    }

    public int getPrunedLexiconSize()
    {
        if (isClone)
        {
            loadInstance();
            return instance.getPrunedLexiconSize();
        }

        return super.getPrunedLexiconSize();
    }

    public void saveModel()
    {
        if (isClone)
        {
            loadInstance();
            instance.saveModel();
            return;
        }

        super.saveModel();
    }

    public void saveLexicon()
    {
        if (isClone)
        {
            loadInstance();
            instance.saveLexicon();
            return;
        }

        super.saveLexicon();
    }

    public void writeModel(java.lang.String a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.writeModel(a0);
            return;
        }

        super.writeModel(a0);
    }

    public void writeLexicon(java.lang.String a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.writeLexicon(a0);
            return;
        }

        super.writeLexicon(a0);
    }

    public void readModel(java.lang.String a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.readModel(a0);
            return;
        }

        super.readModel(a0);
    }

    public void readModel(java.net.URL a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.readModel(a0);
            return;
        }

        super.readModel(a0);
    }

    public void readLexicon(java.net.URL a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.readLexicon(a0);
            return;
        }

        super.readLexicon(a0);
    }

    public void readLexicon(java.lang.String a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.readLexicon(a0);
            return;
        }

        super.readLexicon(a0);
    }

    public void readLabelLexicon(edu.illinois.cs.cogcomp.lbjava.util.ExceptionlessInputStream a0)
    {
        if (isClone)
        {
            loadInstance();
            instance.readLabelLexicon(a0);
            return;
        }

        super.readLabelLexicon(a0);
    }

    public edu.illinois.cs.cogcomp.lbjava.learn.Lexicon demandLexicon()
    {
        if (isClone)
        {
            loadInstance();
            return instance.demandLexicon();
        }

        return super.demandLexicon();
    }

    public static class Parameters extends AdaGradCL.Parameters
    {
        public Parameters()
        {

        }
    }
}