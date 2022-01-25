package ru.netology.cloudstorage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
@Validated
public class AuthorizationToken implements Serializable {
    //ВОПРОС: в спецификации имя поле выглядит как auth-token (272 строка), но я не могу здесь использовать дефис, как быть?
    @Column(unique = true, nullable = false)
    protected String authToken;
}
