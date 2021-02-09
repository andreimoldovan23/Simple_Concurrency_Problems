#include "Semaphore.h"

using namespace std;

Semaphore::Semaphore() { this->freeSpots = 0; }

Semaphore::~Semaphore() {}

void Semaphore::wait() {
	unique_lock<mutex> lk(this->mtx);
	while (this->actual == 0) cvFreeSpot.wait(lk);
	this->actual--;
}

void Semaphore::post() {
	unique_lock<mutex> lk(this->mtx);
	this->actual++;
	cvFreeSpot.notify_one();
}