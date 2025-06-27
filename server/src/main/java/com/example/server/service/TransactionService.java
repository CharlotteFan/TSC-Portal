package com.example.server.service;

import com.example.server.exception.BusinessException;
import com.example.server.model.ExecutionCode;
import com.example.server.model.vo.TransactionSearchVO;
import com.example.server.model.data.Transaction;
import com.example.server.model.vo.TransactionVO;
import com.example.server.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionService {

    // This service will handle transaction-related operations
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        log.info("TransactionService initialized with TransactionRepository");
    }

    @Transactional
    public TransactionVO getTransactionById(Long transactionId, String userId) {
        try {
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new BusinessException(ExecutionCode.NOT_FOUND));

            TransactionVO transactionVO = new TransactionVO();
            BeanUtils.copyProperties(transaction, transactionVO);
            log.info("Transaction detail of " + transactionId + " is retrieved successfully for user: " + userId);
            return transactionVO;
        }catch (Exception e) {
            log.error("Failed to retrieve transaction with id: {}", transactionId, e);
            throw e;
        }
    }

    @Transactional
    public Boolean deleteTransaction(Long transactionId, String userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException(ExecutionCode.NOT_FOUND));

        transactionRepository.deleteById(transaction.getId());
        log.info("Transaction " + transactionId + "gets deleted by user: " + userId);

        return true;
    }

    //to-do search transaction with criteria and pagination
    @Transactional
    public Page<Transaction> searchTransaction(TransactionSearchVO transactionSearchVO) {
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "transactionId");

        Pageable transactionPage = PageRequest.of(
                transactionSearchVO.getPage(),
                transactionSearchVO.getPageSize(),
                Sort.by(order)
        );
        return transactionRepository.findAll(transactionPage);
    }

    @Transactional
    public TransactionVO updateTransaction(TransactionVO transactionVO, String context, String userId) {
        if (ExecutionCode.SUCCESS.getCode() != validateTransaction(transactionVO, context, userId).getCode()) {
            log.error("Transaction validation failed for {} operation by user {}", context, userId);
            throw new BusinessException(ExecutionCode.BUSINESS_ERROR);
        }

        Transaction toUpdate = new Transaction();
        BeanUtils.copyProperties(transactionVO, toUpdate);

        Transaction newTransaction = transactionRepository.save(toUpdate);

        TransactionVO updated = new TransactionVO();
        BeanUtils.copyProperties(updated, newTransaction);

        log.info("Transaction updated successfully by user: {}", userId);
        return updated;
    }

    private ExecutionCode validateTransaction(TransactionVO transaction, String context, String userId) {
        ExecutionCode validator = ExecutionCode.SUCCESS;
        // Perform validation logic here
        if (context != null && !context.isEmpty()) {
            log.info("");
        }

        return validator;
    }
}
