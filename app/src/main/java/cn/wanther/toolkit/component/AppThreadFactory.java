package cn.wanther.toolkit.component;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class AppThreadFactory implements ThreadFactory {
	
	private String mName = "Thread";
	private AtomicInteger mCount = new AtomicInteger();
	
	public AppThreadFactory(){}
	
	public void setName(String name){
		mName = name;
	}
	
	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, mName + " # " + mCount.getAndIncrement());
	}

}
