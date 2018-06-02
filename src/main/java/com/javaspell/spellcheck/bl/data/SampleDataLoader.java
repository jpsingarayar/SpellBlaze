package com.javaspell.spellcheck.bl.data;

import com.javaspell.spellcheck.bl.dictionary.DictionaryInitializer;
import com.javaspell.spellcheck.bl.resourceloader.FileLoader;
import org.apache.log4j.Logger;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Named
public class SampleDataLoader extends DataLoader {
	private static final Logger LOGGER = Logger.getLogger(SampleDataLoader.class.getName());

	@Inject
	private DictionaryInitializer dictionaryLoader;

	@Inject
	@Named("fileLoader")
	private FileLoader fileLoader;
	
	@Value("${service.sampleCorpusFile}")
	private String sampleDataFile;



	private Cache<String,Object> dictionary;

	

	@PostConstruct
	@Override
	public void loadResource(){
		List<String> testDataTerms = null;
		dictionary=dictionaryLoader.getCache();
		
		testDataTerms = fileLoader.loadResource(sampleDataFile);
		long startTime = System.currentTimeMillis();
		long wordCount = 0;
		long termCount=0;


		for(String line:testDataTerms) 
		{
			for (String key : parseWords(line))
			{
				HashMap<String,Object> dictData = dictionaryUtil.indexWord(key);
				if(dictData!=null && !dictData.isEmpty())dictionary.putAll(dictData);
				termCount+=dictData.size();
				wordCount++;					
			}
		}
		dictionaryLoader.setCache(dictionary);
		dictionaryLoader.setCacheSize(dictionaryLoader.getCacheSize()+termCount);


		long endTime = System.currentTimeMillis();
		LOGGER.info("Dictionary: " + wordCount + " words, and "+termCount+" terms in " + (endTime-startTime)+"ms ");
	}	

	private static List<String> parseWords(String text)
	{
		List<String> allMatches = new ArrayList<String>();
		Matcher m = Pattern.compile("[\\w-[\\d_]]+").matcher(text.toLowerCase());
		while (m.find()) {
			allMatches.add(m.group());
		}
		return allMatches;
	}



}
