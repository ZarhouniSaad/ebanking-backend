package com.sid.ebankingbackend.web;

import com.sid.ebankingbackend.Exceptions.BalanceNotSufficientException;
import com.sid.ebankingbackend.Exceptions.BankAccountNotFoundException;
import com.sid.ebankingbackend.dtos.*;
import com.sid.ebankingbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class BankAccountRestController {
    private BankAccountService bankAccountService;

    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> bankAccountDTOList(){
        return bankAccountService.BankAccountList();
    }

    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperationDTO> accountHistory(@PathVariable String accountId){
        return bankAccountService.acccountHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryOperationDTO  getAccountHistory(@PathVariable String accountId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "5") int pageSize) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId,page,pageSize);
    }

    @PostMapping("accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.debit(debitDTO.getAccountId(),debitDTO.getAmount(),debitDTO.getDescription());
        return debitDTO;
    }

    @PostMapping("accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.credit(creditDTO.getAccountId(),creditDTO.getAmount(),creditDTO.getDescription());
        return creditDTO;
    }

    @PostMapping("accounts/transfer")
    public void credit(@RequestBody TransferDTO transferDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.transfer(transferDTO.getAccountSource(),
                transferDTO.getAccountDestination(),
                transferDTO.getAmount());
    }
}
