package org.notes.common.service;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.notes.common.configuration.Configuration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CustomDateSerializer extends JsonSerializer<Date> {

    private TimeZone tz = TimeZone.getTimeZone("UTC");
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Configuration.getStringValue(Configuration.REST_TIME_PATTERN, "yyyy-MM-dd'T'HH:mmZ"));

    @Override
    public void serialize(Date date, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        dateFormat.setTimeZone(tz);
        jgen.writeString(dateFormat.format(date));
    }
}
