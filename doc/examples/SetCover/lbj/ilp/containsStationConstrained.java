// Modifying this comment will cause the next execution of LBJava to overwrite this file.
// discrete{false, true} containsStationConstrained(Neighborhood n) <- SetCover(ContainsStation)

package ilp;

import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.lbjava.infer.*;
import edu.illinois.cs.cogcomp.lbjava.io.IOUtilities;
import edu.illinois.cs.cogcomp.lbjava.learn.*;
import edu.illinois.cs.cogcomp.lbjava.parse.*;


public class containsStationConstrained extends Classifier
{
  private static final ContainsStation __ContainsStation = new ContainsStation();

  public containsStationConstrained()
  {
    containingPackage = "ilp";
    name = "containsStationConstrained";
  }

  public String getInputType() { return "ilp.Neighborhood"; }
  public String getOutputType() { return "discrete"; }

  private static String[] __allowableValues = DiscreteFeature.BooleanValues;
  public static String[] getAllowableValues() { return __allowableValues; }
  public String[] allowableValues() { return __allowableValues; }


  public FeatureVector classify(Object __example)
  {
    return new FeatureVector(featureValue(__example));
  }

  public Feature featureValue(Object __example)
  {
    String result = discreteValue(__example);
    return new DiscretePrimitiveStringFeature(containingPackage, name, "", result, valueIndexOf(result), (short) allowableValues().length);
  }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof Neighborhood))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'containsStationConstrained(Neighborhood)' defined on line 17 of SetCover.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    City head = SetCover.findHead((ilp.Neighborhood) __example);
    SetCover inference = (SetCover) InferenceManager.get("ilp.SetCover", head);

    if (inference == null)
    {
      inference = new SetCover(head);
      InferenceManager.put(inference);
    }

    String result = null;

    try { result = inference.valueOf(__ContainsStation, __example); }
    catch (Exception e)
    {
      System.err.println("LBJ ERROR: Fatal error while evaluating classifier containsStationConstrained: " + e);
      e.printStackTrace();
      System.exit(1);
    }

    return result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof ilp.Neighborhood[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'containsStationConstrained(ilp.Neighborhood)' defined on line 17 of SetCover.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "containsStationConstrained".hashCode(); }
  public boolean equals(Object o) { return o instanceof containsStationConstrained; }
}

