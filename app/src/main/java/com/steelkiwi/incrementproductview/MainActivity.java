package com.steelkiwi.incrementproductview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.steelkiwi.library.IncrementProductView;
import com.steelkiwi.library.listener.OnProductChangeListener;

public class MainActivity extends AppCompatActivity implements OnProductChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IncrementProductView view = (IncrementProductView) findViewById(R.id.productView);
        view.setOnProductChangeListener(this);
    }

    @Override
    public void onCountChange(int count) {
        Toast.makeText(this, "Count - " + count, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfirm(int count) {
        Toast.makeText(this, "You want to buy - " + count + " products", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClose() {
        Toast.makeText(this, "Close", Toast.LENGTH_SHORT).show();
    }
}
