package com.kiryukhin.mental_health.utils.fileConverters;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class AudioProcessingService {

    public void convertAudioToHls(String inputFilePath, String outputDir) throws IOException, InterruptedException {
        new File(outputDir).mkdirs();

        String command = String.format("ffmpeg -i %s -c:a aac -b:a 128k -hls_time 10 -hls_playlist_type vod %s/output.m3u8",
                inputFilePath, outputDir);
        ProcessBuilder builder = new ProcessBuilder(command.split(" "));
        Process process = builder.inheritIO().start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg audio conversion failed with exit code " + exitCode);
        }
    }
}
