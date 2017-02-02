package com.ethannjc.inlayphotos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageListAdapter extends ArrayAdapter {
    private String gallery;
    public ImageListAdapter(Context context, int resource, String gallery) {
        super(context, resource);
        this.gallery = gallery;
    }

    @Nullable
    @Override
    public Integer getItem(int index) {
        return (Integer) FTP.images.keySet().toArray()[index];
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public int getCount() {
        return FTP.images.size();
    }

    @NonNull
    @Override
    public View getView(final int index, View view, ViewGroup parent) {
        final ViewHolder vh;
        final int id = getItem(index);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.image_list_item, parent, false);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        vh.description.setText(FTP.descriptions.get(id));
        vh.thumb.setImageBitmap(FTP.images.get(id));

        vh.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                final EditText edit = new EditText(getContext());
                edit.setText(FTP.descriptions.get(id));
                edit.setSingleLine(true);
                edit.setLines(4);
                edit.setHorizontallyScrolling(false);
                edit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                alert.setTitle("Edit Description");
                alert.setView(edit);
                alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vh.description.setText(edit.getText().toString());
                        FTP.descriptions.put(id, edit.getText().toString());
                        new UpdateDescriptionTask(FTP.currentGallery).execute(id);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        vh.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setMessage("Are you sure you want to delete this image?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new DeleteImageTask(gallery).execute(id);

                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return view;
    }

    private static class ViewHolder {
        ImageView thumb;
        TextView description;
        Button deleteButton, updateButton;
        View view;
        ViewHolder(View view) {
            thumb = (ImageView) view.findViewById(R.id.image_list_entry_thumb);
            description = (TextView) view.findViewById(R.id.image_list_entry_description);
            deleteButton = (Button) view.findViewById(R.id.image_item_entry_delete_button);
            updateButton = (Button) view.findViewById(R.id.image_item_entry_update_button);
            this.view = view;
        }
    }
}
