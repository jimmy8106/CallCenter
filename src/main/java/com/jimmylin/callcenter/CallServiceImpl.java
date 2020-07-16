package com.jimmylin.callcenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallServiceImpl implements CallService {

	private static final Logger logger = LoggerFactory.getLogger(CallServiceImpl.class);

	@Override
	public Employee findAvailableEmployee(Collection<Employee> employeeList, Call call) {
		List<Employee> availableEmployees = employeeList.stream()
				.filter(e -> e.getEmployeeState() == EmployeeState.AVAILABLE).collect(Collectors.toList());
		logger.info("Available employee numbers : " + availableEmployees.size());
		Integer dice = ThreadLocalRandom.current().nextInt(1, 21);
		Integer dice2 = ThreadLocalRandom.current().nextInt(1, 11);
		Optional<Employee> employee;
		if (call.getLevel() == 0) {
			// rolling dice
			if (dice >= 16) {
				logger.info("Employee " + Thread.currentThread().getName() + " can not handle : " + call.getId()
						+ ", level up to : " + (call.getLevel() + 1));
				call.setLevel(1);
			} else {
				employee = availableEmployees.stream().filter(e -> e.getEmployeeType() == EmployeeType.FRESHER)
						.findAny();
				if (!employee.isPresent()) {
					logger.info("No FRESHER");
					return null;
				}
				return employee.get();
			}
		}
		if (call.getLevel() == 1) {
			if (dice2 > 5) {
				logger.info("Employee " + Thread.currentThread().getName() + " can not handle : " + call.getId()
				+ ", level up to : " + (call.getLevel() + 1));
				call.setLevel(2);
			} else {
				employee = availableEmployees.stream().filter(e -> e.getEmployeeType() == EmployeeType.TEAMLEAD)
						.findAny();
				if (!employee.isPresent()) {
					logger.info("No TEAMLEAD");
					return null;
				}
				return employee.get();
			}
		}

		if (call.getLevel() == 2) {
			employee = availableEmployees.stream().filter(e -> e.getEmployeeType() == EmployeeType.PM).findAny();
			if (!employee.isPresent()) {
				logger.info("No PM");
				return null;
			}
			return employee.get();
		}
		return null;

	}

	@Override
	public List<Employee> buildEmployeeList(int number) throws Exception {
		if (number < 2) {
			throw new Exception("need at least 3 Employees");
		}
		List<Employee> list = new ArrayList<>();
		Employee pm = Employee.buildPM();
		list.add(pm);
		Employee tl = Employee.buildTeamLead();
		list.add(tl);
		for (int i = 0; i < number - 2; i++) {
			Employee e = Employee.buildFresher();
			list.add(e);
		}

		return list;
	}

}
