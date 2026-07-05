package com.zqnt.sdk.edge.support;

import com.google.protobuf.Timestamp;
import com.zqnt.utils.common.proto.RequestBase;
import com.zqnt.utils.core.ProtobufHelpers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class MapperSupport {

    private MapperSupport() {
    }

    public static <T> void set(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public static <T> void setIf(BooleanSupplier condition, Consumer<T> setter, Supplier<T> valueSupplier) {
        if (condition.getAsBoolean()) {
            T value = valueSupplier.get();
            if (value != null) {
                setter.accept(value);
            }
        }
    }

    public static String defaultString(String value) {
        return value == null ? "" : value;
    }

    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    public static RequestBase.Builder requestBase(String sn, String tid, LocalDateTime timestamp) {
        return RequestBase.newBuilder()
                .setSn(defaultString(sn))
                .setTid(defaultString(tid))
                .setTimestamp(timestamp != null ? toTimestamp(timestamp) : ProtobufHelpers.now());
    }
}
