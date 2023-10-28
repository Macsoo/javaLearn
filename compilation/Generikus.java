//Általában a generikusok nevei egyetlen betűből állnak, T a típus rövidítése
class ÖtHosszúTömb<T> {
    //Használható bárhol, ahol típus szükséges
    T[] tömb;
    public ÖtHosszúTömb() {
        tömb = new T[5];
    }
    public void setElem(int i, int elem) {
        if (i < 0 || i > 4) 
             return;
        
        tömb[i] = elem;
    }
    public int getElem(int i) {
        return tömb[i];
    }
}
public class Generikus {
    //Lehet metódusban is deklarálni generikust
    public static <U> ÖtHosszúTömb<U> getÖtHosszúTömb() {
        /*Bárhol ahol egy generikus osztály megvan említve,
        a <>-ban ki kell írni a generikusok értékeit.
        Jelenleg ki tudja számolni a Java, szóval nem szükséges,
        csak a zárójel*/ return new ÖtHosszúTömb<>();
    }
    public static void main(String[] args) {
        //Generikusok értékei csak öszetett típusok lehetnek, int helyett Integer
        ÖtHosszúTömb<ÖtHosszúTömb<Integer>> mátrix = getÖtHosszúTömb();
        mátrix.getElem(0).setElem(3, 9);
        for (ÖtHosszúTömb<> vektor: mátrix) {
            for (Integer i: vektor) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }
}