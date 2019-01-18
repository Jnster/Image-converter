package ru.jnster;

public class Main {

    public static void main(String[] args) {
        Bundle bundle = new Bundle("img.png", 20, 20);
        bundle.weaveImage();
        bundle.saveTo("opa.png");
    }
}
