package com.oneriver.fundsearch.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;

@Document(indexName = "funds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword) // Exact Match icin
    private String fundCode;

    @Field(type = FieldType.Text) // full text search icin
    private String fundName;

    @Field(type = FieldType.Keyword)
    private String umbrellaFundType;

    @Field(type = FieldType.Object)  // Nested Object
    private Map<String,Double> returnPeriods;
}
