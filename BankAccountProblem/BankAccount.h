#include <mutex>
#include <vector>
#include "Transaction.h"

class BankAccount {
private:
	std::mutex mtx;
	int id;
	int initialTotal;
	int totalMoney;
	std::vector<Transaction> transactions;

public:
	BankAccount(int id, int totalMoney);
	~BankAccount();
	void sendMoney(int to, int sum);
	void receiveMoney(int from, int sum);
	bool checkTransactions();
	void undoLastTransaction();
	std::unique_lock<std::mutex> aquireLock();
};