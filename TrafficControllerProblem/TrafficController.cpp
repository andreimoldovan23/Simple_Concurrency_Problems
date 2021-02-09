#include <thread>
#include <vector>
#include <string>
#include <iostream>
#include <mutex>
#include <condition_variable>
#include <chrono>

using namespace std;

int carsNumber = 40;
int bridgeCapacity = 5;
int timer = 30;
int timeLimit = 240;
int busyLeftSpots = 0;
int busyRightSpots = 0;
bool currentDirection = true;
mutex outMtx;
mutex leftSemaphore;
mutex rightSemaphore;
condition_variable cvLeft;
condition_variable cvRight;
vector<thread> cars;
thread directionChanger;
auto start = chrono::high_resolution_clock::now();


void changeDirection() {
	currentDirection = !currentDirection;
	condition_variable* cv;
	if (currentDirection)
		cv = &cvLeft;
	else
		cv = &cvRight;
	cv->notify_all();
}


void enterBridge(bool dir) {
	unique_lock<mutex> myLock;
	condition_variable* currentCv;
	int* numberSpots;
	int* numberOppositeSpots;

	if (dir) {
		myLock = unique_lock<mutex>(leftSemaphore, defer_lock);
		currentCv = &cvLeft;
		numberSpots = &busyLeftSpots;
		numberOppositeSpots = &busyRightSpots;
	}
	else {
		myLock = unique_lock<mutex>(rightSemaphore, defer_lock);
		currentCv = &cvRight;
		numberSpots = &busyRightSpots;
		numberOppositeSpots = &busyLeftSpots;
	}
	
	myLock.lock();
	while (dir != currentDirection || (*numberSpots) >= bridgeCapacity || (*numberOppositeSpots) > 0) {
		currentCv->wait(myLock);
	}
	(*numberSpots)++;
	myLock.unlock();
}


void passBridge() {
	this_thread::sleep_for(chrono::seconds(10));
}


void exitBridge(bool dir) {
	unique_lock<mutex> myLock;
	condition_variable* currentCv = new condition_variable();
	condition_variable* oppositeCv = new condition_variable();
	int* numberSpots = new int();
	int* numberOppositeSpots = new int();

	if (dir) {
		myLock = unique_lock<mutex>(leftSemaphore, defer_lock);
		currentCv = &cvLeft;
		oppositeCv = &cvRight;
		numberSpots = &busyLeftSpots;
		numberOppositeSpots = &busyRightSpots;
	}
	else {
		myLock = unique_lock<mutex>(rightSemaphore, defer_lock);
		currentCv = &cvRight;
		oppositeCv = &cvLeft;
		numberSpots = &busyRightSpots;
		numberOppositeSpots = &busyLeftSpots;
	}

	myLock.lock();
	(*numberSpots)--;
	currentCv->notify_all();
	if ((*numberOppositeSpots) == 0 && dir != currentDirection)
		oppositeCv->notify_all();
	myLock.unlock();
}


void printMsg(string msg, int id, bool dir) {
	string dirString = "";
	if (dir)
		dirString = "left";
	else
		dirString = "right";

	string toDisplay = "------------------\n\nCar " + to_string(id) + msg + ", going " + dirString + "\n\n------------------\n\n";
	{
		lock_guard<mutex> out(outMtx);
		cout << toDisplay;
	}
}


void printMsg(string msg) {
	{
		lock_guard<mutex> out(outMtx);
		cout << "------------------\n\n" << msg << "\n\n------------------\n\n";
	}
}


void traverse(int* id, bool* dir) {
	enterBridge(*dir);
	printMsg(" entering bridge", *id, *dir);
	this_thread::sleep_for(chrono::seconds(5));
	
	printMsg(" passing bridge", *id, *dir);
	passBridge();
	
	printMsg(" exiting bridge", *id, *dir);
	this_thread::sleep_for(chrono::seconds(5));
	exitBridge(*dir);

	delete id;
	delete dir;
}


bool elapsed(int limit) {
	auto end = chrono::high_resolution_clock::now();
	auto duration = chrono::duration_cast<chrono::seconds>(end - start);
	if (duration.count() > limit) return true;
	return false;
}


void alternate(int* t) {
	while (true) {
		this_thread::sleep_for(chrono::seconds(*t));
		if (elapsed(timeLimit))
			break;
		printMsg("Changing direction");
		changeDirection();
	}
	delete t;
}


int main() {
	srand(time(NULL));
	for (int i = 0; i < carsNumber / 2; i++) {
		cars.emplace_back(traverse, new int(i), new bool(true));
	}
	for (int i = carsNumber / 2; i < carsNumber; i++) {
		cars.emplace_back(traverse, new int(i), new bool(false));
	}
	directionChanger = thread(alternate, new int(timer));

	for (int i = 0; i < carsNumber; i++)
		cars[i].join();
	directionChanger.join();
	return 0;
}