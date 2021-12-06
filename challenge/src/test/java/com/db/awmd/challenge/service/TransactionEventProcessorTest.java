package com.db.awmd.challenge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.domain.TransactionEvent;
import com.db.awmd.challenge.repository.AccountsRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionEventProcessorTest {
	
	@Autowired
	TransactionEventProcessor transactionEventProcessor;
	
	@Mock
	NotificationService notifyAboutTransferMock ;
	
	@Mock
	AccountsRepository accountsRepositoryMock;
	
	@Autowired
	private AccountsService accountsService;
	
	
	@Test
	public void onApplicationEvent() {
		Account account = new Account("ID101",new BigDecimal(100));
		
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);
		
		notifyAboutTransferMock.notifyAboutTransfer(account, "real");
	 
	    verify(notifyAboutTransferMock, times(1)).notifyAboutTransfer(account, "real");
	    this.transactionEventProcessor.onApplicationEvent(createTransactionEvent());
		assertThat(true).isTrue();
	}

	private TransactionEvent createTransactionEvent() {
		Transaction transaction= new Transaction("TRID0001234", "ID101", "ID102", new BigDecimal(100), 
				Timestamp.from(Instant.now()));
		TransactionEvent transactionEvent = new TransactionEvent(transactionEventProcessor, "ADD", transaction);
		return transactionEvent;
	}

}
