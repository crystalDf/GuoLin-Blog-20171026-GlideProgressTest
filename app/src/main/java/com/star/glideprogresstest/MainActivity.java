package com.star.glideprogresstest;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String BING_PIC_URL = "http://guolin.tech/api/bing_pic";

    private Button mLoadImage;
    private ImageView mImageView;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadImage = findViewById(R.id.load_image_button);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);

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

                    ProgressInterceptor.addListener(bingPic,
                            progress -> mProgressBar.setProgress(progress));

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
                            .into(new DrawableImageViewTarget(mImageView) {

                                @Override
                                public void onLoadStarted(@Nullable Drawable placeholder) {
                                    super.onLoadStarted(placeholder);
                                    mProgressBar.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    super.onResourceReady(resource, transition);
                                    mProgressBar.setVisibility(View.GONE);

                                    ProgressInterceptor.removeListener(bingPic);
                                }
                            });
                });
            }
        });
    }
}
