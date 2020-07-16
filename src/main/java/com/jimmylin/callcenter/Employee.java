package com.jimmylin.callcenter;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdk.nashorn.internal.runtime.ECMAException;

public class Employee implements Runnable {

	private EmployeeState employeeState;

	private EmployeeType employeeType;
	
    private ConcurrentLinkedDeque<Call> incomingCalls;

	public Employee(EmployeeType tpye) {
		this.employeeType = tpye;
		this.employeeState = EmployeeState.AVAILABLE;
		this.incomingCalls = new ConcurrentLinkedDeque<>();
	}

    private static final Logger logger = LoggerFactory.getLogger(Employee.class);
	
	public EmployeeState getEmployeeState() {
		return employeeState;
	}

	public synchronized void setEmployeeState(EmployeeState employeeState) {
	    logger.debug("Employee " + Thread.currentThread().getName() + " state change: " + employeeState);
		this.employeeState = employeeState;
	}

	public EmployeeType getEmployeeType() {
		return employeeType;
	}

	public synchronized void setEmployeeType(EmployeeType employeeType) {
		this.employeeType = employeeType;
	}

	public static Employee buildFresher() {
		return new Employee(EmployeeType.FRESHER);
	}

	public static Employee buildTeamLead() {
		return new Employee(EmployeeType.TEAMLEAD);
	}

	public static Employee buildPM() {
		return new Employee(EmployeeType.PM);
	}

    public synchronized void queuing(Call call) {
        logger.info("Employee " + Thread.currentThread().getName() + " queues a call : " + call.getId());
        this.incomingCalls.add(call);
    }
	
	
	/** enable thread to run
	 * to dispatch the AVAILABLE employee
	 */
	public void run() {
		while (true) {
            if (!this.incomingCalls.isEmpty()) {
                Call call = this.incomingCalls.poll();
                this.setEmployeeState(EmployeeState.WORKING);
                logger.info("Employee " + Thread.currentThread().getName() + " starts a call : " + call.getId() + " and will takes " 
                + call.getDuration() + " seconds");
                try {
                    TimeUnit.SECONDS.sleep(call.getDuration());
                } catch (InterruptedException e) {
                    logger.error("Employee " + Thread.currentThread().getName() + " was interrupted : " + call.getId());
                } finally {
                	 logger.info("Employee " + Thread.currentThread().getName() + " finish a call : " + call.getId() + " and it has taken " 
                             + call.getDuration() + " seconds");
                    this.setEmployeeState(EmployeeState.AVAILABLE);
                }
            }
        }
	}

}
