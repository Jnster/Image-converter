package example;

import ru.jnster.AbstructMask;

public class Main {

    public static void main(String[] args) {
        AbstructMask ellipse = new Ellipse(20,20);
        Bundle bundle = new Bundle("img.png", ellipse);
        bundle.applyMasksOnFull();
        bundle.saveTo("opa.png");
    }
}
