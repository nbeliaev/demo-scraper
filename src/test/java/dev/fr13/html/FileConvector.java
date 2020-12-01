package dev.fr13.html;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileConvector {

    public static String getFileContentAsString(String filePath) {
        return getHtmlString(filePath, StandardCharsets.UTF_8);
    }

    public static String getFileContentAsString(String filePath, Charset charset) {
        return getHtmlString(filePath, charset);
    }

    @NotNull
    private static String getHtmlString(String filePath) {
        var path = Paths.get(filePath);
        try {
            var bytes = Files.readAllBytes(path);
            return convertBytesToString(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(String.format("failed to read %s", filePath));
        }
    }

    @NotNull
    private static String getHtmlString(String filePath, Charset charset) {
        var path = Paths.get(filePath);
        try {
            var bytes = Files.readAllBytes(path);
            return convertBytesToString(bytes, charset);
        } catch (IOException e) {
            throw new RuntimeException(String.format("failed to read %s", filePath));
        }
    }

    @NotNull
    private static String convertBytesToString(byte[] bytes, Charset charset) {
        return new String(bytes, charset);
    }
}
