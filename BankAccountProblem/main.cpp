#include "BankAccount.h"
#include <thread>
#include <iostream>
#include <exception>

using namespace std;

vector<exception_ptr> exceptions;

vector<BankAccount*> generateAccounts() {
	vector<BankAccount*> accounts;
	for (int i = 0; i < 30; i++) {
		int money = rand() % 1000 + 1;
		accounts.push_back(new BankAccount(i + 1, money));
	}
	return accounts;
}

vector<BankAccount*> accounts = generateAccounts();

void performTransactions(int* seed, int* nr) {
	srand(*seed);
	int iterations = 5;
	while (iterations > 0) {
		int fromId = rand() % 30 + 1;
		int toId = rand() % 30 + 1;
		int sum = rand() % 1000 + 1;
		BankAccount* from = accounts[fromId - 1];
		BankAccount* to = accounts[toId - 1];
		{
			unique_lock<mutex> lkFrom = move(from->aquireLock());
			unique_lock<mutex> lkTo = move(to->aquireLock());
			lock(lkFrom, lkTo);
			from->sendMoney(toId, sum);
			to->receiveMoney(fromId, sum);
			if (!from->checkTransactions()) {
				from->undoLastTransaction();
				try {
					throw exception("Transaction failed");
				}
				catch(...){
					exceptions[*nr] = current_exception();
				}
				
			}
			if (!to->checkTransactions()) {
				to->undoLastTransaction();
				try {
					throw exception("Transaction failed");
				}
				catch (...) {
					exceptions[*nr] = current_exception();
				}
			}
		}
		iterations--;
	}
	delete seed;
	delete nr;
}

int main() {
	vector<thread> threads;
	for (int i = 0; i < 4; i++) exceptions.push_back(NULL);
	for (int i = 0; i < 4; i++) threads.emplace_back(performTransactions, new int(i+2), new int(i));
	for (int i = 0; i < 4; i++) threads[i].join();
	for (int i = 0; i < 4; i++) {
		exception_ptr exPtr = exceptions[i];
		try {
			if(exPtr != NULL)
				rethrow_exception(exPtr);
		}
		catch (exception& ex) {
			cout << "Thread exited with exception " << ex.what() << endl;
		}
	}
	return 0;
}