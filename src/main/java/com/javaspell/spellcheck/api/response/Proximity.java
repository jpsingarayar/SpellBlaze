package com.javaspell.spellcheck.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Proximity {
	
	private int count = 0;
	private double distance = 0; 
	private double wordFrequency;
	
	private double editProximity;
	private double phoneticProximity; 
	private double fragmentProximity; 
	private double prefixProximity; 
	private double proximity; // => combined similarity
	private String term;
	
	@Override
	public String toString() {
		return "Proximity [count=" + count + ", distance=" + distance + ", wordFrequency=" + wordFrequency
				+ ", editProximity=" + editProximity + ", phoneticProximity=" + phoneticProximity
				+ ", fragmentProximity=" + fragmentProximity + ", prefixProximity=" + prefixProximity + ", proximity="
				+ proximity + "]";
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public double getWordFrequency() {
		return wordFrequency;
	}
	public void setWordFrequency(double wordFrequency) {
		this.wordFrequency = wordFrequency;
	}
	public double getEditProximity() {
		return editProximity;
	}
	public void setEditProximity(double editProximity) {
		this.editProximity = editProximity;
	}
	public double getPhoneticProximity() {
		return phoneticProximity;
	}
	public void setPhoneticProximity(double phoneticProximity) {
		this.phoneticProximity = phoneticProximity;
	}
	public double getFragmentProximity() {
		return fragmentProximity;
	}
	public void setFragmentProximity(double fragmentProximity) {
		this.fragmentProximity = fragmentProximity;
	}
	public double getPrefixProximity() {
		return prefixProximity;
	}
	public void setPrefixProximity(double prefixProximity) {
		this.prefixProximity = prefixProximity;
	}
	public double getProximity() {
		return proximity;
	}
	public void setProximity(double proximity) {
		this.proximity = proximity;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}

}
