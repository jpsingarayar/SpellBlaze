package com.javaspell.spellcheck.bl.spellservice.settings;

import com.javaspell.spellcheck.bl.dictionary.SuggestItem;
import com.javaspell.spellcheck.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

@Named("spellCheck")
public class SpellCheckInstance implements SpellCheckLookUp{
	
	@Inject
	@Named("spellCheckSettings")
	private SpellCheckSettings settings;
	
	@PostConstruct
	public void loadInstance(){
		this.maxEditDistance=settings.getEditDistanceMax();
		this.editWeight = settings.getEditWeight();
		this.phoneWeight = settings.getPhoneWeight();
		this.prefixWeight = settings.getPrefixWeight();
		this.fragmentWeight = settings.getFragmentWeight();
		this.wordFrequencyWeight = settings.getWordFrequencyWeight();
		this.divisor = settings.getDivisor();
	}
	
	private static final Pattern removePattern = Pattern.compile("[^\\p{L}\\p{N}\\p{Z}]");

	private final CharDistance keyboardDistance = new QwertzKeyboardDistance();

	// Proximity function variables
	private  double editWeight;
	private  double phoneWeight;
	private  double prefixWeight;
	private  double fragmentWeight;
	private  double wordFrequencyWeight;
	private  double divisor;

	private final PrefixProximity prefixProximity = new PrefixProximity();
	
	private Comparator<SuggestItem> proximityComparator = new Comparator<SuggestItem>() {
		public int compare(SuggestItem x, SuggestItem y) {
			int compare = Double.compare(x.proximity, y.proximity);
			if (compare == 0) compare = Integer.compare(x.count, y.count);
			return -1 * compare;
		}
	};
	
	// save for internal calculation
	private  double maxEditDistance;
	
	
	@Override
	public String cleanIndexWord(String word) {
		return word.toLowerCase().replaceAll("\\s+", " ");
	}
	
	@Override
	public String cleanSearchWord(String searchWord) {
		return removePattern.matcher(searchWord).replaceAll("").toLowerCase();
	}
	
	@Override
	public double getReplacementDistance(char a, char b) {
		return keyboardDistance.distance(a, b);
	}

	@Override
	public List<SuggestItem> adjustFinalResult(String searchWord, List<SuggestItem> result) {
		List<SuggestItem> sortedResult = new ArrayList<>();
		long searchWordEudex = Eudex.encode(searchWord);

		for (SuggestItem s : result) {
			s.editProximity = (maxEditDistance - s.distance) / maxEditDistance;

			s.phoneticProximity = Eudex.distance(searchWordEudex, Eudex.encode(s.term));
			// we are using EUDEX but have to normalize the distance by
			// 1-(LN(eudexDistance+1)/9)
			s.phoneticProximity = (1 - ((Math.log(s.phoneticProximity + 1) / Math.log(10) / 9)));

			s.fragmentProximity = FragmentProximity.distance(searchWord, s.term);
			s.prefixProximity = prefixProximity.distance(searchWord, s.term);

			s.proximity = getCombinedProximity(searchWord, s);

			sortedResult.add(s);
		}
		
		// sort by descending proximity, then by descending word
		// frequency/language probability
		Collections.sort(sortedResult, proximityComparator);
		return sortedResult;
	}

	protected double getCombinedProximity(String searchWord, SuggestItem s) {
		return (s.editProximity * editWeight
				+ s.phoneticProximity * phoneWeight
				+ s.fragmentProximity * fragmentWeight
				+ s.prefixProximity * prefixWeight
				+ s.wordFrequency * wordFrequencyWeight) / divisor;
	}

	@Override
	public String toString() {
		return "SpellCheck Instance";
	}
	
	@Override
	public SpellCheckSettings getSettings() {
		// TODO Auto-generated method stub
		return settings;
	}

}
