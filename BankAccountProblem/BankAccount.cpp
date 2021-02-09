#include "BankAccount.h"

using namespace std;

BankAccount::BankAccount(int id, int total) : id{ id }, totalMoney{ total }, initialTotal{ total } {}

BankAccount::~BankAccount() {}

void BankAccount::sendMoney(int to, int sum) {
	Transaction t(this->id, to, sum);
	this->totalMoney -= sum;
	this->transactions.push_back(t);
}

void BankAccount::receiveMoney(int from, int sum) {
	Transaction t(from, this->id, sum);
	this->totalMoney += sum;
	this->transactions.push_back(t);
}

bool BankAccount::checkTransactions() {
	int total = this->initialTotal;
	for (auto t : this->transactions) {
		if (t.getIdFrom() == this->id) total -= t.getSum();
		else total += t.getSum();
	}
	return (total == this->totalMoney);
}

void BankAccount::undoLastTransaction() {
	Transaction last = this->transactions[this->transactions.size() - 1];
	this->transactions.pop_back();
	if (last.getIdFrom() == this->id) this->totalMoney += last.getSum();
	else this->totalMoney -= last.getSum();
}

unique_lock<mutex> BankAccount::aquireLock() { return unique_lock<mutex>(this->mtx, defer_lock); }
