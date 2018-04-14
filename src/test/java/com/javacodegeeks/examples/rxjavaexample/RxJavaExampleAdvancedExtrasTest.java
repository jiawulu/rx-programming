package com.javacodegeeks.examples.rxjavaexample;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RxJavaExampleAdvancedExtrasTest {

	private static Logger logger = LoggerFactory.getLogger(RxJavaExampleAdvancedExtrasTest.class);

	@Test
	public void testSimpleAsync() throws InterruptedException {
		RxJavaExampleAdvancedExtras.simpleAsync();
		Thread.sleep(3000);
	}

	@Test
	public void testSimpleAsyncMulti() throws InterruptedException {
		RxJavaExampleAdvancedExtras.simpleAsyncWithEmitted();;
		Thread.sleep(3000);
	}
	
	@Test
	public void testSimpleAsyncArrayIo() throws InterruptedException {
		RxJavaExampleAdvancedExtras.simpleArrayIo();
		Thread.sleep(3000);
	}
	
	@Test
	public void testSimpleAsyncArrayNewThread() throws InterruptedException {
		RxJavaExampleAdvancedExtras.simpleArrayNewThread();
		Thread.sleep(3000);
	}
	
	@Test
	public void testSimpleAsyncEmmitted() {
		RxJavaExampleAdvancedExtras.simpleAsyncWithEmitted();
	}
	
	@Test
	public void testSimpleAsyncAPICalls() throws InterruptedException {
		RxJavaExampleAdvancedExtras.simpleAsyncAPICalls();
		Thread.sleep(3000);
	}
	
	@Test
	public void testMultipleAsyncAPICalls() throws InterruptedException {
		RxJavaExampleAdvancedExtras.multipleAsyncAPICalls();
		Thread.sleep(3000);
	}
	
	@Test
	public void testMultipleAsyncAPICallsWithThreads() throws InterruptedException {
		RxJavaExampleAdvancedExtras.multipleAsyncAPICallsWithThreads();
		Thread.sleep(3000);
	}
	
	@Test
	public void testFlatMapAsyncAPICalls() throws InterruptedException {
		RxJavaExampleAdvancedExtras.flatMapAsyncAPICalls();;
		Thread.sleep(3000);
	}
	
	@Test
	public void testFlatMapAsyncAPICalls2() throws InterruptedException {
		RxJavaExampleAdvancedExtras.flatMapAsyncAPICalls2();
		Thread.sleep(3000);
	}

	@Test
	public void testFlatMapAsyncAPICalls3() throws InterruptedException {
		RxJavaExampleAdvancedExtras.flatMapAsyncAPICalls3();
		Thread.sleep(3000);
	}
	
	
	@Test
	public void testEventStreamsAPI2() throws InterruptedException, IOException, URISyntaxException {
		RxJavaExampleAdvancedExtras.streamObserable2();
		Thread.sleep(6000);
	}
	

}
