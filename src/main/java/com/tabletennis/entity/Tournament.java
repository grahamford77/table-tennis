package com.tabletennis.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Tournament entity representing a table tennis tournament
 */
@Entity
@Table(name = "tournaments")
@Data
@NoArgsConstructor
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Tournament name is required")
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Tournament date is required")
    @Future(message = "Tournament date must be in the future")
    private LocalDate date;

    @Column(nullable = false)
    @NotNull(message = "Tournament time is required")
    private LocalTime time;

    @Column(nullable = false)
    @NotBlank(message = "Location is required")
    private String location;

    @Column(nullable = false)
    @Positive(message = "Maximum entrants must be a positive number")
    private Integer maxEntrants;

    // Add the relationship to registrations
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentRegistration> registrations = new ArrayList<>();

    public Tournament(String name, String description, LocalDate date, LocalTime time, String location, Integer maxEntrants) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
        this.maxEntrants = maxEntrants;
    }
}
