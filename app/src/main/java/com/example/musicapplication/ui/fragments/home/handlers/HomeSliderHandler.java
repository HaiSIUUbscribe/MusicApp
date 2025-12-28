package com.example.musicapplication.ui.fragments.home.handlers;

import android.os.Handler;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import com.example.musicapplication.R;
import com.example.musicapplication.model.SliderItem;
import com.example.musicapplication.ui.adapter.SliderAdapter;
import java.util.ArrayList;
import java.util.List;

public class HomeSliderHandler {
    private ViewPager2 vpSlider;
    private final Handler sliderHandler = new Handler();
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (vpSlider != null) {
                int current = vpSlider.getCurrentItem();
                int total = vpSlider.getAdapter() != null ? vpSlider.getAdapter().getItemCount() : 0;
                if (total > 0) {
                    vpSlider.setCurrentItem((current + 1) % total);
                }
            }
        }
    };

    public HomeSliderHandler(View view) {
        vpSlider = view.findViewById(R.id.vp_slider);
        setupSlider();
    }

    private void setupSlider() {
        List<SliderItem> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItem("https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=800&q=80"));
        sliderItems.add(new SliderItem("https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&q=80"));
        sliderItems.add(new SliderItem("https://images.unsplash.com/photo-1493225255756-d9584f8606e9?w=800&q=80"));

        if (vpSlider != null) {
            vpSlider.setAdapter(new SliderAdapter(sliderItems, vpSlider));
            vpSlider.setClipToPadding(false);
            vpSlider.setClipChildren(false);
            vpSlider.setOffscreenPageLimit(3);
            vpSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

            CompositePageTransformer transformer = new CompositePageTransformer();
            transformer.addTransformer(new MarginPageTransformer(40));
            transformer.addTransformer((page, position) -> {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            });
            vpSlider.setPageTransformer(transformer);

            vpSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    sliderHandler.removeCallbacks(sliderRunnable);
                    sliderHandler.postDelayed(sliderRunnable, 3000);
                }
            });
        }
    }

    public void onPause() {
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    public void onResume() {
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}