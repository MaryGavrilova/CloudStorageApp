package ru.netology.cloudstorage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column(columnDefinition = "int check(size >-1)")
    protected int size;

    @Lob
    @Column(name = "file", columnDefinition = "BLOB")
    protected byte[] bytes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    protected User user;
}
