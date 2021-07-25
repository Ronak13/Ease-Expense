package com.example.expensetracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class PieFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie, container, false);
        PieChart pieChart = view.findViewById(R.id.pichart);
        ArrayList<PieEntry> records = new ArrayList<>();
        DashboardFragment ob = new DashboardFragment();
        int expense = 0;
        int income = 0;
        expense = DashboardFragment.expense;
        income = DashboardFragment.income;
        int balance = 0;
        balance = income - expense;
        records.add(new PieEntry(expense, "Expense"));
        records.add(new PieEntry(income, "Income"));
        records.add(new PieEntry(balance, "Balance"));

        PieDataSet dataSet = new PieDataSet(records, "Income-Expense report");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(24f);

        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(true);
        pieChart.setCenterText("Income VS Expense");
        pieChart.animate();

        return view;

    }
}