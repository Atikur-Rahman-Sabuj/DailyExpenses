package com.tiringbring.dailyexpenses;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import RoomDb.ExpenseDatabase;

public class MainActivity extends AppCompatActivity {
    public static ExpenseDatabase myAppRoomDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button)findViewById(R.id.btnButton);
        final TextView txtView = (TextView) findViewById(R.id.txtView);
        myAppRoomDatabase = Room.databaseBuilder(getApplicationContext(),ExpenseDatabase.class, "Expensedb").allowMainThreadQueries().build();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchAddExpenseActivity();
            }
        });

    }
    private void LaunchAddExpenseActivity()
    {
        Intent intent = new Intent(this, AddExpense.class);
        startActivity(intent);
    }
}
