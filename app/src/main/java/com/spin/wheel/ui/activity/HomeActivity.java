package com.spin.wheel.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.spin.wheel.R;
import com.spin.wheel.manager.MyDBAdapter;
import com.spin.wheel.manager.Prefs;
import com.spin.wheel.widgets.WheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yogi on 23/07/2016.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static double currentSpinRec;
    private static float lastX;
    private static float lastY;
    private static double theta;
    private static float width;

    static {
        lastX = 0.0f;
        lastY = 0.0f;
        theta = 0.0d;
    }

    WheelView wheelView;
    private Button autoSpinButton;
    private List<String> listItems;
    private String listTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        wheelView = (WheelView) findViewById(R.id.wheelView);
        autoSpinButton = (Button) findViewById(R.id.autoSpinButton);
        autoSpinButton.setOnClickListener(this);

        width = (float) getWindowManager().getDefaultDisplay().getWidth();
        listTitle = "List";
        listItems = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            listItems.add("Item " + i);
        }

        wheelView.setItems(listItems);
        wheelView.setList(listTitle);
        updateWheel(0.0d);
        currentSpinRec = 0.0d;
    }


    private void updateWheel(double rotationVal) {
        if (wheelView != null) {
            wheelView.setRotation(wheelView.getRotationNumber() + rotationVal);
            wheelView.invalidate();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.autoSpinButton:
                wheelView.autoSpin(20.0f * (320.0f / width));
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MyDBAdapter.KEY_ID_COLUMN /*0*/:
                lastX = event.getX();
                lastY = event.getY();
                if (!this.wheelView.withinWheel(lastX, lastY)) {
                    return true;
                }
                this.wheelView.lifted = false;
                return true;
            case MyDBAdapter.NAME_COLUMN /*1*/:
            case 3:
            case 4:
                return true;
            case MyDBAdapter.NAME_REF /*2*/:
                float x;
                float y;
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    x = event.getHistoricalX(i);
                    y = event.getHistoricalY(i);
                    if (this.wheelView.withinWheel(x, y)) {
                        processMovement(x, y, lastX, lastY);
                        lastX = x;
                        lastY = y;
                    } else {
                        this.wheelView.lifted = true;
                    }
                }
                x = event.getX();
                y = event.getY();
                if (this.wheelView.withinWheel(x, y)) {
                    processMovement(x, y, lastX, lastY);
                    lastX = x;
                    lastY = y;
                    this.wheelView.setLastTheta(-theta, width);
                    this.wheelView.lifted = true;
                    return true;
                }
                this.wheelView.lifted = true;
                return true;
            default:
                return false;
        }
    }

    private void processMovement(float _x, float _y, float _lastX, float _lastY) {
        int px = this.wheelView.getPX();
        int py = this.wheelView.getPY();
        float nX = _x - ((float) px);
        float nY = ((float) py) - _y;
        theta = calculateAngle(nX, nY) - calculateAngle(_lastX - ((float) px), ((float) py) - _lastY);
        updateWheel(-theta);
    }

    private double calculateAngle(float x, float y) {
        double angle;
        if (x == 0.0f) {
            if (y > 0.0f) {
                angle = 90.0d;
            } else {
                angle = 270.0d;
            }
        } else if (y == 0.0f) {
            if (x > 0.0f) {
                angle = 0.0d;
            } else {
                angle = 180.0d;
            }
        } else if (x == 0.0f && y == 0.0f) {
            return -1.0d;
        } else {
            angle = Math.toDegrees(Math.atan2((double) y, (double) x)) - 90.0d;
        }
        return angle;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, Prefs.class));
                return true;
            default:
                return false;
        }
    }
}
