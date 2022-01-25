package ru.netology.cloudstorage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
@Validated
public class Identity implements Serializable {
    @NotBlank
    @Column(unique = true, nullable = false)
    protected String login;

    @NotBlank
    @Column(nullable = false)
    protected String password;
}