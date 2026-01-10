package com.oneriver.fundsearch.controller;


import com.oneriver.fundsearch.dto.FundSearchResponse;
import com.oneriver.fundsearch.service.FundSearchService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/funds")
@RequiredArgsConstructor
@Validated
@Slf4j
public class FundSearchController {

    private final FundSearchService fundSearchService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchFunds(@RequestParam(required = false) String query,
                                                           @RequestParam(required = false) String umbrellaFundType,
                                                           @RequestParam(required = false) String returnPeriod,
                                                           @RequestParam(required = false) Double minReturn,
                                                           @RequestParam(required = false) Double maxReturn,
                                                           @RequestParam(required = false) String sortBy,
                                                           @RequestParam(required = false, defaultValue = "asc") String sortOrder,
                                                           @RequestParam(defaultValue = "0") @Min(0) int page,
                                                           @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        Page<FundSearchResponse> results = fundSearchService.searchFunds(
                query, umbrellaFundType, returnPeriod,
                minReturn, maxReturn, sortBy, sortOrder, page, size
        );

        Map<String, Object> response = new HashMap<>();
        response.put("content", results.getContent());
        response.put("pagination", Map.of(
                "currentPage", results.getNumber(),
                "totalPages", results.getTotalPages(),
                "totalElements", results.getTotalElements(),
                "pageSize", results.getSize(),
                "hasNext", results.hasNext(),
                "hasPrevious", results.hasPrevious(),
                "isFirst", results.isFirst(),
                "isLast", results.isLast()
        ));

        return ResponseEntity.ok(response);
    }
}
