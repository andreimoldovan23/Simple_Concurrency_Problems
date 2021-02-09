class Transaction {
private:
	int idFrom;
	int idTo;
	int sum;

public:
	Transaction(int idFrom, int idTo, int sum) : idFrom{idFrom}, idTo{idTo}, sum{sum} {}
	~Transaction() {}
	int getIdFrom() { return this->idFrom; }
	int getIdTo() { return this->idTo; }
	int getSum() { return this->sum; }
};