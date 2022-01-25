package ru.netology.cloudstorage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import javax.validation.Valid;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    protected long id;

    @Embedded
    @Valid
    protected Identity identity;

    @Embedded
    @Valid
    protected AuthorizationToken authorizationToken;

    @Column(columnDefinition = "bit(1) default 0")
    protected boolean isTokenActive = false;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "user",
            cascade = CascadeType.ALL)
    protected List<CloudFile> files;
}
