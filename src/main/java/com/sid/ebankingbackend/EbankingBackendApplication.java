package com.sid.ebankingbackend;

import com.sid.ebankingbackend.Exceptions.BalanceNotSufficientException;
import com.sid.ebankingbackend.Exceptions.BankAccountNotFoundException;
import com.sid.ebankingbackend.Exceptions.CustomerNotfoundException;
import com.sid.ebankingbackend.dtos.BankAccountDTO;
import com.sid.ebankingbackend.dtos.CustomerDTO;
import com.sid.ebankingbackend.entities.*;
import com.sid.ebankingbackend.enums.AccountStatus;
import com.sid.ebankingbackend.enums.OperationType;
import com.sid.ebankingbackend.repositories.AccountOperationRepository;
import com.sid.ebankingbackend.repositories.BankAccountRepository;
import com.sid.ebankingbackend.repositories.CustomerRepository;
import com.sid.ebankingbackend.services.BankAccountService;
import com.sid.ebankingbackend.services.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }

    //@Bean
    CommandLineRunner start(
            AccountOperationRepository accountOperationRepository,
            BankAccountRepository bankAccountRepository,
            CustomerRepository customerRepository
    ){
        return args -> {
            Stream.of("Hassan","Yassine","Aicha").forEach(
                    name->{
                        Customer customer=new Customer();
                        customer.setName(name);
                        customer.setEmail(name+"@gmail.com");
                        customerRepository.save(customer);
                    }
            );
            customerRepository.findAll().forEach(
                    customer -> {
                        CurrentAccount currentAccount=new CurrentAccount();
                        currentAccount.setId(UUID.randomUUID().toString());
                        currentAccount.setBalance(Math.random()*9000);
                        currentAccount.setCreatAt(new Date());
                        currentAccount.setStatus(AccountStatus.CREATED);
                        currentAccount.setCustomer(customer);
                        currentAccount.setOverDraft(9000);
                        bankAccountRepository.save(currentAccount);

                        SavingAccount savingAccount=new SavingAccount();
                        savingAccount.setId(UUID.randomUUID().toString());
                        savingAccount.setBalance(Math.random()*9000);
                        savingAccount.setCreatAt(new Date());
                        savingAccount.setStatus(AccountStatus.CREATED);
                        savingAccount.setCustomer(customer);
                        savingAccount.setInterestRate(5.5);
                        bankAccountRepository.save(savingAccount);

                    }

            );

            bankAccountRepository.findAll().forEach(
                    bankAccount -> {
                        for (int i = 0; i < 10; i++) {
                            AccountOperation accountOperation=new AccountOperation();
                            accountOperation.setAmount(Math.random()*12000);
                            accountOperation.setOperationDate(new Date());
                            accountOperation.setType(Math.random()>0.5? OperationType.CREDIT:OperationType.DEBIT);
                            accountOperation.setBankAccount(bankAccount);
                            accountOperationRepository.save(accountOperation);
                        }
                    }
            );



        };
    }

    //@Bean
    CommandLineRunner commandLineRunner(
            BankAccountService bankAccountService
    ) {
        return args -> {
            Stream.of("Hassan","Imane","Mohammed").forEach(
                    name ->{
                        CustomerDTO customer=new CustomerDTO();
                        customer.setName(name);
                        customer.setEmail(name+"@gmail.com");
                        bankAccountService.saveCustomer(customer);
                    }
            );
            bankAccountService.listCustomer().forEach(
                    customer -> {
                        try {
                            bankAccountService.saveCurrentBankAccount(
                                    Math.random()*90000,
                                    customer.getId(),
                                    9000);
                            bankAccountService.saveSavingBankAccount(
                                    Math.random()*120000,
                                    customer.getId(),
                                    5.5);
                        } catch (CustomerNotfoundException e) {
                            e.printStackTrace();

                        }
                    }
            );
            List<BankAccountDTO> bankAccountList=bankAccountService.BankAccountList();
            for (BankAccountDTO bankAccount:bankAccountList){
                for (int i = 0; i < 10; i++) {
                    bankAccountService.credit(bankAccount.getId(),10000+Math.random()*120000,"Credit");
                    bankAccountService.debit(bankAccount.getId(),1000+Math.random()*9000,"Debit");
                }
            }
        };
    }
}
