package ru.jnster;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public abstract class AbstractArtist implements Artist{
    protected BufferedImage imageResult, imageOrigin;
    protected String format;
    protected int width, height;
    protected AbstructMask mask;

    protected AbstractArtist(String pathToFile, AbstructMask mask){
        openFile(pathToFile);
        this.height = mask.height;
        this.width = mask.width;
        this.mask = mask;
    }

    @Override
    public void openFile(String pathFile) {
        String[] buffer = pathFile.split("[.]");
        format = buffer[buffer.length - 1];
        boolean isLegal = false;
        for (String ex : ImageIO.getReaderFileSuffixes()){
            isLegal |= format.equals(ex);
        }
        if (!isLegal) throw new RuntimeException("Wrong format of file.");
        Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName(format);
        ImageReader reader = null;
        if (iter.hasNext()) reader = iter.next();
        try {
            InputStream inputStream = new FileInputStream(new File(pathFile));
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);
            assert reader != null; //TODO: посмотреть на практике работу
            reader.setInput(imageInputStream);
            imageResult = reader.read(0);
            imageOrigin = reader.read(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveTo(String pathFile) {
        try {
            ImageIO.write(imageResult, format, new File(pathFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void applyMaskOnPart(int X, int Y);

    public String getFormat() {
        return format;
    }
}
