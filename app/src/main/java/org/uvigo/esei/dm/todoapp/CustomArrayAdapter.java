package org.uvigo.esei.dm.todoapp;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<Task> {
    private Context myContext;
    private List<Task> myTasks;

    public CustomArrayAdapter(@NonNull Context context, int resource, @NonNull List<Task> objects) {
        super(context, resource, objects);
        this.myContext = context;
        this.myTasks = objects;
    }

    private static class ViewHolder{
        TextView textView;
        CheckBox checkBox;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView==null){
            viewHolder = new ViewHolder();
            LayoutInflater from = LayoutInflater.from(myContext);
            convertView = from.inflate(R.layout.list_view_item_custom, parent, false);
            viewHolder.textView = convertView.findViewById(R.id.textViewTaskName);
            viewHolder.checkBox = convertView.findViewById(R.id.checkBoxTaskDone);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(myTasks.get(position).getName());

        if (myTasks.get(position).isDone()){
            viewHolder.textView.setPaintFlags(viewHolder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            viewHolder.textView.setPaintFlags(viewHolder.textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        viewHolder.checkBox.setChecked(myTasks.get(position).isDone());

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myTasks.get(position).setDone(isChecked);
                notifyDataSetChanged();
            }
        });

        convertView.setTag(viewHolder);

        return convertView;
    }
}
