package com.javaspell.spellcheck.api.request;

import com.google.common.base.Objects;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class SpellCheckRequest implements Serializable{

	public SpellCheckRequest() {
		super();
	}

	public SpellCheckRequest(String q) {
		super();
		this.q = q;

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6393704393154702686L;
	private String q;
	private boolean hasMultipletokens = false;
	   private boolean cacheOverride = false;
	




	private boolean debug;
	private long id;

	public boolean isValid(){
		return StringUtils.isNotEmpty(this.q);
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}



	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SpellCheckRequest cpRequest = (SpellCheckRequest) o;
		return Objects.equal(q, cpRequest.q);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(q);
	}


	public boolean isHasMultipletokens() {
		return hasMultipletokens;
	}

	public void setHasMultipletokens(boolean hasMultipletokens) {
		this.hasMultipletokens = hasMultipletokens;
	}

	 public String toKey() {
	        return "_" + q;
	    }

	public boolean isCacheOverride() {
		return cacheOverride;
	}

	public void setCacheOverride(boolean cacheOverride) {
		this.cacheOverride = cacheOverride;
	}


}
