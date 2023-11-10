package com.sid.ebankingbackend.web;

import com.sid.ebankingbackend.Exceptions.CustomerNotfoundException;
import com.sid.ebankingbackend.dtos.CustomerDTO;
import com.sid.ebankingbackend.entities.BankAccount;
import com.sid.ebankingbackend.entities.Customer;
import com.sid.ebankingbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustomerRestController {
    private BankAccountService bankAccountService;

    @GetMapping("/customers")
    public List<CustomerDTO> customers(){
        return bankAccountService.listCustomer();
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotfoundException {
        return bankAccountService.getCustomer(customerId);
    }

    @GetMapping("/customers/search")
    public List<CustomerDTO> getCustomer(@RequestParam(name = "keyword",defaultValue = "") String keyword) throws CustomerNotfoundException {
        return bankAccountService.searchCustomer(keyword);
    }

    @PostMapping("/customers")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        return bankAccountService.saveCustomer(customerDTO);
    }
    @PutMapping("/customers/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable Long customerId,@RequestBody CustomerDTO customerDTO){
        customerDTO.setId(customerId);
        return bankAccountService.updateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{customerId}")
    public void deleteCustomer(@PathVariable Long customerId){
        bankAccountService.deleteCustomer(customerId);
    }


}
