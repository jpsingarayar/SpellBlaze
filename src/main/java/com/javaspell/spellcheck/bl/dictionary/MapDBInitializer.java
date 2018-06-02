package com.javaspell.spellcheck.bl.dictionary;

import org.apache.log4j.Logger;
import org.mapdb.*;

import javax.annotation.PostConstruct;
import javax.inject.Named;


@Named("mapDBLoader")
public class MapDBInitializer {
	private static final Logger LOGGER = Logger.getLogger(DictionaryInitializer.class.getName());
	private HTreeMap<String,Object> dictionary;
	private IndexTreeList<String> wordList;
	private  long cacheSize;
	private int maxlength = 0;
	
	@PostConstruct
	public void initializeDictionary() {
		DB db = DBMaker.memoryDB().make();
		 this.setDictionary((HTreeMap<String, Object>) db.hashMap("dictionary").createOrOpen());
		LOGGER.info("Dictionary loaded");
		this.setWordList(db.indexTreeList("wordList",Serializer.STRING).createOrOpen());
		LOGGER.info("WordList initiated");
		
	}

	public long getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(long cacheSize) {
		this.cacheSize = cacheSize;
	}

	/**
	 * @return the maxlength
	 */
	public int getMaxlength() {
		return maxlength;
	}

	/**
	 * @param maxlength the maxlength to set
	 */
	public void setMaxlength(int maxlength) {
		this.maxlength = maxlength;
	}

	/**
	 * @return the wordList
	 */
	public IndexTreeList<String> getWordList() {
		return wordList;
	}

	/**
	 * @param wordList the wordList to set
	 */
	public void setWordList(IndexTreeList<String> wordList) {
		this.wordList = wordList;
	}

	/**
	 * @return the dictionary
	 */
	public HTreeMap<String,Object> getDictionary() {
		return dictionary;
	}

	/**
	 * @param dictionary the dictionary to set
	 */
	public void setDictionary(HTreeMap<String,Object> dictionary) {
		this.dictionary = dictionary;
	}

}
