package com.jimmylin.callcenter;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class JUnitTest {

    private CallService callService;
	
	@Test
	public void test() throws Exception {
		callService = new CallServiceImpl();
		List<Employee> employeeList = callService.buildEmployeeList(20);
		CallCenter center = new CallCenter(employeeList);
		center.start();
        TimeUnit.SECONDS.sleep(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(center);
        TimeUnit.SECONDS.sleep(1);
        
        Call.buildListOfRandomCalls(50).stream().forEach(call -> {
        	center.dispatch(call);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                fail();
            }
        });
        executorService.awaitTermination(60, TimeUnit.SECONDS);
		center.stop();
	}

}
