package com.ethannjc.inlayphotos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;

public class GalleryListAdapter extends ArrayAdapter {
    public GalleryListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Nullable
    @Override
    public String getItem(int index) {
        return (String) FTP.galleries.keySet().toArray()[index];
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public int getCount() {
        return FTP.galleries.size();
    }

    @NonNull
    @Override
    public View getView(int index, View view, ViewGroup parent) {
        ViewHolder vh;
        final String title = getItem(index);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.gallery_list_item, parent, false);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        vh.label.setText(WordUtils.capitalize(title));
        vh.thumb.setImageBitmap(FTP.galleries.get(title));

        return view;
    }

    private static class ViewHolder {
        ImageView thumb;
        TextView label;
        View view;
        ViewHolder(View view) {
            thumb = (ImageView) view.findViewById(R.id.gallery_list_entry_thumb);
            label = (TextView) view.findViewById(R.id.gallery_list_entry_title);
            this.view = view;
        }
    }
}