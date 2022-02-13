package ru.netology.cloudstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// class for returning in response error
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CloudStorageError {
    protected int id;
    protected String message;
}
