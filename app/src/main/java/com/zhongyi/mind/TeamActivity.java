package com.zhongyi.mind;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhongyi.mind.contant.NetConstant;
import com.zhongyi.mind.control.SelectPicPopupWindow;
import com.zhongyi.mind.data.UploadFile;
import com.zhongyi.mind.data.temp.UploadFileTemp;
import com.zhongyi.mind.util.CommonUtils;
import com.zhongyi.mind.util.LogUtils;
import com.zhongyi.mind.util.NetCode;
import com.zhongyi.mind.util.NetUtils;
import com.zhongyi.mind.util.PermissionUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TeamActivity extends Activity implements View.OnClickListener {
    private WebView webview;
    private ImageView home;
    private ImageView pk;
    private ImageView team;
    private ImageView mine;

    private UploadFile uploadFile;
    private boolean flag = true;

    //图片相关类
    //自定义的弹出框类
    private SelectPicPopupWindow menuWindow;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int SDCARD_PERMISSION_REQUEST_CODE = 2;
    private static final int CAMERA_OPEN_REQUEST_CODE = 3;
    private static final int GALLERY_OPEN_REQUEST_CODE = 4;
    private static final int CROP_IMAGE_REQUEST_CODE = 5;
    private static final int REQUEST_CODE_CAMERA = 6;
    private String mCameraFilePath = "";
    private String mCropImgFilePath = "";
    private boolean isClickRequestCameraPermission = false;
    private Activity mActivity;
    private Context mContext;
    private int height;
    private int width;

    private Handler handler = new Handler();
    private File picFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        //设置activity
        ((MyApplication)getApplication()).setActivities(this);
        mActivity = this;
        mContext = this;

        webview = findViewById(R.id.webview);
        home= findViewById(R.id.home);
        pk= findViewById(R.id.pk);
        team= findViewById(R.id.team);
        mine= findViewById(R.id.mine);

        home.setOnClickListener(this);
        pk.setOnClickListener(this);
        team.setOnClickListener(this);
        mine.setOnClickListener(this);


        String url = NetConstant.FRIEND;
        webview.clearCache(true);
        webview.loadUrl(url);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        /***打开本地缓存提供JS调用**/
        webview.getSettings().setDomStorageEnabled(true);
        // Set cache size to 8 mb by default. should be more than enough
        webview.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);

        webview.addJavascriptInterface(new Object() {
            //定义要调用的方法
            //msg由js调用的时候传递
            @SuppressLint("JavascriptInterface")
            @JavascriptInterface
            public void take(int height,int width){
                flag = true;
                // Toast.makeText(getApplicationContext(), height+"---"+width, Toast.LENGTH_SHORT).show();
                TeamActivity.this.height = height;
                TeamActivity.this.width = width;
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(TeamActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(TeamActivity.this.findViewById(R.id.content_rl), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }

            @SuppressLint("JavascriptInterface")
            @JavascriptInterface
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void video(){
                flag = false;
                if (!PermissionUtils.checkCameraPermission(mContext)) {
                    isClickRequestCameraPermission = true;
                    PermissionUtils.requestCameraPermission(mActivity, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    if (!PermissionUtils.checkSDCardPermission(mContext)) {
                        isClickRequestCameraPermission = true;
                        PermissionUtils.requestSDCardPermission(mActivity, SDCARD_PERMISSION_REQUEST_CODE);
                    } else {
                        goCamera();
                    }
                }

            }

            @SuppressLint("JavascriptInterface")
            @JavascriptInterface
            public void reload(){
               webview.reload();
            }

        }, "dynamic");



        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if(Build.VERSION.SDK_INT<26) {
                   view.loadUrl(url);
//                }

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根
            }

            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {

                return true;
            }


            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home:{
                Intent intent = new Intent(TeamActivity.this,MainActivity.class);
                startActivity(intent);
                TeamActivity.this.finish();
            }
                break;
            case R.id.pk:{
                Intent intent = new Intent(TeamActivity.this,PKActivity.class);
                startActivity(intent);
                TeamActivity.this.finish();
            }
            break;
            case R.id.team:{

            }
            break;
            case R.id.mine:{
                Intent intent = new Intent(TeamActivity.this,UserActivity.class);
                startActivity(intent);
                TeamActivity.this.finish();
            }
                break;
        }
    }



    public void goCamera(){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        // 录制视频最大时长15s
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }


    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    if (!PermissionUtils.checkCameraPermission(mContext)) {
                        isClickRequestCameraPermission = true;
                        PermissionUtils.requestCameraPermission(mActivity, CAMERA_PERMISSION_REQUEST_CODE);
                    } else {
                        if (!PermissionUtils.checkSDCardPermission(mContext)) {
                            isClickRequestCameraPermission = true;
                            PermissionUtils.requestSDCardPermission(mActivity, SDCARD_PERMISSION_REQUEST_CODE);
                        } else {
                            CommonUtils.startCamera(mActivity, CAMERA_OPEN_REQUEST_CODE, generateCameraFilePath());
                        }
                    }
                    break;
                case R.id.btn_pick_photo:
                    if (!PermissionUtils.checkSDCardPermission(mContext)) {
                        PermissionUtils.requestSDCardPermission(mActivity, SDCARD_PERMISSION_REQUEST_CODE);
                    } else {
                        CommonUtils.startGallery(mActivity, GALLERY_OPEN_REQUEST_CODE);
                    }
                    break;
                default:
                    break;
            }
        }

    };

    private String generateCameraFilePath() {
        String mCameraFileDirPath = Environment.getExternalStorageDirectory() + File.separator + "camera";
        File mCameraFileDir = new File(mCameraFileDirPath);
        if (!mCameraFileDir.exists()) {
            mCameraFileDir.mkdirs();
        }
        mCameraFilePath = mCameraFileDirPath + File.separator + System.currentTimeMillis() + ".jpg";
        return mCameraFilePath;
    }

    private String generateCropImgFilePath() {
        String mCameraFileDirPath = Environment.getExternalStorageDirectory() + File.separator + "camera";
        File mCameraFileDir = new File(mCameraFileDirPath);
        if (!mCameraFileDir.exists()) {
            mCameraFileDir.mkdirs();
        }
        mCropImgFilePath = mCameraFileDirPath + File.separator + System.currentTimeMillis() + ".jpg";
        return mCropImgFilePath;
    }

    private BitmapFactory.Options getBitampOptions(String path) {
        BitmapFactory.Options mOptions = new BitmapFactory.Options();
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, mOptions);
        return mOptions;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (PermissionUtils.checkRequestPermissionsResult(grantResults)) {
                    if (!PermissionUtils.checkSDCardPermission(mContext)) {
                        PermissionUtils.requestSDCardPermission(mActivity, SDCARD_PERMISSION_REQUEST_CODE);
                    } else {
                        isClickRequestCameraPermission = false;
                        CommonUtils.startCamera(mActivity, CAMERA_OPEN_REQUEST_CODE, generateCameraFilePath());
                    }
                } else {
                    CommonUtils.showMsg(mContext, "打开照相机请求被拒绝!");
                }
                break;
            case SDCARD_PERMISSION_REQUEST_CODE:
                if (PermissionUtils.checkRequestPermissionsResult(grantResults)) {
                    if (isClickRequestCameraPermission) {
                        isClickRequestCameraPermission = false;
                        CommonUtils.startCamera(mActivity, CAMERA_OPEN_REQUEST_CODE, generateCameraFilePath());
                    } else {
                        CommonUtils.startGallery(mActivity, GALLERY_OPEN_REQUEST_CODE);
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_OPEN_REQUEST_CODE:
                    if (data == null || data.getExtras() == null) {
                        //mImg.setImageBitmap(BitmapFactory.decodeFile(mCameraFilePath));

                        BitmapFactory.Options mOptions = getBitampOptions(mCameraFilePath);
                        generateCropImgFilePath();
                        CommonUtils.startCropImage(
                                mActivity,
                                mCameraFilePath,
                                mCropImgFilePath,
                                mOptions.outWidth,
                                mOptions.outHeight,
                                mOptions.outWidth,
                                mOptions.outHeight,
                                CROP_IMAGE_REQUEST_CODE);
                    } else {
                        Bundle mBundle = data.getExtras();
//                        DebugUtils.d(TAG, "onActivityResult::CAMERA_OPEN_REQUEST_CODE::data = " + mBundle.get("data"));
                    }
                    break;
                case GALLERY_OPEN_REQUEST_CODE:
                    if (data == null) {
//                        DebugUtils.d(TAG, "onActivityResult::GALLERY_OPEN_REQUEST_CODE::data null");
                    } else {
//                        DebugUtils.d(TAG, "onActivityResult::GALLERY_OPEN_REQUEST_CODE::data = " + data.getData());
                        String mGalleryPath = CommonUtils.parseGalleryPath(mContext, data.getData());

                        BitmapFactory.Options mOptions = getBitampOptions(mGalleryPath);
                        generateCropImgFilePath();
                        CommonUtils.startCropImage(
                                mActivity,
                                mGalleryPath,
                                mCropImgFilePath,
                                mOptions.outWidth,
                                mOptions.outHeight,
                                mOptions.outWidth,
                                mOptions.outHeight,
                                CROP_IMAGE_REQUEST_CODE);
                    }
                    break;
                case CROP_IMAGE_REQUEST_CODE:
//                    DebugUtils.d(TAG, "onActivityResult::CROP_IMAGE_REQUEST_CODE::mCropImgFilePath = " + mCropImgFilePath);
                    //Bitmap bitmap = BitmapFactory.decodeFile(mCropImgFilePath);
                    File file = new File(mCropImgFilePath);
                    //将图片进行上传
                    saveData(file,"pic");
                    break;
                case REQUEST_CODE_CAMERA: {
                    Uri uri = data.getData();
                    Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToNext()) {
                        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));
                        // 视频路径
                        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                        File videoFile = new File(filePath);
                        // ThumbnailUtils类2.2以上可用  Todo 获取视频缩略图
                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MICRO_KIND);
                        // 图片Bitmap转file
                        picFile = saveBitmapFile(bitmap);
                        // 保存成功后插入到图库，其中的file是保存成功后的图片path。这里只是插入单张图片
                        // 通过发送广播将视频和图片插入相册
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(picFile)));
                        cursor.close();
                        //将视频进行上传
                        saveData(videoFile,"video");
                    }
                }
                break;
            }
        }
    }

    /**
     * 上传文件
     */
    private void saveData(File file,String flag) {
        String url = NetConstant.BASE_URL + NetConstant.UPLOAD;
        //设置回调方法
        NetUtils.setCallback(uploadCallback);
        Map<String, String> keymap = new HashMap<>();
        if ("pic".equals(flag)) {
            keymap.put("type", "dynamic_image");
        }else {
            keymap.put("type", "dynamic_video");
        }
        keymap.put("flag",flag);
        //获取网络数据
        NetUtils.post_file(url, keymap,file);
    }

    //登录回调
    Callback uploadCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                UploadFileTemp tempBean = JSON.parseObject(response.body().string(), UploadFileTemp.class);
                if (tempBean.getCode() == 200) {
                    uploadFile = tempBean.getData();
                    //更新UI界面
                    handler.post(runnableUi);
                } else if (tempBean.getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(TeamActivity.this, tempBean.getMsg(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {

            if (uploadFile!=null&&uploadFile.getFilepath()!=null){
                if (flag) {
                    Toast.makeText(TeamActivity.this,"图片上传成功",Toast.LENGTH_LONG).show();
                    webview.loadUrl("javascript:getImgUrl('" + uploadFile.getFilepath() + "')");
                }else{
                    Toast.makeText(TeamActivity.this,"视频上传成功",Toast.LENGTH_LONG).show();
                    webview.loadUrl("javascript:getVideo('" + uploadFile.getFilepath() + "','" + uploadFile.getThumb() + "')");
                }
                webview.loadUrl("javascript:loadingHide()");
            }
        }
    };

        //保存图片
    public File saveBitmapFile(Bitmap bitmap) {
        File file = new File("/mnt/sdcard/pic/01.jpg");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
