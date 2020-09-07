package cn.membership.common.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import cn.membership.web.constant.WebRequestConstant;

public class ObjectUtil {

    public static int toInt(Object obj, int defaultValue) {
        int i = defaultValue;
        if (obj instanceof Number) {
            i = ((Number) obj).intValue();
        }
        if (obj instanceof String) {
            try {
                i = Integer.parseInt((String) obj);
            } catch (Exception e) {
            }
        }
        return i;
    }

    public static Integer toInteger(Object obj) {
        Integer integer = null;
        if (obj instanceof Number) {
            integer = ((Number) obj).intValue();
        } else if (obj instanceof String) {
            try {
                integer = Integer.parseInt((String) obj);
            } catch (Exception e) {
            }
        }
        return integer;
    }

    public static Double toDouble(Object obj, Double defaultValue) {
        Double d = defaultValue;
        if (obj instanceof Number) {
            d = ((Number) obj).doubleValue();
        }
        if (obj instanceof String) {
            try {
                d = Double.parseDouble((String) obj);
            } catch (Exception e) {
            }
        }
        return d;
    }

    public static String toString(Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Date) {
            String string = obj.toString();
            return string.replace("-", "");
        }
        if (obj instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) obj;
            long time = timestamp.getTime();
            java.util.Date date = new java.util.Date(time);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            return format.format(date);
        }

        return obj.toString();
    }

    //    public static String toURLDecoderString(Object obj) {
    //
    //        String ret = null;
    //        if (obj != null && (obj instanceof String)) {
    //            ret = ((String) obj).intern();
    //            try {
    //                ret = URLDecoder.decode(ret, "UTF-8").intern();
    //            } catch (UnsupportedEncodingException e) {
    //            }
    //        } else if (obj != null) {
    //            ret = obj.toString().intern();
    //        }
    //        return ret;
    //    }

    //    public static String toURLEncoderString(Object obj) {
    //        String ret = null;
    //        if (obj != null && (obj instanceof String)) {
    //            ret = ((String) obj).intern();
    //            try {
    //                ret = URLEncoder.encode(ret, "UTF-8");
    //            } catch (UnsupportedEncodingException e) {
    //            }
    //        } else if (obj != null) {
    //            ret = obj.toString().intern();
    //        }
    //        return ret;
    //    }

    //    public static String[] toURLDecoderArrayString(Object obj) {
    //
    //        String[] ret = null;
    //        if (obj != null && (obj instanceof String[])) {
    //            ret = (String[]) obj;
    //            try {
    //                for (int i = 0; i < ret.length; i++) {
    //                    ret[i] = URLDecoder.decode(ret[i], "UTF-8");
    //                }
    //            } catch (UnsupportedEncodingException e) {
    //            }
    //        }
    //        return ret;
    //    }

    //    public static Date toDate(Object obj) {
    //        if (obj == null) {
    //            return null;
    //        }
    //        if (obj instanceof Date) {
    //            return (Date) obj;
    //        }
    //        if (obj instanceof String) {
    //            String date = obj.toString().replace("-", "");
    //            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    //            try {
    //                long time = format.parse(date).getTime();
    //                return new Date(time);
    //            } catch (ParseException e) {
    //                throw new RuntimeException("Date格式不正确，应该为：yyyyMMdd");
    //            }
    //        }
    //        return null;
    //    }

        public static String toDateString(Object object, String pattern) {
            if (object == null) {
                return null;
            }
            SimpleDateFormat format = new SimpleDateFormat(pattern);
    
            if (object instanceof java.util.Date) {
                java.util.Date date = (java.util.Date) object;
                return format.format(date);
            }
            if (object instanceof String) {
                String time = (String) object;
                try {
                    // 把非数字用""代替
                    time = time.replaceAll("[^0-9]", "");
                    if (time.length() == 14) {// 20140430112233
                        return toDateString(new SimpleDateFormat("yyyyMMddHHmmss").parse(time), pattern);
                    }
    
                    if (time.length() == 8) {// 20140430
                        return toDateString(new SimpleDateFormat("yyyyMMdd").parse(time), pattern);
                    }
                    return time;
                } catch (Exception e) {
                    e.printStackTrace();
                }
    
            }
            return null;
        }

    //    public static String getOneMessage(String message, Integer maxLen) {
    //        if (maxLen == null) {
    //            maxLen = 150;
    //        }
    //        if (message == null)
    //            return null;
    //        try {
    //            byte[] bytes = message.getBytes("utf-8");
    //            if (bytes.length <= maxLen) {
    //                return message;
    //            }
    //            String oneMessage = new String(bytes, 0, maxLen, "utf-8");
    //            if (oneMessage.charAt(oneMessage.length() - 1) == '�') {
    //                oneMessage = oneMessage.substring(0, oneMessage.length() - 1);
    //            }
    //            return oneMessage;
    //        } catch (UnsupportedEncodingException e) {
    //            e.printStackTrace();
    //        }
    //        return message;
    //    }

    public static Long toLong(Object object, Long defaultValue) {
        Long returnVal = defaultValue;
        if (object instanceof Number) {
            returnVal = ((Number) object).longValue();
        }
        if (object instanceof String) {
            try {
                returnVal = Long.decode(object.toString());
            } catch (Exception e) {
            }
        }
        return returnVal;
    }

    // 转成的数据必须有一个参数为string的构造方法
    //    public static <T> T[] split(String content, String regex, T t[]) {
    //        if (regex == null || regex.length() == 0) {
    //            regex = ",";
    //        }
    //        if (content != null && t != null) {
    //            String[] split = content.trim().split(regex);
    //            List<Object> list = new ArrayList<Object>();
    //            for (String str : split) {
    //                try {
    //                    list.add(t.getClass().getComponentType().getConstructor(String.class).newInstance(str));
    //                } catch (Exception e) {
    //                    // e.printStackTrace();
    //                }
    //            }
    //            return list.toArray(t);
    //        }
    //        return null;
    //    }

    public static Collection<Object> toCollection(Object object) {
        Collection<Object> collection = new ArrayList<Object>();
        if (object instanceof Object[]) {
            collection = Arrays.asList((Object[]) object);
        } else if (object instanceof Collection) {
            collection = (Collection<Object>) object;
        } else {
            try {
                collection.add(object);
            } catch (Exception e) {
            }
        }
        return collection;
    }

    public static Collection<Long> toLongCollection(Object object) {
        Collection<Long> longCollection = new ArrayList<Long>();
        Collection<Object> collection = toCollection(object);
        for (Object obj : collection) {
            try {
                longCollection.add(Long.parseLong(obj.toString()));
            } catch (Exception e) {
            }
        }
        return longCollection;
    }

    public static String generateIdentifyingCode() {
        StringBuilder builder = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            builder.append(new Random().nextInt(10));
        }
        return builder.toString();
    }

    /**
     * 功能： 传入一个数，产生另一个伪随机的数（限制：524288个，即依次传入524288个连续的数，依次返回不连续的524288个数）
     * channal可选值： SH（闪银），ZJ（融之家），RS（融360），LB（乐宝贷）
     */
    public static String generateBorrowOrderCode(long id, String channel) {
        //        Set<String> channelSet = new HashSet<String>();
        //        channelSet.add("LB");
        //        channelSet.add("ZJ");
        //        channelSet.add("SH");
        //        channelSet.add("RS");        
        //        if (!channelSet.contains(channel)) {
        //            throw new RuntimeException("渠道必须为："+channelSet.toString()+"之中的一个");
        //        }
        if (null == channel) {
            channel = "NO";
        }
        int iNum = (int) (id % 0x80000);
        String bin = Integer.toBinaryString(iNum);
        while (bin.length() < 19) {
            bin = "0" + bin;
        }
        char chs[] = new char[19];
        chs[0] = bin.charAt(12);
        chs[1] = bin.charAt(3);
        chs[2] = bin.charAt(13);
        chs[3] = bin.charAt(2);
        chs[4] = bin.charAt(16);
        chs[5] = bin.charAt(7);
        chs[6] = bin.charAt(15);
        chs[7] = bin.charAt(0);
        chs[8] = bin.charAt(8);
        chs[9] = bin.charAt(17);
        chs[10] = bin.charAt(9);
        chs[11] = bin.charAt(14);
        chs[12] = bin.charAt(1);
        chs[13] = bin.charAt(10);
        chs[14] = bin.charAt(5);
        chs[15] = bin.charAt(18);
        chs[16] = bin.charAt(4);
        chs[17] = bin.charAt(11);
        chs[18] = bin.charAt(6);
        int code = Integer.parseInt(new String(chs), 2) + 134291;
        return channel + WebRequestConstant.DEFAULT_DATE_FORMAT.format(new java.util.Date()) + code;
    }

    public static String generateLogonPassword() {
        Random random = new Random();
        String code = "";

        for (int i = 0; i < 6; ++i) {
            if (i % 2 == 0) { //偶数位生产随机整数
                code = code + random.nextInt(10);
            } else {//奇数产生随机字母包括大小写
                int temp = random.nextInt(52);
                char x = (char) (temp < 26 ? temp + 97 : (temp % 26) + 65);
                code += x;
            }
        }
        return code;
    }

    public static String generateTransactPassword() {
        StringBuilder builder = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            builder.append(new Random().nextInt(10));
        }
        return builder.toString();
    }

    public static String randomString(int bit) {
        String time = String.format("%x", System.currentTimeMillis());
        String all = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder(time);
        for (int i = time.length(); i < bit; i++) {
            builder.append(all.charAt(new Random().nextInt(all.length())));
        }
        return builder.reverse().toString().substring(0, bit);
    }
    
}
