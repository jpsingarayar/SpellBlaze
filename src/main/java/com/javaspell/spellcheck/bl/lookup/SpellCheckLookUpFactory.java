package com.javaspell.spellcheck.bl.lookup;

import com.javaspell.spellcheck.bl.spellservice.SpellCheckService;
import com.javaspell.spellcheck.util.AccuracyLevel;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SpellCheckLookUpFactory {
	
	@Inject
	@Named("spellCheckService")
	private SpellCheckService spellCheckService;

	public  SpellCheckService getSpellCheckService() {
		return getSpellCheckLookUpInstance(AccuracyLevel.topHit);
	}
	
	public  SpellCheckService getSpellCheckLookUpInstance(AccuracyLevel accuracyLevel) {
		return spellCheckService;
	}
	

}
