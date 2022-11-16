package org.uvigo.esei.dm.todoapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.uvigo.esei.dm.todoapp.database.TaskFacade;

public class TaskCursorAdapter extends CursorAdapter {

    private TaskFacade taskFacade;

    public TaskCursorAdapter(Context context, Cursor c, TaskFacade taskFacade) {
        super(context, c, false);
        this.taskFacade = taskFacade;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_view_item_custom,
                parent,false );
    }

    class ViewHolder{
        TextView textView;
        CheckBox checkBox;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = null;
        if (view!=null) {
            viewHolder = (ViewHolder) view.getTag();
            if (viewHolder == null){
                viewHolder = new ViewHolder();
                TextView textView = view.findViewById(R.id.textViewTaskName);
                viewHolder.textView = textView;
                CheckBox checkBox = view.findViewById(R.id.checkBoxTaskDone);
                viewHolder.checkBox = checkBox;
                view.setTag(viewHolder);
            }
        }

        final Task task = TaskFacade.readTask(cursor);
        viewHolder.textView.setText(task.getName());
        if (task.isDone()){
            viewHolder.textView.setPaintFlags( viewHolder.textView.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            viewHolder.textView.setPaintFlags( viewHolder.textView.getPaintFlags()
                    & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        viewHolder.checkBox.setChecked(task.isDone());
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.setDone(isChecked);
                taskFacade.updateTask(task);
                swapCursor(taskFacade.getTasks());
            }
        });

    }
}
