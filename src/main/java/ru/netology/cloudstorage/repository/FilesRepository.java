package ru.netology.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.model.CloudFile;
import ru.netology.cloudstorage.dto.CloudFileInfo;
import ru.netology.cloudstorage.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilesRepository extends JpaRepository<CloudFile, Long> {

    Optional<CloudFile> findCloudFileByUserAndFilename(User user, String filename);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update CloudFile c set c.filename = :filename where c.id = :id")
    void editFileName(@Param("filename") String filename, @Param("id") long id);

    List<CloudFileInfo> findAllByUser(User user);
}
