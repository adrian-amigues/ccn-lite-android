package ch.unibas.ccn_lite_android;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.Comparator;

/**
 * Represents an Area
 *
 * Created by adrian on 2016-10-18.
 */

public class Area {
    private String name;
    private String description;
    private int photoId;
    private Bitmap bitmap;
    private int smileyId;
    private String uriBase;
    private int valueCounter;
    private boolean selectedPhoto;
    private final int numberOfValues = 1;

    public void setSelectedPhoto(boolean selectedPhoto) {
        this.selectedPhoto = selectedPhoto;
    }

    public boolean isSelectedPhoto() {
        return selectedPhoto;
    }

    public Area(String name, String description, int photoId, String uriBase, Bitmap image) {
        this.name = name;
        this.description = description;
        this.photoId = photoId;
        this.uriBase = uriBase;
        this.valueCounter = 1;
        this.smileyId = R.drawable.face3;
        this.selectedPhoto = false;
        this.bitmap = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap(){ return bitmap;}

    public int getSmileyId() {
        return smileyId;
    }

    public void setSmileyId(int smileyId) {
        this.smileyId = smileyId;
    }

    public String getUri() {
//        return uriBase + valueCounter;
        return uriBase;
    }

    public int getValueCounter() {
        return valueCounter;
    }

    public void increaseValueCounter() {
        valueCounter = (valueCounter % numberOfValues) + 1;
    }
}


