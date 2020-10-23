package com.aixoft.essample.ui.dto;

import com.aixoft.escassandra.model.EventVersion;
import lombok.Value;

@Value
public class ResponseEventVersionDto {
    String version;

    public static ResponseEventVersionDto fromEventVersion(EventVersion eventVersion) {
        return new ResponseEventVersionDto(String.format("%d.%d", eventVersion.getMajor(), eventVersion.getMinor()));
    }
}
