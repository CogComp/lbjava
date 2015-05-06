package ilp;

import LBJ2.classify.ScoreSet;

public class ContainsStation extends DumbLearner
{
  public ContainsStation() { super("ilp.ContainsStation"); }

  public String getInputType() { return "ilp.Neighborhood"; }
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

