package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class Account {

  @NotNull
  @NotEmpty
  private final String accountId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal balance;
  
  private Lock lock = new ReentrantLock(true);

  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = BigDecimal.ZERO;
  }

  @JsonCreator
  public Account(@JsonProperty("accountId") String accountId,
    @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance;
  }
  
  public void credit(BigDecimal amount) {
	  try {
		if(lock.tryLock(2000, TimeUnit.MILLISECONDS)){
			  try{   
			    lock.lock();
			    balance = balance.add(amount);
			  }
			  finally{
			    lock.unlock(); 
			  } 
		  }else {
			  //TODO : I have to think on else case
		  }
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
     
  }

  public void debit(BigDecimal amount) { 
	  try {
		if(lock.tryLock(2000, TimeUnit.MILLISECONDS)){
			  try{   
			    lock.lock();
			    balance = balance.subtract(amount); 
			  }
			  finally{
			    lock.unlock(); 
			  } 
		  }else {
			//TODO : I have to think on else case
		  }
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
    
  }

}
