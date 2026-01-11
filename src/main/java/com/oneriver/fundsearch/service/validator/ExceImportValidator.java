package com.oneriver.fundsearch.service.validator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.multipart.MultipartFile;

public interface ExceImportValidator {

    void validate(MultipartFile file) throws InvalidFormatException, FileSizeLimitExceededException;

}
