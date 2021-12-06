package com.db.awmd.challenge.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

@Data
public class Transaction {

	@NotNull
	@NotEmpty
	
	private String transactionId;
	
	@NotNull
	@NotEmpty
	private String fromAccountId;
	
	@NotNull
	@NotEmpty
	private String toAccountId;

	@NotNull
	@Min(value = 0, message = "it must be positive.")
	private BigDecimal amount;
	
	@NotNull
	@NotEmpty
	private Timestamp transactionTime;
	
	

	public Transaction(String transactionId, String fromAccountId, String toAccountId, BigDecimal amount,
			Timestamp transactionTime) {
		super();
		this.transactionId = transactionId;
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
		this.amount = amount;
		this.transactionTime = transactionTime;
	}
	
	public Transaction(String transactionId, String fromAccountId, String toAccountId, String amount,
			Timestamp transactionTime) {
		super();
		this.transactionId = transactionId;
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
		this.amount = new BigDecimal(amount);
		this.transactionTime = transactionTime;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		return Objects.equals(transactionId, other.transactionId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(transactionId);
	}

	public Transaction() {
	}
	
	

}
