package com.jimmylin.callcenter;

import java.util.Collection;
import java.util.List;

public interface CallService {
	
	Employee findAvailableEmployee(Collection<Employee> employeeList, Call call);
	
	List<Employee> buildEmployeeList(int number) throws Exception;
	
}
