package com.javaspell.spellcheck.bl.dictionary;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class DictionaryEntry {
	public DictionaryEntry() {
	}
	
	public DictionaryEntry(int suggestion) {
		suggestions.add(suggestion);
	}
	
	public TIntList suggestions = new TIntArrayList();
	public int count = 0;

}
