package com.javacodegeeks.examples.rxjavaexample;

import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RxJavaExampleApplication {

	public static void main(String[] args) {
		Optional<CommandLine> cl = setupCommandLineParams(args);
		
		if (args==null || args.length <= 0 || cl.get().hasOption("ssv")) {
			simpleStartVerbose();
		}
		
		if (args==null || args.length <= 0 || cl.get().hasOption("ss")) {
			simpleStart();
		}
		
		if (args==null || args.length <= 0 || cl.get().hasOption("sa")) {
			simpleArray();
		}
		
		if (args==null || args.length <= 0 || cl.get().hasOption("saf")) {
			simpleArrayFull();
		}
		
		if (args==null || args.length <= 0 || cl.get().hasOption("sm")) {
			simpleMap();
		}
		
		if (args==null || args.length <= 0 || cl.get().hasOption("sfm")) {
			simpleFlatMap();
		}
		
		if (args==null || args.length <= 0 || cl.get().hasOption("se")) {
			subscriberExample();
		}
		
		if (args==null || args.length <= 0 || cl.get().hasOption("ce")) {
			consumerExample();
		}

		if (args==null || args.length <= 0 || cl.get().hasOption("fe")) {
			flowableExample();
		}
		
		if (args == null || args.length <= 0 || cl.get().hasOption("fv")) {
			flowableVerbose();
		}
		
		if (args == null || args.length <= 0 || cl.get().hasOption("as")) {
			RxJavaExample2.simpleAsync();
			RxJavaExample2.simpleAsyncWithEmitted();
		}
		
	}

	public static void simpleStartVerbose() {
		Flowable.just("Hello world").subscribe(new Consumer<String>() {
			@Override
			public void accept(String t) throws Exception {
				System.out.println(t);
			}
		});
	}

	public static void simpleStart() {
		Flowable.just("Hello world").subscribe(System.out::println);
	}

	public static void simpleArray() {
		Flowable.fromArray(1, 2, 3, 4).subscribe(System.out::println);
	}

	public static void simpleArrayFull() {
		Flowable.fromArray(1, 2, 3, 4).subscribe(i -> System.out.printf("Entry %d\n", i),
				e -> System.err.printf("Failed to process: %s\n", e), () -> System.out.println("Done"));
	}
	
	public static void simpleMap() {
		Flowable.just("1").map(s -> Integer.parseInt(s)).subscribe(System.out::println);
	}

	public static void simpleFlatMap() {

		Observable.fromArray(1, 2, 3, 4).flatMap(new Function<Integer, ObservableSource<Integer>>() {
			@Override
			public ObservableSource<Integer> apply(Integer t) throws Exception {
				return Observable.just(t + 50);
			}
		}).subscribe(System.out::println);

		Observable.fromArray(1, 2, 3, 4).flatMap(t -> Observable.just(t + 50)).subscribe(System.out::println);

		Observable.fromArray(1, 2, 3, 4).flatMap(t -> Observable.just(Integer.toString(t + 50)))
				.subscribe(s -> System.out.println(s));

		Flowable.fromArray(1, 2, 3, 4).flatMap(t -> Flowable.just(t + 50)).subscribe(System.out::println);

	}

	public static void subscriberExample() {
		Subscriber<Integer> subscriber = new Subscriber<Integer>() {

			@Override
			public void onSubscribe(Subscription s) {

			}

			@Override
			public void onNext(Integer t) {
				System.out.printf("Entry %d\n", t);
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
		Flowable.fromArray(1, 2, 3, 4).subscribe(subscriber);
	}

	public static void consumerExample() {

		Flowable.fromArray(1, 2, 3, 4).subscribe(new Consumer<Integer>() {

			@Override
			public void accept(Integer t) throws Exception {
				System.out.printf("Entry %d\n", t);
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
	}

	private static void flowableExample() {

		Flowable<Integer> flowable = Flowable.create((FlowableEmitter<Integer> emitter) -> {
			emitter.onNext(1);
			emitter.onNext(2);
			emitter.onComplete();
		}, BackpressureStrategy.BUFFER);

		flowable.subscribeOn(Schedulers.io()).subscribe(System.out::println);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	private static void flowableVerbose() {

		Flowable<Integer> flowable = Flowable.create(new FlowableOnSubscribe<Integer>() {

			@Override
			public void subscribe(FlowableEmitter<Integer> e) throws Exception {
				e = new FlowableEmitter<Integer>() {

					@Override
					public void onNext(Integer value) {
						value = 99;
					}

					@Override
					public void onError(Throwable error) {
						System.err.printf("Failed to process: %s\n", error);
					}

					@Override
					public void onComplete() {
						System.out.println("Done");
					}

					@Override
					public void setDisposable(Disposable s) {
					}

					@Override
					public void setCancellable(Cancellable c) {
					}

					@Override
					public long requested() {
						return 0;
					}

					@Override
					public boolean isCancelled() {
						return false;
					}

					@Override
					public FlowableEmitter<Integer> serialize() {
						return null;
					}

					@Override
					public boolean tryOnError(Throwable throwable) {
						return false;
					}
				};
				
			}}, BackpressureStrategy.BUFFER);

		flowable.subscribeOn(Schedulers.io());
	}


	private static Optional<CommandLine> setupCommandLineParams(String[] args) {
		Optional<CommandLine> line = null;
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption("ssv", "simpleStartVerbose", false, "simple hello world example - simpleStartVerbose.");
		options.addOption("ss", "simpleStart", false, "simple example - simpleStart.");
		options.addOption("sa", "simpleArray", false, "simple example - simpleArray.");
		options.addOption("saf", "simpleArrayFull", false, "simple example - simpleArrayFull.");
		options.addOption("sm", "simpleMap", false, "simple example - simpleMap.");
		options.addOption("sm", "simpleFlatMap", false, "simple example - simpleFlatMap.");
		options.addOption("se", "subscriberExample", false, "subscriber example - subscriberExample.");
		options.addOption("ce", "consumerExample", false, "consumer example - consumerExample.");
		options.addOption("fe", "flowableExample", false, "flowable example - flowableExample.");
		options.addOption("fv", "flowableVerbose", false, "flowable verbose - flowableVerbose.");
		options.addOption("as", "async", false, "simple async example - simpleAsync.");
		try {
			line = Optional.ofNullable(parser.parse(options, args));
		} catch (ParseException exp) {
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		}

		return line;
	}
}
