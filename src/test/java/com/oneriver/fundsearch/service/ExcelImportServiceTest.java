package com.oneriver.fundsearch.service;

import com.oneriver.fundsearch.dto.ExcelImportResponse;
import com.oneriver.fundsearch.repository.FundElasticserchRepository;
import com.oneriver.fundsearch.repository.FundRepository;
import com.oneriver.fundsearch.service.validator.ValidationService;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ExcelImportServiceTest {
    @Mock
    private FundRepository fundRepository;
    @Mock
    private FundElasticserchRepository fundElasticserchRepository;
    @Mock
    private ValidationService validationService;

    @InjectMocks
    private ExcelImportService excelImportService;

    @Test
    void importExcel_callsValidation_andSaves() throws Exception {
        MultipartFile file = new MockMultipartFile(
                "file", "unit-test-funds.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createExcelBytes()
        );

        ExcelImportResponse result = excelImportService.importExcel(file);
        assertThat(result).isNotNull();
        verify(validationService).validate(file);
        verify(fundRepository).saveAll(anyList());
        verify(fundElasticserchRepository).saveAll(anyList());

    }

    @Test
    void parseRow() {
    }

    @Test
    void saveFund() {
        //TODO
    }

    private byte[] createExcelBytes() throws Exception {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Sheet1");

            // header rows (ignored)
            sheet.createRow(0).createCell(0, CellType.STRING).setCellValue("H1");
            sheet.createRow(1).createCell(0, CellType.STRING).setCellValue("H2");

            // data row
            Row r = sheet.createRow(2);
            r.createCell(0, CellType.STRING).setCellValue("HDH");
            r.createCell(1, CellType.STRING).setCellValue("HEDEF PORTFÖY DOĞU HİSSE SENEDİ SERBEST (TL) FON (HİSSE SENEDİ YOĞUN FON)");
            r.createCell(2, CellType.STRING).setCellValue("Serbest Şemsiye Fonu");
            r.createCell(3, CellType.NUMERIC).setCellValue(1.8862);
            r.createCell(4, CellType.NUMERIC).setCellValue(32.5782);
            r.createCell(5, CellType.NUMERIC).setCellValue(378.3816);
            r.createCell(6, CellType.NUMERIC).setCellValue(984.2469);
            r.createCell(7, CellType.NUMERIC).setCellValue(1031.3836);
            r.createCell(8, CellType.NUMERIC).setCellValue(1000.0729);
            r.createCell(9, CellType.NUMERIC).setCellValue(14753.1856);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            wb.write(bos);
            return bos.toByteArray();
        }
    }
}