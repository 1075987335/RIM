package com.example.common.serializer;

public interface Serializer {

    byte[] serialize(Object obj);

    <T> T deserialize(byte[] bytes,Class<T> clazz);
}
