package com.tabletennis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO for creating/updating tournaments
 */
@Data
public class TournamentRequest {

    @NotBlank(message = "Tournament name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Time is required")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Maximum entrants is required")
    @Positive(message = "Maximum entrants must be positive")
    private Integer maxEntrants;
}
