//Ismerős?
interface IntComparator {
    //Egyik kisebb-e, mint a másik?
    boolean compare(int egyik, int másik);
}
//Ez is van (kind of) Javában
interface IntConsumer {
    void consume(int x);
}
class Számok {
    //Ne legyen gyerekem, nem fogom tudni elnevezni
    int[] számok;
    public Számok(int[] számok) {
        this.számok = számok;
    }
    public void sorba(IntComparator összehasonlító) {
        //Béna bubble sort
        int temp;
        boolean swapped;
        for (int i = 0; i < számok.length - 1; i++) {
            swapped = false;
            for (int j = 0; j < számok.length - i - 1; j++) {
                if (összehasonlító.compare(számok[i], számok[j])) {
                    temp = számok[j];
                    számok[j] = számok[j + 1];
                    számok[j + 1] = temp;
                    swapped = true;
                } 
            }
            if (swapped == false) 
                 break;
            
        }
    }
    public void csináldMinddel(Runnable ezt) {
        for (int elem: számok) {
            ezt.consume(elem);
        }
    }
}
public class HetiHetes {
    //Nem, erre még nincs megoldás...
    public static void main(String[] args) {
        Számok nyerőLottóSzámok = new Számok(new int[] {80, 75, 14, 6, 2});
        nyerőLottóSzámok.sorba((a, b) -> a < b;);
        IntConsumer kiírató = x -> System.out.println(x);;
        nyerőLottóSzámok.csináldMinddel(kiírató);
        System.out.println();
        nyerőLottóSzámok.sorba((a, b) -> a > b);
        nyerőLottóSzámok.csináldMinddel(kiírató);
    }
}