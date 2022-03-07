package ru.netology.cloudstorage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

// table with files' information
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "files")
public class CloudFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Column(nullable = false)
    protected String filename;

    @Column(nullable = false)
    protected String originalFilename;

    @Column(nullable = false)
    protected String contentType;

    @Column(columnDefinition = "BIGINT check(size >-1)")
    protected long size;

    @Lob
    @Column(name = "file", columnDefinition = "MEDIUMBLOB")
    protected byte[] bytes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    protected User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CloudFile cloudFile = (CloudFile) o;
        return id == cloudFile.id && size == cloudFile.size && filename.equals(cloudFile.filename) && Objects.equals(originalFilename, cloudFile.originalFilename) && Objects.equals(contentType, cloudFile.contentType) && Arrays.equals(bytes, cloudFile.bytes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, filename, originalFilename, contentType, size);
        result = 31 * result + Arrays.hashCode(bytes);
        return result;
    }

    @Override
    public String toString() {
        return "CloudFile{" +
                "filename='" + filename + '\'' +
                ", originalFilename='" + originalFilename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                '}';
    }
}
