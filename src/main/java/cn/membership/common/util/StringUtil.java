package cn.membership.common.util;

/*
 *  广州市触通软件科技有限公司 BN-BPM管理平台   -- http://www.chutong.cn
 *  Copyright (C) 2008-2011 GuangZhou ChuTong Software Company
 */
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串类
 * 
 * @author hhr
 * 
 */
public class StringUtil {
//	/**
//	 * 判断指定的字符串是否为数字
//	 * @param str 字符串
//	 * @return 返回boolean
//	 */
//    public static boolean isNum(String str) {
//        String regex = "0123456789";
//        
//        if(str == null)
//        	return false;
//
//        if(str.length() ==0)
//            return false;
//
//        for(int i=0;i<str.length();i++) {
//            if(regex.indexOf(str.charAt(i)) == -1)
//                return false;
//        }
//        return true;
//    }
	
	/**
	 * 判断某个字符串是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 简单的字符串格式化，性能较好。支持不多于10个占位符，从%1开始计算，数目可变。参数类型可以是字符串、Integer、Object，
	 * 甚至int等基本类型
	 * 、以及null，但只是简单的调用toString()，较复杂的情况用String.format()。每个参数可以在表达式出现多次。
	 * 
	 * @param msgWithFormat
	 * @param autoQuote
	 * @param args
	 * @return
	 */
	public static StringBuilder formatMsg(CharSequence msgWithFormat,boolean autoQuote, Object... args) {
		int argsLen = args.length;
		boolean markFound = false;

		StringBuilder sb = new StringBuilder(msgWithFormat);

		if (argsLen > 0) {
			for (int i = 0; i < argsLen; i++) {
				String flag = "%" + (i + 1);
				int idx = sb.indexOf(flag);
				// 支持多次出现、替换的代码
				while (idx >= 0) {
					markFound = true;
					sb.replace(idx, idx + 2, toString(args[i], autoQuote));
					idx = sb.indexOf(flag);
				}
			}

			if (args[argsLen - 1] instanceof Throwable) {
				StringWriter sw = new StringWriter();
				((Throwable) args[argsLen - 1])
						.printStackTrace(new PrintWriter(sw));
				sb.append("\n").append(sw.toString());
			} else if (argsLen == 1 && !markFound) {
				sb.append(args[argsLen - 1].toString());
			}
		}
		return sb;
	}
	
	public static StringBuilder formatMsg(String msgWithFormat, Object... args) {
		return formatMsg(new StringBuilder(msgWithFormat), true, args);
	}

	public static String toString(Object obj, boolean autoQuote) {
		StringBuilder sb = new StringBuilder();
		if (obj == null) {
			sb.append("NULL");
		} else {
			if (obj instanceof Object[]) {
				for (int i = 0; i < ((Object[]) obj).length; i++) {
					sb.append(((Object[]) obj)[i]).append(", ");
				}
				if (sb.length() > 0) {
					sb.delete(sb.length() - 2, sb.length());
				}
			} else {
				sb.append(obj.toString());
			}
		}
		if (autoQuote
				&& sb.length() > 0
				&& !((sb.charAt(0) == '[' && sb.charAt(sb.length() - 1) == ']') || (sb
						.charAt(0) == '{' && sb.charAt(sb.length() - 1) == '}'))) {
			sb.insert(0, "[").append("]");
		}
		return sb.toString();
	}

	/**
	 * 把字符串中的带‘与"转成\'与\"
	 * 
	 * @param orgStr
	 * @return
	 */
	public static String convertQuot(String orgStr) {
		return orgStr.replace("'", "\\'").replace("\"", "\\\"");
	}

