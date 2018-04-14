package com.javacodegeeks.examples.rxjavaexample;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.javacodegeeks.examples.rxjavaexample.RxJavaExample3;

public class RxJavaExample3Test {

	@Test
	public void test() throws URISyntaxException, IOException, InterruptedException {
		RxJavaExample3.streamObserable();
	}

	@Test
	public void test2() throws URISyntaxException, IOException, InterruptedException {
		RxJavaExample3.streamObserable2();
	}
}
