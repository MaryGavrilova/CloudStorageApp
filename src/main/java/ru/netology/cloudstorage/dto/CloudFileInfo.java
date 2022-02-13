package ru.netology.cloudstorage.dto;

import lombok.Value;

// Class-Based Projection
// class for returning in response file list
@Value
public class CloudFileInfo {
    String filename;
    long size;
}
