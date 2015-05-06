// Modifying this comment will cause the next execution of LBJava to overwrite this file.
// F1B88000000000000000D6CC1BA02C0401541DF5975EE63A0923691C6D6C6D66D57D4602EC8CEC31214CF775C242A85F5EE9C6AECA944905BDE5EAC97754AF1E865703B3978E4833274CD176BA96C11BCC0588224E55F58FDB5C88EC49F27D7FC4413D0A11D6B06DB51C16D823983DF74B6EDC9E29BFF453F1B6387C315FDB2C7A3C000000

package ilp;

import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.lbjava.infer.*;
import edu.illinois.cs.cogcomp.lbjava.io.IOUtilities;
import edu.illinois.cs.cogcomp.lbjava.learn.*;
import edu.illinois.cs.cogcomp.lbjava.parse.*;


public class noEmptyNeighborhoods extends ParameterizedConstraint
{
  private static final ContainsStation __ContainsStation = new ContainsStation();

  public noEmptyNeighborhoods() { super("ilp.noEmptyNeighborhoods"); }

  public String getInputType() { return "ilp.City"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof City))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Constraint 'noEmptyNeighborhoods(City)' defined on line 3 of SetCover.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    City c = (City) __example;

    {
      boolean LBJ2$constraint$result$0;
      {
        LBJ2$constraint$result$0 = true;
        for (java.util.Iterator __I0 = (c.getNeighborhoods()).iterator(); __I0.hasNext() && LBJ2$constraint$result$0; )
        {
          Neighborhood n = (Neighborhood) __I0.next();
          {
            boolean LBJ2$constraint$result$1;
            LBJ2$constraint$result$1 = ("" + (__ContainsStation.discreteValue(n))).equals("" + (true));
            if (!LBJ2$constraint$result$1)
              {
                LBJ2$constraint$result$0 = false;
                for (java.util.Iterator __I1 = (n.getNeighbors()).iterator(); __I1.hasNext() && !LBJ2$constraint$result$0; )
                {
                  Neighborhood n2 = (Neighborhood) __I1.next();
                  LBJ2$constraint$result$0 = ("" + (__ContainsStation.discreteValue(n2))).equals("" + (true));
                }
              }
            else LBJ2$constraint$result$0 = true;
          }
        }
      }
      if (!LBJ2$constraint$result$0) return "false";
    }

    return "true";
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof City[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'noEmptyNeighborhoods(City)' defined on line 3 of SetCover.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "noEmptyNeighborhoods".hashCode(); }
  public boolean equals(Object o) { return o instanceof noEmptyNeighborhoods; }

  public FirstOrderConstraint makeConstraint(Object __example)
  {
    if (!(__example instanceof City))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Constraint 'noEmptyNeighborhoods(City)' defined on line 3 of SetCover.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    City c = (City) __example;
    FirstOrderConstraint __result = new FirstOrderConstant(true);

    {
      Object[] LBJ$constraint$context = new Object[1];
      LBJ$constraint$context[0] = c;
      FirstOrderConstraint LBJ2$constraint$result$0 = null;
      {
        FirstOrderConstraint LBJ2$constraint$result$1 = null;
        {
          FirstOrderConstraint LBJ2$constraint$result$2 = null;
          {
            EqualityArgumentReplacer LBJ$EAR =
              new EqualityArgumentReplacer(LBJ$constraint$context, true)
              {
                public Object getLeftObject()
                {
                  Neighborhood n = (Neighborhood) quantificationVariables.get(0);
                  return n;
                }
              };
            LBJ2$constraint$result$2 = new FirstOrderEqualityWithValue(true, new FirstOrderVariable(__ContainsStation, null), "" + (true), LBJ$EAR);
          }
          FirstOrderConstraint LBJ2$constraint$result$3 = null;
          {
            FirstOrderConstraint LBJ2$constraint$result$4 = null;
            {
              EqualityArgumentReplacer LBJ$EAR =
                new EqualityArgumentReplacer(LBJ$constraint$context, true)
                {
                  public Object getLeftObject()
                  {
                    Neighborhood n2 = (Neighborhood) quantificationVariables.get(1);
                    return n2;
                  }
                };
              LBJ2$constraint$result$4 = new FirstOrderEqualityWithValue(true, new FirstOrderVariable(__ContainsStation, null), "" + (true), LBJ$EAR);
            }
            QuantifierArgumentReplacer LBJ$QAR =
              new QuantifierArgumentReplacer(LBJ$constraint$context)
              {
                public java.util.Collection getCollection()
                {
                  Neighborhood n = (Neighborhood) quantificationVariables.get(0);
                  return n.getNeighbors();
                }
              };
            LBJ2$constraint$result$3 = new ExistentialQuantifier("n2", null, LBJ2$constraint$result$4, LBJ$QAR);
          }
          LBJ2$constraint$result$1 = new FirstOrderDisjunction(LBJ2$constraint$result$2, LBJ2$constraint$result$3);
        }
        LBJ2$constraint$result$0 = new UniversalQuantifier("n", c.getNeighborhoods(), LBJ2$constraint$result$1);
      }
      __result = new FirstOrderConjunction(__result, LBJ2$constraint$result$0);
    }

    return __result;
  }
}

