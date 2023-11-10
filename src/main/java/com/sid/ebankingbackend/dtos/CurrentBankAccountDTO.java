package com.sid.ebankingbackend.dtos;

import com.sid.ebankingbackend.enums.AccountStatus;
import lombok.Data;

import java.util.Date;

@Data
public class CurrentBankAccountDTO extends BankAccountDTO{
    private double overDraft;
}
