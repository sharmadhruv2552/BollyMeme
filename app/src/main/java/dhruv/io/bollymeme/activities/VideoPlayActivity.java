package dhruv.io.bollymeme.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.exoplayer.DummyTrackRenderer;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.upstream.BandwidthMeter;
import com.google.android.exoplayer.util.PlayerControl;
import com.google.android.exoplayer.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import dhruv.io.bollymeme.R;
import dhruv.io.bollymeme.model.BollyVideos;
import dhruv.io.bollymeme.utils.HpLibRendererBuilder;
import dhruv.io.bollymeme.utils.HpLib_HlsHpLibRendererBuilder;

public class VideoPlayActivity extends AppCompatActivity  implements HlsSampleSource.EventListener, View.OnClickListener{
    private static final String TAG = VideoPlayActivity.class.getSimpleName();

    private ExoPlayer player;
    private SurfaceView surface;
    private Handler mainHandler;
    private String video_url;
    private String video_type;
    private HpLibRendererBuilder hpLibRendererBuilder;
    private PlayerControl playerControl;
    private ImageButton btn_pause;

    public static final int TYPE_VIDEO = 0;

    private TrackRenderer videoRenderer;

    @BindView(R.id.bCreateMeme)
    Button buttonCreateMeme;
    @BindView(R.id.rlMemeVideo)
    RelativeLayout relativeMemeVideo;
    @BindView(R.id.ivMemeImage)
    ImageView imageMemeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);

        surface = (SurfaceView) findViewById(R.id.surface_view);
        buttonCreateMeme.setOnClickListener(this);

        mainHandler = new Handler();

//        ButterKnife.bind(this);

        BollyVideos bollyVideos =(BollyVideos) getIntent().getSerializableExtra("bollyVideos");
        video_type = "hls";
//        video_url = bollyVideos.getVideoURL();
        video_url = "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";
        execute();
    }

    private void execute(){

        player = ExoPlayer.Factory.newInstance(2);
        playerControl = new PlayerControl(player);
        if (player != null){
            hpLibRendererBuilder = getHpLibRendererBuilder();
            hpLibRendererBuilder.buildRenderers(this);
        }
    }

    private HpLibRendererBuilder getHpLibRendererBuilder(){

        String userAgent = Util.getUserAgent(this, "HpLib");
        switch (video_type){

            case "hls":
                return new HpLib_HlsHpLibRendererBuilder(this, userAgent, video_url);
            default:
                throw new IllegalStateException("Unsupported type:" + video_url);
        }
    }

    public Handler getMainHandler() {
        return mainHandler;
    }

    public void onRenderersError(Exception e) {}

    public void onRenderers(TrackRenderer[] renderers, BandwidthMeter bandwidthMeter) {
        for (int i = 0; i < renderers.length; i++) {
            if (renderers[i] == null) {
                renderers[i] = new DummyTrackRenderer();
            }
        }
        // Complete preparation.
        this.videoRenderer = renderers[TYPE_VIDEO];
        pushSurface(false);
        player.prepare(renderers);
        player.setPlayWhenReady(true);
    }

    private void pushSurface(boolean blockForSurfacePush) {
        if (videoRenderer == null) {return;}
        if (blockForSurfacePush) {
            player.blockingSendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface.getHolder().getSurface());
        } else {
            player.sendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface.getHolder().getSurface());
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.bCreateMeme:
                playerControl.pause();

                Bitmap bitmap = getBitmapFromView(relativeMemeVideo);
                Log.e(TAG, "Current duration: " + playerControl.getCurrentPosition());
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.
                playerControl.getCurrentPosition();
                imageMemeImage.setImageBitmap(bitmap);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                byte[] byteArray = stream.toByteArray();

//                Intent intent = new Intent(this, EditMemeActivity.class);
//                intent.putExtra("BitmapImage", byteArray);
//                startActivity(intent);
                break;
        }

    }

    @Override
    public void onLoadStarted(int sourceId, long length, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs) {

    }

    @Override
    public void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs) {

    }

    @Override
    public void onLoadCanceled(int sourceId, long bytesLoaded) {

    }

    @Override
    public void onLoadError(int sourceId, IOException e) {

    }

    @Override
    public void onUpstreamDiscarded(int sourceId, long mediaStartTimeMs, long mediaEndTimeMs) {

    }

    @Override
    public void onDownstreamFormatChanged(int sourceId, Format format, int trigger, long mediaTimeMs) {

    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    public Bitmap compressImageBitmap(Bitmap bitmap) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight = 1400.0f;
        float maxWidth = 1200.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        return scaledBitmap;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}
