package com.example.server.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionVO {

    private Long id;
    private String type;
    private BigDecimal amount;
    private String transactionDate;
    private String transactionDescription;
    private String debitAccount;
    private String creditAccount;
    private String status;
    private String lastUpdated;
    private String currency;
    private Long submittedBy;
    private String submittedAt;
    private Long approvedBy;
    private String approvedAt;
}
