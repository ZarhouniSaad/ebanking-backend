package com.sid.ebankingbackend.services;

import com.sid.ebankingbackend.Exceptions.BalanceNotSufficientException;
import com.sid.ebankingbackend.Exceptions.BankAccountNotFoundException;
import com.sid.ebankingbackend.Exceptions.CustomerNotfoundException;
import com.sid.ebankingbackend.dtos.*;
import com.sid.ebankingbackend.entities.*;
import com.sid.ebankingbackend.enums.OperationType;
import com.sid.ebankingbackend.mappers.BankAccountMapperImpl;
import com.sid.ebankingbackend.repositories.AccountOperationRepository;
import com.sid.ebankingbackend.repositories.BankAccountRepository;
import com.sid.ebankingbackend.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private AccountOperationRepository accountOperationRepository;
    private BankAccountRepository bankAccountRepository;
    private CustomerRepository customerRepository;
    private BankAccountMapperImpl dtoMapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, Long customerId, double overDraft) throws CustomerNotfoundException {

        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer==null)
            throw new CustomerNotfoundException("Customer not found");
        CurrentAccount bankAccount=new CurrentAccount();
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatAt(new Date());
        bankAccount.setBalance(initialBalance);
        bankAccount.setCustomer(customer);
        bankAccount.setOverDraft(overDraft);
        CurrentAccount savedAccount= bankAccountRepository.save(bankAccount);
        return dtoMapper.fromCurrentAccount(savedAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, Long customerId, double interestRate) throws CustomerNotfoundException {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer==null)
            throw new CustomerNotfoundException("Customer not found");
        SavingAccount bankAccount=new SavingAccount();
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatAt(new Date());
        bankAccount.setBalance(initialBalance);
        bankAccount.setCustomer(customer);
        bankAccount.setInterestRate(interestRate);
        SavingAccount savedAccount= bankAccountRepository.save(bankAccount);
        return dtoMapper.fromSavingAccount(savedAccount);
    }


    @Override
    public List<CustomerDTO> listCustomer() {
        List<Customer> customers= customerRepository.findAll();
        List<CustomerDTO> customerDTOS =
                customers.stream().map(
                                customer -> dtoMapper.fromCustomer(customer)
                        )
                        .collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("BankAccout not found"));
        if (bankAccount instanceof SavingAccount){
            return dtoMapper.fromSavingAccount((SavingAccount) bankAccount);
        }else {
            return dtoMapper.fromCurrentAccount((CurrentAccount) bankAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
        if (bankAccount.getBalance()<amount)
            throw new BalanceNotSufficientException("Balance not sufficient");
        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setAmount(amount);
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("BankAccout not found"));
        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setAmount(amount);
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource,amount,"transfert to"+accountIdDestination);
        credit(accountIdDestination,amount,"transfert from"+accountIdSource);
    }

    @Override
    public List<BankAccountDTO> BankAccountList(){
       List<BankAccount> bankAccountList=bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccountList.stream().map(
                bankAccount -> {
                    if (bankAccount instanceof SavingAccount) {
                        return dtoMapper.fromSavingAccount((SavingAccount) bankAccount);
                    } else {
                        return dtoMapper.fromCurrentAccount((CurrentAccount) bankAccount);
                    }
                }
        ).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotfoundException {
        Customer customer= customerRepository.findById(customerId).orElseThrow(
                ()-> new CustomerNotfoundException("Customer not found")
        );
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public List<AccountOperationDTO> acccountHistory(String accountId){
        List<AccountOperation> accountOperationList = accountOperationRepository.findByBankAccountId(accountId);
        List<AccountOperationDTO> accountOperationDTOS = accountOperationList.stream().map(
                accountOperation -> {
                    return dtoMapper.fromAccountOperation(accountOperation);
                }
        ).collect(Collectors.toList());
        return accountOperationDTOS;
    }

    @Override
    public AccountHistoryOperationDTO getAccountHistory(String accountId, int currentPage, int pageSize) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount==null)
            throw new BankAccountNotFoundException("Account not found");
        Page<AccountOperation> accountOperations=accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(currentPage,pageSize));
        AccountHistoryOperationDTO accountHistoryOperationDTO=new AccountHistoryOperationDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(
                accountOperation -> {
                    return dtoMapper.fromAccountOperation(accountOperation);
                }
        ).collect(Collectors.toList());
        accountHistoryOperationDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryOperationDTO.setAccountId(bankAccount.getId());
        accountHistoryOperationDTO.setBalance(bankAccount.getBalance());
        accountHistoryOperationDTO.setCurrentPage(currentPage);
        accountHistoryOperationDTO.setPageSize(pageSize);
        accountHistoryOperationDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryOperationDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomer(String keyword) {
        List<Customer> listCustomer = customerRepository.findByNameContains(keyword);
        List<CustomerDTO> customerDTOS = listCustomer.stream().map(
                customer -> {
                    return dtoMapper.fromCustomer(customer);
                }
        ).collect(Collectors.toList());
        return customerDTOS;
    }




}
