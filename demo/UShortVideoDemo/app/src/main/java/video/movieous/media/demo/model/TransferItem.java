package video.movieous.media.demo.model;

import video.movieous.engine.image.UPhotoMovieType;

public class TransferItem {

    public TransferItem(int imgRes, String name, UPhotoMovieType type) {
        this.imgRes = imgRes;
        this.name = name;
        this.type = type;
    }

    public int imgRes;
    public String name;
    public UPhotoMovieType type;
}
