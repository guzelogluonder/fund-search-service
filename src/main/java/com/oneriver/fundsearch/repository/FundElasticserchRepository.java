package com.oneriver.fundsearch.repository;

import com.oneriver.fundsearch.document.FundDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundElasticserchRepository extends ElasticsearchRepository<FundDocument, String> {
}
