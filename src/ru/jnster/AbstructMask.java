package ru.jnster;

public abstract class AbstructMask implements Mask {
    protected int[][] matrix;
    protected int width, height;

    protected AbstructMask(){
        width = height = 0;
        matrix = new int[height][width];
        createMask();
    }

    protected AbstructMask(int width, int height){
        this.height = height;
        this.width = width;
        matrix = new int[this.width][this.height];
        createMask();
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
