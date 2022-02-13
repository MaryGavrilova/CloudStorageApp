package ru.netology.cloudstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

// class for getting in request new file name to edit current file name
@AllArgsConstructor
@NoArgsConstructor
@Data
@Validated
public class CloudFileName {
    @NotBlank
    protected String name;
}
