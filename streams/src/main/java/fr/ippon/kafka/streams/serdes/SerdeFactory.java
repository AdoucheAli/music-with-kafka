package fr.ippon.kafka.streams.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.WindowedDeserializer;
import org.apache.kafka.streams.kstream.internals.WindowedSerializer;

import java.util.Map;

public class SerdeFactory {
    public static <T> Serde<T> createSerde(Class<T> clazz, Map<String, Object> serdeProps) {
        Serializer<T> serializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", clazz);
        serializer.configure(serdeProps, false);

        Deserializer<T> deserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", clazz);
        deserializer.configure(serdeProps, false);

        return Serdes.serdeFrom(serializer, deserializer);
    }


}
