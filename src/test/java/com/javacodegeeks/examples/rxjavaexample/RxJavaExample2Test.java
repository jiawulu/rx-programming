package com.javacodegeeks.examples.rxjavaexample;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.javacodegeeks.examples.rxjavaexample.RxJavaExample2;

public class RxJavaExample2Test {

	@Test
	public void testSimpleAsync() {
		RxJavaExample2.simpleAsync();
	}

	@Test
	public void testSimpleAsyncEmmitted() {
		RxJavaExample2.simpleAsyncWithEmitted();
	}
	
	@Test
	public void testSimpleAsyncAPICalls() throws InterruptedException {
		RxJavaExample2.simpleAsyncAPICalls();
		Thread.sleep(3000);
	}

	@Test
	public void testFlatMapAsyncAPICalls() throws InterruptedException {
		RxJavaExample2.flatMapAsyncAPICalls();
		Thread.sleep(3000);
	}
	
	@Test
	public void testFlatMapAsyncAPICalls2() throws InterruptedException {
		RxJavaExample2.flatMapAsyncAPICalls2();
		Thread.sleep(3000);
	}

	@Test
	public void testFlatMapAsyncAPICalls3() throws InterruptedException {
		RxJavaExample2.flatMapAsyncAPICalls3();
		Thread.sleep(3000);
	}
	
	
	@Test
	public void testEventStreamsAPI2() throws InterruptedException, IOException {
		RxJavaExample2.eventStreamAPI2();
		Thread.sleep(6000);
	}
	
	@Test
	public void testEventStreamsAPI3() throws InterruptedException, ExecutionException, IOException {
		RxJavaExample2.eventStreamAPI3();
		Thread.sleep(30000);
	}
	
	@Test
	public void testEventStreamsAPI4() throws InterruptedException, ExecutionException, IOException {
		RxJavaExample2.eventStreamAPI4();
		Thread.sleep(6000);
	}
	
	@Test
	public void testEventStreamsAPI5() throws InterruptedException, ExecutionException, IOException {
		RxJavaExample2.eventStreamAPI5();
		Thread.sleep(30000);
	}
	
	@Test
	public void testEventStreamAPI_NoObservable() throws InterruptedException, ExecutionException, IOException {
		RxJavaExample2.eventStreamAPI_NoObservable();
		Thread.sleep(3000);
	}
	
	private Object equalTo(int i) {
		// TODO Auto-generated method stub
		return null;
	}
}
