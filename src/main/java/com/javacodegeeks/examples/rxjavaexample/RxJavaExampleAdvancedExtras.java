package com.javacodegeeks.examples.rxjavaexample;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import rx.apache.http.ObservableHttp;
import rx.apache.http.ObservableHttpResponse;
import rx.functions.Action1;
import rx.functions.Func1;

public class RxJavaExampleAdvancedExtras {

	private static Logger logger = LoggerFactory.getLogger(RxJavaExampleAdvancedExtras.class);

	static int counter = 0;

	public static void simpleAsync() {
		Flowable.create((FlowableEmitter<String> s) -> {
			try {
				logger.info("Executing async flowable.");
				Thread.sleep(1000);
				logger.info("Finished async flowable.");
			} catch (Exception e) {
			}
			s.onComplete();
		}, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.newThread()).subscribe();

		logger.info("Print finished async method.");
	}

	public static void simpleArrayIo() {
		logger.info("Thread {}", Thread.currentThread().getName());
		Flowable.fromArray(1, 2, 3, 4).subscribeOn(Schedulers.io()).subscribe(
				i -> logger.info("Thread {}, Integer {}", Thread.currentThread().getName(), i.intValue()),
				e -> logger.error("failed to process"));
	}

	public static void simpleArrayNewThread() {
		logger.info("Thread {}", Thread.currentThread().getName());
		Flowable.fromArray(1, 2, 3, 4).subscribeOn(Schedulers.newThread()).observeOn(Schedulers.newThread()).subscribe(
				i -> logger.info("Thread {}, Integer {}", Thread.currentThread().getName(), i.intValue()),
				e -> logger.error("failed to process"));
	}

