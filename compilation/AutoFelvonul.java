class Jármű {
    int kerekekSzáma;
    public Jármű(int kerekekSzáma) {
        this.kerekekSzáma = kerekekSzáma;
    }
    public String leírás() {
        return "Jármű " + kerekekSzáma + " kerékkel.";
    }
}
class Autó extends Jármű {
    String márka;
    public Autó(String márka) {
        super(4);
        this.márka = márka;
    }
    public String leírás() {
        return márka + " márkájú autó " + kerekekSzáma + " kerékkel.";
    }
}
class Suzuki extends Autó {
    String típus;
    public Suzuki(String típus) {
        super("Suzuki");
        this.típus = típus;
    }
    public String leírás() {
        return "Kicsi kocsi Suzuki " + típus + " " + kerekekSzáma + " kerékkel.";
    }
}
public class AutoFelvonulás {
    static String leírásVisszafele(Jármű leírandó) {
        return new StringBuilder(leírandó.leírás()).reverse().toString();
    }
    public static void main(String[] args) {
        Jármű tricikli = new Jármű(3);
        //Működik, csak nehogy félreértés legyen belőle
        Jármű lovasKocsi = new Autó("Ferrari");
        Suzuki kicsiKocsi = new Suzuki("Swift");
        System.out.println("A felsorakozott járművek:");
        System.out.println(leírásVisszafele(tricikli));
        System.out.println(leírásVisszafele(lovasKocsi));
        System.out.println(leírásVisszafele(kicsiKocsi));
        System.out.println("Mondtam ne az üvegtáblák mögé üljünk!");
    }
}