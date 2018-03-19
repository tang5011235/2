package test.mycrophelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CropHelper mCropHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imageView = (ImageView) findViewById(R.id.image);
        Button gallery = (Button) findViewById(R.id.btn_gallery);
        Button camera = (Button) findViewById(R.id.btn_camera);
        imageView.setOnClickListener(this);
        gallery.setOnClickListener(this);
        camera.setOnClickListener(this);
        mCropHelper = new CropHelper(this);
        mCropHelper.setImageListener(new CropHelper.ImageListener() {
            @Override
            public void onSelectImageFromGallery(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gallery:
                mCropHelper.gallery();
                break;
            case R.id.btn_camera:
                mCropHelper.camera();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCropHelper.onActivityResultForMedia(requestCode, resultCode, data);
    }
}
