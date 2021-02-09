#include <thread>
#include <mutex>
#include <shared_mutex>
#include <vector>
#include <iostream>
#include <chrono>

using namespace std;

#define LIMIT 5

mutex outStreamMutex;
shared_mutex haveEatenMutex;
vector<mutex*> forkMutexes;
vector<bool> allAte;

void thinkAndEat(int* number) {
	int philoNumber = *number;
	while (true) {
		{
			lock_guard<mutex> outStreamLock(outStreamMutex);
			cout << "The philosopher " << philoNumber << " is thinking..." << endl;
		}

		this_thread::sleep_for(chrono::seconds(3));

		{
			shared_lock<shared_mutex> lock(haveEatenMutex);
			int i = 0;
			for (; i < LIMIT; i++)
				if (!allAte[i]) break;
			if (i == LIMIT) break;
		}

		mutex* rightFork = forkMutexes[philoNumber];
		mutex* leftFork = forkMutexes[(philoNumber + 1) % LIMIT];
		if (rightFork->try_lock()) {
			if (leftFork->try_lock()) {
				{
					lock_guard<mutex> outStreamLock(outStreamMutex);
					cout << "The philospher " << philoNumber << " is eating!" << endl;
				}
				this_thread::sleep_for(chrono::seconds(3));
				{
					unique_lock<shared_mutex> lk(haveEatenMutex);
					allAte[(philoNumber + 1) % LIMIT] = true;
				}
				leftFork->unlock();
			}
			rightFork->unlock();
		}
	}
}

int main() {
	for (int i = 0; i < LIMIT; i++) {
		allAte.push_back(false);
		forkMutexes.push_back(new mutex());
	}
	vector<thread> philosophers;
	vector<int*> forks;
	for (int i = 0; i < LIMIT; i++) {
		forks.push_back(new int(i));
		philosophers.emplace_back(thinkAndEat, forks[i]);
	}
	for (int i = 0; i < LIMIT; i++) {
		philosophers[i].join();
		delete forks[i];
		delete forkMutexes[i];
	}
	cout << "Done" << endl;
	return 0;
}