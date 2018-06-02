package com.javaspell.spellcheck.bl.spellservice;

import com.google.common.collect.Ordering;
import com.javaspell.spellcheck.api.request.SpellCheckRequest;
import com.javaspell.spellcheck.api.response.Proximity;
import com.javaspell.spellcheck.api.response.ServiceResponse;
import com.javaspell.spellcheck.bl.data.DictionaryUtil;
import com.javaspell.spellcheck.bl.dictionary.DictionaryEntry;
import com.javaspell.spellcheck.bl.dictionary.DictionaryInitializer;
import com.javaspell.spellcheck.bl.dictionary.SuggestItem;
import com.javaspell.spellcheck.bl.spellservice.settings.SpellCheckLookUp;
import com.javaspell.spellcheck.bl.spellservice.settings.SpellCheckSettings;
import com.javaspell.spellcheck.util.AccuracyLevel;
import org.ehcache.Cache;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;


@Named
public class SpellCheckService {
	@Inject
	@Named("spellCheck")
	private SpellCheckLookUp spellCheck;

	@Inject
	@Named("spellCheckSettings")
	private SpellCheckSettings spellCheckSettings;

	private Cache<String, Object> dictionary;

	@PostConstruct
	public void init(){			
		editDistanceMax = spellCheckSettings.getEditDistanceMax();
		accuracyLevel = spellCheckSettings.getAccuracyLevel();
		topK = spellCheckSettings.getTopK();
		deletionWeight = spellCheckSettings.getDeletionWeight();
		insertionWeight = spellCheckSettings.getInsertionWeight();
		transpositionWeight = spellCheckSettings.getTranspositionWeight();
		replaceWeight = spellCheckSettings.getReplaceWeight();	
	}


	private AccuracyLevel accuracyLevel;

	@Inject
	private DictionaryInitializer dictionaryLoader;

	private int editDistanceMax;

	// limit suggestion list to topK entries
	private  int topK; 

	// Damerau function variables
	private  double deletionWeight;

	private double insertionWeight;

	private double replaceWeight;

	private double transpositionWeight;

	private final Comparator<SuggestItem> distanceCountComparator = new Comparator<SuggestItem>() {
		public int compare(SuggestItem x, SuggestItem y) {
			return (2 * Double.compare(x.distance, y.distance) - Integer.compare(x.count, y.count));
		}
	};	



	// List of unique words. By using the suggestions (int) as index for this
	// list they are translated into the original String.
	private List<String> wordlist;



	public List<String> findSimilarWords(String searchQuery) {
		List<SuggestItem> suggestions = lookup(searchQuery);

		List<String> similarWords = new ArrayList<>();
		suggestions.forEach(suggestion -> similarWords.add(suggestion.term));

		return similarWords;
	}
	public ServiceResponse getCorrectedWord(SpellCheckRequest request)
	{
		ServiceResponse response = new ServiceResponse();
		List<String> searchTokens = splitSearchPhrase(request.getQ());
		if(searchTokens.size()>1) request.setHasMultipletokens(true);
		if(request.isHasMultipletokens())
		{
		// TODO handle multiple tokens in the search word
			StringJoiner joiner = new StringJoiner(" ");
			response.setOrigPhrase(request.getQ());
			HashMap<String,Double> similarityMap = new HashMap<>();
			List<String> correctedWords = new ArrayList<>();
			List<Proximity> proximityList = new ArrayList<>();
			
			for(String token: searchTokens) {
				List<SuggestItem> correctedList = lookup(token);
				for(SuggestItem item: correctedList)
				{					
					joiner.add(item.term);
					similarityMap.put(item.term, item.proximity);
					correctedWords.add(item.term);
					proximityList.add(generateProximity(item));
				}

			}
			response.setCorrectedWords(correctedWords);
			response.setEditDistanceMax(editDistanceMax);
			response.setCorrectedPhrase(joiner.toString());
			response.setProximity(proximityList);
			response.setSimilarityMap(similarityMap);
			response.setSimilarity(getCombinedSimilarty(similarityMap));
			return response;
			
		}
		
		List<SuggestItem> correctedList = lookup(request.getQ());
		response.setOrigPhrase(request.getQ());	
		HashMap<String,Double> similarityMap = new HashMap<>();
		List<String> correctedWords = new ArrayList<>();
		List<Proximity> proximityList = new ArrayList<>();
		for(SuggestItem item: correctedList)
		{
			proximityList.add(generateProximity(item));
			correctedWords.add(item.term);
			similarityMap.put(item.term, item.proximity);
			response.setCorrectedPhrase(item.term);
			response.setEditDistanceMax(editDistanceMax);
			response.setSimilarity(item.proximity);
			response.setProximity(proximityList);
			response.setSimilarityMap(similarityMap);
			response.setCorrectedWords(correctedWords);
			
			
		}
		return response;

	}

