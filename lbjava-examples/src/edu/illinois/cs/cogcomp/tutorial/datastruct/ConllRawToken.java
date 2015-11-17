package edu.illinois.cs.cogcomp.tutorial.datastruct;

import java.util.ArrayList;
import java.util.Arrays;

public class ConllRawToken {
	public int sentId,wordId;
	public String entType, POS, phrase;
	public String[] splitWords, splitPOS;
	public ArrayList<ConllRelation> relatedTokens=null;
	public ConllRelation t;
	public ConllRawSentence s=new ConllRawSentence(sentId);
	public void setPhrase(String phrase){
		this.phrase=phrase;
		splitWords=phrase.split("/");

	}

	public void setPOS(String POS){
		this.POS=POS;
		splitPOS=POS.split("/");
	}

	public int getLength(){
		return splitWords.length;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConllRawToken that = (ConllRawToken) o;

		if (sentId != that.sentId) return false;
		if (wordId != that.wordId) return false;
		if (POS != null ? !POS.equals(that.POS) : that.POS != null) return false;
		if (entType != null ? !entType.equals(that.entType) : that.entType != null) return false;
		if (phrase != null ? !phrase.equals(that.phrase) : that.phrase != null) return false;
		if (relatedTokens != null ? !relatedTokens.equals(that.relatedTokens) : that.relatedTokens != null)
			return false;
		if (s != null ? !s.equals(that.s) : that.s != null) return false;
		if (!Arrays.equals(splitPOS, that.splitPOS)) return false;
		if (!Arrays.equals(splitWords, that.splitWords)) return false;
		if (t != null ? !t.equals(that.t) : that.t != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = sentId;
		result = 31 * result + wordId;
		result = 31 * result + (entType != null ? entType.hashCode() : 0);
		result = 31 * result + (POS != null ? POS.hashCode() : 0);
		result = 31 * result + (phrase != null ? phrase.hashCode() : 0);
		result = 31 * result + (splitWords != null ? Arrays.hashCode(splitWords) : 0);
		result = 31 * result + (splitPOS != null ? Arrays.hashCode(splitPOS) : 0);
		result = 31 * result + (relatedTokens != null ? relatedTokens.hashCode() : 0);
		result = 31 * result + (t != null ? t.hashCode() : 0);
		result = 31 * result + (s != null ? s.hashCode() : 0);
		return result;
	}

	public String getPhrase(boolean isLowerCase){
		if(isLowerCase){
			return (new String(phrase)).toLowerCase();
		}

		return phrase;
	}

	public String[] getWords(boolean isLowerCase){
		if(isLowerCase){
			String[] returnString=new String[splitWords.length];
			for(int i=0;i<splitWords.length;i++){
				returnString[i]=splitWords[i].toLowerCase();
			}
			return returnString;
		}
		return splitWords;
	}
	public void setRelation(ConllRelation r){
		t=r;
	}
	public ConllRelation getRelation()
	{

		return(t);
	}
	public ConllRelation getparteners(ConllRawToken e)
	{
		for (int i=0;i<s.relations.size();i++)
		{
			if (s.relations.elementAt(i).e1.wordId==e.wordId || s.relations.elementAt(i).e2.wordId==e.wordId){

			}
		}
		// example.ConllRelation t= new example.ConllRelation();
		// t.e2=this;
		// t.e1=e;
		// t.sentId=e.sentId;
		// t.wordId2=this.wordId;
		// t.wordId1=e.wordId;

		return(t);
	}


	public void printInstance(){
		System.out.println("sent: "+sentId+" wordId: "+wordId+" phrase: "+phrase+" POS: "+POS+" entity type: "+entType);
	}

	@Override
	public String toString() {
		return "ConllRawToken{" +
				"sentId=" + sentId +
				", wordId=" + wordId +
				", entType='" + entType + '\'' +
				", phrase='" + phrase + '\'' +
				'}';
	}
}