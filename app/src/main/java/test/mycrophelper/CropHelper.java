package test.mycrophelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

/**
 * Created by Administrator on 2018/3/19.
 */

public class CropHelper {
    public final static String URL_GALLERY = "";
    public final static String URL_CAMARA = "";

    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";

    private Activity activity;
    private File tempFile;
    private Uri mUri;
    private Uri uri;
    private String path;


    public CropHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        activity.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /**
     * 从相机获取
     */
    public void camera() {
        // 激活相机
        Intent intent = new Intent(ACTION_IMAGE_CAPTURE);

        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            tempFile = new File(Environment.getExternalStorageDirectory(), "cacheImge/" +
                    PHOTO_FILE_NAME);
            // 从文件中创建uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mUri = FileProvider.getUriForFile(activity, "test.mycrophelper", tempFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                mUri = Uri.fromFile(tempFile);
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        activity.startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /**
     * 判断sdcard是否被挂载
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void onActivityResultForMedia(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    Uri uri = data.getData();
                    crop(uri);
                }
                break;

            case PHOTO_REQUEST_CUT:
                if (data != null && mImageListener != null) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    mImageListener.onSelectImageFromGallery(bitmap);
                }
                break;
            case PHOTO_REQUEST_CAREMA:
                if (data != null) {
                    int sdkVersion = Integer.valueOf(Build.VERSION.SDK);
                    if (sdkVersion >= 19) {
                        path = this.mUri.getPath();
                        path = PictureUtils.getPath_above19(activity, this.mUri);
                    } else {
                        path = PictureUtils.getFilePath_below19(activity, this.mUri);
                    }
                    crop(Uri.parse(path));
                }
            default:

                break;
        }
    }

    /**
     * 剪切图片
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        activity.startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private ImageListener mImageListener;

    public void setImageListener(ImageListener imageListener) {
        mImageListener = imageListener;
    }

    interface ImageListener {
        void onSelectImageFromGallery(Bitmap bitmap);
    }

}
