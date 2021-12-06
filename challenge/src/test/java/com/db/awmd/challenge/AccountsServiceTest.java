package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.TransactionException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();


	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}

	@Test
	public void testIsNegative_positive() throws Exception {
		assertThat(this.accountsService.isPositive(new BigDecimal(10))).isTrue();
	}

	@Test
	public void testIsNegative_negative() throws Exception {
		assertThat(this.accountsService.isPositive(new BigDecimal(-1))).isFalse();
	}

	@Test
	public void testIsNegative_zero() throws Exception {
		assertThat(this.accountsService.isPositive(new BigDecimal(0))).isFalse();
	}

	@Test
	public void testHasSufficientBalance_zero() throws Exception {
		assertThat(this.accountsService.hasSufficientBalance(new BigDecimal(0))).isTrue();
	}

	@Test
	public void testHasSufficientBalance_positive() throws Exception {
		assertThat(this.accountsService.hasSufficientBalance(new BigDecimal(1))).isTrue();
	}

	@Test
	public void testTransferAmount() throws Exception {
		Account account1 = createAccount("ID101", new BigDecimal(1000));
		Account account2 = createAccount("ID102", new BigDecimal(10));

		System.out.println("Before transaction");
		System.out.println(account1.getAccountId() + " :" + account1.getBalance());
		System.out.println(account2.getAccountId() + " :" + account2.getBalance());

		//TODO
		assertThat(this.accountsService.transferAmount("ID101", "ID102", new BigDecimal(100))).isNotNull();

		System.out.println("After Transaction");
		System.out.println(account1.getAccountId() + " :" + account1.getBalance());
		System.out.println(account2.getAccountId() + " :" + account2.getBalance());
	}
	
	@Test(expected = TransactionException.class)
	public void testTransferAmount_noSufficientBalance() throws Exception {
		Account account1 = createAccount("ID105", new BigDecimal(100));
		Account account2 = createAccount("ID106", new BigDecimal(1000));
		
		System.out.println("Before transaction");
		System.out.println(account1.getAccountId() + " :" + account1.getBalance());
		System.out.println(account2.getAccountId() + " :" + account2.getBalance());

		this.accountsService.transferAmount("ID105", "ID106",new BigDecimal(101));

		exceptionRule.expect(TransactionException.class);
	    exceptionRule.expectMessage("No Sufficient Amount available.");
	    
		System.out.println("After Transaction");
		System.out.println(account1.getAccountId() + " :" + account1.getBalance());
		System.out.println(account2.getAccountId() + " :" + account2.getBalance());
	}

	@Test(expected = TransactionException.class)
	public void testTransferAmount_transactionAmountShouldNotZero() throws Exception {
		Account account1 = createAccount("ID107", new BigDecimal(100));
		Account account2 = createAccount("ID108", new BigDecimal(1000));
		
		System.out.println("Before transaction");
		System.out.println(account1.getAccountId() + " :" + account1.getBalance());
		System.out.println(account2.getAccountId() + " :" + account2.getBalance());

		this.accountsService.transferAmount("ID105", "ID106",new BigDecimal(-1));

		exceptionRule.expect(TransactionException.class);
	    exceptionRule.expectMessage("Transaction amount should not be Zero or Negative.");
	    
		System.out.println("After Transaction");
		System.out.println(account1.getAccountId() + " :" + account1.getBalance());
		System.out.println(account2.getAccountId() + " :" + account2.getBalance());
	}
	@Test
	public void testTransferAmount_withThread() {

		Account account1 = createAccount("ID103", new BigDecimal(100));
		Account account2 = createAccount("ID104", new BigDecimal(100));

		ExecutorService executorService = Executors.newFixedThreadPool(10);

		IntStream.rangeClosed(1, 10000).forEach(i -> {
			executorService.submit(() -> {
				this.accountsService.transferAmount("ID103", "ID104", new BigDecimal(10));
				System.out.println("account1 Final Balance: " + account1.getBalance());
				System.out.println("account2 Final Balance: " + account2.getBalance());
			});

		});

		executorService.shutdown();
		
	}

	public Account createAccount(String accountId, BigDecimal balance) {
		Account account = new Account(accountId);
		account.setBalance(balance);
		this.accountsService.createAccount(account);
		return account;
	}
}
