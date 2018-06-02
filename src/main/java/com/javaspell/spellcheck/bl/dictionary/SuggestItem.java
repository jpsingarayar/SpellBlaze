package com.javaspell.spellcheck.bl.dictionary;

public class SuggestItem {
	@Override
	public String toString() {
		return "SuggestItem [term=" + term + ", count=" + count + ", distance=" + distance + ", wordFrequency="
				+ wordFrequency + ", editProximity=" + editProximity + ", phoneticProximity=" + phoneticProximity
				+ ", fragmentProximity=" + fragmentProximity + ", prefixProximity=" + prefixProximity + ", proximity="
				+ proximity + "]";
	}

	public String term = "";
	public int count = 0;
	public double distance = 0; 
	public double wordFrequency;
	
	public double editProximity;
	public double phoneticProximity; 
	public double fragmentProximity; 
	public double prefixProximity; 
	public double proximity; // => final similarity

	@Override
	public boolean equals(Object obj) {
		return term.equals(((SuggestItem) obj).term);
	}

	@Override
	public int hashCode() {
		return term.hashCode();
	}
}
