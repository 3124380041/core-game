package org.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request để tạo match mới.
 */
@Data
public class CreateMatchRequest {
    
    @NotNull(message = "Player 1 ID is required")
    private Long player1Id;

    @NotNull(message = "Player 2 ID is required")
    private Long player2Id;
}

