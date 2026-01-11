package com.oneriver.fundsearch.service.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class ExcelImportValidator implements ExceImportValidator, Ordered {

    @Override
    public void validate(MultipartFile file) throws InvalidFormatException, FileSizeLimitExceededException {

        log.info("Excel file validation started: {}", file.getOriginalFilename());
        fileIsNotEmpty(file);
        fileIsExcel(file);
        fileSize(file);

    }

    public void fileIsNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("File is empty");
            throw new IllegalArgumentException("File is cannot be empty");
        }
    }


    public void fileIsExcel(MultipartFile file) throws InvalidFormatException {
        if (!file.getOriginalFilename().endsWith(".xlsx") && !file.getOriginalFilename().endsWith(".xls")) {
            log.warn("File is not excel");
            throw new InvalidFormatException("Only Excel files allowed");
        }
    }

    public void fileSize(MultipartFile file) throws FileSizeLimitExceededException {
        if (file.getSize() > 10 * 1024 * 1024) {
            long permitted = (10 * 1024 * 1024);
            throw new FileSizeLimitExceededException("File size limit Exceeded", file.getSize(), permitted);
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
