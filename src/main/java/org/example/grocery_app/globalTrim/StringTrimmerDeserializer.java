package org.example.grocery_app.globalTrim;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class StringTrimmerDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        String value = jsonParser.getText();
        return value != null ? value.trim() : null;
    }
}