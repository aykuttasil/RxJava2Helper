package com.aykuttasil.reacthelper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by aykutasil on 14.11.2016.
 */

public class ReactHelper {

    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<Boolean> checkInternet(Context context) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {

                if (checkConnection(context)) {
                    emitter.onNext(true);
                } else {
                    emitter.onError(new Error("Internet bağlantınızı kontrol ediniz !"));
                }

            }
        });
    }

    public static <T> Observable<T> sendRequestWithCheckInternet(Context context, Observable<T> observableRequest) {

        return checkInternet(context)
                .flatMap(new Function<Boolean, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(Boolean aBoolean) throws Exception {
                        return observableRequest;
                    }
                })
                .compose(applySchedulers());
    }

    public static boolean checkConnection(Context con) {
        ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
