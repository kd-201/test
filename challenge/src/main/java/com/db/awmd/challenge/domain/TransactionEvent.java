package com.db.awmd.challenge.domain;

import org.springframework.context.ApplicationEvent;


public class TransactionEvent extends ApplicationEvent{
	private static final long serialVersionUID = 1L;
    
    private String eventType;
    private Transaction transaction;
    
    public TransactionEvent(Object source, String eventType, Transaction transaction) 
    {
        super(source);
        this.eventType = eventType;
        this.transaction = transaction;
    }

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
    
    
 
}
