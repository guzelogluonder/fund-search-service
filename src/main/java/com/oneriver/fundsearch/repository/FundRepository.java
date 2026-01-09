package com.oneriver.fundsearch.repository;

import com.oneriver.fundsearch.model.Fund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FundRepository extends JpaRepository<Fund, Long> {
    Optional<Fund> findByFundCode(String fundCode);
}
