package com.javacodegeeks.examples.rxjavaexample;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sun.tools.javac.comp.Flow;
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

public class RxJavaExampleAdvanced {

	private static Logger logger = LoggerFactory.getLogger(RxJavaExampleAdvanced.class);

	static int counter = 0;

	public static void simpleAsync() {
		logger.info("Starting simple async");

		Flowable.create((FlowableEmitter<String> s) -> {
			try {
				logger.info("Executing async flowable.");
				Thread.sleep(200);
				s.onNext("james");
				logger.info("Finished async flowable.");
			} catch (Exception e) {
			}
			logger.info("-------------------------");
			s.onComplete();
		}, BackpressureStrategy.BUFFER).repeat(4).observeOn(Schedulers.from(Executors.newFixedThreadPool(5))).subscribeOn(Schedulers.newThread()).subscribe(new Consumer<String>() {
			@Override
			public void accept(String s) throws Exception {
				Thread.sleep(400);
				logger.error(">> accept value " + s);
			}
		});

		logger.info("Finished simple async");
	}

	public static void simpleAsyncAPICalls() {
		logger.info("Starting async api");
		logger.info("Main Thread: {}", Thread.currentThread().getName());
		Flowable.create((FlowableEmitter<String> s) -> {
			try {
				String result = makeCallString("http://localhost:8080/jcg/service/stream/no");
				logger.info("Emitted thread: {}", Thread.currentThread().getName());
				logger.info("Result: {}", result);
				s.onNext(result);
			} catch (Exception e) {
				s.onError(e);
			}
			s.onComplete();
		}, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.newThread()).subscribe(logger::info);

		logger.info("Ending async api");
	}

	public static void multipleAsyncAPICalls() {
		logger.info("Starting multi async api");
		logger.info("Main Thread: {}", Thread.currentThread().getName());
		Flowable.fromArray("http://localhost:8080/jcg/service/stream/no", "http://localhost:8080/jcg/service/stream/no",
				"http://localhost:8080/jcg/service/stream/no", "http://localhost:8080/jcg/service/stream/no")
				.map(new Function<String, String>() {
					int resultCount = 0;

					@Override
					public String apply(String t) throws Exception {
						String result = makeCallString(t);
						logger.info("Emitted thread: {}", Thread.currentThread().getName());
						logger.info("Result {}: {}", resultCount++, result);
						return result + " on " + Thread.currentThread().getName();
					}
				}).subscribeOn(Schedulers.newThread()).subscribe(logger::info);
		logger.info("Ending multi async api");
	}

	public static void simpleAsyncMulti() {
		Observable.just(1, 2)
				.flatMap(item -> Observable.just(item).subscribeOn(Schedulers.newThread())
						.doOnNext(i -> System.out.println("Thread:" + Thread.currentThread())))
				.subscribe(System.out::println);
	}

	public static void multipleAsyncAPICallsWithAllNewThreads() {
		Observable
				.just("http://localhost:8080/jcg/service/stream/no", "http://localhost:8080/jcg/service/stream/no",
						"http://localhost:8080/jcg/service/stream/no", "http://localhost:8080/jcg/service/stream/no")
				.flatMap(item -> Observable.just(item).subscribeOn(Schedulers.newThread()).doOnNext(i -> {
					logger.info(makeCallString(i));
					logger.info(Thread.currentThread().toString());
				})).subscribe(System.out::println);
	}

	public static void flatMapZipAsyncAPICalls() {
		Flowable<String> result = Flowable.create((FlowableEmitter<String> s) -> {
			try {
				String r = makeCallString("http://localhost:8080/jcg/service/stream/no/int/list");
				logger.info("Emitted thread: {}", Thread.currentThread().getName());
				logger.info("Result: {}", r);
				s.onNext(r);
			} catch (Exception e) {
				s.onError(e);
			}
			s.onComplete();
		}, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.newThread());
		Flowable<String> result2 = Flowable.create((FlowableEmitter<String> s) -> {
			try {
				String r = makeCallString("http://localhost:8080/jcg/service/stream/no/string/list");
				logger.info("Emitted thread: {}", Thread.currentThread().getName());
				logger.info("Result: {}", r);
				s.onNext(r);
			} catch (Exception e) {
				s.onError(e);
			}
			s.onComplete();
		}, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.newThread());
		Flowable.zip(result, result2, (s, s2) -> s + s2).subscribe(System.out::println);
	}

	public static void streamObserable() throws URISyntaxException, IOException, InterruptedException {
		logger.info("Executing Streaming Observable Over Http");
		CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
		httpclient.start();

		ObservableHttp
				.createRequest(HttpAsyncMethods.createGet("http://localhost:8080/jcg/service/stream/event2"),
						httpclient)
				.toObservable().flatMap(new Func1<ObservableHttpResponse, rx.Observable<String>>() {
					@Override
					public rx.Observable<String> call(ObservableHttpResponse response) {
						return response.getContent().map(new Func1<byte[], String>() {

							@Override
							public String call(byte[] bb) {
								logger.info("Emitted thread: {}", Thread.currentThread().getName());
								logger.info("timestamp inner "
										+ SimpleDateFormat.getDateTimeInstance().format(new Date().getTime()));
								logger.info("counter: " + RxJavaExampleAdvanced.counter++);
								return new String(bb);
							}

						});
					}
				}).buffer(5, TimeUnit.SECONDS, 5, rx.schedulers.Schedulers.io())
				.subscribeOn(rx.schedulers.Schedulers.io()).subscribe(new Action1<List<String>>() {

					@Override
					public void call(List<String> resp) {
						logger.info("Emitted thread: {}", Thread.currentThread().getName());
						logger.info("timestamp " + SimpleDateFormat.getDateTimeInstance().format(new Date().getTime()));
						logger.info(resp.toString());
					}
				});

	}

	private static <T> T makeCall(String URI, T clazz) {
		RestTemplate restTemplate = new RestTemplate();
		@SuppressWarnings("unchecked")
		T result = (T) restTemplate.getForObject(URI, clazz.getClass());
		return result;
	}

	private static String makeCallString(String URI) {
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(URI, String.class);
		return result;
	}

	private static List<Object> makeCall(String URI) {
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new MappingJackson2HttpMessageConverter());
		restTemplate.setMessageConverters(converters);
		Object[] result = restTemplate.getForObject(URI, Object[].class);
		return Arrays.asList(result);
	}

	private static String[] returnList() {
		return (String[]) Arrays.asList("test", "test1", "test2").toArray();

	}
}
