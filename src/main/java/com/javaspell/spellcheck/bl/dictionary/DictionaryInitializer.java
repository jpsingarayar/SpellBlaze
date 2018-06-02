package com.javaspell.spellcheck.bl.dictionary;

import org.apache.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named("dictionaryLoader")
public class DictionaryInitializer {
	private static final Logger LOGGER = Logger.getLogger(DictionaryInitializer.class.getName());
	private Cache<String,Object> dictionary;
	private List<String> wordList;
	private  long cacheSize;
	private int maxlength;

	@PostConstruct
	public void initializeCache() {
		CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()      

				.build(true);
		this.dictionary = cacheManager.createCache("myCache",
				CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Object.class,
						ResourcePoolsBuilder.heap(100000000)).build());
		this.setWordList(new ArrayList<>());
		LOGGER.info("Dictionary loaded");
	}

	public Cache<String,Object> getCache(){
				return dictionary;
	}

	public void setCache(Cache<String,Object> dictionary) {
		this.dictionary=dictionary;
	}

	public  long getCacheSize() {
		return cacheSize;
	}

	public  void setCacheSize(long cacheSize) {
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
	public List<String> getWordList() {
		return wordList;
	}

	/**
	 * @param wordList the wordList to set
	 */
	public void setWordList(List<String> wordList) {
		this.wordList = wordList;
	}


}
