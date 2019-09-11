package com.hbidriver.app.banner;

import com.hbidriver.app.model.SlidesModel;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

/**
 * @author S.Shahini
 * @since 2/12/18
 */

public class MainSliderAdapter extends SliderAdapter {

    private List<SlidesModel.DataEntity> list;

    public MainSliderAdapter(List<SlidesModel.DataEntity> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        if(null==list&& list.size()<0){
            return 0;
        }
        return list.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
//        switch (position) {
//            case 0:
//                viewHolder.bindImageSlide("https://assets.materialup.com/uploads/dcc07ea4-845a-463b-b5f0-4696574da5ed/preview.jpg");
//                break;
//            case 1:
//                viewHolder.bindImageSlide("https://assets.materialup.com/uploads/20ded50d-cc85-4e72-9ce3-452671cf7a6d/preview.jpg");
//                break;
//            case 2:
//                viewHolder.bindImageSlide("https://assets.materialup.com/uploads/76d63bbc-54a1-450a-a462-d90056be881b/preview.png");
//                break;
//        }
        viewHolder.bindImageSlide(list.get(position).getImagePath());
    }

}
