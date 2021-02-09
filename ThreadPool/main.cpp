#include "ThreadPool.h"

#include <iostream>
#include <string>

using namespace std;

void addError(int x, int y) {
	string str(to_string(x) + " + " + to_string(y));
	throw runtime_error(str);
}

void add(int x, int y) {
	cout << x << " + " << y << " = " << x + y << endl;
}

int main() {
	int nrThreads = 4;
	ThreadPool th(nrThreads);

	auto f1 = bind(add, 1, 1);
	auto f2 = bind(add, 0, 10);
	auto f3 = bind(add, 15, 15);
	auto f4 = bind(add, 25, 15);
	auto f5 = bind(addError, 3, 7);
	auto f6 = bind(addError, 4, 7);

	th.addJob(f1);
	th.addJob(f2);
	th.addJob(f5);
	th.addJob(f3);
	th.addJob(f4);
	th.addJob(f6);

	vector<vector<exception_ptr>> exceptions = th.shutdown();
	for (int i = 0; i < exceptions.size(); i++) {
		for (int j = 0; j < exceptions[i].size(); j++) {
			try {
				rethrow_exception(exceptions[i][j]);
			}
			catch (exception& e) {
				cout << "Thread " << i << " exited with exception: " << e.what() << endl;
			}
		}
	}
	return 0;
}