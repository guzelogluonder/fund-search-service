package com.oneriver.fundsearch.service.validator;

import com.oneriver.fundsearch.exception.handler.FundImportException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class ExcelImportValidator implements ExceImportValidator, Ordered {

    @Override
    public void validate(MultipartFile file) {

        log.info("Excel file validation started: {}", file.getOriginalFilename());
        fileIsNotEmpty(file);
        fileIsExcel(file);
        fileSize(file);

    }

    public void fileIsNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("File is empty");
            throw new FundImportException("File is cannot be empty");
        }
    }


    public void fileIsExcel(MultipartFile file){
        if (!file.getOriginalFilename().endsWith(".xlsx") && !file.getOriginalFilename().endsWith(".xls")) {
            log.warn("File is not excel");
            throw new FundImportException("Only Excel files allowed");
        }
    }

    public void fileSize(MultipartFile file) {
        if (file.getSize() > 10 * 1024 * 1024) {
            long permitted = (10 * 1024 * 1024);
            throw new FundImportException("File size limit Exceeded");
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
