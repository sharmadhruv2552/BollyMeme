package dhruv.io.bollymeme.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dhruv.io.bollymeme.R;

public class EditMemeActivity extends AppCompatActivity {

    @BindView(R.id.ivMeme)
    ImageView imageMeme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meme);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        byte[] byteArray = intent.getByteArrayExtra("BitmapImage");

        Log.e("MEME", byteArray.toString());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

        imageMeme.setImageBitmap(bmp);
    }
}
