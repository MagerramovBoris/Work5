package com.example.work5.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.work5.PhotoGallery;
import com.example.work5.R;
import com.example.work5.model.Photo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private List<Photo> photos;
    private Context context;
    private PhotoGallery photo_gallery;
    public PhotoAdapter(Context context, PhotoGallery photo_gallery) {
        this.context = context;
        this.photo_gallery = photo_gallery;
        this.photos = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(onClickListener);
        //Получить асинхронно изображение по url'у
        Picasso.with(context)
                .load(photos.get(position).getPhotoUrl())
                .into(holder.image_view);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    //Обновить список изображений
    public void updatePhotoList(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_view;

        ViewHolder(View view) {
            super(view);
            image_view = view.findViewById(R.id.iv_photo);
        }
    }

    //Обработчик нажатия на элемент списка
    final private View.OnClickListener onClickListener = view -> {
        photo_gallery.onClick(view);
    };
}
