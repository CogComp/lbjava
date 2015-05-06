// Modifying this comment will cause the next execution of LBJava to overwrite this file.
// F1B88000000000000000B4ECFCB2E292A4CCCCB21580E4D217ECF2B4D22592E2D4ACA4D4E2929C7D07ECC29A45846D458A65078CBC77DCD2829A4FB4DCC4FC84ACF2AC8CFCF4926D846D4B658A50008D11AADB34000000

package ilp;

import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.lbjava.infer.*;
import edu.illinois.cs.cogcomp.lbjava.io.IOUtilities;
import edu.illinois.cs.cogcomp.lbjava.learn.*;
import edu.illinois.cs.cogcomp.lbjava.parse.*;


public class SetCover$subjectto extends ParameterizedConstraint
{
  private static final noEmptyNeighborhoods __noEmptyNeighborhoods = new noEmptyNeighborhoods();

  public SetCover$subjectto() { super("ilp.SetCover$subjectto"); }

  public String getInputType() { return "ilp.City"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof City))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Constraint 'SetCover$subjectto(City)' defined on line 12 of SetCover.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    City c = (City) __example;

    {
      boolean LBJ2$constraint$result$0;
      LBJ2$constraint$result$0 = __noEmptyNeighborhoods.discreteValue(c).equals("true");
      if (!LBJ2$constraint$result$0) return "false";
    }

    return "true";
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof City[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'SetCover$subjectto(City)' defined on line 12 of SetCover.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "SetCover$subjectto".hashCode(); }
  public boolean equals(Object o) { return o instanceof SetCover$subjectto; }

  public FirstOrderConstraint makeConstraint(Object __example)
  {
    if (!(__example instanceof City))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Constraint 'SetCover$subjectto(City)' defined on line 12 of SetCover.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    City c = (City) __example;
    FirstOrderConstraint __result = new FirstOrderConstant(true);

    {
      FirstOrderConstraint LBJ2$constraint$result$0 = null;
      LBJ2$constraint$result$0 = __noEmptyNeighborhoods.makeConstraint(c);
      __result = new FirstOrderConjunction(__result, LBJ2$constraint$result$0);
    }

    return __result;
  }
}

