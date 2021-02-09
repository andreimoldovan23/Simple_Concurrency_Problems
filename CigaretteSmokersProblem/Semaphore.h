#include <mutex>
#include <condition_variable>

class Semaphore {
private:
	std::mutex mtx;
	std::condition_variable cvFreeSpot;
	int freeSpots;
	
public:
	Semaphore();

	~Semaphore();

	void wait();

	void post();
};