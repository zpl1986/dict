/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.common.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * 过滤字符
 * @author zhongpeiliang
 * @version 0.0.1
 * @since
 */
public class FilterCharUtil {

    /**
     * 过滤掉超过3个字节的UTF8字符
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     */

    public static String filterOffUtf8Mb4(String text) {
        try {
            if (null == text) {
                return text;
            }
            byte[] bytes = text.getBytes("utf-8");
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            int i = 0;
            while (i < bytes.length) {
                short b = bytes[i];
                if (b > 0) {
                    buffer.put(bytes[i++]);
                    continue;
                }

                b += 256; // 去掉符号位

                if (((b >> 5) ^ 0x6) == 0) {
                    buffer.put(bytes, i, 2);
                    i += 2;
                } else if (((b >> 4) ^ 0xE) == 0) {
                    buffer.put(bytes, i, 3);
                    i += 3;
                } else if (((b >> 3) ^ 0x1E) == 0) {
                    i += 4;
                } else if (((b >> 2) ^ 0x3E) == 0) {
                    i += 5;
                } else if (((b >> 1) ^ 0x7E) == 0) {
                    i += 6;
                } else {
                    buffer.put(bytes[i++]);
                }
            }
            buffer.flip();
            return new String(buffer.array(), "utf-8").trim();
        } catch (Exception e) {
            return text;
        }
    }

    public static boolean isStringAllUtf8(String text) {
        if (null == text) {
            return true;
        }
        return text.equals(filterOffUtf8Mb4(text));
    }
}