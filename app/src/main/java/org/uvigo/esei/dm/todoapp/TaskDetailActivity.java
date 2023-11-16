package org.uvigo.esei.dm.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.uvigo.esei.dm.todoapp.database.DBManager;
import org.uvigo.esei.dm.todoapp.database.TaskFacade;

public class TaskDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Integer id = getIntent().getExtras().getInt(DBManager.TASK_COLUMN_ID);
        TaskFacade taskFacade = new TaskFacade((TodoApplication) getApplication());
        Task task = taskFacade.getTaskById(id);
        TextView textView  =findViewById(R.id.textViewTaskNameDetail);
        textView.setText(task.getName());

        textView = findViewById(R.id.textViewTaskDoneDetail);
        textView.setText(task.isDone()?"DONE!":"PENDING TASK....");

    }
}