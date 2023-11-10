package com.sid.ebankingbackend.services;

import com.sid.ebankingbackend.Exceptions.BalanceNotSufficientException;
import com.sid.ebankingbackend.Exceptions.BankAccountNotFoundException;
import com.sid.ebankingbackend.Exceptions.CustomerNotfoundException;
import com.sid.ebankingbackend.dtos.*;

import java.util.List;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, Long customerId, double overDraft) throws CustomerNotfoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, Long customerId, double interestRate) throws CustomerNotfoundException;
    List<CustomerDTO> listCustomer();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId,double amount,String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource,String accoutIdDestination,double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;


    List<BankAccountDTO> BankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotfoundException;

    List<AccountOperationDTO> acccountHistory(String accountId);

    AccountHistoryOperationDTO getAccountHistory(String accountId, int currentPage, int pageSize) throws BankAccountNotFoundException;

    List<CustomerDTO> searchCustomer(String keyword);
}
