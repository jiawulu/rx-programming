package com.javacodegeeks.examples.rxjavaexample;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuzhong on 2018/4/14.
 * @version 1.0
 */
public class IntervalTest {

    private static Logger logger = LoggerFactory.getLogger(IntervalTest.class);

    @Test
    public void test() throws InterruptedException {
        Flowable.interval(1, TimeUnit.SECONDS)
            .observeOn(Schedulers.newThread())
            .map(aLong -> {
                DTO1 dto1 = new DTO1();
                dto1.setId(aLong);
                logger.info(dto1.toString());
                return dto1;
            })
            .observeOn(Schedulers.newThread())
            .map(dto1 -> {
                DTO2 dto2 = new DTO2(dto1);
                logger.warn(dto2.toString());
                return dto2;
            })
            .observeOn(Schedulers.io())
            .subscribe(dto2 -> {
                logger.error(dto2.toString());
            });

        Thread.sleep(5000);

    }


    public static class DTO1{
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "DTO1{" +
                "id=" + id +
                '}';
        }
    }

    public static class DTO2{
        private DTO1 dto1;

        public DTO2(DTO1 dto1) {
            this.dto1 = dto1;
        }

        public DTO1 getDto1() {
            return dto1;
        }

        @Override
        public String toString() {
            return "DTO2{" +
                "dto1=" + dto1 +
                '}';
        }
    }

}
