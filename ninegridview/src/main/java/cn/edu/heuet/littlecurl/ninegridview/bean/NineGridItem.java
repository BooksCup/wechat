package cn.edu.heuet.littlecurl.ninegridview.bean;

import java.io.Serializable;

/**
 * 因为既要承载 Image 又要承载 Video，将来还有可能承载 Voice
 * 概括一下就是媒体信息，所以就叫成了 NineGridItem
 */
public class NineGridItem implements Serializable {
    public String thumbnailUrl;
    public String bigImageUrl;
    public int imageViewHeight;
    public int imageViewWidth;
    public int imageViewX;
    public int imageViewY;

    public String videoUrl;

    public int nineGridViewItemHeight;
    public int nineGridViewItemWidth;
    public int nineGridViewItemX;
    public int nineGridViewItemY;

    public NineGridItem(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public NineGridItem(String thumbnailUrl, String bigImageUrl) {
        this.thumbnailUrl = thumbnailUrl;
        this.bigImageUrl = bigImageUrl;
    }

    public NineGridItem(String thumbnailUrl, String bigImageUrl, String videoUrl) {
        this.thumbnailUrl = thumbnailUrl;
        this.bigImageUrl = bigImageUrl;
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getBigImageUrl() {
        return bigImageUrl;
    }

    public void setBigImageUrl(String bigImageUrl) {
        this.bigImageUrl = bigImageUrl;
    }

    public int getImageViewHeight() {
        return imageViewHeight;
    }

    public void setImageViewHeight(int imageViewHeight) {
        this.imageViewHeight = imageViewHeight;
    }

    public int getImageViewWidth() {
        return imageViewWidth;
    }

    public void setImageViewWidth(int imageViewWidth) {
        this.imageViewWidth = imageViewWidth;
    }

    public int getImageViewX() {
        return imageViewX;
    }

    public void setImageViewX(int imageViewX) {
        this.imageViewX = imageViewX;
    }

    public int getImageViewY() {
        return imageViewY;
    }

    public void setImageViewY(int imageViewY) {
        this.imageViewY = imageViewY;
    }

    public int getNineGridViewItemHeight() {
        return nineGridViewItemHeight;
    }

    public void setNineGridViewItemHeight(int nineGridViewItemHeight) {
        this.nineGridViewItemHeight = nineGridViewItemHeight;
    }

    public int getNineGridViewItemWidth() {
        return nineGridViewItemWidth;
    }

    public void setNineGridViewItemWidth(int nineGridViewItemWidth) {
        this.nineGridViewItemWidth = nineGridViewItemWidth;
    }

    public int getNineGridViewItemX() {
        return nineGridViewItemX;
    }

    public void setNineGridViewItemX(int nineGridViewItemX) {
        this.nineGridViewItemX = nineGridViewItemX;
    }

    public int getNineGridViewItemY() {
        return nineGridViewItemY;
    }

    public void setNineGridViewItemY(int nineGridViewItemY) {
        this.nineGridViewItemY = nineGridViewItemY;
    }

    @Override
    public String toString() {
        return "NineGridItem{" +
                "imageViewY=" + imageViewY +
                ", imageViewX=" + imageViewX +
                ", imageViewWidth=" + imageViewWidth +
                ", imageViewHeight=" + imageViewHeight +
                ", bigImageUrl='" + bigImageUrl + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                '}';
    }
}
