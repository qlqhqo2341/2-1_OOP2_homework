class Queue
{
public:
	Queue(void);
	bool isEmpty();
	bool isFull();
	void EnQueue(int);
	int DeQueue();
	~Queue(void);
private:
	int arr[10];
	int size;
	int last;
	int first;
};

Queue::Queue(void)
{
	size = 10;
	last = 10;
	first = 10;
}
Queue::~Queue(void)
{
}

bool Queue::isEmpty()
{
	if((last)%size==first)
		return true;
	else
		return false;
}
bool Queue::isFull()
{
	if((last+1)%size==first)
		return true;
	else
		return false;
}
void Queue::EnQueue(int data)
{
	if(!Queue::isFull()){
		arr[last] = data;
		last = (last+1)%size;
	}
}
int Queue::DeQueue()
{
	if(!Queue::isEmpty()){
		return arr[first++];
		first = (first+1)%size;
	}
}