class MrCsemege {
    private int maiVevők;
    private int bevétel;
    private void napKezdete() {
        bevétel = 0;
        maiVevők = 0;
    }
    public MrCsemege() {
        napKezdete();
    }
    public void eladás(int ár) {
        bevétel += ár;
        maiVevők++;
    }
    public int getMaiVevők() {
    return maiVevők;
}
    public int getBevétel() {
        return bevétel;
    }
}
public class KisboltVsNAV {
    public static void main(String[] args) {
        MrCsemege kisbolt = new MrCsemege();
        kisbolt.eladás(1000);
        kisbolt.eladás(2000);
        System.out.println(kisbolt.getMaiVevők());
        //Próbavásárlás, de minden okés
        kisbolt.eladás(1500);
        kisbolt.eladás(100);
        //Mondjuk valamiért árulnak autót is...
        kisbolt.eladás(10000000);
        System.out.println(kisbolt.getBevétel());
        System.out.println(kisbolt.getMaiVevők());
    }
}