//Ez amúgy alapból van Javában, de a példa kedvéért létrehozom
interface Runnable {
    void run();
}
public class HelpMe {
    public static void main(String[] args) {
        Runnable one = new Runnable() {
            /*Ez nem kötelező, de egy jó IDE ideteszi, mert hibát jelez, ha egy olyan metódust akarunk
            létrehozni, amilyen nevű és paraméterű metódus nincs a szülő osztályban/megvalósítandó
            interfészben.*/ @Override
            void run() {
                System.out.println("Maffiózó");
            }
        };
        Runnable two = new Runnable() {
            @Override
            void run() {
                System.out.println("mellettem");
            }
        };
        Runnable buckleMyShoes = new Runnable() {
            //Nem? Senki? Lejárt már? Okés, bocsi.
            @Override
            void run() {
                System.out.println("parkol");
            }
        };
        two.run();
        buckleMyShoes.run();
        one.run();
    }
}