package org.example.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.model.UserProjects;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileUserProjectRepository implements UserProjectRepository {

    private final Path storageDir;
    private final ObjectMapper mapper;

    public FileUserProjectRepository(String storageDir) {
        this.storageDir = Path.of(storageDir);

        this.mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            Files.createDirectories(this.storageDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create storage directory", e);
        }
    }

    private File getFile(long chatId) {
        return storageDir.resolve("chat_" + chatId + ".json").toFile();
    }

    @Override
    public void save(long chatId, UserProjects userProjects) {
        try {
            mapper.writeValue(getFile(chatId), userProjects);
        } catch (IOException e) {
            throw new RuntimeException("Cannot save user projects", e);
        }
    }

    @Override
    public Optional<UserProjects> findByChatId(long chatId) {
        File file = getFile(chatId);

        if (!file.exists()) {
            return Optional.empty();
        }

        try {
            return Optional.of(mapper.readValue(file, UserProjects.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(long chatId) {
        try {
            Files.deleteIfExists(getFile(chatId).toPath());
        } catch (IOException e) {
            throw new RuntimeException("Cannot delete file", e);
        }
    }
}