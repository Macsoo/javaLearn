abstract class Emlős {
    int getVégtagokSzáma() {
        return 4;
    }
    abstract String getHang();
}
class Rovar {
    int getVégtagokSzáma() {
        return 6;
    }
}
interface PatásEmlős {
    int getPatákSzáma();
}
interface SzarvasEmlős {
    int getSzarvakSzáma();
}
interface RepülőEmlős {
    int getSzárnyakSzáma();
}
class Kecske extends Emlős implements PatásEmlős, SzarvasEmlős {
    
    public Kecske() {
        
    }
}
public class MEEEEE {
    public static void main(String[] args) {
        Kecske meki = new Kecske();
        /*
        Az instanceof megnézi, hogy egy adott objektum egy osztály leszármazottja,
        vagy egy interfészt implementál-e
        */ if (!(meki instanceof Kecske)) {
            System.out.println("Az Univerzum haldoklik.");
        } 
        System.out.println(meki.getVégtagokSzáma() + " végtagja van Mekinek.");
        if (meki instanceof PatásEmlős) {
            System.out.println("És " + meki.getPatákSzáma() + " patája van egy lábán.");
        } 
        if (meki instanceof SzarvasEmlős) {
            System.out.println("Illetve " + meki.getSzarvakSzáma() + " szarva.");
        } 
        if (meki instanceof RepülőEmlős) {
            System.out.println("Na meg " + meki.getSzárnyakSzáma() + " szárnya.");
        } 
    }
}