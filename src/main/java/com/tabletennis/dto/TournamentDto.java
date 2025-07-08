package com.tabletennis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for Tournament data transfer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private Integer maxEntrants;
    private String displayName;
    private List<RegistrationDto> registrations;
    private Integer currentRegistrations;
    private boolean isStarted;
    private boolean isFull;
}
