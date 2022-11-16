package org.uvigo.esei.dm.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.uvigo.esei.dm.todoapp.database.DBManager;
import org.uvigo.esei.dm.todoapp.database.TaskFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String MYTASKS_PREFERENCE = "MYTASKS_PREFERENCE";

    //private List<Task> myTasks;
    //private ArrayAdapter<Task> myArrayAdapter;
    private TaskCursorAdapter taskCursorAdapter;
    private TaskFacade taskFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskFacade = new TaskFacade(getDBManager());
        ListView listView = findViewById(R.id.listViewTasks);
        taskCursorAdapter = new TaskCursorAdapter(MainActivity.this, null, taskFacade);
        listView.setAdapter(taskCursorAdapter);
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
                Cursor cursor = (Cursor) taskCursorAdapter.getItem(position);
                Task task  = TaskFacade.readTask(cursor);
                intent.putExtra(DBManager.TASK_COLUMN_ID, task.getId());
                startActivity(intent);
            }
        });

    }

    private DBManager getDBManager() {
        return ((TodoApplication) getApplication()).getDbManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ListView listView = findViewById(R.id.listViewTasks);
        Cursor cursor = taskFacade.getTasks();
        taskCursorAdapter.changeCursor(cursor);
    }


    @Override
    protected void onPause() {
        super.onPause();
        getDBManager().close();
        this.taskCursorAdapter.getCursor().close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        this.getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        super.onContextItemSelected(item);
        if (item.getItemId() == R.id.modifyTask){
            showEditTaskDialog(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
            return true;
        }else if (item.getItemId() == R.id.deleteTask){
            showDeleteTaskDialog(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
            return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.addTask){
            showCreateTaskDialog();
            return true;
        }else if (item.getItemId() == R.id.deleteDone){
            showConfirmDeleteDoneDialog();
            return true;
        }else if (item.getItemId() == R.id.toggleDone){
            showToggleDoneDialog();
            return true;
        }
        return false;
    }

    private void showToggleDoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Toggle Done");
        builder.setMessage("Toggle Done all tasks?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                taskFacade.toggleDone();
                taskCursorAdapter.changeCursor(taskFacade.getTasks());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();

    }

    private void showConfirmDeleteDoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete Done");
        builder.setMessage("All Done Task will be deleted. Are you sure?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                taskFacade.deleteDone();
                taskCursorAdapter.changeCursor(taskFacade.getTasks());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void showCreateTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("New Task");
        final EditText editText = new EditText(MainActivity.this);
        editText.setText("Task ?");
        builder.setView(editText);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String taskName = editText.getText().toString();
                Task task = new Task(taskName);
                taskFacade.createTask(task);
                taskCursorAdapter.changeCursor(taskFacade.getTasks());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void showDeleteTaskDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Remove Task");
        Cursor deleteCursor = (Cursor) taskCursorAdapter.getItem(position);
        final Task task = TaskFacade.readTask(deleteCursor);
        builder.setMessage("Remove task " + task.getName()+ ", are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                taskFacade.removeTask(task);
                taskCursorAdapter.changeCursor(taskFacade.getTasks());
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void showEditTaskDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Edit Task");
        final EditText editText = new EditText(MainActivity.this);
        Cursor editCursor = (Cursor) taskCursorAdapter.getItem(position);
        final Task task = TaskFacade.readTask(editCursor);
        editText.setText(task.getName());
        builder.setView(editText);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editText.getText().toString();
                task.setName(newName);
                taskFacade.updateTask(task);
                taskCursorAdapter.changeCursor(taskFacade.getTasks());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }
}