package ru.jnster;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Iterator;


public class Bundle {
    private BufferedImage imageResult, imageOrigin;
    private String format;
    private int widthBundle, heightBundle;
    private int[][] mask;
    private double stepInBezye;
    private boolean isFlipped = false;

    public Bundle(String pathToFile){
        this(pathToFile, 10, 10);
    }

    public Bundle (String pathToFile, int widthBundle, int heightBundle){
            openFile(pathToFile);
            this.heightBundle = heightBundle;
            this.widthBundle = widthBundle;
        createMask();
    }

    public void weaveImage(){
        int widthBound = widthBundle * (imageResult.getWidth() / widthBundle);
        int heightBound = Math.round(heightBundle * 0.6f) * (imageResult.getHeight() / Math.round(heightBundle * 0.6f));
        Graphics g = imageResult.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0,imageResult.getWidth(), imageResult.getHeight());
        for (int x = 0; x < widthBound; x += widthBundle){
            for (int y = 0; y < heightBound; y += Math.round(heightBundle * 0.6f)){
                applyMaskWithFilter(x,y);
            }
            isFlipped = !isFlipped;
        }
    }

    public void saveTo(String fileName) {
        try {
            ImageIO.write(imageResult, format, new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Нахождение точки на кривой Безье в зависимости от времени
     * @param t             Время от 0.0 до 1.0
     * @param pStart        Точка начала кривой
     * @param pControlLeft  Первая вспомагательная точка
     * @param pControlRight Вторая вспомагательная точка
     * @param pFinish       Точка конца кривой
     * @return Точка на кривой для указанного времени
     */
    //TODO: зависимость шагов времени (от 0 до 1) от размера участка?
    private Point calculateBezierFunction(double t, Point pStart, Point pControlLeft, Point pControlRight, Point pFinish)
    {
        double x,y;
        x = pStart.x*(Math.pow(-t,3)+3*Math.pow(t,2)-3*t+1)
                +3*pControlLeft.x*t*(Math.pow(t,2)-2*t+1)
                +3*pControlRight.x*Math.pow(t,2)*(1-t)+pFinish.x*Math.pow(t,3);
        y = pStart.y*(Math.pow(-t,3)+3*Math.pow(t,2)-3*t+1)
                +3*pControlLeft.y*t*(Math.pow(t,2)-2*t+1)
                +3*pControlRight.y*Math.pow(t,2)*(1-t)+pFinish.y*Math.pow(t,3);
        return new Point((int)Math.round(x),(int)Math.round(y));
    }

    /**
     * Создание маски (координат пикселей линий фигуры) эллипса с помощью двух кривых Безье.
     *
     */
    private void createMask(){
        mask = new int[heightBundle][widthBundle];
        boolean isEllipse;
        Point pStart = new Point( Math.round(widthBundle * 0.20f), heightBundle / 2),
            pControlLeftTop = new Point(0, Math.round(heightBundle * 0.25f)), //TODO: поиграть с коэффициентом
            pControlRightTop = new Point(Math.round(widthBundle * 0.25f), 0), //TODO: поиграть с коэффициентом
            pControlLeftBottom = new Point(Math.round(widthBundle * 0.75f), heightBundle), //TODO: поиграть с коэффициентом; нужен ли -1?
            pControlRightBottom = new Point(widthBundle, Math.round(heightBundle * 0.75f)), //TODO: поиграть с коэффициентом; нужен ли -1?
            pFinish = new Point( Math.round(widthBundle * 0.80f), heightBundle / 2);
        Point result;
        createStepInBezye();
        for (double counter = 0.0; counter <= 1.0; counter += stepInBezye){
            result = calculateBezierFunction(counter,pStart,pControlLeftTop,pControlRightTop,pFinish);
            mask[result.y][result.x] = 1;
            result = calculateBezierFunction(counter,pStart,pControlLeftBottom,pControlRightBottom,pFinish);
            mask[result.y][result.x] = 1;
        }
        for (int[] row: mask){
            isEllipse = false;
            for (int counter = 0; counter < row.length; counter++){
                if(row[counter] == 1){
                    if ((counter + 1 < row.length) && (row[counter + 1] == 1)){ //если несколько единиц под ряд,
                        continue;                                               //то ничего не делать, пока "ряд" не закончится
                    }
                    isEllipse = !isEllipse;
                    continue;
                }
                if (isEllipse){
                    row[counter] = -1;
                }
            }
        }
        for(int[] row : mask){
            if (row[row.length - 1] != 0){            //Если закрасилось лишнее
                for (int counter = row.length - 1; counter >= 0; counter--){
                    if (row[counter] == 1) break;
                    else row[counter] = 0;
                }
            }
        }
    }

    /**
     * Применение маски на массив пикселей (RGBA)
     * @param pixels массив пикселе для обработки
     * @return тот же массив после обработки
     */
    private int[] applyMask(int[] pixels){
        for(int y = 0; y < heightBundle* 3 / 4; y++){
            for(int x = 0; x < widthBundle; x++){
                if (mask[y][x] == 0){
                    pixels[y * widthBundle + x * 4] = 0;
                    pixels[y * widthBundle + x * 4 + 1] = 0;
                    pixels[y * widthBundle + x * 4 + 2] = 0;
                }
            }
        }
        return pixels;
    }

    private void applyMask(int X, int Y){
        for(int y = 0; y < Math.round(heightBundle * 0.85f); y++){
            try {
                if(!isFlipped) {
                    for (int x = 0; x < widthBundle; x++) {
                        if (mask[y + Math.round(heightBundle * 0.15f)][x] != 0) {
                            imageResult.setRGB(X + x, Y + y, imageOrigin.getRGB(X + x, Y + y));
                        }
                    }
                }
                else{
                    for (int x = widthBundle - 1; x >= 0; x--) {
                        if (mask[y + Math.round(heightBundle * 0.15f)][widthBundle - 1 - x] != 0) {
                            imageResult.setRGB(X + x, Y + y, imageOrigin.getRGB(X + x, Y + y));
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException are){
              continue;
            }
        }
    }

    private void applyMaskWithFilter (int X, int Y){
        int color;
        //TODO: Уточнить область выборки цветов
        int[] colors = new int[widthBundle * heightBundle];
        try {
            imageOrigin.getRGB(X, Y, widthBundle, heightBundle, colors, 0, widthBundle);
            Arrays.sort(colors);
            color = colors[colors.length / 2];
            for (int y = 0; y < Math.round(heightBundle * 0.85f); y++) {
                try {
                    if (!isFlipped) {
                        for (int x = 0; x < widthBundle; x++) {
                            if (mask[y + Math.round(heightBundle * 0.15f)][x] != 0) {
                                imageResult.setRGB(X + x, Y + y, color);
                            }
                        }
                    } else {
                        for (int x = widthBundle - 1; x >= 0; x--) {
                            if (mask[y + Math.round(heightBundle * 0.15f)][widthBundle - 1 - x] != 0) {
                                imageResult.setRGB(X + x, Y + y, color);
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException are) {
                    continue;
                }
            }
        } catch (ArrayIndexOutOfBoundsException are) {
        }
    }

    /**
     * Вычисление шага для нахождения координат кривой Безье. Зависит от длины и ширины обрабатываемого участка.
     */
    private void createStepInBezye(){
        int buffer = widthBundle > heightBundle ? widthBundle : heightBundle;
        int counter = 0;
        while (buffer != 0){
            buffer /= 10;
            counter++;
        }
        stepInBezye = Math.pow(10, -counter);
    }

    private void openFile(String pathFile) {
        String[] buffer = pathFile.split("[.]");
        format = buffer[buffer.length - 1];
        boolean isLeagal = false;
        for (String ex : ImageIO.getReaderFileSuffixes()){
            isLeagal |= format.equals(ex);
        }
        if (!isLeagal) throw new RuntimeException("Wrong format of file.");
        Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName(format);
        ImageReader reader = null;
        if (iter.hasNext()) reader = iter.next();
        try {
            InputStream inputStream = new FileInputStream(new File(pathFile));
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);
            reader.setInput(imageInputStream);
            imageResult = reader.read(0);//reader.getNumImages(false));
            imageOrigin = reader.read(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
