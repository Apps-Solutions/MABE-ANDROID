package com.sellcom.tracker;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;


/**
 * Created by hugo.figueroa on 14/04/15.
 */
public class FragmentSignature extends Fragment {


    final static public String      TAG                     = "signature";
    private Context                 context;
    Fragment                        fragment;
    FragmentManager                 fragmentManager         = getFragmentManager();
    FrameLayout                     signatureContainer;
    private TextView                signatureHint;
    Signature                       signature;
    private Bitmap                  mBitmap;
    View mView;
    setSignatureImg                 setSignatureImg;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signature, container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        signatureContainer = (FrameLayout) view.findViewById(R.id.signature_layout);
        signature = new Signature(context, null);
        signatureContainer.addView(signature, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mView = signatureContainer;

        signatureHint = (TextView) view.findViewById(R.id.signature_hint);

        ((MainActivity)getActivity()).mNavigationDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        return view;
    }

    @Override
    public void onDetach(){
        Log.d(TAG,"ON DETACH");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.signature, menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
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
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_signature:
                mView.setDrawingCacheEnabled(true);
                String result = signature.save(mView);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                getActivity().onBackPressed();
                setSignatureImg.getSignatureImgBase64(result);

            /*
                Log.d("Capture signature", "Entrando en save_asignature");

                mView.setDrawingCacheEnabled(true);
                String result = signature.save(mView);
                Bundle b = new Bundle();
                b.putString("data", result);

                Log.d("Capture signature","Imprimiendo result -> " + result);

                Intent intent = new Intent();
                intent.putExtras(b);
                getActivity().setResult(getActivity().RESULT_OK, intent);
                getActivity().finish();
*/
                break;
            case R.id.clear_field:
                signature.clear();
                break;
            case R.id.discard:
                getActivity().onBackPressed();
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
                mBitmap =  Bitmap.createBitmap(signatureContainer.getWidth(), signatureContainer.getHeight(), Bitmap.Config.RGB_565);
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
            getActivity().invalidateOptionsMenu();
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
            getActivity().invalidateOptionsMenu();

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


    public interface setSignatureImg{
        public void getSignatureImgBase64(String imgSignature);
    }

    public void setSetSignatureImg(setSignatureImg listener) {
        setSignatureImg = listener;
    }

}
