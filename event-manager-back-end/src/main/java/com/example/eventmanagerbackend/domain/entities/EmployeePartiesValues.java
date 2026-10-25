package com.example.eventmanagerbackend.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePartiesValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_type_id", nullable = false)
    private EmployeeType employeeType;

    @ManyToOne
    @JoinColumn(name = "party_id")
    @JsonIgnore
    private Party party;

    @Column(nullable = false)
    private BigDecimal value;

    public EmployeePartiesValues(
            EmployeeType employeeType,
            Party party,
            BigDecimal value
    ) {
        this.employeeType = employeeType;
        this.party = party;
        this.value = value;
    }

}
