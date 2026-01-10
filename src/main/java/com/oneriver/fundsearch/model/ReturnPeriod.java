package com.oneriver.fundsearch.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "return_periods")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String periodName;

    @Column
    private Double returnValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_id",nullable = false)
    @JsonIgnore
    private Fund fund;

}
