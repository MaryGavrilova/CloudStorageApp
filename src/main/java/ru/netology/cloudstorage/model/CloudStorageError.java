package ru.netology.cloudstorage.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CloudStorageError {
    protected String message;
    protected int id;
}
