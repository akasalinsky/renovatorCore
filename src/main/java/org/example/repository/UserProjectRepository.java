package org.example.repository;

import org.example.model.User;
import org.example.model.UserProjects;

import java.util.Optional;

public interface UserProjectRepository {
    void save(Long chatId, UserProjects userProjects);
    Optional<UserProjects> findByChatId(long chatId);
    void delete(long chatId1);
}
