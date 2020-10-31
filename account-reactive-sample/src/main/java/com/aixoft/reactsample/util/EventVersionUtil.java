package com.aixoft.reactsample.util;

import com.aixoft.escassandra.model.EventVersion;
import com.aixoft.reactsample.exception.UnexpectedEventVersionException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventVersionUtil {
    public static EventVersion parseEventVersion(String eventVersionStr) {
        String[] versions = eventVersionStr.split("\\.");

        if(versions.length != 2) {
            throw new UnexpectedEventVersionException(String.format("Event format shall follow 'major.minor' but is '%s'", eventVersionStr));
        }

        return new EventVersion(Integer.parseInt(versions[0]), Integer.parseInt(versions[1]));
    }
}
