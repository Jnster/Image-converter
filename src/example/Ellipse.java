package example;

import ru.jnster.AbstructMask;

import java.awt.*;

public class Ellipse extends AbstructMask {
    private double stepInBezye;

    protected Ellipse() {
        super();
    }

    protected Ellipse(int width, int height) {
        super(width, height);
    }

    @Override
    public void createMask() {
        boolean isEllipse;
        Point pStart = new Point( Math.round(width * 0.20f), height / 2),
                pControlLeftTop = new Point(0, Math.round(height * 0.25f)), //TODO: поиграть с коэффициентом
                pControlRightTop = new Point(Math.round(width * 0.25f), 0), //TODO: поиграть с коэффициентом
                pControlLeftBottom = new Point(Math.round(width * 0.75f), height), //TODO: поиграть с коэффициентом; нужен ли -1?
                pControlRightBottom = new Point(width, Math.round(height * 0.75f)), //TODO: поиграть с коэффициентом; нужен ли -1?
                pFinish = new Point( Math.round(width * 0.80f), height / 2);
        Point result;
        createStepInBezye();
        for (double counter = 0.0; counter <= 1.0; counter += stepInBezye){
            result = calculateBezierFunction(counter,pStart,pControlLeftTop,pControlRightTop,pFinish);
            matrix[result.y][result.x] = 1;
            result = calculateBezierFunction(counter,pStart,pControlLeftBottom,pControlRightBottom,pFinish);
            matrix[result.y][result.x] = 1;
        }
        for (int[] row: matrix){
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
        for(int[] row : matrix){
            if (row[row.length - 1] != 0){            //Если закрасилось лишнее
                for (int counter = row.length - 1; counter >= 0; counter--){
                    if (row[counter] == 1) break;
                    else row[counter] = 0;
                }
            }
        }
    }

    /**
     * Вычисление шага для нахождения координат кривой Безье. Зависит от длины и ширины обрабатываемого участка.
     */
    private void createStepInBezye(){
        int buffer = width > height ? width : height;
        int counter = 0;
        while (buffer != 0){
            buffer /= 10;
            counter++;
        }
        stepInBezye = Math.pow(10, -counter);
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
}
