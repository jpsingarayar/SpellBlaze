package com.javaspell.spellcheck.util;


import com.javaspell.spellcheck.api.request.SpellCheckRequest;
import org.apache.log4j.Logger;


public class MiscUtil {
    private static Logger logger = Logger.getLogger(MiscUtil.class.getName());

    public static String getCacheKey(SpellCheckRequest serviceRequest) {
        return serviceRequest.toKey();
    }
    public static boolean isCacheOverride(SpellCheckRequest serviceRequest) {
        return serviceRequest.isCacheOverride();
    }

   
}
