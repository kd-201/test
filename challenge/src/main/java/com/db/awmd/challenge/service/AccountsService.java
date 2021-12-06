package com.db.awmd.challenge.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.domain.TransactionEvent;
import com.db.awmd.challenge.exception.AccountNotPresentException;
import com.db.awmd.challenge.exception.TransactionException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;

	// @Getter
	// Map<String, Transaction> transactionHistory = new ConcurrentHashMap<String,
	// Transaction>();

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) throws AccountNotPresentException {
		return this.accountsRepository.getAccount(accountId);
	}

	public Account transferAmount(String accountFromId, String accountToId, BigDecimal amount)
			throws AccountNotPresentException, TransactionException {
		log.info("account from : {}", accountFromId);
		log.info("Account to : {}", accountToId);
		log.info("Transaction Amount {}", amount);

		if (!isPositive(amount)) {
			throw new TransactionException("Transaction amount should not be Zero or Negative.");
		}

		Account fromAccount = getAccount(accountFromId);
		Account toAccount = getAccount(accountToId);

		// validate balance after deduction, it should not be in minus.
		if (!hasSufficientBalance(fromAccount.getBalance().subtract(amount)))
			throw new TransactionException("No Sufficient Amount available.");

		try {
			fromAccount.debit(amount);
			toAccount.credit(amount);

			String transactionId = UUID.randomUUID().toString();
			Timestamp transactionTime = Timestamp.from(Instant.now());

			Transaction transaction = new Transaction(transactionId, fromAccount.getAccountId(),
					toAccount.getAccountId(), amount, transactionTime);

			publisher.publishEvent(new TransactionEvent(this, "UPDATE", transaction));

		} catch (Exception e) {
			throw new TransactionException(e.getMessage());
		}
		return fromAccount;
	}

	public boolean isPositive(BigDecimal amount) {
		boolean isPositive = false;
		if (amount.compareTo(new BigDecimal(0)) == 1) {
			log.debug(amount + " is greter than 0.");
			isPositive = true;
		} else {
			log.debug(amount + " is less than 0 or 0.");

		}
		return isPositive;

	}

	public void validateBalance(BigDecimal balance) {
		if (!isPositive(balance)) {
			throw new TransactionException("No Sufficient Amount available.");

		}

	}

	public boolean hasSufficientBalance(BigDecimal balance) {
		if (balance.compareTo(BigDecimal.ZERO) == 0)
			return true;
		if (isPositive(balance)) {
			return true;
		}
		return false;

	}
}
