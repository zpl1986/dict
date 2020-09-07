/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 命名工具类
 * 
 * @author N. SUN
 * @version 0.0.1
 * @since 	
 */
public class NamingUtil {
    public static String convertLowerCaseWithUnderscore2CamelCase(final String lowerCaseWithUnderscoreStr) {
        String camelCaseStr = null;

        if (null != lowerCaseWithUnderscoreStr) {
            StringBuffer sb = new StringBuffer();
            
            String[] fragments = lowerCaseWithUnderscoreStr.split("_");
            if (null != fragments && fragments.length > 0) {
                
                for (int i = 0; i < fragments.length; i++) {
                    if (null != fragments[i]) {
                        if (0 == i) {
                            sb.append(fragments[i]);
                        } else {
                            sb.append(fragments[i].substring(0, 1).toUpperCase().concat(fragments[i].substring(1)));
                        }
                    } // if (null != fragments[i])
                } // for (int i = 0; i < fragments.length; i++)
                                
            } // if (null != fragments && fragments.length > 0)
            
            
            camelCaseStr = sb.toString();
        } // if (null != lowerCaseWithUnderscoreStr)
        
        return camelCaseStr;
    }
    
    public static String convertLowerCaseWithUnderscore2PascalCase(final String lowerCaseWithUnderscoreStr) {
        String pascalCaseStr = null;

        if (null != lowerCaseWithUnderscoreStr) {
            String camelCaseStr = convertLowerCaseWithUnderscore2CamelCase(lowerCaseWithUnderscoreStr);
            if (null != camelCaseStr && camelCaseStr.length() > 0) {
                pascalCaseStr = camelCaseStr.substring(0, 1).toUpperCase().concat(camelCaseStr.substring(1));
            } // if (null != camelCaseStr && camelCaseStr.length() > 0)
            
        } // if (null != lowerCaseWithUnderscoreStr)
        
        return pascalCaseStr;
    }
    

    public static String convertCamelCase2LowerCaseWithUnderscore(final String camelCaseStr) {
        return convertPascalCase2LowerCaseWithUnderscore(camelCaseStr);
    }

    public static String convertPascalCase2LowerCaseWithUnderscore(final String pascalCaseStr) {

        String underscoreCaseStr = null;

        if (null != pascalCaseStr) {
            StringBuffer sb = new StringBuffer();

            String regexStr = "[A-Z]";
            Matcher matcher = Pattern.compile(regexStr).matcher(pascalCaseStr);
            while (matcher.find()) {
                String g = matcher.group();
                matcher.appendReplacement(sb, "_" + g.toLowerCase());
            } // while (matcher.find())
            matcher.appendTail(sb);

            if (sb.charAt(0) == '_') {
                sb.delete(0, 1);
            } // if (sb.charAt(0) == '_')

            underscoreCaseStr = sb.toString();
        } // if (null != camelCaseStr)

        return underscoreCaseStr;
    }
}
