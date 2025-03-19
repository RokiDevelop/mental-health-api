package com.kiryukhin.mental_health.utils.storages;

public interface BunnyStreamingStorageService extends StreamingStorageService {
    void deleteVideoStreamFile(String videoGuid);
    void deleteAudioStreamFile(String videoGuid);

    String createVideoStreamFile(String title);

    String createAudioStreamFile(String title);

    String uploadVideoFile(String videoGuid, String path);

    String uploadAudioStreamFile(String videoGuid, String path);
}
