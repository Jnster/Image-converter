package example;

import ru.jnster.AbstractArtist;
import ru.jnster.AbstructMask;

import java.awt.*;
import java.util.Arrays;


public class Bundle extends AbstractArtist {
    private int[][] matrix;
    private boolean isFlipped = false;

    protected Bundle(String pathToFile, AbstructMask mask) {
        super(pathToFile, mask);
        this.matrix = mask.getMatrix();
    }

    private void applyMaskWithFilter (int X, int Y){
        int color;
        //TODO: Уточнить область выборки цветов
        int[] colors = new int[width * height];
        try {
            imageOrigin.getRGB(X, Y, width, height, colors, 0, width);
            Arrays.sort(colors);
            color = colors[colors.length / 2];
            for (int y = 0; y < Math.round(height * 0.85f); y++) {
                try {
                    if (!isFlipped) {
                        for (int x = 0; x < width; x++) {
                            if (matrix[y + Math.round(height * 0.15f)][x] != 0) {
                                imageResult.setRGB(X + x, Y + y, color);
                            }
                        }
                    } else {
                        for (int x = width - 1; x >= 0; x--) {
                            if (matrix[y + Math.round(height * 0.15f)][width - 1 - x] != 0) {
                                imageResult.setRGB(X + x, Y + y, color);
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException are) {
                }
            }
        } catch (ArrayIndexOutOfBoundsException are) {
        }
    }


    @Override
    public void applyMaskOnPart(int X, int Y) {
        for(int y = 0; y < Math.round(height * 0.85f); y++){
            try {
                if(!isFlipped) {
                    for (int x = 0; x < width; x++) {
                        if (matrix[y + Math.round(height * 0.15f)][x] != 0) {
                            imageResult.setRGB(X + x, Y + y, imageOrigin.getRGB(X + x, Y + y));
                        }
                    }
                }
                else{
                    for (int x = width - 1; x >= 0; x--) {
                        if (matrix[y + Math.round(height * 0.15f)][width - 1 - x] != 0) {
                            imageResult.setRGB(X + x, Y + y, imageOrigin.getRGB(X + x, Y + y));
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException are){
            }
        }
    }

    @Override
    public void applyMasksOnFull() {
        int widthBound = width * (imageResult.getWidth() / width);
        int heightBound = Math.round(height * 0.6f) * (imageResult.getHeight() / Math.round(height * 0.6f));
        Graphics g = imageResult.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0,imageResult.getWidth(), imageResult.getHeight());
        for (int x = 0; x < widthBound; x += width){
            for (int y = 0; y < heightBound; y += Math.round(height * 0.6f)){
                applyMaskWithFilter(x,y);
            }
            isFlipped = !isFlipped;
        }
    }
}
