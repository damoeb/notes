package org.notes.common.endpoints;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.notes.common.configuration.Configuration;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CustomDateDeserializer extends JsonDeserializer<Date> {

    private TimeZone tz = TimeZone.getTimeZone("UTC");
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Configuration.getStringValue(Configuration.REST_TIME_PATTERN, Configuration.DATE_FORMAT_PATTERN));

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            dateFormat.setTimeZone(tz);
            return dateFormat.parse(jsonParser.getText());
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }
}
