package com.oneriver.fundsearch.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {

    private final List<ExceImportValidator> excelImportValidators;

    public void validate(MultipartFile file) {
        log.info("Excel file validation started: {}", file.getOriginalFilename());

        excelImportValidators.forEach(validator -> {
            try {
                validator.validate(file);
            } catch (FileSizeLimitExceededException e) {
                throw new RuntimeException(e);
            }
        });

        log.info("Excel file validation completed: {}", file.getOriginalFilename());
    }

}
