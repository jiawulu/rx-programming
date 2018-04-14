package com.javacodegeeks.examples.rxjavaexample;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RxJavaExampleAdvancedTest {

	private static Logger logger = LoggerFactory.getLogger(RxJavaExampleAdvancedTest.class);

	@Test
	public void testSimpleAsync() throws InterruptedException {
		RxJavaExampleAdvanced.simpleAsync();
		Thread.sleep(3000);
	}

	@Test
	public void testSimpleAsyncMulti() throws InterruptedException {
		RxJavaExampleAdvanced.simpleAsyncMulti();
		Thread.sleep(3000);
	}
	
	@Test
	public void testSimpleAsyncAPICalls() throws InterruptedException {
		RxJavaExampleAdvanced.simpleAsyncAPICalls();
		Thread.sleep(3000);
	}
	
	@Test
	public void testMultipleAsyncAPICalls() throws InterruptedException {
		RxJavaExampleAdvanced.multipleAsyncAPICalls();
		Thread.sleep(3000);
	}
	
	@Test
	public void testMultipleAsyncAPICallsWithThreads() throws InterruptedException {
		RxJavaExampleAdvanced.multipleAsyncAPICallsWithAllNewThreads();
		Thread.sleep(3000);
	}
	
	@Test
	public void flatMapZipAsyncAPICalls() throws InterruptedException {
		RxJavaExampleAdvanced.flatMapZipAsyncAPICalls();
		Thread.sleep(3000);
	}
	
	@Test
	public void testEventStreamsAPI() throws InterruptedException, IOException, URISyntaxException {
		RxJavaExampleAdvanced.streamObserable();
		Thread.sleep(25000);
	}
	

}
