/*
 * Copyright (c) 2019 Fachhochschule Nordwestschweiz (FHNW)
 * All Rights Reserved. 
 */

package bank.local;

/* Simple Server -- not thread safe */

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import bank.Account;
import bank.Bank;
import bank.InactiveException;
import bank.OverdrawException;

public class ConprBankDriver implements bank.BankDriver {
  private ConprBank bank = null;

  @Override
  public void connect(String[] args) {
    bank = new ConprBank();
  }

  @Override
  public void disconnect() {
    bank = null;
  }

  @Override
  public Bank getBank() {
    return bank;
  }
}

class ConprBank implements Bank {

  private Map<String, ConprAccount> accounts = new HashMap<String, ConprAccount>();

  @Override
  public Set<String> getAccountNumbers() {
    Lock l = new ReentrantLock();
    Set<String> activeAccountNumbers = new HashSet<>();
    l.lock();
    try{
      for (ConprAccount acc : accounts.values()) {
        if (acc.isActive()) {
          activeAccountNumbers.add(acc.getNumber());
        }
      }
    }finally {
      l.unlock();
    }
    return activeAccountNumbers;
  }

  @Override
  public String createAccount(String owner) {
    final ConprAccount a = new ConprAccount(owner);
    accounts.put(a.getNumber(), a);
    return a.getNumber();
  }

  @Override
  public boolean closeAccount(String number) {
    final ConprAccount a = accounts.get(number);
    if (a != null) {
      if (a.getBalance() != 0 || !a.isActive()) {
        return false;
      }
      a.passivate();
      return true;
    }
    return false;
  }

  @Override
  public Account getAccount(String number) {
    return accounts.get(number);
  }

  @Override
  public void transfer(Account from, Account to, double amount)
      throws IOException, InactiveException, OverdrawException {
    from.withdraw(amount);
    to.deposit(amount);
  }
}

class ConprAccount implements Account {
  private static int id = 0;

  private String number;
  private String owner;
  private double balance;
  private boolean active = true;

  ConprAccount(String owner) {
    this.owner = owner;
    this.number = "CONPR_ACC_" + id++;
  }

  @Override
  public double getBalance() {
    return balance;
  }

  @Override
  public String getOwner() {
    return owner;
  }

  @Override
  public String getNumber() {
    return number;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  void passivate() {
    active = false;
  }

  @Override
  public void deposit(double amount) throws InactiveException {
    if (!active)
      throw new InactiveException("account not active");
    if (amount < 0)
      throw new IllegalArgumentException("negative amount");
    balance += amount;
  }

  @Override
  public void withdraw(double amount) throws InactiveException, OverdrawException {
    if (!active)
      throw new InactiveException("account not active");
    if (amount < 0)
      throw new IllegalArgumentException("negative amount");
    if (balance - amount < 0)
      throw new OverdrawException("account cannot be overdrawn");
    balance -= amount;
  }

}
