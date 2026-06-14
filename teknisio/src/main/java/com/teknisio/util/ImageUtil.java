package com.teknisio.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.ByteArrayInputStream;
import java.util.Base64;

/**
 * Utility to load profile photos from base64 strings.
 */
public class ImageUtil {

    /**
     * Load an Image from a base64 string.
     * Supports "data:image/png;base64,..." prefix or raw base64.
     * Returns null if the string is null/empty/invalid.
     */
    public static Image imageFromBase64(String base64) {
        if (base64 == null || base64.isBlank()) return null;
        try {
            String raw = base64;
            if (raw.contains(",")) {
                raw = raw.substring(raw.indexOf(',') + 1);
            }
            byte[] bytes = Base64.getDecoder().decode(raw.trim());
            return new Image(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            System.err.println("ImageUtil.imageFromBase64: failed to decode — " + e.getMessage());
            return null;
        }
    }

    /**
     * Apply a base64 photo to an ImageView.
     * If decoding fails or base64 is null, the ImageView is unchanged.
     */
    public static void applyBase64ToImageView(ImageView imageView, String base64) {
        Image img = imageFromBase64(base64);
        if (img != null) {
            imageView.setImage(img);
        }
    }

    /**
     * Apply rounded rectangle clip to an ImageView.
     */
    public static void applyRoundedClip(ImageView iv, double width, double height, double arcRadius) {
        Rectangle clip = new Rectangle(width, height);
        clip.setArcWidth(arcRadius);
        clip.setArcHeight(arcRadius);
        iv.setClip(clip);
    }
}
