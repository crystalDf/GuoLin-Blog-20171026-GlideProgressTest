package com.star.glideprogresstest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String BING_PIC_URL = "http://guolin.tech/api/bing_pic";

    private Button mLoadImage;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadImage = findViewById(R.id.load_image_button);
        mImageView = findViewById(R.id.image_view);

        mLoadImage.setOnClickListener(view -> loadBingPic());
    }

    private void loadBingPic() {

        String requestBingPic = BING_PIC_URL;

        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();

                runOnUiThread(() -> {

                    RequestOptions requestOptions =
                            new RequestOptions()
                                    .placeholder(R.drawable.emma_loading)
                                    .error(R.drawable.emma_error)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .override(Target.SIZE_ORIGINAL);

                    Glide
                            .with(MainActivity.this)
                            .load(bingPic)
                            .apply(requestOptions)
                            .into(mImageView);
                });
            }
        });
    }
}
