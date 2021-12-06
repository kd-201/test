package com.db.awmd.challenge.service;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.domain.TransactionEvent;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionEventProcessor implements ApplicationListener<TransactionEvent> {
	
	@Getter
	NotificationService notifyAboutTransfer ;
	
	@Getter
	AccountsRepository accountsRepository;
	
	public TransactionEventProcessor(NotificationService notifyAboutTransfer, AccountsRepository accountsRepository) {
		super();
		this.notifyAboutTransfer = notifyAboutTransfer;
		this.accountsRepository = accountsRepository;
	}
	
	public void onApplicationEvent(TransactionEvent event) 
    {
		TransactionEvent transactionEvent = (TransactionEvent) event;
		log.info("Transaction " + transactionEvent.getEventType() + " with details : " + transactionEvent.getTransaction());

        Transaction transaction = transactionEvent.getTransaction();
        notifyAboutTransfer.notifyAboutTransfer(accountsRepository.getAccount(transaction.getFromAccountId()),
        		transaction.toString());
    }


}
