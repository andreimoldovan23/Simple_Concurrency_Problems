#include <mutex>
#include <condition_variable>
#include <queue>

typedef int TElem;

class ProducerConsumerQueue {
private:
	std::mutex mtx;
	std::condition_variable cvProducer;
	std::condition_variable cvConsumer;
	int limit;
	int actual;
	std::queue<TElem> q;

public:
	ProducerConsumerQueue(int limit);

	~ProducerConsumerQueue();

	void add(TElem elem);

	TElem remove();
};