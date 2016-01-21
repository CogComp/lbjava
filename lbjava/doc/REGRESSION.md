#7 Tutorial: Regression

As mentioned in [Section 2 Basics and definitions](DEFINITION.md), there are two feature types in LBJava: discrete and real. In machine learning, classification refers to the problem of predicting the class of unlabeled data. The output type is `discrete`. On the other hand, regression refers to the problem that the desired output consisting of continuous type, or `real`. [Section 3 A working example: classifying newsgroup documents into topics](20NEWSGROUP.md) gives an example of how to use LBJava for discrete type and this tutorial is dedicated to real type.

##7.1 Setting Up

Let's name a class as `MyData` and use it for internal representation.

In terms of internal data structure, from the data set examples, there are two fields: feature vector and label, while the label being real or continuous type. Intuitively, feature vector and label are declared as the following:

```java
	private List<Double> features;
    private double label;
```

The class `MyData` is the representation for a single example from the data set. However, the data set consists many examples. Let's name a class as `MyDataReader` for the internal data structure for the data set.

For data structure, `lines` denotes all lines of examples in the data set. `currentLineNumber` keeps track which line that we are reading now.

```java
    private final List<String> lines;
    private int currentLineNumber;
```

The constructor of `MyDataReader` reads each line from the data set file and store them into internal data structure `lines`.

```java
    public MyDataReader(String filePath) {
        this.lines = new ArrayList<>();
        this.currentLineNumber = 0;

        Reader reader;
        try {
            reader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String eachLine;
            while ((eachLine = bufferedReader.readLine()) != null) {
                lines.add(eachLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

```

`MyDataReader` is inherited from `Parser`. Method `next()` is overrided in `MyDataReader` serving as a next iterator.

The function body is shown below:

```java
    public Object next() {
        if (currentLineNumber < lines.size()) {
            MyData ret = new MyData(lines.get(currentLineNumber));
            this.currentLineNumber ++;
            return ret;
        }
        return null;
    }

```



##7.2 Classifier Declarations

##7.3 Using `RegressionClassifier` in a Java Program

##7.4 Testing a Real Classifier
