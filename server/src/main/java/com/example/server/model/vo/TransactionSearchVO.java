package com.example.server.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionSearchVO {
    private String type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String status;

    private int page = 0;
    private int pageSize = 50;
    private String sortBy;
}
