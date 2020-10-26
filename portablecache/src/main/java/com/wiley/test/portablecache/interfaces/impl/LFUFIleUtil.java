package com.wiley.test.portablecache.interfaces.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiley.test.portablecache.exception.CacheException;
import com.wiley.test.portablecache.exception.ErrorCode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LFUFIleUtil<K, V> extends FileUtil<K, V> {

    private String countKeysFileName;
    private String countKeysTempFileName;

    public LFUFIleUtil(String cacheType, int cacheSize, Class<V> type) {
        super(cacheType, cacheSize, type);
        countKeysFileName = folderPath + File.separator + cacheType + "-count.txt";
        countKeysTempFileName = folderPath + File.separator + cacheType + "-tmp-count.txt";

        File countFile = new File(countKeysFileName);
        if (!countFile.exists()) {
            try {
                countFile.createNewFile();
            } catch (IOException e) {
                throw new CacheException(ErrorCode.ERROR_CREATING_CACHE_FILE);
            }
        }
    }

    @Override
    public V addNewEntryToCache(K key, V value) throws IOException {
        writeToExistingFile(key, value);
        return value;
    }

    @Override
    public V getEntry(K key) throws IOException {
        if (queryForKey(key) != null) {
            updateCountFile(true, new ObjectMapper().writeValueAsString(key), GET);
            return updateCache(key, GET);
        }
        return null;
    }

    @Override
    public V deleteEntry(K key) throws IOException {
        if (queryForKey(key) != null) {
            V value = queryForKey(key);
            updateCountFile(true, new ObjectMapper().writeValueAsString(key), DELETE);
            return updateCache(key, DELETE);
        }
        return null;
    }

    private void writeToExistingFile(K key, V value) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        File cacheFile = new File(cacheFileName);
        String newEntry = mapper.writeValueAsString(key) + delemeter + mapper.writeValueAsString(value);
        String cacheTempFileName = folderPath + File.separator + "tmp-" + cacheType + ".txt";
        File cacheTempFile = new File(cacheTempFileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(cacheFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(cacheTempFile));) {
            String currentLine;
            String keyJson = mapper.writeValueAsString(key);
            boolean keyExist = false;
            boolean cacheFull = isCacheFull();
            if (!cacheFull) {
                while ((currentLine = reader.readLine()) != null) {
                    if (!currentLine.split(delemeter)[0].equals(keyJson)) {
                        writer.write(currentLine + System.lineSeparator());
                    } else {
                        keyExist = true;
                    }
                }
            } else {
                V entryInFile = queryForKey(key);
                if (entryInFile == null) {
                    String keyToBeDeleted = getKeyToBeDeleted();
                    if (keyToBeDeleted != null) {
                        updateCountFile(true, keyToBeDeleted, DELETE);
                    }
                    while ((currentLine = reader.readLine()) != null) {
                        if (!currentLine.split(delemeter)[0].equals(keyToBeDeleted)) {
                            writer.write(currentLine + System.lineSeparator());
                        }
                    }
                } else {
                    while ((currentLine = reader.readLine()) != null) {
                        if (!currentLine.split(delemeter)[0].equals(keyJson)) {
                            writer.write(currentLine + System.lineSeparator());
                        } else {
                            keyExist = true;
                        }
                    }
                }
            }
            updateCountFile(keyExist, keyJson, ADD);
            if (!keyExist && !cacheFull) {
                updateCacheMeta(ADD);
            }
            // add new entry
            writer.write(newEntry + System.lineSeparator());
        }
        moveFile(cacheTempFile, cacheFile.getAbsolutePath());

    }

    private void updateCountFile(boolean keyExist, String keyJson, String action)
            throws FileNotFoundException, IOException {
        File countKeysFile = new File(countKeysFileName);
        File countKeysTempFile = new File(countKeysTempFileName);
        String currentLine;
        Integer countInFile = 0;
        try (BufferedReader countReader = new BufferedReader(new FileReader(countKeysFile));
             BufferedWriter countWriter = new BufferedWriter(new FileWriter(countKeysTempFile));) {
            if (keyExist) {
                if (!DELETE.equals(action)) {
                    while ((currentLine = countReader.readLine()) != null) {
                        String[] keyValuePair = currentLine.split(delemeter);
                        if (keyJson.equals(keyValuePair[0])) {
                            countInFile = Integer.parseInt(keyValuePair[1]);
                        } else {
                            countWriter.write(currentLine + System.lineSeparator());
                        }
                    }
                    countWriter.append(keyJson + delemeter + (++countInFile) + System.lineSeparator());
                } else {
                    while ((currentLine = countReader.readLine()) != null) {
                        String[] keyValuePair = currentLine.split(delemeter);
                        if (!keyJson.equals(keyValuePair[0])) {
                            countWriter.write(currentLine + System.lineSeparator());
                        }
                    }
                }
            } else {
                while ((currentLine = countReader.readLine()) != null) {
                    countWriter.write(currentLine + System.lineSeparator());
                }
                if (!DELETE.equals(action)) {
                    countWriter.append(keyJson + delemeter + "1" + System.lineSeparator());
                }
            }
        }
        moveFile(countKeysTempFile, countKeysFile.getAbsolutePath());

    }

    private String getKeyToBeDeleted() throws FileNotFoundException, IOException {
        File countKeysFile = new File(countKeysFileName);
        String currentLine;
        int minCount = 0;
        String keyJsonToBeDeleted = null;
        try (BufferedReader countReader = new BufferedReader(new FileReader(countKeysFile))) {
            currentLine = countReader.readLine();
            minCount = Integer.parseInt(currentLine.split(delemeter)[1]);
            keyJsonToBeDeleted = currentLine.split(delemeter)[0];
            while ((currentLine = countReader.readLine()) != null) {
                String[] keyValuePair = currentLine.split(delemeter);
                if (Integer.parseInt(keyValuePair[1]) < minCount) {
                    minCount = Integer.parseInt(keyValuePair[1]);
                    keyJsonToBeDeleted = keyValuePair[0];
                }
            }
        }
        return keyJsonToBeDeleted;
    }

    @Override
    public void clearCache() throws FileNotFoundException, IOException {
        super.clearCache();
        File countKeysFile = new File(countKeysFileName);
        Path path = Paths.get(countKeysFile.getAbsolutePath());
        Files.delete(path);
    }
}
