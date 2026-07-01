package org.example.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.model.UserProjects;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileUserProjectRepository implements UserProjectRepository {
    private final Map<Long, UserProjects> cache = new ConcurrentHashMap<>();
    private final File file;
    private final ObjectMapper mapper;

    public FileUserProjectRepository(String filePath, long chatId) {
        this.file = new File(filePath);
        this.mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //loadData(chatId);
    }

    /*private void loadData(long chatId) {
        if (file.exists()) {
            try {
                Room[] room = mapper.readValue(file, Room[].class);
                UserProjects newUserProject = new UserProjects()

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    private void saveData() {
        try {
            mapper.writeValue(file, cache);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Long chatId, UserProjects userProjects) {
        cache.put(chatId, userProjects);
        saveData();
    }

    @Override
    public Optional<UserProjects> findByChatId(long chatId) {
        return Optional.ofNullable(cache.get(chatId));
    }

    @Override
    public void delete(long chatId){
        cache.remove(chatId);
    }

}