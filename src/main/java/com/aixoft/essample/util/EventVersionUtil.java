package com.aixoft.essample.util;

import com.aixoft.escassandra.model.EventVersion;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventVersionUtil {
    public static EventVersion parseEventVersion(String eventVersionStr) {
        String[] versions = eventVersionStr.split("\\.");

        if(versions.length != 2) {
            //throws
        }

        return new EventVersion(Integer.parseInt(versions[0]), Integer.parseInt(versions[1]));
    }
}