	private double getCombinedSimilarty(HashMap<String, Double> similarityMap) {
		// TODO Auto-generated method stub
		double combinedSimilarty =0.0;
		for(double d: similarityMap.values())
		{
			combinedSimilarty+=d;
		}
		return combinedSimilarty/similarityMap.size();
		
	}
	private Proximity generateProximity(SuggestItem item) {
		// TODO Auto-generated method stub
		Proximity proximity = new Proximity();
		proximity.setTerm(item.term);
		proximity.setCount(item.count);
		proximity.setDistance(item.distance);
		proximity.setEditProximity(item.editProximity);
		proximity.setFragmentProximity(item.fragmentProximity);
		proximity.setPhoneticProximity(item.phoneticProximity);
		proximity.setPrefixProximity(item.prefixProximity);
		proximity.setProximity(item.proximity);
		proximity.setWordFrequency(item.wordFrequency);
		
		return proximity;
	}
	public List<SuggestItem> lookup(String searchWord) {
		String cleanedSearchWord = spellCheck.cleanSearchWord(searchWord);
		dictionary = dictionaryLoader.getCache();
		wordlist = dictionaryLoader.getWordList();

		// Check if the input string is too big for this dictionary lookup
		if (cleanedSearchWord.length() - editDistanceMax > DictionaryUtil.maxlength)
			return new ArrayList<SuggestItem>();

		List<String> candidates = new ArrayList<String>();
		HashSet<String> candidatesUniq = new HashSet<String>();

		List<SuggestItem> suggestions = new ArrayList<SuggestItem>();
		HashSet<String> checkedWords = new HashSet<String>();

		Object dictionaryEntry;

		// Add search term as a candicate
		candidates.add(cleanedSearchWord);

		while (candidates.size() > 0) {
			String candidate = candidates.remove(0);

			nosort: {

				// if accuracy is lower than 2, save some time by early
				// termination (of candidate check)
				// if candidate distance is already higher than
				// distance of first suggestion
				if ((accuracyLevel.ordinal() < 2)
						&& (suggestions.size() > 0)
						&& (cleanedSearchWord.length() - candidate.length() > suggestions.get(0).distance))
					break nosort;

				// read entry from dictionary
				dictionaryEntry = dictionary.get(candidate);
				if (dictionaryEntry != null) {
					DictionaryEntry matchedDictionaryItem = asDictionaryItem(dictionaryEntry);

					// if count>0 then candidate entry is correct dictionary
					// term, not only delete item
					if ((matchedDictionaryItem.count > 0) && checkedWords.add(candidate)) {
						// add correct dictionary term term to suggestion list
						SuggestItem si = new SuggestItem();
						si.term = candidate;
						si.count = matchedDictionaryItem.count;
						si.wordFrequency = ((double) si.count / dictionaryLoader.getCacheSize());
						si.distance = getMaxDistance(cleanedSearchWord, candidate);

						si.distance = spellCheck.adjustDistance(cleanedSearchWord, candidate, si.distance);

						if (si.distance <= editDistanceMax) {
							suggestions.add(si);
						}
						// early termination
						if ((accuracyLevel.ordinal() < 2) && (cleanedSearchWord.length() - candidate.length() == 0))
							break nosort;
					}

					// iterate through suggestions (to other correct dictionary
					// items) of delete item and add them to suggestion list
					for (int wordNr : matchedDictionaryItem.suggestions.toArray()) {
						// save some time by skipping double items early:
						// different deletes of the input term can lead to
						// the same suggestion
						String suggestion = wordlist.get(wordNr);
						if (checkedWords.add(suggestion)) {
							
							double distance = 0;
							if (!suggestion.equals(cleanedSearchWord)) {
								// Case 1: if only deletes match the dictionary
								if (suggestion.length() == candidate.length()) {
									distance = getMaxDistance(cleanedSearchWord, candidate);
								} else if (cleanedSearchWord.length() == candidate.length()) {
									distance = getMaxDistance(suggestion, candidate);

									// Case 2: if further edits additional to
									// the deletes need to happen in order to
									// match the dictionary
								} else {
									// Speed up the distance calculation by  removing the common suffix and prefix .
									int prefixLength = 0;
									int suffixLength = 0;

									while ((prefixLength < suggestion.length()) && (prefixLength < cleanedSearchWord.length())
											&& (suggestion
													.charAt(
															prefixLength) == cleanedSearchWord.charAt(prefixLength)))
										prefixLength++;

									while ((suffixLength < suggestion.length() - prefixLength)
											&& (suffixLength < cleanedSearchWord.length() - prefixLength)
											&& (suggestion.charAt(suggestion.length() - suffixLength - 1) == cleanedSearchWord
											.charAt(cleanedSearchWord.length() - suffixLength - 1)))
										suffixLength++;

									if ((prefixLength > 0) || (suffixLength > 0)) {
										distance = cxpDamerauLevenshtein(
												cleanedSearchWord.substring(prefixLength, cleanedSearchWord.length() - suffixLength),
												suggestion.substring(prefixLength, suggestion.length() - suffixLength));

									} else {
										distance = cxpDamerauLevenshtein(cleanedSearchWord, suggestion);
									}
									distance = spellCheck.adjustDetailedDistance(cleanedSearchWord, suggestion, distance, prefixLength, suffixLength);
								}
							}

							// save some time.
							// remove all existing suggestions of higher
							// distance, if accuracy <2
							if ((accuracyLevel.ordinal() < 2)
									&& (suggestions.size() > 0)
									&& (suggestions.get(0).distance > distance)) {
								suggestions.clear();
							}

							// do not process higher distances than those
							// already found, if accuracy < 2
							if ((accuracyLevel.ordinal() < 2)
									&& (suggestions.size() > 0)
									&& (distance > suggestions.get(0).distance)) {
								continue;
							}

							distance = spellCheck.adjustDistance(cleanedSearchWord, candidate, distance);
							if (distance <= editDistanceMax) {
								Object suggestedItem = dictionary.get(suggestion);
								if (suggestedItem != null) {
									SuggestItem si = new SuggestItem();
									si.term = suggestion;
									si.count = ((DictionaryEntry) suggestedItem).count;
									si.wordFrequency = ((double) si.count / dictionaryLoader.getCacheSize());
									si.distance = distance;
									suggestions.add(si);
								}
							}
						}
					} // end for each
				} 
				// The acutal word not found in dictionary so polulate the edits recursively until  the editmdistance max is reached
				if (cleanedSearchWord.length() - candidate.length() < editDistanceMax) {
					// save some time: do not create edits with edit distance
					// smaller than suggestions already found
					if ((accuracyLevel.ordinal() < 2) && (suggestions.size() > 0) && (cleanedSearchWord.length() - candidate
							.length() >= suggestions.get(0).distance)) continue;

					for (int i = 0; i < candidate.length(); i++) {
						String delete = candidate.substring(0, i) + candidate.substring(i + 1);
						if (candidatesUniq.add(delete)) candidates.add(delete);
					}
				}
			} // end lable nosort
		} // end while

		return pickSuggestions(cleanedSearchWord, editDistanceMax, suggestions);
	}
	private DictionaryEntry asDictionaryItem(Object entry) {
		if (entry instanceof DictionaryEntry) {
			return (DictionaryEntry) entry;
		} else if (entry instanceof Integer) {
			// if value is an integer, word is also a fragment from another word
			// => append fragment to suggestions
			DictionaryEntry dictItem = new DictionaryEntry();
			dictItem.suggestions.add((int) entry);
			return dictItem;
		} else {
			throw new IllegalStateException("unknown dictionary entry type found: " + entry.getClass().getSimpleName());
		}
	}

