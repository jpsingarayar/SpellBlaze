package com.javaspell.spellcheck.bl.data;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;


@Named
public abstract class DataLoader {
	 @Inject
	    protected DictionaryUtil dictionaryUtil;
	 @PostConstruct
	    public abstract void loadResource();
	 	
	

}
