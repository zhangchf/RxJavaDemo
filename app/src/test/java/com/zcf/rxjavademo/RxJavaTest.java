package com.zcf.rxjavademo;

import com.zcf.rxjavademo.web.Comment;
import com.zcf.rxjavademo.web.Post;
import com.zcf.rxjavademo.web.RetrofitBuilder;
import com.zcf.rxjavademo.web.User;
import com.zcf.rxjavademo.web.WebAPI;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subscribers.DisposableSubscriber;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Created by zhangchf on 08/02/2018.
 */

public class RxJavaTest {
    
    @Test
    public void testRxJava() {
        Flowable.just(1).subscribe(new DisposableSubscriber<Integer>() {
            @Override
            public void onStart() {
//                request(1);
            }

            @Override
            public void onNext(Integer v) {
                System.out.println(v);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }

            // the rest is omitted for brevity
        });
    }


    int counter;

    int computeValue() {
        return ++counter;
    }

    @Test
    public void testJust() {

        Flowable<Integer> o = Flowable.just(computeValue());

        o.subscribe(System.out::println);
        o.subscribe(System.out::println);
    }

    @Test
    public void testFromCallable() {
        Flowable<Integer> o = Flowable.fromCallable(() -> computeValue());

        o.subscribe(System.out::println);
        o.subscribe(System.out::println);

        Flowable<Integer> o2 = Flowable.just("This doesn't matter").map(ignored -> computeValue());

        o2.subscribe(System.out::println);
        o2.subscribe(System.out::println);
    }

    @Test
    public void testIterable() {
        Iterable<Integer> iterable = () -> new Iterator<Integer>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Integer next() {
                return i++;
            }
        };

        Flowable.fromIterable(iterable).take(5).subscribe(System.out::println);
    }

    @Test
    public void testGenerate() {
        Flowable<Integer> o = Flowable.generate(
                () -> new FileInputStream(".gitignore"),
                (inputstream, output) -> {
                    try {
                        int abyte = inputstream.read();
                        if (abyte < 0) {
                            output.onComplete();
                        } else {
                            output.onNext(abyte);
                        }
                    } catch (IOException ex) {
                        output.onError(ex);
                    }
                    return inputstream;
                },
                inputstream -> {
                    try {
                        inputstream.close();
                    } catch (IOException ex) {
                        RxJavaPlugins.onError(ex);
                    }
                }
        );

        o.subscribe(System.out::println);
    }

    @Test
    public void testCreate() {
        Flowable.create(new FlowableOnSubscribe<Object>() {
            @Override
            public void subscribe(FlowableEmitter<Object> emitter) throws Exception {

            }
        }, BackpressureStrategy.BUFFER);
    }

    @Test
    public void testObservalbe() {
        WebAPI webAPI = new RetrofitBuilder().build(WebAPI.BASE_URL).create(WebAPI.class);

        webAPI.getPosts().flatMap(response -> {
            if (response.isSuccessful()) {
                Post[] posts = response.body();
                return Observable.fromArray(posts);
            } else {
                return Observable.error(new Throwable(response.message()));
            }
        }).take(5).flatMap(post -> webAPI.getComments(post.id)).reduce(new HashMap<Integer, Comment[]>(), (integerHashMap, response) -> {
            if (response.isSuccessful()) {
                Comment[] comments1 = response.body();
                if (comments1.length > 0) {
                    integerHashMap.put(comments1[0].postId, comments1);
                }
                return integerHashMap;
            } else {
                throw new HttpException(response);
            }
        }).zipWith(webAPI.getUsers().singleOrError(), new BiFunction<HashMap<Integer,Comment[]>, Response<User[]>, String>() {
            @Override
            public String apply(HashMap<Integer, Comment[]> integerHashMap, Response<User[]> response) throws Exception {
                if (response.isSuccessful()) {
                    return response.body().toString() + "/n" + integerHashMap.toString();
                }
                throw new Exception(response.message());
            }
        }).subscribe(System.out::println, System.out::println);

    }
}
