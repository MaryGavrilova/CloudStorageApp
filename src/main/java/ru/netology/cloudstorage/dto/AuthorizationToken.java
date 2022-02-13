package ru.netology.cloudstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// class for returning in response token
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthorizationToken {
    protected String authToken;
}
