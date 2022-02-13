package ru.netology.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.model.BlackListToken;

import java.util.List;

@Repository
public interface BlackListTokensRepository extends JpaRepository<BlackListToken, Long> {

    List<BlackListToken> findAllByToken(String token);
}
