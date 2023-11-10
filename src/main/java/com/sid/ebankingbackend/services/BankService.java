package com.sid.ebankingbackend.services;

import com.sid.ebankingbackend.entities.BankAccount;
import com.sid.ebankingbackend.entities.CurrentAccount;
import com.sid.ebankingbackend.entities.SavingAccount;
import com.sid.ebankingbackend.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankService {
    @Autowired
    BankAccountRepository bankAccountRepository;

    public void consulter(){
        BankAccount bankAccount = bankAccountRepository.findById("4d111e84-6f92-4df8-8f59-ca56b7ff2dd1").orElse(null);
        System.out.println("********************************");
        System.out.println(bankAccount.getId());
        System.out.println(bankAccount.getBalance());
        System.out.println(bankAccount.getStatus());
        System.out.println(bankAccount.getCreatAt());
        System.out.println(bankAccount.getCustomer().getName());
        if (bankAccount instanceof CurrentAccount){
            System.out.println("overDraft"+((CurrentAccount) bankAccount).getOverDraft());
        }else if (bankAccount instanceof SavingAccount){
            System.out.println("Rate=>"+((SavingAccount)bankAccount).getInterestRate());
        }

        bankAccount.getAccountOperations().forEach(
                accountOperation -> {
                    System.out.println("=============================");
                    System.out.println(accountOperation.getId());
                    System.out.println(accountOperation.getAmount());
                    System.out.println(accountOperation.getType());
                }
        );
    }
}
