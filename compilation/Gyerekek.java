//Csak Throwable típusokat lehet eldobni és elkapni
class Labda extends Throwable {
    
}
class PirosLabda extends Labda {
    
}
class KékLabda extends Labda {
    
}
class SárgaLabda extends Labda {
    
}
class ZöldLabda extends Labda {
    
}
public class Gyerekek {
    public static void geri() {
        throw new KékLabda();
    }
    public static void pisti() {
        try {
            geri();
        } catch (Labda e) {
            throw new SárgaLabda();
        } catch (KékLabda e) {
            throw new ZöldLabda();
        }
    }
    public static void julcsi() {
        try {
            pisti();
        } catch (PirosLabda e) {
            throw new ZöldLabda();
        } catch (KékLabda e) {
            throw new PirosLabda();
        }
    }
    public static void main(String[] args) {
        Labda labda;
        //Julcsi felől repülhet labda
        try {
            julcsi();
        } catch (Labda l) {
            //Ha a dobott dolog egy Labda, elkapja
            labda = l;
        } catch (KékLabda l) {
            //Soha nem fog lefutni, mert a KékLabda egy Labda, ezért az előző catch elkapta
            labda = new ZöldLabda();
        }
        String válasz;
        if (labda instanceof PirosLabda) {
            válasz = "Piros";
        } else 
             if (labda instanceof KékLabda) {
                válasz = "Kék";
            } else 
                 if (labda instanceof SárgaLabda) {
                    válasz = "Sárga";
                } else 
                     if (labda instanceof ZöldLabda) {
                        válasz = "Zöld";
                    } 
        System.out.println(válasz.equals("Sárga"));
    }
}