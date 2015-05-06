// Modifying this comment will cause the next execution of LBJava to overwrite this file.
// F1B8800000000000000053C8B4A038040150FA2F6933B9C50C5AB84401112720F3F2E43247343D64240FEE1D546B545598EB8E4D988723A6B73D1583CC8A52E3890F547495AC86E5CC668E90C91B9B24F6B03AF1EC63E2735EA0700BB441827743D6FDCF7D9E207FDCD649789DA9276C1F30831E8A6587000000

package ilp;

import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.lbjava.infer.*;
import edu.illinois.cs.cogcomp.lbjava.io.IOUtilities;
import edu.illinois.cs.cogcomp.lbjava.learn.*;
import edu.illinois.cs.cogcomp.lbjava.parse.*;
import java.util.*;


public class SetCover extends ILPInference
{
  public static City findHead(Neighborhood n)
  {
    return n.getParentCity();
  }


  public SetCover() { }
  public SetCover(City head)
  {
    super(head, new GurobiHook());
    constraint = new SetCover$subjectto().makeConstraint(head);
  }

  public String getHeadType() { return "ilp.City"; }
  public String[] getHeadFinderTypes()
  {
    return new String[]{ "ilp.Neighborhood" };
  }

  public Normalizer getNormalizer(Learner c)
  {
    return new IdentityNormalizer();
  }
}

