package com.tiringbring.dailyexpenses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;

import RoomDb.ExpenseDatabase;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tiringbring.dailyexpenses.DataController.BarEntryDataController;
import com.tiringbring.dailyexpenses.DataController.MySharedPreferences;
import com.tiringbring.dailyexpenses.DataController.PieEntryDataController;
import com.tiringbring.dailyexpenses.Utility.OnSwipeTouchListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StartActivity extends AppCompatActivity {
    public static ExpenseDatabase myAppRoomDatabase;
    private BarChart mChart;
    private PieChart pieChart;
    Long dailyLimit;
    private Date pieDate;
    private Button btnAddNew;
    private Button btnShowList;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        myAppRoomDatabase = Room.databaseBuilder(getApplicationContext(),ExpenseDatabase.class, "Expensedb").allowMainThreadQueries().build();
        btnAddNew = (Button) findViewById(R.id.btnAddNew);
        btnShowList = (Button) findViewById(R.id.btnShowList);
        mChart= (BarChart) findViewById(R.id.barChart);
        pieChart = (PieChart) findViewById(R.id.pieChart);
        dailyLimit = new MySharedPreferences(getApplicationContext()).getDayilyLimit();

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddExpense.class);
                startActivity(intent);
            }
        });
        btnShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExpenseList.class);
                startActivity(intent);
            }
        });
        SetPieChartDate();
        BindDataToPieChart();

        mChart.getDescription().setEnabled(false);
        BarEntryDataController beDataController = new BarEntryDataController();
        List<BarEntry> data = beDataController.GetBarEntries();
        //generating colors
        List<Integer> colors = new ArrayList<>();
        for (BarEntry be:
                data) {
            if(be.getY()>dailyLimit){
                colors.add(Color.RED);
            }
            else {
                colors.add(Color.GREEN);
            }
        }
        BarDataSet set = new BarDataSet(data, "");
        //set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setColors(colors);
        set.setDrawValues(true);
        BarData barData = new BarData(set);
        barData.setBarWidth(.8f);
        mChart.setData(barData);
        mChart.setExtraOffsets(0, 0, 0, 0);


        mChart.getContentRect().set(0, 0, mChart.getWidth(), mChart.getHeight());
        mChart.animateY(500);
        mChart.setScaleEnabled(false);
        mChart.setDrawValueAboveBar(true);
        mChart.setDrawBorders(false);
        //mChart.setExtraOffsets(0,0,0,0);
        mChart.getLegend().setEnabled(false);
        mChart.setVisibleXRangeMaximum(7); // allow 20 values to be displayed at once on the x-axis, not more
        mChart.moveViewToX(-1);
        XAxis xAxis = mChart.getXAxis();
        YAxis left = mChart.getAxisLeft();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(beDataController.getXAxisValues()));
        left.setDrawLabels(false); // no axis labels
        left.setDrawAxisLine(false); // no axis line
        left.setDrawGridLines(false); // no grid lines
        left.setDrawZeroLine(false); // draw a zero line
        mChart.getAxisRight().setEnabled(false); // no right axis
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.BLACK);
        //xAxis.setCenterAxisLabels(true);
        mChart.setDrawGridBackground(false);
        mChart.setFitBars(false);
        mChart.invalidate();

        pieChart.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){
            public void onSwipeTop() {
                //Toast.makeText(StartActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                //Toast.makeText(StartActivity.this, "right", Toast.LENGTH_SHORT).show();
                ChangePieDate(1);
                pieChart.clear();
                BindDataToPieChart();
            }
            public void onSwipeLeft() {
                //Toast.makeText(StartActivity.this, "left", Toast.LENGTH_SHORT).show();
                ChangePieDate(-1);
                pieChart.clear();
                BindDataToPieChart();
            }
            public void onSwipeBottom() {
                //Toast.makeText(StartActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settingMenuButton) {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void SetPieChartDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            pieDate = formatter.parse(formatter.format(calendar.getTime()));
        }catch (Exception ex){

        }
        //calendar.add(Calendar.DAY_OF_MONTH, -1); to substract date
    }
    private  void ChangePieDate(int noOfDay){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pieDate);
        calendar.add(Calendar.DAY_OF_MONTH, noOfDay);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar2 = Calendar.getInstance();
        Date today = new Date(), expectedDay = new Date();

        try {
            today = formatter.parse(formatter.format(calendar2.getTime()));
        }catch (Exception ex){

        }
        try {
            expectedDay = formatter.parse(formatter.format(calendar.getTime()));
        }catch (Exception ex){

        }
        if(expectedDay.compareTo(today)<=0){
            pieDate = expectedDay;
        }
    }

    private void BindDataToPieChart() {
        pieChart.setUsePercentValues(false);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        pieChart.getDescription().setText("");

        pieChart.setCenterText(dateFormat.format(pieDate));
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);

        pieChart.getLegend().setEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(35f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        ArrayList<PieEntry> yValues = new PieEntryDataController().GetList(pieDate);
        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(1f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);

    }
}
