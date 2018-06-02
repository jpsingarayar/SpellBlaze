package com.javaspell.spellcheck.bl.data;

import com.javaspell.spellcheck.bl.dictionary.DictionaryEntry;
import com.javaspell.spellcheck.bl.dictionary.DictionaryInitializer;
import com.javaspell.spellcheck.util.AccuracyLevel;
import org.ehcache.Cache;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/* This class indexes a input term into dictionary.
 * 
 * The Dictionary holds the actual words and also the deletes that has been
 * derived for words. A term can be the actual word and a  delete formed from
 * another  at the same time.
 * 
 * A DictionaryEntry is used for word, word/delete, and delete with multiple
 * suggestions. Int is used for deletes with a single suggestion (the
 * majority of entries).

 */
@Named
public class DictionaryUtil {

	@Inject
	DictionaryInitializer dictionaryLoader;

	private int editDistanceMax = 2;
	private  List<String> wordlist;
	public static int maxlength = 0;
	private AccuracyLevel accuracyLevel = AccuracyLevel.topHit;
	private static HashMap<String, Object> dictionary;
	private Cache<String,Object> parentDictionary;

	
	public  HashMap<String, Object> indexWord(String word) {
		dictionary = new HashMap<>();
		wordlist = dictionaryLoader.getWordList();
		parentDictionary = dictionaryLoader.getCache();
		DictionaryEntry value = appendToDictionary(word);
		
		if (value.count == 1) {
			indexFragments(word);
		}
		
		// Store the maximum length of the indexed word.
		if(!(dictionaryLoader.getMaxlength()>maxlength));
		{
			dictionaryLoader.setMaxlength(maxlength);
		}
		return dictionary;
	}


	private DictionaryEntry appendToDictionary(String word) {
		DictionaryEntry value;
		Object dictionaryEntry = parentDictionary.get(word);

		//Its a  known term
		if (dictionaryEntry != null) {
			value = asDictionaryEntry(dictionaryEntry);

			//Replace int value with a  DictionaryEntry Object
			if (dictionaryEntry instanceof Integer) {
				dictionary.put(word, value);
			}
			
			//Avoid  int overflow
			if (value.count < Integer.MAX_VALUE) value.count++;
		}
		// new term to  be indexed
		else if (wordlist.size() < Integer.MAX_VALUE) {
			value = new DictionaryEntry();
			value.count++;
			dictionary.put(word, value);
			if (word.length() > maxlength) maxlength = word.length();
		} else {
			throw new IllegalStateException("can not index word since wordlist reached limit of Integer.MAX_VALUE");
		}
		return value;
	}

	private DictionaryEntry asDictionaryEntry(Object entry) {
		if (entry instanceof DictionaryEntry) {
			return (DictionaryEntry) entry;
		} else if (entry instanceof Integer) {
			DictionaryEntry dictItem = new DictionaryEntry();
			dictItem.suggestions.add((int) entry);
			return dictItem;
		} else {
			throw new IllegalStateException("unknown entry type found: " + entry.getClass().getSimpleName());
		}
	}
	private void indexFragments(String word) {
		wordlist.add(word);
		int wordNr = wordlist.size() - 1;
		// Generate the deletes for the words
		for (String fragment : getEdits(word, 0, new HashSet<String>())) {
			Object dictionaryEntry;
			dictionaryEntry = parentDictionary.get(fragment);
			if (dictionaryEntry != null) {
				// this term already exist, it could be any of the following 
				// it could  be a  actual  word or a delete of another word.
				if (dictionaryEntry instanceof Integer) {
					DictionaryEntry dictItem = asDictionaryEntry(dictionaryEntry);
					dictionary.put(fragment, dictItem);
					if (wordNr != (int) dictionaryEntry)
						addLowestDistance(dictItem, word, wordNr, fragment);
				} else if (!((DictionaryEntry) dictionaryEntry).suggestions.contains(wordNr))
					addLowestDistance((DictionaryEntry) dictionaryEntry, word, wordNr, fragment);
			} else {
				dictionary.put(fragment, wordNr);
			}
		}
	}
	
	/* Generate only the deletes and no  transposes/replaces/inserts which are expensive and language dependent
	 * 
	 */
	private HashSet<String> getEdits(String word, int editDistance, HashSet<String> deletes) {
		editDistance++;
		if (word.length() > 1) {
			for (int i = 0; i < word.length(); i++) {
				String delete = word.substring(0, i) + word.substring(i + 1);
				if (deletes.add(delete)) {
					if (editDistance < editDistanceMax) getEdits(delete, editDistance, deletes);
				}
			}
		}
		return deletes;
	}

	
	/*
	 * Remove all existing suggestions if the existing suggestion is of  higher distance to  save time and dictionary space
	 */
	private void addLowestDistance(DictionaryEntry item, String word, int wordNr, String fragment) {
		int indexedDistance = item.suggestions.size() > 0
				? wordlist.get(item.suggestions.get(0)).length() - fragment.length()
						: -1;
				int fragmentDistance = word.length() - fragment.length();
				if ((accuracyLevel.ordinal() < 2) && (indexedDistance > fragmentDistance)) {
					item.suggestions.clear();
				}
				if ((accuracyLevel.ordinal() == 2)
						|| (item.suggestions.size() == 0)
						|| (indexedDistance >= fragmentDistance)) {
					item.suggestions.add(wordNr);
				}
	}


}
