package com.xu.parallax;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;


public class MainActivity extends Activity {
    private ParallaxListView listview;
    private String[] indexArr = { "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ParallaxListView) findViewById(R.id.listview);

        //listview在拉到顶的时候会有蓝色的阴影效果。设置超过滚动的模式，有3个模式
        listview.setOverScrollMode(ListView.OVER_SCROLL_NEVER);//永远不显示蓝色阴影

        //添加header
        View headerView = View.inflate(this,R.layout.layout_header, null);
        ImageView imageView = (ImageView) headerView.findViewById(R.id.imageView);
        //调用这个方法
        listview.setParallaxImageView(imageView);
        listview.addHeaderView(headerView);
        //小细节，最后才设置adapter
        //给listView设置数据。布局文件使用安卓的，上面的数组
        listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, indexArr));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
