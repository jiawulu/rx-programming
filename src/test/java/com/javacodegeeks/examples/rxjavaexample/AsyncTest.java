package com.javacodegeeks.examples.rxjavaexample;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuzhong on 2018/4/14.
 * @version 1.0
 */
public class AsyncTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncTest.class);
    static ExecutorService executorService = Executors.newCachedThreadPool();

    @Test
    public void test() throws InterruptedException {

        Flowable.create((FlowableEmitter<String> flowableEmitter)  -> {
            LOGGER.info("startNetworking...");
            startNetworking(string -> {
                LOGGER.info("on success " + string);
                flowableEmitter.onNext(string);
                flowableEmitter.onComplete();
            });
        }, BackpressureStrategy.BUFFER)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe(s -> {
               LOGGER.error("on subscribe " + s);
            });


        Thread.sleep(2000);

    }

    public static void startNetworking(Callback callback) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("start to do request");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LOGGER.info("start to callback");
                callback.success(new Date().toString());
            }
        });
    }

    public static interface Callback {

        public void success(String string);

    }
}
