package com.oneriver.fundsearch.controller;

import com.oneriver.fundsearch.dto.ExcelImportResponse;
import com.oneriver.fundsearch.model.Fund;
import com.oneriver.fundsearch.service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/import")
@RequiredArgsConstructor
public class ImportController {

    private final ExcelImportService excelImportService;

    @PostMapping("/excel")
    public ResponseEntity<ExcelImportResponse> importExcel(@RequestParam("file") MultipartFile file) {
        ExcelImportResponse message = excelImportService.importExcel(file);
        return ResponseEntity.ok(message);
    }

}
