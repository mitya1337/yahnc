package mitya.yahnc.network;

import mitya.yahnc.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mitya on 02.07.2016.
 */
public abstract class RestService<T> {
    protected final T service;

    protected RestService(Class<T> serviceType) {
        this(serviceType, BuildConfig.API_URL);
    }

    protected RestService(Class<T> serviceType, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        service = retrofit.create(serviceType);
    }

    public T getService() {
        return service;
    }
}
