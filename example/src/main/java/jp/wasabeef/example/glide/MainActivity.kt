package jp.wasabeef.example.glide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.wasabeef.example.glide.MainAdapter.Type.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.list).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MainAdapter(context, mutableListOf(
                    Mask, NinePatchMask, RoundedAll,
                    RoundedTopLeft,
                    RoundedTopRight,
                    RoundedBottomLeft,
                    RoundedBottomRight,
                    RoundedTop,
                    RoundedBottom,
                    RoundedLeft,
                    RoundedRight,
                    RoundedOtherTopLeft,
                    RoundedOtherTopRight,
                    RoundedOtherBottomLeft,
                    RoundedOtherBottomRight,
                    RoundedDiagonalFromTopLeft,
                    RoundedDiagonalFromTopRight,
                    CropTop, CropCenter, CropBottom, CropSquare, CropCircle, BorderCircleCrop, Grayscale,
                    Blur, SupportRSBlur, Toon, Sepia, Contrast, Invert, Pixel, Sketch, Swirl, Brightness,
                    Kuawahara, Vignette
            ))
        }
    }
}
