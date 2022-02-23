package ru.netology.cloudstorage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// class for returning in response token
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthorizationToken {
    @JsonProperty("auth-token")
    protected String authToken;
}
