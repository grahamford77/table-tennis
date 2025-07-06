package com.tabletennis.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Tournament entity representing a table tennis tournament
 */
@Entity
@Table(name = "tournaments")
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

    // Constructors
    public Tournament() {}

    public Tournament(String name, String description, LocalDate date, LocalTime time, String location, Integer maxEntrants) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
        this.maxEntrants = maxEntrants;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMaxEntrants() {
        return maxEntrants;
    }

    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
    }

    /**
     * Get display name for tournament dropdown showing date and name
     */
    public String getDisplayName() {
        return String.format("%s - %s",
            date.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")),
            name);
    }
}
