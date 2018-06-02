package com.javaspell.spellcheck.bl.spellservice.settings;

import com.javaspell.spellcheck.util.AccuracyLevel;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.inject.Named;

@Named("spellCheckSettings")
public class SpellCheckSettings {
	@Value("${service.editDistanceMax}")
	private int editDistanceMax;

	private AccuracyLevel accuracyLevel = AccuracyLevel.topHit;
	
	
	@Value("${service.topK}")
	private int topK;

	// Damerau function variables
	@Value("${service.deletionWeight}")
	private double deletionWeight;

	@Value("${service.insertionWeight}")
	private double insertionWeight;

	@Value("${service.replaceWeight}")
	private double replaceWeight;

	@Value("${service.transpositionWeight}")
	private double transpositionWeight;

	//Proximity function variables
	// Proximity function variables
	@Value("${service.editWeight}")
	private  double editWeight;
	
	@Value("${service.phoneWeight}")
	private  double phoneWeight;
	
	@Value("${service.prefixWeight}")
	private  double prefixWeight;
	
	@Value("${service.fragmentWeight}")
	private  double fragmentWeight;
	
	@Value("${service.wordFrequencyWeight}")
	private  double wordFrequencyWeight;
	
	@PostConstruct
	public void init()
	{
		this.divisor = this.editWeight+this.phoneWeight+this.prefixWeight+this.fragmentWeight+this.wordFrequencyWeight;
	}
	
	private  double divisor;
	
	public double getDivisor() {
		return divisor;
	}

	public double getEditWeight() {
		return editWeight;
	}

	public double getPhoneWeight() {
		return phoneWeight;
	}

	public double getPrefixWeight() {
		return prefixWeight;
	}

	public double getFragmentWeight() {
		return fragmentWeight;
	}

	public double getWordFrequencyWeight() {
		return wordFrequencyWeight;
	}

	public int getEditDistanceMax() {
		return editDistanceMax;
	}

	public AccuracyLevel getAccuracyLevel() {
		return accuracyLevel;
	}

	public int getTopK() {
		return topK;
	}

	public double getDeletionWeight() {
		return deletionWeight;
	}

	public double getInsertionWeight() {
		return insertionWeight;
	}

	public double getReplaceWeight() {
		return replaceWeight;
	}

	public double getTranspositionWeight() {
		return transpositionWeight;
	}



}
