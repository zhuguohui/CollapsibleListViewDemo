package com.zgh.collapsiblelistviewdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.zgh.collapsiblelistview.CollapsibleListView;

public class MainActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    CollapsibleListView clv_1,clv_2,clv_3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter=new ArrayAdapter<String>(this,R.layout.simple_list_item,new String[]{"Item1","Item2","Item3","Item4","Item5","Item6","Item7"});
        clv_1= (CollapsibleListView) this.findViewById(R.id.clv_1);
        clv_2= (CollapsibleListView) this.findViewById(R.id.clv_2);
        clv_3= (CollapsibleListView) this.findViewById(R.id.clv_3);
        clv_1.setAdapter(adapter);
        clv_2.setAdapter(adapter);
        clv_3.setAdapter(adapter);
        clv_1.getListview().setOnItemClickListener(new MyOnItemClickListener("first"));
        clv_2.getListview().setOnItemClickListener(new MyOnItemClickListener("second"));
        clv_3.getListview().setOnItemClickListener(new MyOnItemClickListener("thirdly"));
    }

    public  class MyOnItemClickListener implements AdapterView.OnItemClickListener{
        String title;
        public MyOnItemClickListener(String title){
            this.title=title;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(MainActivity.this,title+" "+parent.getAdapter().getItem(position)+" click",Toast.LENGTH_SHORT).show();
        }
    }
}
