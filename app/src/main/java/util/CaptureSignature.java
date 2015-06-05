package util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sellcom.tracker.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by juanc.jimenez on 07/07/14.
 */
public class CaptureSignature extends ActionBarActivity {

    FrameLayout signatureContainer;
    TextView signatureHint;
    Signature signature;
    private Bitmap mBitmap;
    View mView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        signatureContainer = (FrameLayout) findViewById(R.id.signature_layout);
        signature = new Signature(this, null);
        signatureContainer.addView(signature, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mView = signatureContainer;

        signatureHint = (TextView) findViewById(R.id.signature_hint);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.signature, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem saveButton = menu.findItem(R.id.save_signature);
        MenuItem clearButton = menu.findItem(R.id.clear_field);

        if (saveButton != null) {
            if (signature.isEmpty())
                saveButton.setEnabled(false);
            else
                saveButton.setEnabled(true);
        }
        if (clearButton != null) {
            if (signature.isEmpty()) {
                clearButton.setEnabled(false);
                signatureHint.setVisibility(View.VISIBLE);
            }
            else {
                clearButton.setEnabled(true);
                signatureHint.setVisibility(View.INVISIBLE);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_signature:
                Log.d("Capture signature","Entrando en save_asignature");

                mView.setDrawingCacheEnabled(true);
                String result = signature.save(mView);
                Bundle b = new Bundle();
                b.putString("data", result);

                Log.d("Capture signature","Imprimiendo result -> " + result);

                Intent intent = new Intent();
                intent.putExtras(b);
                setResult(RESULT_OK,intent);
                finish();

                break;
            case R.id.clear_field:
                signature.clear();
                break;
            case R.id.discard:
                setResult(RESULT_CANCELED, new Intent());
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class Signature extends View
    {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();
        private boolean isEmpty = true;

        public Signature(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public String save(View v)
        {
            if(mBitmap == null) {
                mBitmap =  Bitmap.createBitmap (signatureContainer.getWidth(), signatureContainer.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(mBitmap);
            try {

                v.draw(canvas);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
                outputStream.flush();
                outputStream.close();

                byte[] bytes = outputStream.toByteArray();
                return Base64.encodeToString(bytes, Base64.DEFAULT);

            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public void clear()
        {
            path.reset();
            isEmpty = true;
            invalidate();
            invalidateOptionsMenu();
        }

        @Override
        protected void onDraw(Canvas canvas) {

            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            float eventX = event.getX();
            float eventY = event.getY();
            isEmpty = false;
            invalidateOptionsMenu();

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++)
                    {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        public boolean isEmpty() {
            return isEmpty;
        }

        private void expandDirtyRect(float historicalX, float historicalY)
        {
            if (historicalX < dirtyRect.left)
            {
                dirtyRect.left = historicalX;
            }
            else if (historicalX > dirtyRect.right)
            {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top)
            {
                dirtyRect.top = historicalY;
            }
            else if (historicalY > dirtyRect.bottom)
            {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY)
        {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}
