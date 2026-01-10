package com.oneriver.fundsearch.service;

import com.oneriver.fundsearch.document.FundDocument;
import com.oneriver.fundsearch.dto.FundRowData;
import com.oneriver.fundsearch.model.Fund;
import com.oneriver.fundsearch.model.ReturnPeriod;
import com.oneriver.fundsearch.repository.FundElasticserchRepository;
import com.oneriver.fundsearch.repository.FundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelImportService {

    private final FundRepository fundRepository;
    private final FundElasticserchRepository fundElasticserchRepository;

    public void importExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = 0;

            for (Row row : sheet) {
                if (++rowNumber <= 2) continue; //header'sa

                saveFund(parseRow(row));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
        public FundRowData parseRow(Row row) {
        Map<String,Double> returnPeriods = new HashMap<>();
        returnPeriods.put("1 Ay (%)", getDouble(row.getCell(3)));
        returnPeriods.put("3 Ay (%)", getDouble(row.getCell(4)));
        returnPeriods.put("6 Ay (%)", getDouble(row.getCell(5)));
        returnPeriods.put("Yılbaşı (%)", getDouble(row.getCell(6)));
        returnPeriods.put("1 Yıl (%)", getDouble(row.getCell(7)));
        returnPeriods.put("3 Yıl (%)", getDouble(row.getCell(8)));
        returnPeriods.put("5 Yıl (%)", getDouble(row.getCell(9)));
        return FundRowData.builder()
                .fundCode(getString(row.getCell(0)))
                .fundName(getString(row.getCell(1)))
                .umbrellaFundType(getString(row.getCell(2)))
                .returnPeriods(returnPeriods)
                .build();
    }

    public void saveFund(FundRowData data){
        Fund fund = fundRepository.findByFundCode(data.getFundCode()).orElse(new Fund());
        fund.setFundCode(data.getFundCode());
        fund.setFundName(data.getFundName());
        fund.setUmbrellaFundType(data.getUmbrellaFundType());
        fund.getReturnPeriods().clear();

        Fund finalFund = fund;
        data.getReturnPeriods().forEach((period, value) ->{
            ReturnPeriod returnPeriod = new ReturnPeriod();
            returnPeriod.setPeriodName(period);
            returnPeriod.setReturnValue(value);
            finalFund.addReturnPeriod(returnPeriod);
        });

        fund = fundRepository.save(fund);

        FundDocument doc = new FundDocument();
        doc.setId(fund.getId().toString());
        doc.setFundCode(data.getFundCode());
        doc.setFundName(data.getFundName());
        doc.setUmbrellaFundType(data.getUmbrellaFundType());
        doc.setReturnPeriods(data.getReturnPeriods());
        fundElasticserchRepository.save(doc);

    }

    private String getString(Cell cell){
        if(cell == null) return null;
        try{
            return switch (cell.getCellType()){
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> String.valueOf((long)cell.getNumericCellValue());
                default -> null;
            };
        }catch (Exception e){
            return null;
        }
    }

    private Double getDouble(Cell cell){
        if(cell == null) return null;
        try {
            return switch (cell.getCellType()){
                case NUMERIC -> cell.getNumericCellValue();
                case STRING -> {
                    String value = cell.getStringCellValue().trim();
                    yield value.isEmpty() ? null : Double.parseDouble(value.replace(",","."));
                }
                default -> null;
            };
        }catch (Exception e){
            return null;
        }
    }
}
