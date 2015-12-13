package com.nju.cs.screencamera;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * UI主要操作
 * 也是控制二维码识别的主要入口
 */
public class MainActivity extends AppCompatActivity {
    private CameraPreview mPreview;//相机
    final static LinkedBlockingQueue<byte[]> rev = new LinkedBlockingQueue<>();//图像信息队列

    /**
     * 界面初始化,设置界面,调用CameraSettings()设置相机参数
     *
     * @param savedInstanceState 默认参数
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        CameraSettings cameraSettings = new CameraSettings();
        cameraSettings = null;
        TextView debugView = (TextView) findViewById(R.id.debug_view);
        TextView infoView = (TextView) findViewById(R.id.info_view);
        debugView.setGravity(Gravity.BOTTOM);
        infoView.setGravity(Gravity.BOTTOM);
    }

    /**
     * 打开相机实时识别二维码
     * 一个线程将相机预览帧加入队列
     * 一个线程从队列中取出预览帧进行二维码识别
     *
     * @param view 默认参数
     */
    public void openCamera(View view) {
        final TextView debugView = (TextView) findViewById(R.id.debug_view);
        final TextView infoView = (TextView) findViewById(R.id.info_view);
        final CameraPreview mPreview = new CameraPreview(this, rev);
        this.mPreview = mPreview;
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        EditText editTextFileName = (EditText) findViewById(R.id.fileName);
        final String newFileName = editTextFileName.getText().toString();
        final Handler nHandler = new Handler();
        Thread worker = new Thread() {
            @Override
            public void run() {
                File out = new File(Environment.getExternalStorageDirectory() + "/Download/" + newFileName);
                CameraToFile cameraToFile=new CameraToFile(debugView, infoView, nHandler, CameraSettings.previewWidth, CameraSettings.previewHeight, mPreview);
                cameraToFile.cameraToFile(rev, out);
            }
        };
        worker.start();
    }

    /**
     * 释放相机
     *
     * @param view 默认参数
     */
    public void stop(View view) {
        mPreview.stop();
    }

    /**
     * 选取文件,操作会打开文件目录浏览器,选取文件
     *
     * @param view 默认参数
     */
    public void selectFile(View view) {
        File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
        FileDialog fileDialog = new FileDialog(this, mPath);
        //fileDialog.setFileEndsWith(".txt");
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                EditText editText = (EditText) findViewById(R.id.videoFilePath);
                editText.setText(file.toString());
            }
        });
        fileDialog.showDialog();
    }

    /**
     * 打开文件时的方法,寻找指定后缀文件的打开方法
     *
     * @param checkItsEnd 用来判断的文件后缀类型
     * @param fileEndings 指定的文件后缀类型
     * @return 匹配则返回true, 否则返回false
     */
    public boolean checkEndsWithInStringArray(String checkItsEnd,
                                              String[] fileEndings) {
        for (String aEnd : fileEndings) {
            if (checkItsEnd.endsWith(aEnd))
                return true;
        }
        return false;
    }

    /**
     * 处理视频文件,从视频帧识别二维码
     *
     * @param view 默认参数
     */
    public void processVideo(View view) {
        final TextView debugView = (TextView) findViewById(R.id.debug_view);
        final TextView infoView = (TextView) findViewById(R.id.info_view);
        EditText editTextVideoFilePath = (EditText) findViewById(R.id.videoFilePath);
        final String videoFilePath = editTextVideoFilePath.getText().toString();
        EditText editTextFileName = (EditText) findViewById(R.id.fileName);
        final String newFileName = editTextFileName.getText().toString();
        final Handler nHandler = new Handler();
        Thread worker = new Thread() {
            @Override
            public void run() {
                File out = new File(Environment.getExternalStorageDirectory() + "/Download/" + newFileName);
                VideoToFile videoToFile=new VideoToFile(debugView, infoView, nHandler);
                videoToFile.videoToFile(videoFilePath,rev, out);
            }
        };
        worker.start();
        VideoToFrames videoToFrames = new VideoToFrames();
        try {
            videoToFrames.testExtractMpegFrames(rev, videoFilePath);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理单个图片,识别二维码
     *
     * @param view 默认参数
     */
    public void processImg(View view) {
        final TextView debugView = (TextView) findViewById(R.id.debug_view);
        final TextView infoView = (TextView) findViewById(R.id.info_view);
        EditText editTextVideoFilePath = (EditText) findViewById(R.id.videoFilePath);
        final String videoFilePath = editTextVideoFilePath.getText().toString();
        final Handler nHandler = new Handler();
        Thread worker = new Thread() {
            @Override
            public void run() {
                SingleImgToFile singleImgToFile=new SingleImgToFile(debugView, infoView, nHandler);
                singleImgToFile.singleImg(videoFilePath);
            }
        };
        worker.start();
    }

    /**
     * 在APP内打开文件
     *
     * @param view 默认参数
     */
    public void openFile(View view) {
        EditText editTextFileName = (EditText) findViewById(R.id.fileName);
        String newFileName = editTextFileName.getText().toString();
        String filePath = Environment.getExternalStorageDirectory() + "/Download/" + newFileName;
        File file = new File(filePath);
        if (file.isFile()) {
            String fileName = file.toString();
            Intent intent;
            if (checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingImage))) {
                intent = OpenFiles.getImageFileIntent(file);
                startActivity(intent);
            } else if (checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingWebText))) {
                intent = OpenFiles.getHtmlFileIntent(file);
                startActivity(intent);
            } else if (checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingPackage))) {
                intent = OpenFiles.getApkFileIntent(file);
                startActivity(intent);

            } else if (checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingAudio))) {
                intent = OpenFiles.getAudioFileIntent(file);
                startActivity(intent);
            } else if (checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingVideo))) {
                intent = OpenFiles.getVideoFileIntent(file);
                startActivity(intent);
            } else if (checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingText))) {
                intent = OpenFiles.getTextFileIntent(file);
                startActivity(intent);
            } else if (checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingPdf))) {
                intent = OpenFiles.getPdfFileIntent(file);
                startActivity(intent);
            } else if (checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingWord))) {
                intent = OpenFiles.getWordFileIntent(file);
                startActivity(intent);
            } else if (checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingExcel))) {
                intent = OpenFiles.getExcelFileIntent(file);
                startActivity(intent);
            } else if (checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingPPT))) {
                intent = OpenFiles.getPPTFileIntent(file);
                startActivity(intent);
            } else {
                new AlertDialog.Builder(this).setTitle("错误").setItems(new String[]{"无法打开，请安装相应的软件！"}, null).setNegativeButton("确定", null).show();
            }
        } else {
            new AlertDialog.Builder(this).setTitle("错误").setItems(new String[]{"对不起，这不是文件！"}, null).setNegativeButton("确定", null).show();
        }
    }

}
