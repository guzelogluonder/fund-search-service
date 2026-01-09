package com.oneriver.fundsearch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "funds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String fundCode;

    @Column(nullable = false)
    private String fundName;

    @Column(nullable = false)
    private String umbrellaFundType;

    @OneToMany(mappedBy = "fund",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnPeriod> returnPeriods = new ArrayList<>();


}
