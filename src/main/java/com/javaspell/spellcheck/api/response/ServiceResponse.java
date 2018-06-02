package com.javaspell.spellcheck.api.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ServiceResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8970900860028418659L;
	private String origPhrase;
	private long id;
	private String correctedPhrase;
	private List<String> correctedWords;
	private double similarity;
	private HashMap<String,Double> similarityMap;
	private long responseTime;
	private List<Proximity> proximity = new ArrayList<Proximity>();
	private int editDistanceMax;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	public long getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getEditDistanceMax() {
		return editDistanceMax;
	}
	public void setEditDistanceMax(int editDistanceMax) {
		this.editDistanceMax = editDistanceMax;
	}
	public String getOrigPhrase() {
		return origPhrase;
	}
	public void setOrigPhrase(String origPhrase) {
		this.origPhrase = origPhrase;
	}
	public String getCorrectedPhrase() {
		return correctedPhrase;
	}
	public void setCorrectedPhrase(String correctedPhrase) {
		this.correctedPhrase = correctedPhrase;
	}
	public HashMap<String,Double> getSimilarityMap() {
		return similarityMap;
	}
	public void setSimilarityMap(HashMap<String,Double> similarityMap) {
		this.similarityMap = similarityMap;
	}
	public List<String> getCorrectedWords() {
		return correctedWords;
	}
	public void setCorrectedWords(List<String> correctedWords) {
		this.correctedWords = correctedWords;
	}
	public List<Proximity> getProximity() {
		return proximity;
	}
	public void setProximity(List<Proximity> proximity) {
		this.proximity = proximity;
	}

}
