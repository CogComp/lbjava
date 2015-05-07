package edu.illinois.cs.cogcomp.tutorial.datastruct;

public class ConllRawInstance {
	public int sentId,wordId;
	public String entType, POS, phrase;
	
/*	public void setPhrase(String unProcPhrase){
		String[] parts=unProcPhrase.split("/");
		
	}*/

	public void printInstance(){
		System.out.println("sent: "+sentId+" wordId: "+wordId+" phrase: "+phrase+" POS: "+POS+" entity type: "+entType);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConllRawInstance that = (ConllRawInstance) o;

		if (sentId != that.sentId) return false;
		if (wordId != that.wordId) return false;
		if (POS != null ? !POS.equals(that.POS) : that.POS != null) return false;
		if (entType != null ? !entType.equals(that.entType) : that.entType != null) return false;
		if (phrase != null ? !phrase.equals(that.phrase) : that.phrase != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = sentId;
		result = 31 * result + wordId;
		result = 31 * result + (entType != null ? entType.hashCode() : 0);
		result = 31 * result + (POS != null ? POS.hashCode() : 0);
		result = 31 * result + (phrase != null ? phrase.hashCode() : 0);
		return result;
	}
}
