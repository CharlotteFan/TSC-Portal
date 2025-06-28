package com.example.server.service;

import com.example.server.exception.BusinessException;
import com.example.server.model.ExecutionCode;
import com.example.server.model.vo.TransactionSearchVO;
import com.example.server.model.data.Transaction;
import com.example.server.model.vo.TransactionVO;
import com.example.server.repository.TransactionRepository;
import com.example.server.util.Constants;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    //todo: cache the search results
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

    //Todo
    @Transactional
    public TransactionVO approveTransaction(TransactionVO transactionVO, String context, String userId) {
        // Validate the transaction before adding
        ExecutionCode validation = validateTransaction(transactionVO, context, userId);
        if (ExecutionCode.SUCCESS.getCode() != validation.getCode()) {
            log.error("Transaction validation failed for {} operation by user {}", context, userId);
            throw new BusinessException(validation);
        }

        String status = transactionVO.getStatus();
        // if the context is approve, then status will be set to APPROVED, if context is cancel or reject, then status will be set to CANCELLED or REJECTED
        status = switch (context) {
            case Constants.TX_CONTEXT_APPROVE -> Constants.TX_STATUS_APPROVED;
            case Constants.TX_CONTEXT_CANCEL -> Constants.TX_STATUS_CANCELLED;
            case Constants.TX_CONTEXT_REJECT -> Constants.TX_STATUS_REJECTED;
            default -> status; // 保持原状态
        };

        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(transactionVO, transaction);
        transaction.setStatus(status);
        transaction.setApprovedBy(userId);
        transaction.setApprovedAt(LocalDateTime.now());

        transaction.setLastUpdated( LocalDateTime.now());
        Transaction saved = transactionRepository.save(transaction);

        TransactionVO result = new TransactionVO();
        BeanUtils.copyProperties(saved, result);
        return result;
    }

    @Transactional
    public TransactionVO updateTransaction(TransactionVO transactionVO, String context, String userId) {
        ExecutionCode validation = validateTransaction(transactionVO, context, userId);
        if (ExecutionCode.SUCCESS.getCode() != validateTransaction(transactionVO, context, userId).getCode()) {
            log.error("Transaction validation failed for {} operation by user {}", context, userId);
            throw new BusinessException(validation);
        }

        Transaction toUpdate = new Transaction();
        BeanUtils.copyProperties(transactionVO, toUpdate);

        toUpdate.setLastUpdated(LocalDateTime.now());
        toUpdate.setSubmittedBy(userId);
        toUpdate.setSubmittedAt(LocalDateTime.now());
        toUpdate.setStatus(Constants.TX_STATUS_SUBMITTED);

        Transaction newTransaction = transactionRepository.save(toUpdate);

        TransactionVO updated = new TransactionVO();
        BeanUtils.copyProperties(newTransaction, updated);

        log.info("Transaction updated successfully by user: {}", userId);
        return updated;
    }

    private ExecutionCode validateTransaction(TransactionVO transaction, String context, String userId) {
        ExecutionCode validator = ExecutionCode.SUCCESS;
        // Perform validation logic here
        validator = validateTransactionProperties(transaction, userId);

        if (context != null && !context.isEmpty()) {
            log.info("Operation context: {}", context);

            switch (context) {
                case Constants.TX_CONTEXT_CREATE:
                    // Validate creation context
                    if (transaction.getId() != null) {
                        log.error("Transaction ID should not be set for creation");
                        return ExecutionCode.INVALID_PARAMETER;
                    }
                    break;
                case Constants.TX_CONTEXT_UPDATE:
                    // Validate update context
                    if (transaction.getId() == null) {
                        log.error("Transaction ID is required for update");
                        return ExecutionCode.INVALID_PARAMETER;
                    }
                    // Check if the transaction exists
                    else if (!transactionRepository.existsById(transaction.getId())) {
                        log.error("Transaction with ID {} does not exist", transaction.getId());
                        return ExecutionCode.NOT_FOUND;
                    }
                    // Check if the tranaction is already cancelled or approved
                    if (transaction.getStatus() != null &&
                        (transaction.getStatus().equals(Constants.TX_STATUS_CANCELLED) ||
                         transaction.getStatus().equals(Constants.TX_STATUS_APPROVED))) {
                        log.error("Transaction cannot be updated if already cancelled or approved");
                        return ExecutionCode.BUSINESS_ERROR;
                    }
                    break;
                case Constants.TX_CONTEXT_APPROVE:
                    // Validate approval context
                    if (transaction.getStatus() == null || !transaction.getStatus().equals(Constants.TX_STATUS_SUBMITTED)) {
                        log.error("Transaction must be in SUBMITTED status to approve");
                        return ExecutionCode.BUSINESS_ERROR;
                    }
                    break;
                case Constants.TX_CONTEXT_REJECT:
                case Constants.TX_CONTEXT_CANCEL:
                    // Validate rejection or cancellation context
                    if (transaction.getStatus() == null || transaction.getStatus().equals(Constants.TX_STATUS_CANCELLED)) {
                        log.error("Transaction cannot be cancelled or rejected if already cancelled");
                        return ExecutionCode.BUSINESS_ERROR;
                    }
                    break;
                default:
                    log.error("Unknown operation context: {}", context);
                    return ExecutionCode.BUSINESS_ERROR;
            }
        }

        return validator;
    }

    private ExecutionCode validateTransactionProperties(TransactionVO transaction, String userId) {
        if (transaction == null || userId == null || userId.isEmpty()) {
            return ExecutionCode.INVALID_PARAMETER;
        }
        if (transaction.getAmount() == null) {
            return ExecutionCode.INVALID_PARAMETER.withProperty("amount");
        }
        if (transaction.getTransactionDate() == null) {
            return ExecutionCode.INVALID_PARAMETER.withProperty("transactionDate");
        }
        String desc = transaction.getTransactionDescription();
        if (desc == null || desc.isEmpty() || desc.trim().isEmpty() || desc.length() > 150) {
            return ExecutionCode.INVALID_PARAMETER.withProperty("transactionDescription");
        }
        if (transaction.getDebitAccount() == null || transaction.getDebitAccount().isEmpty()) {
            return ExecutionCode.INVALID_PARAMETER.withProperty("debitAccount");
        }
        if (transaction.getCreditAccount() == null || transaction.getCreditAccount().isEmpty()) {
            return ExecutionCode.INVALID_PARAMETER.withProperty("creditAccount");
        }
        return ExecutionCode.SUCCESS;
    }

}
