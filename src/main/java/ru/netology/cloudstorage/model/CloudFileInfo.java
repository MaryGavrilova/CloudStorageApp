package ru.netology.cloudstorage.model;

import lombok.Value;

//Class-Based Projection
@Value
public class CloudFileInfo {
    String filename;
    int size;
}
