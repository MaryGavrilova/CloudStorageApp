package ru.netology.cloudstorage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

// table with black list tokens
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tokens")
public class BlackListToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Column(nullable = false)
    protected String token;
}
