package com.example.btap7;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class để tạo Retrofit instance
 */
public class RetrofitClient {
    private static Retrofit retrofit = null;

    /**
     * Lấy Retrofit client instance
     * @return Retrofit instance
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Const.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Lấy ServiceAPI instance
     * @return ServiceAPI
     */
    public static ServiceAPI getServiceAPI() {
        return getClient().create(ServiceAPI.class);
    }
}
