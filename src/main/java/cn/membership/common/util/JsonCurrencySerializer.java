/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.common.util;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Json货币序列化工具
 * 
 * @author N. SUN
 * @version 0.0.1
 * @since 	
 */
public class JsonCurrencySerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(Double currency, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {

        BigDecimal bigDecimal = new BigDecimal(currency);
        gen.writeNumber(bigDecimal.setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
    }

}
