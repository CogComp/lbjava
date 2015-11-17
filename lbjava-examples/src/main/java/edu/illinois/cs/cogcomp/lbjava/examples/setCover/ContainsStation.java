package edu.illinois.cs.cogcomp.lbjava.examples.setCover;

import edu.illinois.cs.cogcomp.lbjava.classify.ScoreSet;

public class ContainsStation extends DumbLearner
{
  public ContainsStation() { super("edu.illinois.cs.cogcomp.lbjava.examples.setCover.ContainsStation"); }

  public String getInputType() { return "edu.illinois.cs.cogcomp.lbjava.examples.setCover.Neighborhood"; }
  public String[] allowableValues() {
    return new String[]{ "false", "true" };
  }

  public ScoreSet scores(Object example)
  {
    ScoreSet result = new ScoreSet();
    result.put("false", 0);
    result.put("true", -1);
    return result;
  }
}

