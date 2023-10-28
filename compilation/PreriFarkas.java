 class Gyalogkakukk {
    double sebességKmPerHban;
    public Gyalogkakukk(double sebességKmPerHban) {
        this.sebességKmPerHban = sebességKmPerHban;
    }
     double getSebességMPerSben() {
        return sebességKmPerHban / 3.6;
    }
}
class Kengyelfutó extends Gyalogkakukk {
    public Kengyelfutó() {
        super(120.0);
    }
    //Mit jelent itt a final kulcsszó?
    public final double getSebességMPerSben() {
        return 33.3333;
    }
}
public class PreriFarkas {
    public static void main(String[] args) {
        Kengyelfutó gyalogkakukk = new Kengyelfutó();
        Gyalogkakukk viliEllensége = new Gyalogkakukk(120.0);
        System.out.println(gyalogkakukk);
        System.out.println(viliEllensége);
    }
}