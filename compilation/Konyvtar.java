class Könyv {
    int oldalakSzáma = 0;
    //Van értelme léteznie az Oldal-nak Könyv nélkül?
    class Oldal {
        String[] sorok;
        public Oldal(String oldal) {
            this.sorok = oldal.split("\n");
            //Elérjük a külső objektumot, ami létrehozta ezt
            Könyv.this.oldalakSzáma++;
        }
    }
    public Könyv(String tartalom) {
        String[] oldalTartalmak = tartalom.split("\n\n");
        for (int i = 0; i < oldalTartalmak.length; i++) {
            //EZ (this) hozza létre az Oldal-akat
            this.new Oldal(oldalTartalmak[i]);
        }
    }
}
public class Konyvtar {
    public static void main(String[] args) {
        Könyv abc = new Könyv("A\nB\n\nC\nD\n\nE\nF\n\nG\n...");
        //Szólj, hogy soha ne írjak könyvet, mert senki nem fogja elolvasni
        Könyv zyx = new Könyv("Z\n\nY\n\nX\n\nW\n\n...");
        System.out.println(abc.oldalakSzáma);
        System.out.println(zyx.oldalakSzáma);
    }
}