	public static void simpleAsyncWithEmitted() {
		Flowable.create((FlowableEmitter<String> s) -> {
			try {
				logger.info("Executing async flowable. " + Thread.currentThread().getName());
				Thread.sleep(1000);
				logger.info("Finished async flowable.");
			} catch (Exception e) {
			}
			s.onNext("emitted " + Thread.currentThread().getName());
			s.onNext("emitted 2 " + Thread.currentThread().getName());
			s.onComplete();
		}, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.newThread()).subscribe(System.out::println);

		logger.info("Print finished async method.");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			logger.error("Interrupted thread.", e);
		}
	}

	public static void simpleAsyncAPICalls() {
		String test = "";
		logger.info("Starting async api");
		Flowable.create(new FlowableOnSubscribe<String>() {
			@Override
			public void subscribe(FlowableEmitter<String> e) throws Exception {
				makeCallString("http://localhost:8080/jcg/service/stream/no");
				logger.info("emitted thread" + Thread.currentThread().getName());
			}
		}, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).subscribe(System.out::println);

		logger.info("Ending async api");

	}

	public static void multipleAsyncAPICalls() {
		logger.info("Thread {}", Thread.currentThread().getName());
		logger.info("Starting async api");
		Flowable.fromArray("http://localhost:8080/jcg/service/stream/no", "http://localhost:8080/jcg/service/stream/no",
				"http://localhost:8080/jcg/service/stream/no", "http://localhost:8080/jcg/service/stream/no")
				.map(new Function<String, String>() {
					@Override
					public String apply(String t) throws Exception {
						makeCallString(t);
						return Thread.currentThread().getName();
					}
				}).subscribeOn(Schedulers.newThread()).subscribe(System.out::println);
		logger.info("Ending async api");
	}

	public static void multipleAsyncAPICallsWithThreads() {
		logger.info("Thread {}", Thread.currentThread().getName());
		logger.info("Starting async api");
		Flowable.fromArray("http://localhost:8080/jcg/service/stream/no", "http://localhost:8080/jcg/service/stream/no",
				"http://localhost:8080/jcg/service/stream/no", "http://localhost:8080/jcg/service/stream/no")
				.flatMap(new Function<String, Publisher<String>>() {
					@Override
					public Publisher<String> apply(String t) throws Exception {
						logger.info("Publisher Thread {}", Thread.currentThread().getName());
						return Flowable.just(t);
					}
				}).subscribeOn(Schedulers.newThread()).doOnNext(new Consumer<String>() {
					@Override
					public void accept(String t) throws Exception {
						logger.info("Consumer Thread {}", Thread.currentThread().getName());
						makeCallString(t);
					}
				}).subscribe(System.out::println);
		logger.info("Ending async api");
	}

	public static void multipleAsyncAPICallsWithThreads2() {
		logger.info("Thread {}", Thread.currentThread().getName());
		logger.info("Starting async api");
		Observable
				.just("http://localhost:8080/jcg/service/stream/no", "http://localhost:8080/jcg/service/stream/no",
						"http://localhost:8080/jcg/service/stream/no", "http://localhost:8080/jcg/service/stream/no")
				.flatMap(new Function<String, ObservableSource<String>>() {
					@Override
					public ObservableSource<String> apply(String t) throws Exception {
						logger.info("Publisher Thread {}", Thread.currentThread().getName());
						return Observable.just(t);
					}
				}).subscribeOn(Schedulers.newThread()).doOnNext(new Consumer<String>() {
					@Override
					public void accept(String t) throws Exception {
						logger.info("Consumer Thread {}", Thread.currentThread().getName());
						makeCallString(t);
					}
				}).subscribe(System.out::println);
		logger.info("Ending async api");
	}

	public static void multipleAsyncAPICallsWithThreads3() {
		Observable
				.just("http://localhost:8080/jcg/service/stream/no", "http://localhost:8080/jcg/service/stream/no",
						"http://localhost:8080/jcg/service/stream/no", "http://localhost:8080/jcg/service/stream/no")
				.flatMap(item -> Observable.just(item).subscribeOn(Schedulers.newThread()).doOnNext(i -> {
					logger.info(makeCallString(i));
					logger.info(Thread.currentThread().toString());
				})).subscribe(System.out::println);
	}

	public static void flatMapAsyncAPICalls() {
		String test = "";
		logger.info("Starting async api");

		Observable<String> result = Observable.fromArray("1", "2", "3");
		Observable<String> result2 = Observable.fromArray(returnList("http://localhost:8080/jcg/service/stream/no"));
		Observable<String> result4 = Observable.zip(result, result2, (s, s2) -> s + s2);

		Observable<Object> result5 = result4.flatMap((r) -> Observable.just(r.toString()));
		result5.subscribeOn(Schedulers.io()).subscribe(System.out::println);

	}

	@SuppressWarnings("unchecked")
	public static void flatMapAsyncAPICalls2() {
		String test = "";
		logger.info("Starting async api");

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
		logger.info("Starting async api");

		List<String> list = null;
		String[] strList = new String[0];
		list = makeCall("http://localhost:8080/jcg/service/stream/no/int/list");
		Flowable<String> result = Flowable.fromArray(list.toArray(strList));
		strList = makeCall("http://localhost:8080/jcg/service/stream/no/string/list", strList);

		Flowable<String> result2 = Flowable.fromArray(strList);
		Flowable<String> result4 = Flowable.zip(result, result2, new BiFunction<String, String, String>() {
			@Override
			public String apply(String t1, String t2) throws Exception {
				logger.info("Func: " + t1 + t2);
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
				logger.info("Done");
			}
		});

		Flowable<String> result5 = result4.flatMap((r) -> Flowable.just(r.toString()));
		result5.subscribe(new Consumer<String>() {

			@Override
			public void accept(String t) throws Exception {
				logger.info("C-Entry: " + t);

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
				logger.info("Done");
			}

		};
		result5.subscribeOn(Schedulers.io()).subscribe(subscriber2);

		// Flowable.fromArray(1, 2, 3, 4).subscribe(i ->
		// System.out.printf("iEntry %d\n", i),
		// e -> System.err.printf("iFailed to process: %s\n", e), () ->
		// logger.info("iDone"));

	}

	public static void streamObserable2() throws URISyntaxException, IOException, InterruptedException {
		logger.info("---- executeStreamingViaObservableHttpWithForEach");
		CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
		httpclient.start();

		// URL against
		// https://github.com/Netflix/Hystrix/tree/master/hystrix-examples-webapp
		// More information at
		// https://github.com/Netflix/Hystrix/tree/master/hystrix-contrib/hystrix-metrics-event-stream
		ObservableHttp
				.createRequest(HttpAsyncMethods.createGet("http://localhost:8080/jcg/service/stream/event2"),
						httpclient)
				// ObservableHttp.createRequest(HttpAsyncMethods.createGet("http://localhost:8989/hystrix-examples-webapp/hystrix.stream"),
				// client)
				.toObservable().flatMap(new Func1<ObservableHttpResponse, rx.Observable<String>>() {

					@Override
					public rx.Observable<String> call(ObservableHttpResponse response) {
						return response.getContent().map(new Func1<byte[], String>() {

							@Override
							public String call(byte[] bb) {
								logger.info("timestamp inner "
										+ SimpleDateFormat.getDateTimeInstance().format(new Date().getTime()));
								logger.info("counter: " + RxJavaExample3.counter++);
								return new String(bb);
							}

						});
					}
				}).buffer(5, TimeUnit.SECONDS, 5, rx.schedulers.Schedulers.io())
				.subscribeOn(rx.schedulers.Schedulers.io()).subscribe(new Action1<List<String>>() {

					@Override
					public void call(List<String> resp) {
						logger.info("timestamp " + SimpleDateFormat.getDateTimeInstance().format(new Date().getTime()));
						logger.info(resp.toString());
					}
				});

		Thread.sleep(20000);
	}

	private static <T> T makeCall(String URI, T clazz) {
		RestTemplate restTemplate = new RestTemplate();
		T result = (T) restTemplate.getForObject(URI, clazz.getClass());
		// logger.info(result.toString());
		return result;
	}

	private static String makeCallString(String URI) {
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(URI, String.class);
		return result;
	}

	private static List makeCall(String URI) {
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new MappingJackson2HttpMessageConverter());
		restTemplate.setMessageConverters(converters);
		Object[] result = restTemplate.getForObject(URI, Object[].class);
		// logger.info(result.toString());
		return Arrays.asList(result);
	}

	private static String[] returnList(String URI) {
		return (String[]) Arrays.asList("test", "test1", "test2").toArray();

	}
}
