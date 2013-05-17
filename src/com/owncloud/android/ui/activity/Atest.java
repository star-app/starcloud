package com.owncloud.android.ui.activity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.owncloud.android.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class Atest extends Activity implements OnTouchListener {
    //Í¼Æ¬ä¯ÀÀ¡¢Ëõ·Å¡¢ÍÏ¶¯¡¢×Ô¶¯¾ÓÖÐ
    private String resource_id;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    DisplayMetrics dm;
    ImageView imgView;
    Bitmap bitmap;

    float minScaleR;//×îÐ¡Ëõ·Å±ÈÀý
    static final float MAX_SCALE = 4f;// ×î´óËõ·Å±ÈÀý

    static final int NONE = 0;// ³õÊ¼×´Ì¬
    static final int DRAG = 1;// ÍÏ¶¯
    static final int ZOOM = 2;// Ëõ·Å
    int mode = NONE;

    PointF prev = new PointF();
    PointF mid = new PointF();
    float dist = 1f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.multitouch);
        
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        resource_id = bundle.getString("id");
        
//        imgView = (ImageView) findViewById(R.id.imageView);// »ñÈ¡¿Ø¼þ
        //bitmap = BitmapFactory.decodeFile(resource_id);// »ñÈ¡Í¼Æ¬×ÊÔ´
        File file = new File(resource_id);
        FileInputStream fs=null;
        try {
           fs = new FileInputStream(file);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }
        
        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
        bfOptions.inDither=false;
        bfOptions.inPurgeable=true;
        bfOptions.inSampleSize = 1;
        bfOptions.inTempStorage = new byte[12 * 1024];
        
        if(fs != null)
           try {
               bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
           } catch (IOException e) {
               e.printStackTrace();
           }finally{ 
               if(fs!=null) {
                   try {
                       fs.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
         }
        
        Matrix matrix_2 = new Matrix();
        matrix_2.setRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix_2, true);
        
        imgView.setImageBitmap(bitmap);// Ìî³ä¿Ø¼þ
        imgView.setOnTouchListener(this);// ÉèÖÃ´¥ÆÁ¼àÌý
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);// »ñÈ¡·Ö±æÂÊ 
        minZoom();
        center();
        imgView.setImageMatrix(matrix);
        imgView.invalidate();
    }

    //´¥ÆÁ¼àÌý
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        // Ö÷µã°´ÏÂ
        case MotionEvent.ACTION_DOWN:
            savedMatrix.set(matrix);
            prev.set(event.getX(), event.getY());
            mode = DRAG;
            break;
            // ¸±µã°´ÏÂ
        case MotionEvent.ACTION_POINTER_DOWN:
            dist = spacing(event);
            // Èç¹ûÁ¬ÐøÁ½µã¾àÀë´óÓÚ10£¬ÔòÅÐ¶¨Îª¶àµãÄ£Ê½
            if (spacing(event) > 10f) {
                savedMatrix.set(matrix);
                midPoint(mid, event);
                mode = ZOOM;
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
            mode = NONE;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mode == DRAG) {
                matrix.set(savedMatrix);
                matrix.postTranslate(event.getX() - prev.x, event.getY()
                        - prev.y);
            } else if (mode == ZOOM) {
                float newDist = spacing(event);
                if (newDist > 10f) {
                    matrix.set(savedMatrix);
                    float tScale = newDist / dist;
                    matrix.postScale(tScale, tScale, mid.x, mid.y);
                }
            }
            break;
        }
        imgView.setImageMatrix(matrix);
        CheckView();
        return true;
    }

    //ÏÞÖÆ×î´ó×îÐ¡Ëõ·Å±ÈÀý£¬×Ô¶¯¾ÓÖÐ
    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScaleR) {
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
                matrix.set(savedMatrix);
            }
        }
        center();
    }

    //×îÐ¡Ëõ·Å±ÈÀý£¬×î´óÎª100%
    private void minZoom() {
        minScaleR = Math.min(
                (float) dm.widthPixels / (float) bitmap.getWidth(),
                (float) dm.heightPixels / (float) bitmap.getHeight());
        if (minScaleR < 1.0) {
            matrix.postScale(minScaleR, minScaleR);
        }
    }

    private void center() {
        center(true, true);
    }

    //ºáÏò¡¢×ÝÏò¾ÓÖÐ
    protected void center(boolean horizontal, boolean vertical) {

        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            // Í¼Æ¬Ð¡ÓÚÆÁÄ»´óÐ¡£¬Ôò¾ÓÖÐÏÔÊ¾¡£´óÓÚÆÁÄ»£¬ÉÏ·½Áô¿ÕÔòÍùÉÏÒÆ£¬ÏÂ·½Áô¿ÕÔòÍùÏÂÒÆ
            int screenHeight = dm.heightPixels;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = imgView.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = dm.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    //Á½µãµÄ¾àÀë
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    //Á½µãµÄÖÐµã
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    
    //¼àÌý·µ»Ø¼ü
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.finish();
            //ÉèÖÃÇÐ»»¶¯»­£¬´Ó×ó±ß½øÈë£¬ÓÒ±ßÍË³ö
//            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);  
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}