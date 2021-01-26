package com.example.work5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.work5.api.ServiceAPI;
import com.example.work5.db.PhotosDB;
import com.example.work5.model.FlickrPhotos;
import com.example.work5.model.Photo;
import com.example.work5.view.PhotoAdapter;
import com.google.gson.Gson;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PhotoGallery extends AppCompatActivity {
    // Настройки приложения
    String key_flickr = "";




    // RecyclerView
    PhotoAdapter adapter;
    RecyclerView list_view;
    PhotosDB photos_db;
    List<Photo> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);
        //Получить ссылку на БД
        photos_db = PhotosDB.getDatabase(this);
        // адаптер для RecyclerView
        adapter = new PhotoAdapter(this, this);
        list_view = findViewById(R.id.rv_photos);
        GridLayoutManager layout_manager = new GridLayoutManager(this, 3);
        list_view.setLayoutManager(layout_manager);
        list_view.setAdapter(adapter);
        //Запросить изображения с flickr.com
        getPhotosFromFlickr();
    }

    //Изменить главное меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        //Прослушиваем поисковую строку
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //Сработает при отправке введенного текста
            @Override
            public boolean onQueryTextSubmit(String query) {
                getSearchPhotosFromFlickr(query);
                return true;
            }
            //Сработает при вводе текста
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    // Обработчик нажатий пунктов меню из toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Выбор действия зависит от id элемента в меню
        switch (item.getItemId()) {
            case (R.id.photo_db):
                getPhotosFromDB();
                break;
            case (R.id.photo_flickr):
                getPhotosFromFlickr();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Вызывается из PhotoAdapter, когда происходит нажатие на фото
    public void onClick(View view) {
        int position = (int) view.getTag();
        Intent intent = new Intent(this, DetailPhoto.class);
        Gson gson = new Gson();
        intent.putExtra("photo", gson.toJson(photos.get(position)));
        intent.putExtra("position", position);
        startActivityForResult(intent, 1);
    }

    //Возврат в главное activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        boolean is_removed = intent.getBooleanExtra("is_removed", false);
        //Если произошло удаление фото из БД, то удалить данное фото из списка
        if (is_removed) {
            int position = intent.getIntExtra("position", 0);
            photos.remove(position);
            adapter.notifyDataSetChanged();
        }
    }

    //GET запрос на flickr для получения общедоступных изображений
    public void getPhotosFromFlickr() {
        if (key_flickr != "") {
            ServiceAPI.getFlickrAPI().getRecent(key_flickr).enqueue(new Callback<FlickrPhotos>() {
                @Override
                public void onResponse(Call<FlickrPhotos> call, Response<FlickrPhotos> response) {
                    // Если ответ с сервера придет в виде с ошибкой, то возникнет ошибка
                    try {
                        photos = response.body().getPhotos().getPhoto();
                        adapter.updatePhotoList(photos);
                    }
                    // Обработка ошибок
                    catch (Exception error) {
                        Toast.makeText(PhotoGallery.this, "Error Token.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<FlickrPhotos> call, Throwable t) {
                    Toast.makeText(PhotoGallery.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //Получить изображния из БД
    public void getPhotosFromDB() {
        photos_db.request(
                () -> photos = photos_db.photoDao().LoadAll(),
                () -> adapter.updatePhotoList(photos)
        );
    }

    //GET запрос на flickr для получения изображений по введенному запросу в поиске
    public void getSearchPhotosFromFlickr(String text) {
        if (key_flickr != "") {
            ServiceAPI.getFlickrAPI().getSearchPhotos(key_flickr, text).enqueue(new Callback<FlickrPhotos>() {
                @Override
                public void onResponse(Call<FlickrPhotos> call, Response<FlickrPhotos> response) {
                    // Если ответ с сервера придет в виде с ошибкой, то возникнет ошибка
                    try {
                        photos = response.body().getPhotos().getPhoto();
                        adapter.updatePhotoList(photos);
                    }
                    // Обработка ошибок
                    catch (Exception error) {
                        Toast.makeText(PhotoGallery.this, "Error Token.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<FlickrPhotos> call, Throwable t) {
                    Toast.makeText(PhotoGallery.this,
                            "An error occurred during networking",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }
    }
}