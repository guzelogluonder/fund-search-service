package com.oneriver.fundsearch.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.oneriver.fundsearch.document.FundDocument;
import com.oneriver.fundsearch.dto.FundSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class FundSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public Page<FundSearchResponse> searchFunds(String fundCode, String fundName, String umbrellaFundType, String sortBy, String sortOrder,
                                                int page, int size) {
        try {
            List<Query> queries = createElasticSearchClauses(fundCode, fundName, umbrellaFundType);
            if (queries.isEmpty()) {
                return new PageImpl<>(List.of());
            } else {
                Query mainQuery = buildMustClause(queries);
                NativeQuery searchQuery = createElasticSearchQueryWithSorting(mainQuery, sortBy, sortOrder, page, size);

                SearchHits<FundDocument> hits = elasticsearchOperations.search(searchQuery, FundDocument.class);
                List<FundSearchResponse> results = hits.stream().map(hit -> docToSearchResponse(hit.getContent())).toList();

                return new PageImpl<>(results, PageRequest.of(page, size), hits.getTotalHits());
            }
        } catch (Exception e) {
            log.error("Error searching funds", e);
            throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }
    }

    private List<Query> createElasticSearchClauses(String fundCode, String fundName, String type) {

        List<Query> queries = new ArrayList<>();

        if (StringUtils.hasText(fundCode)) {
            Query fundCodeQuery = Query.of(query -> query.wildcard(doc -> doc.field("fundCode").value("*" + fundCode + "*").caseInsensitive(true)));
            queries.add(fundCodeQuery);
        }

        if (StringUtils.hasText(fundName)) {
            Query fundNameQuery = Query.of(query -> query.wildcard(doc -> doc.field("fundName").value("*" + fundName + "*").caseInsensitive(true)));
            queries.add(fundNameQuery);
        }

        if (StringUtils.hasText(type)) {
            Query umbrellaFundTypeQuery = Query.of(query -> query.term(t -> t.field("umbrellaFundType").value(type)));
            queries.add(umbrellaFundTypeQuery);
        }

        return queries;
    }

    private NativeQuery createElasticSearchQueryWithSorting(Query query, String sortBy, String sortOrder, int page, int size) {
        var builder = NativeQuery.builder().withQuery(query).withPageable(PageRequest.of(page, size));

        if (StringUtils.hasText(sortBy)) {
            SortOrder order = "desc".equalsIgnoreCase(sortOrder) ? SortOrder.Desc : SortOrder.Asc;
            String field;
            if (sortBy.startsWith("returnPeriods.")) {
                field = sortBy;
            } else if ("fundName".equals(sortBy)) {
                throw new RuntimeException("Fund name cannot be sorted");//exception duzenle
            } else {
                field = sortBy;
            }

            builder.withSort(s -> s.field(f -> {
                var fb = f.field(field).order(order);
                if (sortBy.startsWith("returnPeriods.")) fb.missing("_last");
                return fb;
            }));
        }

        return builder.build();
    }

    private Query buildMustClause(List<Query> queries) {
        return Query.of(queryBuilder -> queryBuilder.bool(boolBuilder -> {
                    for (Query query : queries) {
                        boolBuilder.must(query);
                    }
                    return boolBuilder;
                })
        );
    }

    private FundSearchResponse docToSearchResponse(FundDocument doc) {
        return new FundSearchResponse(doc.getFundCode(), doc.getFundName(), doc.getUmbrellaFundType(), doc.getReturnPeriods());
    }
}
