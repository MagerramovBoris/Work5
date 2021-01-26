package com.example.work5.api;

import com.example.work5.model.FlickrPhotos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrAPI {
    // Получить фото
    @GET("services/rest/?method=flickr.photos.getRecent&format=json&nojsoncallback=1")
    Call<FlickrPhotos>
    getRecent(@Query("api_key") String api_key);

    // Получить фото по запросу
    @GET("services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1")
    Call<FlickrPhotos>
    getSearchPhotos(
            @Query("api_key") String api_key,
            @Query("text") String keyWord
    );
}
