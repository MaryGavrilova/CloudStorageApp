package ru.netology.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.model.User;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByIdentityLoginAndIdentityPassword(String login, String password);

    Optional<User> findUserByAuthorizationTokenAuthToken(String authToken);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query ("update User u set u.isTokenActive = true where u.id = :id")
    void activateUserToken(@Param("id") long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query ("update User u set u.isTokenActive = false where u.id = :id")
    void deactivateUserToken(@Param("id") long id);
}
