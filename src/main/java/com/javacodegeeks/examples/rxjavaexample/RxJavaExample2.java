package com.javacodegeeks.examples.rxjavaexample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.util.EntityUtils;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rx.apache.http.ObservableHttp;
import rx.apache.http.ObservableHttpResponse;
import rx.functions.Action1;
import rx.functions.Func1;

public class RxJavaExample2 {

	public static void simpleAsync() {
		Flowable.create((FlowableEmitter<String> s) -> {
			try {
				System.out.println("Executing async flowable.");
				Thread.sleep(1000);
				System.out.println("Finished async flowable.");
			} catch (Exception e) {
			}
			s.onComplete();
		}, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.newThread()).subscribe();

		System.out.println("Print finished async method.");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void simpleAsyncWithEmitted() {
		Flowable.create((FlowableEmitter<String> s) -> {
			try {
				System.out.println("Executing async flowable.");
				Thread.sleep(1000);
				System.out.println("Finished async flowable.");
			} catch (Exception e) {
			}
			s.onNext("emitted");
			s.onComplete();
		}, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).subscribe(System.out::println);

		System.out.println("Print finished async method.");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void simpleAsyncAPICalls() {
		String test = "";
		System.out.println("Starting async api");
		Flowable.create(new FlowableOnSubscribe<String>() {
			@Override
			public void subscribe(FlowableEmitter<String> e) throws Exception {
				makeCall("http://localhost:8080/jcg/service/stream/no", test);
			}
		}, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).subscribe(System.out::println);

		System.out.println("Ending async api");

	}

	public static void flatMapAsyncAPICalls() {
		String test = "";
		System.out.println("Starting async api");

		Observable<String> result = Observable.fromArray("1", "2", "3");
		Observable<String> result2 = Observable.fromArray(returnList("http://localhost:8080/jcg/service/stream/no"));
		Observable<String> result4 = Observable.zip(result, result2, (s, s2) -> s + s2);

		Observable<Object> result5 = result4.flatMap((r) -> Observable.just(r.toString()));
		result5.subscribeOn(Schedulers.io()).subscribe(System.out::println);

	}

	@SuppressWarnings("unchecked")
	public static void flatMapAsyncAPICalls2() {
		String test = "";
		System.out.println("Starting async api");

		List<String> list = null;
		String[] strList = new String[0];
		list = makeCall("http://localhost:8080/jcg/service/stream/no/int/list");
		Observable<String> result = Observable.fromArray(list.toArray(strList));
		strList = makeCall("http://localhost:8080/jcg/service/stream/no/string/list", strList);

		Observable<String> result2 = Observable.fromArray(strList);
		Observable<String> result4 = Observable.zip(result, result2, (s, s2) -> s + s2);

		Observable<Object> result5 = result4.flatMap((r) -> Observable.just(r.toString()));
		result5.subscribeOn(Schedulers.io()).subscribe(System.out::println);

	}

	@SuppressWarnings("unchecked")
	public static void flatMapAsyncAPICalls3() {
		String test = "";
		System.out.println("Starting async api");

		List<String> list = null;
		String[] strList = new String[0];
		list = makeCall("http://localhost:8080/jcg/service/stream/no/int/list");
		Flowable<String> result = Flowable.fromArray(list.toArray(strList));
		strList = makeCall("http://localhost:8080/jcg/service/stream/no/string/list", strList);

		Flowable<String> result2 = Flowable.fromArray(strList);
		Flowable<String> result4 = Flowable.zip(result, result2, new BiFunction<String, String, String>() {
			@Override
			public String apply(String t1, String t2) throws Exception {
				System.out.println("Func: " + t1 + t2);
				return t1 + t2;
			}

		});

		result4.subscribe(new Consumer<String>() {

			@Override
			public void accept(String t) throws Exception {
				System.out.printf("Entry %s\n", t);
			}
		}, new Consumer<Throwable>() {
			@Override
			public void accept(Throwable t) throws Exception {
				System.err.printf("Failed to process: %s\n", t);
			}
		}, new Action() {
			@Override
			public void run() throws Exception {
				System.out.println("Done");
			}
		});

		Flowable<String> result5 = result4.flatMap((r) -> Flowable.just(r.toString()));
		result5.subscribe(new Consumer<String>() {

			@Override
			public void accept(String t) throws Exception {
				System.out.println("C-Entry: " + t);

			}

		});

		Subscriber<String> subscriber2 = new Subscriber<String>() {

			@Override
			public void onSubscribe(Subscription s) {
				s.request(Long.MAX_VALUE);
			}

			@Override
			public void onNext(String t) {
				System.out.printf("S-Entry %s\n", t);
			}

			@Override
			public void onError(Throwable t) {
				System.err.printf("Failed to process: %s\n", t);
			}

			@Override
			public void onComplete() {
				System.out.println("Done");
			}

		};
		result5.subscribeOn(Schedulers.io()).subscribe(subscriber2);

		// Flowable.fromArray(1, 2, 3, 4).subscribe(i ->
		// System.out.printf("iEntry %d\n", i),
		// e -> System.err.printf("iFailed to process: %s\n", e), () ->
		// System.out.println("iDone"));

	}


	private static void eventStreamAPI() {
		CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
		ObservableHttp.createGet("http://www.wikipedia.com", httpClient).toObservable().flatMap((r) -> r.getContent())
				.toBlockingObservable();

		// .flatMap({ObservableHttpResponse response ->
		// return response.getContent()
		// .map({byte[] bb-> return new String(bb);});
		// })

		// .subscribeOn(Schedulers.io()).subscribe(System.out::println);

		// .flatMap({ ObservableHttpResponse response ->
		// return response.getContent().map({ byte[] bb ->
		// return new String(bb);
		// });
		// })
		// .toBlockingObservable()
		// .forEach({ String resp ->
		// // this will be invoked once with the response
		// println(resp);
		// });
	}

	public static void eventStreamAPI2() throws IOException, InterruptedException {
		CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
		httpClient.start();
		rx.Observable<ObservableHttpResponse> response = ObservableHttp
				.createGet("http://localhost:8080/jcg/service/stream/event", httpClient).toObservable();
		rx.Observable<String> body = response.take(5).flatMap((r) -> r.getContent().map((b) -> new String(b)));
		
		body.forEach((s) -> System.out.println(s));
		Thread.sleep(6000);
		httpClient.close();
	}

	public static void eventStreamAPI3() {
		CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
		httpClient.start();
		HttpGet request = new HttpGet("http://localhost:8080/jcg/service/stream/yes");
		HttpAsyncRequestProducer httpRequest = HttpAsyncMethods.create(request);
		ObservableHttp<ObservableHttpResponse> observeHttp = ObservableHttp.createRequest(httpRequest, httpClient);
		//Observable<Object> response = (Observable<Object>) observeHttp.toObservable().flatMap((r) -> r.getContent().map((b) -> new String(b)));
		rx.Observable<String> body = observeHttp.toObservable().flatMap(new Func1<ObservableHttpResponse, rx.Observable<String>>() {
			@Override
			public rx.Observable<String> call(ObservableHttpResponse t1) {
				System.out.println("test");
				return t1.getContent().map(new Func1<byte[], String>() {
					@Override
					public String call(byte[] t1) {
						System.out.println("testinner "+ t1.toString());
						return new String(t1);
					}
					
				});
			}
		});
		observeHttp.toObservable().subscribe(new Action1<ObservableHttpResponse>() {
			//System.out.println("testhttp");
			@Override
			public void call(ObservableHttpResponse t1) {
				System.out.println("testhttp ");
				HttpResponse r = t1.getResponse();
				System.out.println(r.getStatusLine().getStatusCode());
				System.out.println(r.getEntity().isChunked());
				try {
					System.out.println(r.getEntity().getContentLength());
					System.out.println(EntityUtils.toString(r.getEntity(), "UTF-8"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				rx.Observable<byte[]> bytes = t1.getContent();
				bytes.subscribe(new Action1<byte[]>() {
					@Override
					public void call(byte[] t1) {
						System.out.println("testinnerhttp1 ");
						System.out.println(new String(t1));
						
					}});
				
				rx.Observable<String> test = bytes.map(new Func1<byte[], String>() {
					@Override
					public String call(byte[] t1) {
						System.out.println("testinnerhttp2 ");
						System.out.println(new String(t1));
						return new String(t1);
					}
					
				});
			}
		}
		, new Action1<Throwable>() {
			@Override
			public void call(Throwable t1) {
				System.out.println("Error: " + t1.getMessage());
			}
		}
		);
		
		//body.toBlocking().forEach((s) -> System.out.println(s));
		body.subscribe(new Action1<String>() {

			@Override
			public void call(String t1) {
				System.out.println("on Next test");
				System.out.println(t1);
				
			}
			
		});
	}

	public static void eventStreamAPI4() {
		//CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
		final RequestConfig requestConfig = RequestConfig.custom()
			     .setSocketTimeout(3000)
			     .setConnectTimeout(3000).build();
		final CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
			     .setDefaultRequestConfig(requestConfig)
			     .setMaxConnPerRoute(20)
			     .setMaxConnTotal(50)
			     .build();
		httpclient.start();
		HttpGet request = new HttpGet("http://localhost:8080/jcg/service/stream/yes");
		HttpAsyncRequestProducer httpRequest = HttpAsyncMethods.create(request);
		ObservableHttp<ObservableHttpResponse> observeHttp = ObservableHttp.createRequest(httpRequest, httpclient);
		rx.Observable<String> body = observeHttp.toObservable().flatMap((r) -> r.getContent().map((b) -> new String(b)));
		body.subscribe(new Action1<String>(){
			@Override
			public void call(String t1) {
				System.out.println("test:");
			}
			
		});
	}

	public static void eventStreamAPI5() {
		//CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
		//Observable<ObservableHttpResponse> observable = ObservableHttp.createGet("http://localhost:8080/jcg/service/stream/yes", client).toObservable();
		//.flatMap(response -> response.getContent().map(String::new));;
//		CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
//		httpClient.start();
//		HttpGet request = new HttpGet("http://localhost:8080/jcg/service/stream/yes");
//		HttpAsyncRequestProducer httpRequest = HttpAsyncMethods.create(request);
//		ObservableHttp<ObservableHttpResponse> observeHttp = ObservableHttp.createRequest(httpRequest, httpClient);
//		rx.Observable<ObservableHttpResponse> httpObs =  observeHttp.toObservable();
//		//httpObs.buffer(10, 1).forEach((b) -> System.out.println("next"));;
//		//httpObs.
//		//httpObs.forEach((b) -> System.out.println("next"));
//		//httpObs.subscribe((b) -> System.out.println("next"), (t) -> System.out.println("thrown"), () -> System.out.println("Done"));
//		rx.Observable<String> body = httpObs.flatMap((r) -> r.getContent().map((b) -> new String(b)));
//		body.subscribe((b) -> System.out.println("next"), (t) -> System.out.println("thrown"), () -> System.out.println("Done"));
	}
	
	public static void eventStreamAPI_NoObservable() throws InterruptedException, ExecutionException, IOException {
		CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
		httpClient.start();
		HttpGet request = new HttpGet("http://localhost:8080/jcg/service/stream/yes");
	     
	    Future<HttpResponse> future = httpClient.execute(request, new FutureCallback<HttpResponse>() {

			@Override
			public void completed(HttpResponse result) {
				String responseString = "yuck";
				try {
					responseString = EntityUtils.toString(result.getEntity(), "UTF-8");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(responseString);
			}

			@Override
			public void failed(Exception ex) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void cancelled() {
				// TODO Auto-generated method stub
				
			}
	    	
	    });
	    HttpResponse response = future.get();
	    //assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
	    httpClient.close();
	}
	
	private static <T> T makeCall(String URI, T clazz) {
		RestTemplate restTemplate = new RestTemplate();
		T result = (T) restTemplate.getForObject(URI, clazz.getClass());
		// System.out.println(result.toString());
		return result;
	}

	private static List makeCall(String URI) {
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new MappingJackson2HttpMessageConverter());
		restTemplate.setMessageConverters(converters);
		Object[] result = restTemplate.getForObject(URI, Object[].class);
		// System.out.println(result.toString());
		return Arrays.asList(result);
	}

	private static String[] returnList(String URI) {
		return (String[]) Arrays.asList("test", "test1", "test2").toArray();

	}
}