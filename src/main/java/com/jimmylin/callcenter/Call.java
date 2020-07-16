package com.jimmylin.callcenter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Call {

    private static final Logger logger = LoggerFactory.getLogger(Call.class);
	
	// in seconds, assumption in 5 to 10 seconds 
	private Integer duration;
	
	public Integer getDuration() {
		return duration;
	}

	private static final Integer MIN_DURATION = 5;
	
	private static final Integer MAX_DURATION = 10;
	
	// an numeric id in order 
	private String id;

	public String getId() {
		return id;
	}

	private Integer level;
	
	public Call(String id,Integer duration) {
		this.id = id;
        this.duration = duration;
        level = 0;
    }
	
	// 
	public static Call BuildACall(String id) {
		return new Call(id, ThreadLocalRandom.current().nextInt(MIN_DURATION, MAX_DURATION + 1));
    }
	
	public static List<Call> buildListOfRandomCalls(Integer listSize) {
        List<Call> callList = new ArrayList<>();
        for (int i = 1; i <= listSize; i++) {
            callList.add(BuildACall(String.valueOf(i)));
        }
        return callList;
    }
	
	public void printCall() {
		logger.debug("Call id: {}, duration: {}",id, duration);
	}

	public Integer getLevel() {
		return level;
	}

	public synchronized void setLevel(Integer level) {
		this.level = level;
	}
}
