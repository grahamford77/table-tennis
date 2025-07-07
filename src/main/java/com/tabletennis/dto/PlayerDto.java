package com.tabletennis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for Player data transfer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {
    private Long id;
    private String firstName;
    private String surname;
    private String email;
    private String fullName;
}
