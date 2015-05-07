package edu.illinois.cs.cogcomp.tutorial.datastruct;

public class ConllRelation {
	public int wordId1, wordId2;
	public int sentId;
	public ConllRawSentence s=new ConllRawSentence(sentId);
	public String relType;
	public ConllRawToken e1;
	public ConllRawToken e2;

	public void printRelation(){
		System.out.println(" word1: "+wordId1+" word2: "+wordId2+" reltype: "+relType);
	}

	@Override
	public String toString() {
		return "ConllRelation{" +
				"wordId1=" + wordId1 +
				", wordId2=" + wordId2 +
				", sentId=" + sentId +
				", s=" + s +
				", relType='" + relType + '\'' +
				", e1=" + e1 +
				", e2=" + e2 +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConllRelation that = (ConllRelation) o;

		if (sentId != that.sentId) return false;
		if (wordId1 != that.wordId1) return false;
		if (wordId2 != that.wordId2) return false;
		if (e1 != null ? !e1.equals(that.e1) : that.e1 != null) return false;
		if (e2 != null ? !e2.equals(that.e2) : that.e2 != null) return false;
		if (relType != null ? !relType.equals(that.relType) : that.relType != null) return false;
		if (s != null ? !s.equals(that.s) : that.s != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = wordId1;
		result = 31 * result + wordId2;
		result = 31 * result + sentId;
		result = 31 * result + (s != null ? s.hashCode() : 0);
		result = 31 * result + (relType != null ? relType.hashCode() : 0);
		result = 31 * result + (e1 != null ? e1.hashCode() : 0);
		result = 31 * result + (e2 != null ? e2.hashCode() : 0);
		return result;
	}
}