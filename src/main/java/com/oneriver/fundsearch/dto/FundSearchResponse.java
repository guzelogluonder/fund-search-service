package com.oneriver.fundsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundSearchResponse {

    private String fundCode;
    private String fundName;
    private String umbrellaFundType;
    private Map<String,Double> returnPeriods;

}