	public static synchronized String encryptSha256(String inputStr) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			byte digest[] = md.digest(inputStr.getBytes("UTF-8"));

//			return new String(Base64.encodeBase64(digest));
			return "";
			// return (new BASE64Encoder()).encode(digest);
			// return new String(Hex.encodeHex(digest));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * HTML实体编码转成普通的编码
	 * 
	 * @param dataStr
	 * @return
	 */
	public static String htmlEntityToString(final String dataStr) {
		int start = 0;
		int end = 0;
		final StringBuffer buffer = new StringBuffer();
		while (start > -1) {
			int system = 10;// 进制
			if (start == 0) {
				int t = dataStr.indexOf("&#");
				if (start != t)
					start = t;
			}
			end = dataStr.indexOf(";", start + 2);
			String charStr = "";
			if (end != -1) {
				charStr = dataStr.substring(start + 2, end);
				// 判断进制
				char s = charStr.charAt(0);
				if (s == 'x' || s == 'X') {
					system = 16;
					charStr = charStr.substring(1);
				}
			}
			// 转换
			try {
				char letter = (char) Integer.parseInt(charStr, system);
				buffer.append(new Character(letter).toString());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			// 处理当前unicode字符到下一个unicode字符之间的非unicode字符
			start = dataStr.indexOf("&#", end);
			if (start - end > 1) {
				buffer.append(dataStr.substring(end + 1, start));
			}

			// 处理最后面的非unicode字符
			if (start == -1) {
				int length = dataStr.length();
				if (end + 1 != length) {
					buffer.append(dataStr.substring(end + 1, length));
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * 把String转成html实体字符
	 * 
	 * @param str
	 * @return
	 */
	public static String stringToHtmlEntity(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			switch (c) {
			case 0x0A:
				sb.append(c);
				break;

			case '<':
				sb.append("&lt;");
				break;

			case '>':
				sb.append("&gt;");
				break;

			case '&':
				sb.append("&amp;");
				break;

			case '\'':
				sb.append("&apos;");
				break;

			case '"':
				sb.append("&quot;");
				break;

			default:
				if ((c < ' ') || (c > 0x7E)) {
					sb.append("&#x");
					sb.append(Integer.toString(c, 16));
					sb.append(';');
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	// 转unicode
	public static String stringToUnicode(String s) {
		String unicode = "";
		char[] charAry = new char[s.length()];
		for (int i = 0; i < charAry.length; i++) {
			charAry[i] = (char) s.charAt(i);
			unicode += "\\u" + Integer.toString(charAry[i], 16);
		}
		return unicode;
	}

	public static String unicodeToString(String unicodeStr) {
		StringBuffer sb = new StringBuffer();
		String str[] = unicodeStr.toUpperCase().split("\\\\U");
		for (int i = 0; i < str.length; i++) {
			if (str[i].equals(""))
				continue;
			char c = (char) Integer.parseInt(str[i].trim(), 16);
			sb.append(c);
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		// String password = "未命名任务";
		// String result = stringToHtmlEntity(password);

		// String str="&#x672a;&#x547d;&#x540d;&#x4efb;&#x52a1;";
		// String result=htmlEntityToString(str);

		// byte[]dis={-10, -32, -95, -30, -84, 65, -108, 90, -102, -89, -1,
		// -118, -118, -86, 12, -21, -63, 42, 59, -52, -104, 26, -110, -102,
		// -43, -49, -127, 10, 9, 14, 17, -82};
		// System.out.println("array:"+ new String(Hex.encodeHex(dis)));
		// System.out.println("result:" + result);

		// String content="pd6717864949166496642.40.连线2";
		// String encode=stringToUnicode(content);
		String vm = "123";
		System.out.println(StringUtil.encryptSha256(vm));

		// System.out.println("another:" + unicodeToString(encode));
	}

	public static String html2Text(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script>]*?>[\s\S]*?<\/script>
																										// }
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style>]*?>[\s\S]*?<\/style>
																									// }
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

			p_script = java.util.regex.Pattern.compile(regEx_script,
					java.util.regex.Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = java.util.regex.Pattern.compile(regEx_style,
					java.util.regex.Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = java.util.regex.Pattern.compile(regEx_html,
					java.util.regex.Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			textStr = htmlStr;

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return textStr;// 返回文本字符串
	}
	
	/**
     * 简单的把Object对象装换为String对象
     * 如果输入参数为null则返回长度为0的字符串
     * 如果输入参数为String，返回则把该String的两端空
     * 字符串去掉后的字符串
     */
    public static String o2s(Object o) {
        if (null == o) {
            return "";
        }
        if (o instanceof String) {
            return ((String) o).trim();
        }
        return o.toString().trim();
    }

    /**
     * 把对象转换成int，失败返回0
     *
     * @param o 对象
     * @return int
     */
    public static int o2i(Object o) {
        int r = 0;
        try {
            r = Integer.parseInt(o2s(o));
        }
        catch (Throwable e) {
        }
        return r;
    }

    /**
     * 把对象转换成long，失败返回0
     *
     * @param o 对象
     * @return int
     */
    public static long o2l(Object o) {
        long r = 0;
        try {
            r = Long.parseLong(o2s(o));
        }
        catch (Throwable e) {
        }
        return r;
    }

    /**
     * 把对象转换成double，失败返回0
     *
     * @param o 对象
     * @return int
     */
    public static double o2d(Object o) {
        double r = 0;
        try {
            r = Double.parseDouble(o2s(o));
        }
        catch (Throwable e) {
        }
        return r;
    }
    
    /**
     * 判断LIST是否为空
     *
     * @param list
     * @return
     */
    public static boolean isListNull(List list) {
        if (list != null && !list.isEmpty()) {
            return true;
        } else {
            return false;
        }

    }
    
    /**
     * 分割字符串
     *
     * @param tempaborts 被分割的字符串
     * @param dot        String 分割符号
     * @return String[] 返回数组
     */
    public static String[] getAborts(String tempaborts, String dot) {
        //创建StringTokenizer对象
        StringTokenizer st = new StringTokenizer(tempaborts, dot);
        //获取总数
        int len = st.countTokens();
        String[] val = new String[len];
        int m = 0;
        //分割
        while (st.hasMoreTokens()) {
            val[m] = st.nextToken();
            m++;
        }
        return val;
    }
    
    /**
     * 格式化SQL 中的IN 参数
     * @param value  aa,bb  or  aa;bb
     * @return 'aa','bb'
     */
    public static String formatValueToSQLIN(String value) {
		StringBuffer sql = new StringBuffer();
		if (value == null || value.trim().length() == 0) {
			return "";
		}
		try {
			String[] va = value.indexOf(",") == -1 ? value.split(";") : value.split(",");
			for (int i = 0; i < va.length; i++) {
               if(va == null || va[i].trim().length() == 0){
            	   continue;
               }
               sql.append(",'"+va[i].trim()+"'");
			}
		} catch (Exception e) {
			return "";
		}
		return sql.substring(1);
	}
    
    /**
     * 验证手机号码
     * @param mobiles
     * @return
     * @author HHR
     * @since
     */
    public static boolean isMobileNO(String mobiles){
      Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
      Matcher m = p.matcher(mobiles);
      //System.out.println(m.matches()+"---");
      return m.matches();
    }
}
