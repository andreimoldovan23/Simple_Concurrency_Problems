#include "ThreadPool.h"

using namespace std;

ThreadPool::ThreadPool(int cap) : maxCapacity{ cap }, over{ false }, terminated{ false } {
	for (int i = 0; i < maxCapacity; i++) {
		workers.emplace_back(&ThreadPool::execJob, this, new int(i));
		exceptions.push_back(vector<exception_ptr>());
	}
}

ThreadPool::~ThreadPool() {
	if (!terminated) shutdown();
}

vector<vector<exception_ptr>> ThreadPool::shutdown() {
	{
		lock_guard<mutex> lk(tasksMutex);
		over = true;
	}
	conditionVar.notify_all();

	for (int i = 0; i < maxCapacity; i++)
		workers[i].join();
	workers.clear();
	terminated = true;

	return exceptions;
}

void ThreadPool::addJob(function<void()> f) {
	{
		lock_guard<mutex> lk(tasksMutex);
		tasks.push_back(f);
	}
	conditionVar.notify_one();
}

void ThreadPool::execJob(int* id) {
	while (true) {
		function<void()> toExecute;
		{
			unique_lock<mutex> lk(tasksMutex);
			conditionVar.wait(lk, [this] {
				return !tasks.empty() || over;
				});

			if (!tasks.empty()) {
				toExecute = tasks.front();
				tasks.erase(tasks.begin());
			}
			else {
				break;
			}
		}

		try {
			toExecute();
		}
		catch (...) {
			exceptions[*id].push_back(current_exception());
		}
	}
}

