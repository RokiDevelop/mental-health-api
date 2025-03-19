package com.kiryukhin.mental_health.models.courses;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum VideoResolutionEnum {
    P2160 ("2160p","4K", 2160, "3840:2160"),
    P1440 ("1440p", "2K", 1440, "2560:1440"),
    P1080 ("1080p", "Full HD", 1080, "1920:1080"),
    P720 ("720p", "HD", 720, "1280:720"),
    P480 ("480p", "SD", 480, "848:480");

    private final String strResolution;
    private final String format;
    private final Integer intValue;
    private final String scale;

    VideoResolutionEnum(String strResolution, String format, Integer intValue, String scale) {
        this.strResolution = strResolution;
        this.format = format;
        this.intValue = intValue;
        this.scale = scale;
    }

    public static VideoResolutionEnum fromString(String strResolution) {
        for (VideoResolutionEnum resolution : VideoResolutionEnum.values()) {
            if (resolution.strResolution.equalsIgnoreCase(strResolution)) {
                return resolution;
            }
        }
        throw new IllegalArgumentException("Unknown resolution: " + strResolution);
    }
}
