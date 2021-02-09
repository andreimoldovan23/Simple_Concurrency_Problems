#include "Semaphore.h"
#include <chrono>
#include <vector>
#include <iostream>
#include <string>
#include <utility>

using namespace std;

typedef pair<string, string> materialCombination;
typedef vector<materialCombination> materialsList;

Semaphore* smokerWithTobacco = new Semaphore();
Semaphore* smokerWithPaper = new Semaphore();
Semaphore* smokerWithMatches = new Semaphore();
Semaphore* agentSem = new Semaphore();
bool over = false;

void agentThread(int ptr) {
	srand(time(NULL));
	int nrIterations = ptr;
	materialsList mts{
	materialCombination("TOBACCO", "PAPER"),
	materialCombination("TOBACCO", "MATCHES"),
	materialCombination("PAPER", "MATCHES")
	};
	while (nrIterations > 0) {
		int resourceNumber = rand() % 3;
		switch (resourceNumber) {
		case 0:
			smokerWithMatches->post();
			break;
		case 1:
			smokerWithPaper->post();
			break;
		case 2:
			smokerWithTobacco->post();
			break;
		}
		nrIterations--;
		agentSem->wait();
	}
	over = true;
	smokerWithMatches->post();
	smokerWithPaper->post();
	smokerWithTobacco->post();
	delete agentSem;
}

void smokerThread(string s, Semaphore* sem) {
	while (true) {
		sem->wait();
		if (over)break;
		cout << "Smoker with " << s << " is smoking" << endl;
		this_thread::sleep_for(chrono::seconds(3));
		agentSem->post();
	}
	delete sem;
}

int main() {
	int nrIterations = 10;
	string s1("TOBACCO");
	string s2("PAPER");
	string s3("MATCHES");
	vector<thread> threads;
	threads.emplace_back(smokerThread, s1, smokerWithTobacco);
	threads.emplace_back(smokerThread, s2, smokerWithPaper);
	threads.emplace_back(smokerThread, s3, smokerWithMatches);
	threads.emplace_back(agentThread, nrIterations);
	for (int i = 0; i < 4; i++) threads[i].join();
	cout << "Done" << endl;
	return 0;
}