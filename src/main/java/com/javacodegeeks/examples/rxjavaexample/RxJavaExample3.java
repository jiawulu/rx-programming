package com.javacodegeeks.examples.rxjavaexample;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;

import rx.Observable;
import rx.apache.http.ObservableHttp;
import rx.apache.http.ObservableHttpResponse;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxJavaExample3 {

	    static int counter = 0;
	    
	    public static void streamObserable() throws URISyntaxException, IOException, InterruptedException {
	        System.out.println("---- executeStreamingViaObservableHttpWithForEach");
	        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
	        httpclient.start();
	        
	        // URL against https://github.com/Netflix/Hystrix/tree/master/hystrix-examples-webapp
	        // More information at https://github.com/Netflix/Hystrix/tree/master/hystrix-contrib/hystrix-metrics-event-stream
	        ObservableHttp.createRequest(HttpAsyncMethods.createGet("http://localhost:8080/jcg/service/stream/event"), httpclient)
	        //ObservableHttp.createRequest(HttpAsyncMethods.createGet("http://localhost:8989/hystrix-examples-webapp/hystrix.stream"), client)
	                .toObservable()
	                .flatMap(new Func1<ObservableHttpResponse, Observable<String>>() {

	                    @Override
	                    public Observable<String> call(ObservableHttpResponse response) {
	                        return response.getContent().map(new Func1<byte[], String>() {

	                            @Override
	                            public String call(byte[] bb) {
	                            	System.out.println("counter: " + RxJavaExample3.counter++);
	                                return new String(bb);
	                            }

	                        });
	                    }
	                })
	                .filter(new Func1<String, Boolean>() {

	                    @Override
	                    public Boolean call(String t1) {
	                        return !t1.startsWith(": ping");
	                    }
	                })
	                .take(3)
	                //.toBlocking()
	                //.sub
	                .subscribeOn(Schedulers.io())
	                .subscribe(new Action1<String>() {

	                    @Override
	                    public void call(String resp) {
	                    	System.out.println("timestamp " + SimpleDateFormat.getDateTimeInstance().format(new Date().getTime()));
	                        System.out.println(resp);
	                    }
	                });
	        
	        Thread.sleep(10000);
	        httpclient.close();
	    }
	    
	    
	    public static void streamObserable2() throws URISyntaxException, IOException, InterruptedException {
	        System.out.println("---- executeStreamingViaObservableHttpWithForEach");
	        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
	        httpclient.start();
   
	        // URL against https://github.com/Netflix/Hystrix/tree/master/hystrix-examples-webapp
	        // More information at https://github.com/Netflix/Hystrix/tree/master/hystrix-contrib/hystrix-metrics-event-stream
	        ObservableHttp.createRequest(HttpAsyncMethods.createGet("http://localhost:8080/jcg/service/stream/event2"), httpclient)
	        //ObservableHttp.createRequest(HttpAsyncMethods.createGet("http://localhost:8989/hystrix-examples-webapp/hystrix.stream"), client)
	                .toObservable()
	                .flatMap(new Func1<ObservableHttpResponse, Observable<String>>() {

	                    @Override
	                    public Observable<String> call(ObservableHttpResponse response) {
	                        return response.getContent().map(new Func1<byte[], String>() {

	                            @Override
	                            public String call(byte[] bb) {
	    	                    	System.out.println("timestamp inner " + SimpleDateFormat.getDateTimeInstance().format(new Date().getTime()));
	                            	System.out.println("counter: " + RxJavaExample3.counter++);
	                                return new String(bb);
	                            }

	                        });
	                    }
	                })
	                .buffer(5, TimeUnit.SECONDS, 5, Schedulers.io())
	                .subscribeOn(Schedulers.io())
	                .subscribe(new Action1<List<String>>() {

	                    @Override
	                    public void call(List<String> resp) {
	                    	System.out.println("timestamp " + SimpleDateFormat.getDateTimeInstance().format(new Date().getTime()));
	                        System.out.println(resp.toString());
	                    }
	                });
	        
	        Thread.sleep(20000);
	    }
}
