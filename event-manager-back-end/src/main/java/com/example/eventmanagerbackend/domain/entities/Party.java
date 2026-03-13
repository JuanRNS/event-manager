package com.example.eventmanagerbackend.domain.entities;

import com.example.eventmanagerbackend.domain.enums.StatusParty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Party {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String location;
    
    @Column(name = "name_client", nullable = false)
    private String nameClient;
    
    @Column(nullable = false)
    private LocalDateTime date;

    @Column(name = "status")
    private StatusParty status = StatusParty.AGENDADA;

    @Column(name = "number_of_people", nullable = false)
    private Long numberOfPeople;

    @Column(name = "hour_start", nullable = false)
    private String hourStart;

    @Column(name = "hour_end", nullable = false)
    private String hourEnd;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeePartiesValues> values = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "employee_parties",
            joinColumns = @JoinColumn(name = "party_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> partyEmployees = new ArrayList<>();

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "material_id")
    private Material material;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public void addValues(EmployeePartiesValues employeePartiesValues){
        values.add(employeePartiesValues);
    }

}
