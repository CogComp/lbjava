---
title: Spam Classification
excerpt: This tutorial will walk you through the steps to create a simple classifier with LBJava.
---



# Spam Classification 
This is a quick-start tutorial on Learning Based Java (LBJava). The goal of this tutorial is to walk through the basic workings of LBJava so that the reader can  
use LBJava for most simple classification tasks. This is not a comprehensive API. The classifier that we will build here is a spam classifier. All the files and data
are available for download. For more in-depth information on LBJava, visit the [LBJava homepage](http://cogcomp.cs.illinois.edu/page/software_view/LBJ).

# 1. Define the classifier
            
We define a classifier called SpamClassifier using the LBJava syntax. I've explained the code in the comments. Don't skip over them!
            
            
SpamClassifier.lbj
            
          
          
# 2. Read the Data
            
            
## Define the Data Wrapper

We mentioned the Document object in SpamClassifier.lbj. If this doesn't sound familiar, go back and read the comments again - this is 
important. The Document class is a container or a wrapper for the 
data that you want to classify. The guiding principle is that this class must expose the information from which features will be extracted. 

For example, in NLP, we usually extract features from text, so the text must be made available (in this case, the method is getWords()). In computer vision, they generally extract features from pixels, so the pixels must be made available.

Since this object is used in training as well as testing, it must also store the label which describes the data. For example, if we were creating a
sunrise detector, the data would be an image, and each label would be either "SUNRISE" or "NOT SUNRISE". In this case, we have a getLabel() method.

Document.java
            

## Define the Data Reader

Now, we have the container for our data, we just need a way to 
read the raw data into the container. For this, we have a creatively named DocumentReader.

The big idea with this class is that it serves up the "next" Document object (by some definition of "next").
In this case, the reader loads all the files from a directory into a list, and just iterates through them, serving
the next Document in the list on each call to next(). When next() has no more Documents to offer, it 
returns null.
              
Notice that this class must implement the Parser interface, defined in LBJava. 
          
          
# 3. Train and Test
            
Having defined the above files, the rest is easy. Recall: LBJava now understands what
kind of classifier we want (and all the parameters), what the data looks like, and how to read it.  

Run the following in a command line in the root directory of this project:
            
~~~bash
 $ ./scripts/compileLBJ lbj/SpamClassifier.lbj 
~~~
            
This will train the model we specified in the lbj file and then test it, printing the scores. Modify parameters as you
wish until these numbers are to your liking. (By the way, LBJava has ways to sweep parameters automatically. You define the sweep in the .lbj file.)
         
# 4. Use as a Java Object
            
            
The beauty of LBJava is that all the specification, training, and testing we did above is now encapsulated in a Java class
called SpamClassifier, contained in the lbjsrc/ directory. Below, we have a small application which demonstrates a simple
way to use the classifier.



Run the following commands to compile and run the SpamClassifierApplication.
            
~~~bash
$ ./scripts/compileSpamApp
$ ./scripts/runSpamApp
~~~
            

          

