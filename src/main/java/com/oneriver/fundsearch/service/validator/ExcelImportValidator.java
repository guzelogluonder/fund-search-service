package com.oneriver.fundsearch.service.validator;

import com.oneriver.fundsearch.exception.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class ExcelImportValidator implements ExceImportValidator, Ordered {

    @Override
    public void validate(MultipartFile file) throws IOException {

        log.info("Excel file validation started: {}", file.getOriginalFilename());
        fileIsNotEmpty(file);
        fileIsExcel(file);
        fileSize(file);

    }

    public void fileIsNotEmpty(MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Workbook workbook = WorkbookFactory.create(is);

        Sheet sheet = workbook.getSheetAt(0);

        if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
            log.warn("File is empty");
            throw new InvalidFileException("File cannot be empty");
        }
    }


    public void fileIsExcel(MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".xlsx") && !file.getOriginalFilename().endsWith(".xls")) {
            log.warn("File is not excel");
            throw new InvalidFileException("Only Excel files allowed");
        }
    }

    public void fileSize(MultipartFile file) {
        if (file.getSize() > 10 * 1024 * 1024) {
            long permitted = (10 * 1024 * 1024);
            throw new InvalidFileException("File size limit Exceeded");
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
