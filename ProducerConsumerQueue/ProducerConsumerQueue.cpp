#include "ProducerConsumerQueue.h"

using namespace std;

ProducerConsumerQueue::ProducerConsumerQueue(int limit) : limit{ limit } { this->actual = 0; }

ProducerConsumerQueue::~ProducerConsumerQueue() {}

void ProducerConsumerQueue::add(TElem e) {
	unique_lock<mutex> lock(this->mtx);
	while (this->actual == this->limit) this->cvProducer.wait(lock);
	this->q.push(e);
	this->actual++;
	if(this->actual == 1) this->cvConsumer.notify_one();
}

TElem ProducerConsumerQueue::remove() {
	unique_lock<mutex> lock(this->mtx);
	while (this->actual == 0) this->cvConsumer.wait(lock);
	TElem e = this->q.front();
	this->q.pop();
	this->actual--;
	if (this->actual == this->limit - 1) this->cvProducer.notify_one();
	return e;
}