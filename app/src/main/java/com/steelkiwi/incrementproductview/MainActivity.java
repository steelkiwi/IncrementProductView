package com.steelkiwi.incrementproductview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.steelkiwi.library.IncrementProductView;
import com.steelkiwi.library.listener.OnStateListener;

public class MainActivity extends AppCompatActivity implements OnStateListener {

    private TextView amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amount = (TextView) findViewById(R.id.amount);
        IncrementProductView view = (IncrementProductView) findViewById(R.id.productView);
        view.setOnStateListener(this);
        amount.setText("$" + 0);
    }

    @Override
    public void onCountChange(int count) {
        amount.setText("$" + count * 45);
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
