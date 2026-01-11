package com.oneriver.fundsearch.service.validator;


import org.springframework.web.multipart.MultipartFile;

public interface ExceImportValidator {

    void validate(MultipartFile file);

}
