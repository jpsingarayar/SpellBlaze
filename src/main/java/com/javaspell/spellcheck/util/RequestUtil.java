package com.javaspell.spellcheck.util;

import com.javaspell.spellcheck.api.request.SpellCheckRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class RequestUtil {
	public static <T> T getFirstIfPresent(List<T> inputList) {
        if (inputList == null || inputList.size() == 0) {
            return null;
        }
        return inputList.get(0);
    }

    public static SpellCheckRequest buildRequest(long id, String q, String debug){
    	SpellCheckRequest request = new SpellCheckRequest();

        request.setId(id);
        request.setQ(q);

        if (StringUtils.isNotEmpty(debug)) {
            request.setDebug(BooleanUtils.toBoolean(debug));
        }

        return request;
    }
}
