package io.github.kusoroadeolu.astronaut;

import io.github.kusoroadeolu.astronaut.exceptions.IndexPersistenceException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtils {

    public static byte[] compress(String content) {
        var outputStream = new ByteArrayOutputStream();
        try (var gzip = new GZIPOutputStream(outputStream)) {
            gzip.write(content.getBytes(StandardCharsets.UTF_8));
        }  catch (IOException e) {
            throw new IndexPersistenceException("Failed to compress content to index file", e);
        }
        return outputStream.toByteArray();
    }

    public static String decompress(byte[] compressed)  {
        try (var gzip = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            return new String(gzip.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IndexPersistenceException("Failed to decompress content from index file", e);
        }
    }

    public static String compressToBase64(String content) {
        return Base64.getEncoder().encodeToString(compress(content));
    }

    public static String hash(String content) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static String decompressFromBase64(String base64) {
        return decompress(Base64.getDecoder().decode(base64));
    }
}