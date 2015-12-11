package com.nju.cs.screencamera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zhantong on 15/12/9.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback,Camera.PreviewCallback {
    private static final String TAG = "main";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private LinkedBlockingQueue<byte[]> frames;
    private boolean pause=false;
    public CameraPreview(Context context,LinkedBlockingQueue<byte[]> frames) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        this.frames=frames;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mCamera=Camera.open();
        mCamera.setPreviewCallback(this);
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.d(TAG, "preview failed");
        }
        mCamera.setParameters(CameraSettings.parameters);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera=null;
    }
    public void focus(){
        pause=true;
        mCamera.autoFocus(null);
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        pause=false;
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Thread preview_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mCamera.startPreview();
            }
        }, "preview_thread");
        preview_thread.start();

    }
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(!pause) {
            try {
                frames.put(data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //frames.add(data);
        //Log.d("queue length:", Integer.toString(frames.size()));
    }
    public void stop(){
        mHolder.removeCallback(this);
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera=null;
    }
}
