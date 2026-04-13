package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request để tạo player mới.
 */
@Data
public class CreatePlayerRequest {
    
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Name is required")
    private String name;

    private String password;
}

