package com.company.accounter.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum RecordType implements EnumClass<String> {

    USER("A"),
    TOTAL("B");

    private final String id;

    RecordType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RecordType fromId(String id) {
        for (RecordType at : RecordType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}