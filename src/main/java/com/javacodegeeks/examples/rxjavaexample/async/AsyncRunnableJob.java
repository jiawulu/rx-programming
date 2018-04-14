package com.javacodegeeks.examples.rxjavaexample.async;

public class AsyncRunnableJob implements Runnable {

	@Override
	public void run() {
		try {
			Thread.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
