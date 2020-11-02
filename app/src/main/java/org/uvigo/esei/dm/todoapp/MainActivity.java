package org.uvigo.esei.dm.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String MYTASKS_PREFERENCE = "MYTASKS_PREFERENCE";
    private List<Task> myTasks;
    private ArrayAdapter<Task> myArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE);
        if (preferences.contains(MYTASKS_PREFERENCE)){
            String myTasksJson = preferences.getString(MYTASKS_PREFERENCE, "");
            Gson gson = new Gson();
            Task[] tasks = gson.fromJson(myTasksJson, Task[].class);
            myTasks = new ArrayList<Task>(Arrays.asList(tasks));
        }else
            myTasks = new ArrayList<Task>();

        //myArrayAdapter = new ArrayAdapter<Task>(MainActivity.this, android.R.layout.simple_list_item_1, myTasks);
        myArrayAdapter = new CustomArrayAdapter(MainActivity.this, R.layout.list_view_item_custom, myTasks);
        ListView listView = findViewById(R.id.listViewTasks);
        listView.setAdapter(myArrayAdapter);

        registerForContextMenu(listView);

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
        }else if (item.getItemId() == R.id.saveTasks){
            Gson gson = new Gson();
            String myTasksJson = gson.toJson(myTasks);
            SharedPreferences preferences = getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE);
            preferences.edit().putString(MYTASKS_PREFERENCE, myTasksJson).apply();
            Toast.makeText(MainActivity.this, "Save Done", Toast.LENGTH_SHORT).show();
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
                for (Task task: myTasks){
                    task.setDone(true);
                }
                myArrayAdapter.notifyDataSetChanged();
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
                List<Task> toRemove = new ArrayList<Task>();
                for (Task task: myTasks){
                    if (task.isDone()){
                        toRemove.add(task);
                    }
                }
                myTasks.removeAll(toRemove);
                myArrayAdapter.notifyDataSetChanged();
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
                myTasks.add(new Task(editText.getText().toString()));
                myArrayAdapter.notifyDataSetChanged();
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
        builder.setMessage("Remove task " + myTasks.get(position).getName()+ ", are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myTasks.remove(position);
                myArrayAdapter.notifyDataSetChanged();
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
        String taskName = myTasks.get(position).getName();
        editText.setText(taskName);
        builder.setView(editText);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editText.getText().toString();
                myTasks.get(position).setName(newName);
                myArrayAdapter.notifyDataSetChanged();
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