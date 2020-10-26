package com.wiley.test.portablecache.interfaces.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public class LRUFileUtil<K, V> extends FileUtil<K, V> {

    public LRUFileUtil(String cacheType, int cacheSize, Class<V> type) {
        super(cacheType, cacheSize, type);
    }

    @Override
    public V addNewEntryToCache(K key, V value) throws IOException {
        writeToExistingFile(key, value);
        return value;
    }

    @Override
    public V getEntry(K key) throws IOException {
        if (queryForKey(key) != null) {
            return updateCache(key, GET);
        }
        return null;
    }

    @Override
    public V deleteEntry(K key) throws IOException {
        if (queryForKey(key) != null) {
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
            int i = 0;
            if (!cacheFull) {
                while ((currentLine = reader.readLine()) != null) {

                    // remove if the entry is already there and added it to the end
                    if (!currentLine.split(delemeter)[0].equals(keyJson)) {
                        writer.write(currentLine + System.lineSeparator());
                    } else {
                        keyExist = true;
                    }

                }
            } else {
                while ((currentLine = reader.readLine()) != null) {
                    ++i;
                    V entryInFile = queryForKey(key);
                    if (entryInFile == null) {
                        // remove first entry when the cache is full
                        if (i != 1) {
                            writer.write(currentLine + System.lineSeparator());
                        }
                    } else {
                        // remove if the entry is already there and added it to the end
                        if (!currentLine.split(delemeter)[0].equals(keyJson)) {
                            writer.write(currentLine + System.lineSeparator());
                        } else {
                            keyExist = true;
                        }
                    }

                }
            }

            if (!keyExist && !cacheFull) {
                updateCacheMeta(ADD);
            }
            // add new entry
            writer.write(newEntry + System.lineSeparator());
        }
        moveFile(cacheTempFile, cacheFile.getAbsolutePath());

    }
}