	private double getMaxDistance(String fromString, String toString) {
		boolean isDelete = fromString.length() > toString.length();
		return (isDelete ? deletionWeight : insertionWeight)
				* (isDelete ? fromString.length() - toString.length() : toString.length() - fromString.length());
	}

	private double cxpDamerauLevenshtein(String a, String b) {
		double[][] d = new double[b.length() + 1][a.length() + 1]; // 2d matrix

		// Step 1
		if (a.length() == 0) return b.length();
		if (b.length() == 0) return a.length();

		// Step 2
		for (int i = a.length(); i >= 0; i--)
			d[0][i] = i * deletionWeight;
		for (int j = b.length(); j >= 0; j--)
			d[j][0] = j;

		// Step 4
		for (int j = 1; j <= b.length(); j++) {
			char b_j = b.charAt(j - 1);

			// Step 3
			for (int i = 1; i <= a.length(); i++) {
				char a_i = a.charAt(i - 1);

				// CXP Damerau operations
				double min = min(
						d[j - 1][i - 1],
						d[j - 1][i],
						d[j][i - 1]);
				if (a_i == b_j) {
					d[j][i] = min;
				} else if (i == j) {
					d[j][i] = min + (replaceWeight * spellCheck.getReplacementDistance(b_j, a_i)); // replace

					if (i > 1 && a_i == b.charAt(j - 2) && a.charAt(i - 2) == b_j) {
						d[j][i] = Math.min(d[j][i], d[j - 2][i - 2] + transpositionWeight); // transpose
					}
				} else if (i > j) {
					d[j][i] = min + deletionWeight; // delete
				} else if (i < j) {
					d[j][i] = min + insertionWeight; // insert
				}
			}
		}
		// Step 5
		return d[b.length()][a.length()];
	}

	private double min(double a, double b, double c) {
		return Math.min(a, Math.min(b, c));
	}

	/*private int getDictSize(Cache dictionary) {
		int count =0;
		Iterator<Cache.Entry<String, Object>> it = dictionary.iterator();
		  while(it.hasNext()) {
			  System.out.println("int");
		    count++;
		  }
		  return count;
	}*/

	private List<SuggestItem> pickSuggestions(String searchWord, int editDistanceMax, List<SuggestItem> suggestions) {
		int k = suggestions.size();
		if ((accuracyLevel == AccuracyLevel.topHit) && (suggestions.size() > 1))
			k = 1;
		else if (suggestions.size() > topK) {
			k = topK;
		}

		List<SuggestItem> returnSuggestions;
		if (k >= suggestions.size()) {
			returnSuggestions = suggestions;
		} else {
			returnSuggestions = Ordering.from(distanceCountComparator).leastOf(suggestions, k);
		}

		return spellCheck.adjustFinalResult(searchWord, returnSuggestions);
	}

	private List<String> splitSearchPhrase(String searchPhrase)
	{
		return Arrays.asList(searchPhrase.split("\\s+"));
	}
} 
