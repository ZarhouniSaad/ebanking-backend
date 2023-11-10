package com.sid.ebankingbackend.dtos;

import com.sid.ebankingbackend.enums.AccountStatus;
import lombok.Data;

import java.util.Date;
@Data
public abstract class BankAccountDTO {
    private String id;
    private double balance;
    private Date creatAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private String type;
}
