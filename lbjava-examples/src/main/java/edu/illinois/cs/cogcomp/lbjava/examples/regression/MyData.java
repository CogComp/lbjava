package edu.illinois.cs.cogcomp.lbjava.examples.regression;

import java.util.*;

public class MyData {

    private List<Double> features;
    private double label;

    public MyData(String line) {
        this.features = new ArrayList<>();

        for (String each : line.split(" ")) {
            features.add(Double.parseDouble(each));
        }

        label = features.get(features.size()-1);
        features.remove(features.size()-1);
    }

    public List<Double> getFeatures() {
        return this.features;
    }

    public double getLabel() {
        return label;
    }

    public void printData() {
        System.out.println(features.toString());
        System.out.println(label);
        System.out.println();
    }
}