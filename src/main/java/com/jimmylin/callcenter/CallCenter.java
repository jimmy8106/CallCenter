package com.jimmylin.callcenter;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallCenter implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(CallCenter.class);
	
    private Boolean open;
	
    public static final Integer MAX_THREADS = 20;
    
    // using dequque, when a employee finish
    // their job, they will be top on quque
    private ConcurrentLinkedDeque<Employee> employees;
    
    // using dequque, when a call is unsolveable
    // it will put back to top of queue
    private ConcurrentLinkedDeque<Call> incomingCalls;
	
    private ExecutorService executorService;
    
    private CallService callService;
    

    public CallCenter(List<Employee> employees) {
		super();
		this.employees = new ConcurrentLinkedDeque(employees);
		this.callService = new CallServiceImpl();
        this.incomingCalls = new ConcurrentLinkedDeque<>();
        this.executorService = Executors.newFixedThreadPool(MAX_THREADS);
	}
    
    public synchronized void start() {
        this.open = true;
        logger.info("Employee " + Thread.currentThread().getName() + "starts");
        for (Employee employee : this.employees) {
            this.executorService.execute(employee);
        }
    }

    public synchronized void stop() {
        this.open = false;
        this.executorService.shutdown();
    }

    public synchronized Boolean getOpen() {
        return open;
    }
    
    public synchronized void dispatch(Call call) {
        logger.info("new call coming " + call.getDuration() + " seconds");
        this.incomingCalls.add(call);
    }
    
    @Override
    public void run() {
        while (getOpen()) {
            if (this.incomingCalls.isEmpty()) {
                continue;
            } else {
                Call call = this.incomingCalls.poll();
                Employee employee = this.callService.findAvailableEmployee(this.employees, call);
                if (employee == null) {
                    try {
						TimeUnit.SECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                    logger.info("requeue call : " +  call.getId() + ", level to : " + call.getLevel());
                	this.incomingCalls.addFirst(call);
                    continue;
                }
                try {
                    employee.queuing(call);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    this.incomingCalls.addFirst(call);
                }
            }
        }
    }
    
    
}
