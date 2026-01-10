package com.oneriver.fundsearch.service;

import com.oneriver.fundsearch.document.FundDocument;
import com.oneriver.fundsearch.dto.FundRowData;
import com.oneriver.fundsearch.model.Fund;
import com.oneriver.fundsearch.model.ReturnPeriod;
import com.oneriver.fundsearch.repository.FundElasticserchRepository;
import com.oneriver.fundsearch.repository.FundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelImportService {

    private final FundRepository fundRepository;
    private final FundElasticserchRepository fundElasticserchRepository;

    @Transactional
    public void importExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<FundRowData> fundRowDataList = new ArrayList<>();
            int rowNumber = 0;
            for (Row row : sheet) {
                if (rowNumber < 2) { //header'sa
                    rowNumber++;
                } else {
                    fundRowDataList.add(parseRow(row));
                }
            }
            saveFund(fundRowDataList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FundRowData parseRow(Row row) {
        Map<String, Double> returnPeriods = new HashMap<>();
        returnPeriods.put("1 Ay (%)", convertToDouble(row.getCell(3)));
        returnPeriods.put("3 Ay (%)", convertToDouble(row.getCell(4)));
        returnPeriods.put("6 Ay (%)", convertToDouble(row.getCell(5)));
        returnPeriods.put("Yılbaşı (%)", convertToDouble(row.getCell(6)));
        returnPeriods.put("1 Yıl (%)", convertToDouble(row.getCell(7)));
        returnPeriods.put("3 Yıl (%)", convertToDouble(row.getCell(8)));
        returnPeriods.put("5 Yıl (%)", convertToDouble(row.getCell(9)));
        return FundRowData.builder()
                .fundCode(convertToString(row.getCell(0)))
                .fundName(convertToString(row.getCell(1)))
                .umbrellaFundType(convertToString(row.getCell(2)))
                .returnPeriods(returnPeriods)
                .build();
    }

    public void saveFund(List<FundRowData> dataList) {
        List<Fund> listFundEntities = new ArrayList<>();
        for (FundRowData data : dataList) {
            Fund fund = new Fund();
            fund.setFundCode(data.getFundCode());
            fund.setFundName(data.getFundName());
            fund.setUmbrellaFundType(data.getUmbrellaFundType());
            data.getReturnPeriods().forEach(
                    (periodName, returnValue) -> {
                        ReturnPeriod returnPeriod = new ReturnPeriod();
                        returnPeriod.setPeriodName(periodName);
                        returnPeriod.setReturnValue(returnValue);
                        returnPeriod.setFund(fund);
                        fund.getReturnPeriods().add(returnPeriod);// here
                    });
            listFundEntities.add(fund);
        }

        List<Fund> savedFund = fundRepository.saveAll(listFundEntities);

        List<FundDocument> fundDocuments = new ArrayList<>();
        for (int i = 0; i < savedFund.size(); i++) {
            Fund fund = savedFund.get(i);
            FundDocument doc = new FundDocument();
            doc.setId(fund.getId().toString());
            doc.setFundCode(fund.getFundCode());
            doc.setFundName(fund.getFundName());
            doc.setUmbrellaFundType(fund.getUmbrellaFundType());
            doc.setReturnPeriods(dataList.get(i).getReturnPeriods());
            fundDocuments.add(doc);
        }
        fundElasticserchRepository.saveAll(fundDocuments);

    }

    private String convertToString(Cell cell) {
        if (cell == null) {
            return null;
        }
        try {
            CellType cellType = cell.getCellType();
            if (cellType == CellType.STRING) {
                return cell.getStringCellValue();
            } else if (cellType == CellType.NUMERIC) {
                return String.valueOf((long) cell.getNumericCellValue());
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private Double convertToDouble(Cell cell) {
        if (cell == null) {
            return null;
        }
        try {
            CellType cellType = cell.getCellType();
            if (cellType == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cellType == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? null : Double.parseDouble(value.replace(",", "."));
            } else {
                return null;
            }

        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid number format: " + cell.getStringCellValue());
        }
    }
}
