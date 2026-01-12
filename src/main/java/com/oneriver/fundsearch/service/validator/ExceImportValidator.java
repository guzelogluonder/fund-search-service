package com.oneriver.fundsearch.service.validator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExceImportValidator {

    void validate(MultipartFile file) throws InvalidFormatException, IOException;

}
