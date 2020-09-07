/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.common.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Json日期序列化工具
 * 
 * @author N. SUN
 * @version 0.0.1
 * @since 	
 */
public class JsonDateSerializer extends JsonSerializer<Date> {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {

        gen.writeString(dateFormat.format(date));
    }

}
