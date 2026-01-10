package com.oneriver.fundsearch.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class FundRowData {

    private String fundCode;
    private String fundName;
    private String umbrellaFundType;
    private Map<String,Double> returnPeriods;

}
