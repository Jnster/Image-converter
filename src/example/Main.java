package example;

import ru.jnster.AbstructMask;

public class Main {

    public static void main(String[] args) {
        int width = 10, height = 10;
        AbstructMask ellipse = new Ellipse(width,height);
        Bundle bundle = new Bundle("example.jpeg", ellipse, true);
        bundle.applyMasksOnFull();
        bundle.saveTo("result" + width + 'x' + height + '.' + bundle.getFormat() );
        System.out.println("All is done!");
    }
}
