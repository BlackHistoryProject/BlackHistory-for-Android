package jp.promin.android.blackhistory;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

/**
 * Created by atsumi on 2016/09/27.
 */
public class PhotoStructure {
    private String path;
    private Context context;
    private Uri fileUrl;
    private ExifInterface exif;

    public PhotoStructure(Context context, @NonNull Uri fileUrl) {
        this.context = context;
        this.fileUrl = fileUrl;
        this.path = fileUrl.getPath();
        try {
            this.exif = new ExifInterface(fileUrl.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return new File(this.path).getName();
    }

    public Uri getDirectUrl() {
        return Uri.fromFile(new File(String.valueOf(this.fileUrl.getPath())));
    }

    public Bitmap getBitmap() {
        try {
            Matrix matrix = getMatrix();
            BitmapFactory.Options sizeOp = new BitmapFactory.Options();
            sizeOp.inSampleSize = 2;

            Bitmap bitmap = BitmapFactory.decodeFile(this.path, sizeOp);
            if (matrix != null) {
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            return BitmapFactory.decodeFile(this.path, sizeOp);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            ((Activity) context).finish();
            return null;
        }
    }

    private Matrix getMatrix(){
        if(this.exif == null) return null;
        Matrix matrix = new Matrix();
        final int attr = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (attr) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.postScale(-1f, 1f);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.postScale(1f, -1f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(-90f);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.postRotate(90f);
                matrix.postScale(1f, -1f);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.postRotate(-90f);
                matrix.postScale(1f, -1f);
                break;
            default:
                return null;
        }
        return matrix;
    }
}
