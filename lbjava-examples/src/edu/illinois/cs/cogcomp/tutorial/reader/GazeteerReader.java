package edu.illinois.cs.cogcomp.tutorial.reader;

//import edu.illinois.cs.cogcomp.example.er_task.datastruct.ConllRawToken;

import edu.illinois.cs.cogcomp.tutorial.datastruct.ConllRawToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class GazeteerReader {
	public String listName;
	public ArrayList<String> listGazet;
	public ArrayList<String[]> listWords;
	
	public GazeteerReader(String fileName, String listName, boolean isLowerCase){
		try {
			this.listName=listName;
			listGazet=new ArrayList<String>();
			listWords=new ArrayList<String[]>();
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if(isLowerCase){
					line=line.toLowerCase();
				}
				listGazet.add(line);
				listWords.add(line.split(" |\n|\t"));
			}	
			
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void addFile(String fileName, boolean isLowerCase){
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if(isLowerCase){
					line=line.toLowerCase();
				}
				listGazet.add(line);
				listWords.add(line.split(" |\n|\t"));
			}
			
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
		
	public boolean testMembership(String cand){
		return listGazet.contains(cand);
	}
	
	public boolean testMembership(String[] cand){
		String[] phrases;
		for(int i=0;i<listGazet.size();i++){
			phrases=listGazet.get(i).split(" |\n|\t");
			if(phrases.length!=cand.length) return false;
			
			for(int j=0;j<cand.length;j++){
				if(!phrases[j].equals(cand[j])){
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean testMembership(ConllRawToken ct){
		if(ct.getLength()>1){
			return testMembership(ct.getWords(true));
		}
		return testMembership(ct.getPhrase(true));
	}
	
	public boolean subArray(String[] big, String[] small, int index){
		for(int i=0;i<small.length;i++){
		
			if(!big[i+index].equals(small[i])){
				return false;
			}
		}
		return true;
	}
	
	public boolean containsAny(ConllRawToken ct){
		String[] temp1,temp2;
		temp2=ct.getWords(true);
		for(int i=0;i<listWords.size();i++){
			temp1=listWords.get(i);
			for(int j=0;j<=temp2.length-temp1.length;j++){
				if(subArray(temp2,temp1,j)){
					return true;
				}
			}
		}
		return false;			
	}
	
	public boolean isContainedIn(ConllRawToken ct){
		String[] temp1,temp2;
		temp2=ct.getWords(true);
		for(int i=0;i<listWords.size();i++){
			temp1=listWords.get(i);
			for(int j=0;j<=temp1.length-temp2.length;j++){
				if(subArray(temp1,temp2,j)){
					return true;
				}
			}
		}
		return false;			
	}
}
