#pragma once

#include <vector>
#include <condition_variable>
#include <mutex>
#include <thread>
#include <functional>
#include <exception>


class ThreadPool {
private:
	std::vector<std::thread> workers;
	std::vector<std::function<void()>> tasks;
	std::vector<std::vector<std::exception_ptr>> exceptions;
	std::condition_variable conditionVar;
	std::mutex tasksMutex;
	int maxCapacity;
	bool terminated;
	bool over;

public:
	ThreadPool(int);
	~ThreadPool();
	void addJob(std::function<void()>);
	void execJob(int*);
	std::vector<std::vector<std::exception_ptr>> shutdown();
};