package com.oneriver.fundsearch.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.mockito.Mockito.when;

class FundSearchControllerTest {

    private InputStream excelFile;

    @BeforeEach
    void setUp() {

        excelFile = getClass().getResourceAsStream("src/main/resources/test-funds.xlsx");

    }

    @Test
    void testImportExcel() {

    }

}