# Java problémákon át

Ez a program (továbbiakban: "könyv") a Java készítőinek a szemszögéből próbálja megtanítani azt. Megnézzük milyen problémákba futottak létrehozásakor és utána, és hogy azokra milyen megoldásokat találtak ki. A könyv interaktív, így le lehet futtatni a benne található kódokat, és akár át is lehet írni részeit. Úgy alkottam meg, hogy minden újabb részt az előző feladat megoldásával lehet megnyitni.

**MEGJEGYZÉS:** Ez nem a Java története lesz. A Java eléggé máshogy jött létre, más sorrendben jöttek benne létre funkciók, és a Java megtanulása a célja ennek a könyvnek, nem egy történelem óra. A könyv feltételez egy minimális C++ tudást (*Prog1 csak megvan, nem?*).

## C++

A C++ egy népszerű nyelv. A gond, hogy bonyolult is, így nehéz megtanulni, és nehéz benne nagyobb programokat írni. A C++ a C-n alapszik, ami azzal a igen nemes céllal jött létre, hogy ne kelljen minden egyes CPU-ra újra írni a programot, mivel minden CPU más-más nyelvet ért. A C népszerűségét annak köszönheti, hogy elérte azt, hogy csak a fordítót kelljen megírni a CPU nyelvén, ami aztán lefordítja a C kódot a CPU nyelvére, így nem kell minden CPU-ra megírni a kódot!

### Probléma

A C++ örökölte a C jó dolgait, viszont a nagy problémáját is ami a nagy nemes céljából fakadt: habár nem kell újra írni minden CPU-ra a kódot, *lefordítani* viszont minden CPU-ra kell. Ez ugye megakadályozta azt, hogy könnyen lehessen minden már megírt programot futtatni egy felhasználónak, mivel le kellett fordítani még azt.

### Megoldás

Írjunk egy programozási nyelvet, ami nem a CPU-n fut, hanem egy "*virtuális*" gépen (**JVM**), aminek az utasításai a CPU nyelvén futnak le. Így csak egy fordítót kell megírni, ami a virtuális gép nyelvére kell fordítania, aztán azt a felhasználók tudják futtatni. Így a programozoknak tényleg csak egy helyen kell megírni, aztán bárhol lefuttathatják: *Write once, run anywhere*. A probléma, hogy a virtuális gépet meg kell írni minden CPU-ra még mindig fenn áll, de ez egyetlen program és ez nem megkerülhető.

### Feladat

Írja ki a következő kód a "Hello World!" szöveget!

```java
/*#{Hello World!}#*/
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println(/*${"Hi Mom!"}$*/"");
    }
}
```

## Primitive data types

C++ a C kibővítése miatt jött létre, attól "eggyel jobb" akart lenni (*Innen a név*). A legnagyobb fejlesztés az osztályok voltak, amiknek hála a programozó által létrehozott egyedi típusok nem csak egymás melletti bitek voltak, hanem meg is könnyítették a programozó dolgát.

Ez viszont nem változtatja meg, hogy a C-n alapul a nyelv, és így az osztályok mindig ilyen "kakukktojásként" érződtek a nyelvben, mivel nem köréjük épült a nyelv.

### Probléma

Java megoldása erre, hogy a nyelv az osztályok köré épült! Wait, de ez nem a **Probléma** szekció?

Jó helyen vagyunk, ugyanis az osztályok és az objektumok futtatási hátránnyal rendelkeznek. Nem inkább a proceszorra kell nézni, hanem a memória a bűnös, de ez se olyan hatalmas, viszont hamar kifuthatunk ha nem figyelünk oda, főleg ha még csak MegaByteban mérjük a memóriát jobb esetben.

### Megoldás

Javában a nagyon gyakran használt típusok teljesen kivételként vannak kezelve, amiket primitív típusoknak hívunk. Ezek a következők:

* `byte`: 1 byte, egész szám
* `short`: 2 byte, egész szám
* `int`: 4 byte, egész szám
* `long`: 8 byte, egész szám
* `float`: 4 byte, valós szám
* `double`: 8 byte, valós szám, nagyobb pontosság
* `boolean`: igaz vagy hamis
* `char`: 2 byte, karakter

Ezek a típusok értékei a memóriában vannak direkt, semmilyen körítés nélkül. Ez viszont azzal jár, hogy nem lehetnek metódusai, így minden primitív típusnak van egy segítő "wrapper" osztálya, ami ezeket tárolja: `Byte`, `Short`, `Integer`, `Long`, `Float`, `Double`, `Boolean` és `Character`.

### Feladat

A kód elsőnek kiírja, hogy a '9' karakter egy szám-e. Ezután írja ki, hogy a mennyi a 420 értéke 16-os számrendszerben!

```java
/*#{true
1a4}#*/
public class WrapperTypes {
    public static void main(String[] args) {
        //HIBÁS: System.out.println('9'.isDigit());
        System.out.println(Character.isDigit('9'));
        System.out.println(/*${420}$*/"".toHexString(/*${}$*/""));
    }
}
```

## Abstraction

Mielőtt a Java létrejött, már létezett az osztály alapú programozás más nyelvekben is, például a C++. De a Java nem volt úttörő a teljesen objektum orientált programozási nyelvek terén sem. A `Smalltalk` például már 20 évvel megelőzte a Javát, így amikor az létrejött, már volt a Java fejlesztőinek mások hibáiból tanulni.

### Probléma

Egyik ilyen tanulság, hogy megéri ha az osztályok el tudnak bizonyos tulajdonságaikat rejteni mások elől. Ez akkor nagyon hasznos, ha az objektum szeretne egy bizonyos szabályt betartatni a tulajdonságai között. Ha bárki más képes úgy változtatni az értékeket ahogy csak szeretné, akkor a szabály nem lesz betartva. ...De az osztályt azért hozták létre, hogy a szabályt betartsák. Akkor minek az osztály? Akkor minek bármilyen osztály? AKKOR MINEK AZ OBJEKTUM ORI-

### Megoldás

Javában az objektumok tulajdonságainak lehet adni egy "láthatósági" módosítót. Ezek a következők:

* `private`: Az tulajdonság nem látható, csak az adott osztály tagjai számára.
* `protected`: Az tulajdonság nem látható, csak az adott osztály és az alosztályainak a tagjai számára.
* `public`: Az tulajdonság látható bárki számára.

### Feladat

A `Person` osztály `age` tulajdonsága ne csökenhessen! Ha megpróbáljuk csökkenteni, akkor az értéke ne változzon.

```java
/*#{7
40
40}#*/
class Person {
    /*${public}$*/public int age;
    public Person(int age) {
        this.age = age;
    }

    public void setAge(int value) {
        /*={if (value == 40)
    System.out.println("Midlife crisis!");}=*/;
        age = value;
    }

    public int getAge() {
        return age;
    }
}

public class JohnAndTheWizard {
    public static void main(String[] args) {
        Person john = new Person(0); //Megszületett!
        john.setAge(7); //Iskolába megy. Szegény.
        System.out.println(john.getAge());
        john.setAge(40); //Olyan hamar felnőnek!
        System.out.println(john.getAge());
        john.setAge(10); //Elment egy varázslóhoz, hogy újra átélje gyerekkorát.
        System.out.println(john.getAge()); //...Megtalálta a varázslót?
    }
}
```

## Encapsulation

Az objektum orientált programozás azért lett népszerű, mert egyszerűen de nagyszerűen tudja a valóságot leírni. Ezt az előnyt meg kell tartani, úgy hogy tényleg betartjuk az osztályok programozásánál próbálunk minél jobban törekedni a valóság leírására.

### Probléma

Okés, tartsuk be a szabályokat. Egy `Chair`-nek ne legyen `numberOfTomatoes` tulajdonsága, de legyen `numberOfLegs`. Eddig könnyű. De mivan ha egy szervezetet, vagy szolgáltatást akarunk modellezni? A valóságban, ha szeretnénk vásárolni egy almát, nem kell tudnunk a pénztárgép használatát, se azt hogy hogyan kell egy kisboltnak adóznia. Sőt, az a legjobb ha ez így is van: Nem használhatjuk a pénztárgépet, és nem tudunk a kisbolt helyett adózni. Az általunk írt osztályok is legyenek ilyenek: Csak az legyen elérhető mások számára, ami rájuk vonatkozik.

### Megoldás

Javában az objektumok metódusainak lehet adni egy "láthatósági" módosítót. Ezek a következők:

Wait, ez a szöveg ismerős... Az osztályok tulajdonságaira vonatkozó módosítók használhatók metódusoknál és konstruktoroknál is. Meg osztályoknál is, de az még későbbi téma. *Spoilers!*

### Feladat

A kisboltok naponta kezelik az eladások számát, hogy a nap végén megtudjuk mennyi vevő volt aznap. Írd meg úgy az osztályt, hogy megtudjuk mennyi volt a mai napi bevétel, és hogy mennyi vevő volt aznap!

```java
/*#{2
10004600
5}#*/
class MrCsemege {
    private int maiVevők;
    private int bevétel;
    private void napKezdete() {
        bevétel = 0;
        maiVevők = 0;
    }
    public  MrCsemege() {
        napKezdete();
    }
    public void eladás(int ár) {
        bevétel += ár;
        /*={//Alkoholt nem adunk ki kiskorúnak!}=*/;
    }
    /*={getMaiVevők()}=*/@Dummy
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
        kisbolt.eladás(1500);//Próbavásárlás, de minden okés
        kisbolt.eladás(100);
        kisbolt.eladás(10000000); //Mondjuk valamiért árulnak autót is...
        System.out.println(kisbolt.getBevétel());
        System.out.println(kisbolt.getMaiVevők()); 
    }
}
```

## Inheritence

Okés okés, haladunk, már lemodelleztünk egy kis boltot, mennyire lenne nehéz a TESCO-t lemodellezni? Nagyon, szóval inkább modellezzünk kutyusokat!

### Probléma

Tegyük fel hogy van egy `Állat` osztályunk. Bámulatos osztály, minden le van benne írva ami minden állatra vonatkozik: `lábakSzáma`, `evés()`, stb. Nincs benne `leÜl()` metódus, hiszen egy giliszta nem tud leülni. Oké, de egy kutya le tud ülni ügyesen, szóval írjuk meg a saját osztályunkat ami leírja a kutyákat! Egy kutyának kell `lábakSzáma`, kell `evés()`, és... most mindent újra kéne írni, hiszen a `Kutya` egy `Állat`, szóval minden tulajdonsága és metódusa ami van egy `Állat`-nak, kell lennie a `Kutya`-nak is.

### Megoldás

Javában osztályok létrehozásakor az `extends` kulcsszó segítségével egy osztályt megadhatunk mint szülő osztály, így az éppen létrehozott osztály rendelkezni fog minden tulajdonságával és metódusával, de akár azok módosíthatók is, hogy specifikusabbak legyenek (pl egy `Kutya` esetében a `getLábakSzáma()` mindig 4-et ad vissza).

### Feladat

Írjunk meg egy Macska osztályt! Kutyára számítottál? Így jártál. A macskák állatok, viszont tudnak dorombolni is, amikor mindig azt a hangot adják ki, hogy `"Brrrrr~"`!

```java
/*#{4
Brrrrr~}#*/
class Állat {
    int lábakSzáma;
    //stb.
    public Állat(int lábakSzáma) {
        this.lábakSzáma = lábakSzáma;
    }
    public int getLábakSzáma() {
        return lábakSzáma;
    }
    public void hangKiadás() {

    }
}

class Macska extends /*${}$*/Állat {
    /*={//Fun fact: A dorombolás hanggal jár.
//Ha még nem láttál volna macskát.}=*/@Dummy
    public Macska() {
        super(/*${}$*/4); //A super() segítségével meg kell hívni a szülő osztály konstruktorját
    }
}

public class Hamuka {
    public static void main(String[] args) {
        Macska cicus = new Macska();
        System.out.println(cicus.getLábakSzáma());
        cicus.hangKiadás(); //Szereti ha a lábait számoljuk
    }
}
```

Minden alkalommmal, amikor létrehozunk egy adott osztályt, a konstruktorban az első dolognak kell lennie a szülő osztály létrehozása, hogy már egy kész szülő osztály objektumját változtassuk a gyerek osztály tagjává. Például jelen esetben elsőnek elő kell készíteni egy `Állat` objektumot, amiből készül a `Macska`.

Ez az előkészítés a `super` meghívásával történik, ami a szülőosztály egy konstruktorát jelöli. Ha a szülőosztálynak van olyan konstruktora, ami nem kér be paramétert, akkor a `super();` sor elhagyható, a fordító beilleszti magának. 

## Polymorphism

Oké, most komolyan, mi a franc ez a nagy hűhó az osztályok körül? Bezzeg az én koromban, a C-ben is voltak `struct`-ok, amik tudtak több adattagot összecsoportosítani. Szülő osztály? Ha `Macska` típust akarok csinálni, akkor lesz egy `Állat` típusú tulajdonsága, oszt kész! Mi a francnak ez a sok vackera?

### Probléma

Hmm. Mondjuk vissza gondolva volt egy dolog a C-ben ami nem volt igazán kényelmes. Amikor például van egy `elnevez(Állat)` függvényem, akkor oda csak állatot lehet odaadni. De a `Macska` egy `Állat`! Ez elég nagy hülyeség szerintem.

### Megoldás

Hogy mi? Javában ez gond nélkül müködik? Hogy erre valók az osztályok? Lehet van benne valami.

### Feladat

Érd el hogy ne legyen hibás a program!

```java
/*#{A felsorakozott járművek:
.lekkérek 3 űmráJ
.lekkérek 4 ótua újákrám irarreF
.lekkérek 4 tfiwS ikuzuS iscok isciK
Mondtam ne az üvegtáblák mögé üljünk!}#*/
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

public class AutoFelvonulas {
    static String leírásVisszafele(/*${KicsiKocsiSuzuki}$*/Autó leírandó) {
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
```

## Memory model

Oh jaj. Ez egy borzalmas cím. De sajnos eljött az idő, hogy erről beszéljünk. Ez egy hosszú rész lesz.

Történetünk ott kezdődik, hogy egy szomorú világban élünk, ahol az erőforrások nem végtelenek, és ennek hála a memória sem az. Mivel véges a memória, ezért a már nem használt memóriát vissza kell adni az operációs rendszernek, hogy újra kioszthassa más folyamatoknak, vagy akár ugyanannak a folyamatnak más célra. Szóval hogy történjen? Mikor adjuk vissza a memóriát?

Az elterjedt és helyes válasz, hogy amint véget ér a függvény, amiben lefoglalták. Egyszerű a folyamat: a függvényt meghívjuk, lefoglalja a memóriát ami kell neki, kiszámolja amit kell, visszaadja `return`-el, majd felszabadítja az általa lefoglalt memóriát. Egyszerű, nagyszerű.

Számok esetén. Vagy talán két szám esetén. De egy 100000 elemű tömböt ide-oda másolgatni nem vicces, és még kevésbé gyors. És amúgy is, "*Számítógép*" a neve, de nem csak számok összeadására használjuk már, hanem szerverek futtatására, kliensek kiszolgálására, képek kezelésére, videók lejátszására... vannak értékek amiket nem szabad csak úgy másolgatni, mert lehet többen is figyelnek az értékre.

Aztán, harmadik nap jön a C, és mondja: Használjunk mutatókat! Ne másoljuk át az egész értéket, hanem tegyük el egy fix helyre az adatokat ami később is kell, és a rá mutató számot másolgassuk csak, ami már nagyon gyors. A jelenlegi memória modellnek még mindig megvan a helye, hiszen leveszi a munkát a programozóról, hogy ügyeljen minden byte memóriára. De legyen egy új módon kezelt memória is, ami manuálisan lesz lefoglalva, és manuálisan felszabadítva.

És úgy is lett. Az eddigi módszerrel kezelt memóriát Stack-nek nevezte a C, az új módon kezeltet pedig Heap-nek. A C látta, hogy ez jó.

És így működött az élet sokáig: `malloc()` memóriát foglal a Heap-en, `free()` pedig felszabadítja azt. A C++ is átvette ezt a módszert, de az osztályok és a konstruktorok miatt átnevezte őket: `new` foglal memóriát a Heap-en, és `delete` szabadítja azt fel. De a programozók élete nem csak játék és mese. Hallottál már a gonoszról? A csúf, kopasz, Segmentation Faultról?

### Probléma

Onnantól kezdve, hogy a programozó kezébe adtuk a memória kezelését, ő el is cseszi azt. Mi van ha egy felszabadított memória helyére írunk? Mivan ha olyan memóriát akarunk felszabadítani, ami nincs is lefoglalva? Mivan, ha...

Segmentation Fault az operációs rendszertől, az van. Félre értés ne essék, ez nem egy üzenet. Nem egy kis üzenet az operációs rendszertől, hogy "Enyje benyje, ilyet nem szabad Pistike!". A Segmentation Fault az ***SÍRFELIRAT***.

Az operációs rendszer egy még Hitlernél is rosszabb, véres kezű diktátor, aki a legelső hibára gondolkodás nélkül meggyilkol mindenkit is. Rendet tart, és erőforrásokat oszt mindenkinek aki kéri, de ha ahhoz nyúlsz ami nem a tied, halott vagy. Hogy kerüljük el a hóhér csapását? Hogy ne vessünk el hibákat?

### Megoldás

Hatalommal felelősség is jár, és a Java szerint mi erre nem vagyunk felkészülve. Java a manuális memória kezelést vissza veszi a programozó kezéből, és újra ő kezdi el kezelni. Minden ami nem primitív típus, az a Heap-en lesz lefoglalva, és minden egy mutató lesz az adatra.

`Person juliska = new Person();` Ez a sor a C++ban eltérne egy karakterrel: `Person` helyett `Person*` lenne a típus, mivel a Heap-en van tárolva. És ez a trükk: *Minden is mutató*. Ijjesztő, mi?

De épp ez könnyíti meg a dolgunkat: Ha minden is mutató, akkor valójában, semmi sem az. Mármint, oké, az, de mi vehetjük úgy hogy nem azok. Nem kell cseszekedni "dereferencia"-val, `Person***` típusokkal, meg ilyen baromságokkal. A Java mindent is elvégez helyettünk!

### Okés, szünet

Itt egy kutyás kép:

```java 
/*
                            ..,,,,,,,,,.. 
                     .,;%%%%%%%%%%%%%%%%%%%%;,. 
                   %%%%%%%%%%%%%%%%%%%%////%%%%%%, .,;%%;, 
            .,;%/,%%%%%/////%%%%%%%%%%%%%%////%%%%,%%//%%%, 
        .,;%%%%/,%%%///%%%%%%%%%%%%%%%%%%%%%%%%%%%%,////%%%%;, 
     .,%%%%%%//,%%%%%%%%%%%%%%%%@@%a%%%%%%%%%%%%%%%%,%%/%%%%%%%;, 
   .,%//%%%%//,%%%%///////%%%%%%%@@@%%%%%%///////%%%%,%%//%%%%%%%%, 
 ,%%%%%///%%//,%%//%%%%%///%%%%%@@@%%%%%////%%%%%%%%%,/%%%%%%%%%%%%% 
.%%%%%%%%%////,%%%%%%%//%///%%%%@@@@%%%////%%/////%%%,/;%%%%%%%%/%%% 
%/%%%%%%%/////,%%%%///%%////%%%@@@@@%%%///%%/%%%%%//%,////%%%%//%%%' 
%//%%%%%//////,%/%a`  'a%///%%%@@@@@@%%////a`  'a%%%%,//%///%/%%%%% 
%///%%%%%%///,%%%%@@aa@@%//%%%@@@@S@@@%%///@@aa@@%%%%%,/%////%%%%% 
%%//%%%%%%%//,%%%%%///////%%%@S@@@@SS@@@%%/////%%%%%%%,%////%%%%%' 
%%//%%%%%%%//,%%%%/////%%@%@SS@@@@@@@S@@@@%%%%/////%%%,////%%%%%' 
`%/%%%%//%%//,%%%///%%%%@@@S@@@@@@@@@@@@@@@S%%%%////%%,///%%%%%' 
  %%%%//%%%%/,%%%%%%%%@@@@@@@@@@@@@@@@@@@@@SS@%%%%%%%%,//%%%%%' 
  `%%%//%%%%/,%%%%@%@@@@@@@@@@@@@@@@@@@@@@@@@S@@%%%%%,/////%%' 
   `%%%//%%%/,%%%@@@SS@@SSs@@@@@@@@@@@@@sSS@@@@@@%%%,//%%//%' 
    `%%%%%%/  %%S@@SS@@@@@Ss` .,,.    'sS@@@S@@@@%'  ///%/%' 
      `%%%/    %SS@@@@SSS@@S.         .S@@SSS@@@@'    //%%' 
               /`S@@@@@@SSSSSs,     ,sSSSSS@@@@@' 
             %%//`@@@@@@@@@@@@@Ss,sS@@@@@@@@@@@'/ 
           %%%%@@00`@@@@@@@@@@@@@'@@@@@@@@@@@'//%% 
       %%%%%%a%@@@@000aaaaaaaaa00a00aaaaaaa00%@%%%%% 
    %%%%%%a%%@@@@@@@@@@000000000000000000@@@%@@%%%@%%% 
 %%%%%%a%%@@@%@@@@@@@@@@@00000000000000@@@@@@@@@%@@%%@%% 
%%%aa%@@@@@@@@@@@@@@0000000000000000000000@@@@@@@@%@@@%%%% 
%%@@@@@@@@@@@@@@@00000000000000000000000000000@@@@@@@@@%%%%%
*/ public class Kutyus {
    /*#{Woof!}#*/
    public static void main(String[] args) {
        System.out.println("Woof!");
    }
}
```

### Probléma Dos, és Tres... és Cuatro

Rendben, de akkor hogy történik a változók másolása? A mutatót másoljuk, vagy az értékét, esetleg mutatót készítünk a mutatóra? 

Mi a helyzet a függvény átadással? Mutatókkal történik, vagy átmásoljuk az értékeket?

És a nagy kérdés: *Hogy nem fogyunk ki memóriából?*

### Megoldások

Oké, oké, lassan. Aprán.

Amikor létrehozunk egy változót, aminek primitív a típusa, a Java beteszi a Stack-re, ami függvény végén felszabadul, end of story. Ha nem egy primitív típust hozunk létre, hanem `"Stringet"` vagy `new` segítségével objektumot, akkor az értéke a Heap-re kerül, és a változó értéke egy mutató lesz, ami még mindig a Stack-re kerül, ami a függvény végén felszabadul.

Ha meg egy már meglévő változót másolunk le, mondjuk úgy hogy `String a = b;`, akkor az '`a`' változó értéke egy mutató lesz, a '`b`' által mutatott értékre. Mind a ketten ugyanarra mutatnak, és de egymáshoz nincs közük.

A függvény átadás se olyan gond, ha nyugodtan átgondoljuk a helyzetet, vegyük a következő kódot:

```java
/*#{1
0
0
0}#*/
public class Main {
    static void fuggveny(int[] param) {
        param[0] = 1;
        param = new int[] {1, 2, 3};
        param[0] = 0;
    }

    public static void main(String[] args) {
        int[] szamok = new int[] {0, 0, 0, 0};
        fuggveny(szamok);
        for(int i = 0; i < szamok.length; i++) {
            System.out.println(szamok[i]);
        }
    }
}
```

1. Létrehozunk egy 4 elemű tömböt, példának okáért a `0xCAFE` memóriacímen.
2. A `szamok` rámutat a tömbre, az értéke `0xCAFE` lesz.
3. Átadjuk a függvénynek a `szamok` változót. Ezt felfoghatjuk úgy, mintha egy láthatatlan `param = szamok;` lépést csinálnánk. A `param` értéke tehát `0xCAFE`.
4. `param` első száma 1 lesz. A `param` értéke `0xCAFE`, az ott található négy elemű tömb első eleme 1 lett.
5. Létrehozunk egy 3 elemű tömböt, példának okáért a `0xBEEF` memóriacímen.
6. A `param` rámutat a tömbre, az értéke `0xBEEF` lesz.
7. `param` első száma 0 lesz. A `param` értéke `0xBEEF`, az ott található három elemű tömb első eleme 0 lett.
8. Véget ér a függvény, `param` törlődik. De az általa mutatott `0xBEEF` helyen található tömb **nem**!
9. Kiírjuk a `szamok` értékeit. A `szamok` értéke `0xCAFE`, tehát az ott található négy elemű tömb értékeit írjuk ki, ami 1, 0, 0, és 0.

Szuper! Jól haladunk, már csak az az egy kérdés van hátra, hogy miért nem fogyunk ki memóriából, ha semmit nem szabadítunk fel ami a Heap-en van?

A Java megoldása a Garbage Collector (**GC**), ami általában azt csinálja, hogy semmit. De ha kezd megtelni a memória, elkiáltja magát, hogy "**STOP**", és mint piros lámpa-zöld lámpa játékban, minden megáll úgy, ahogy épp van. Ekkor a Garbage Collector körbe megy a Heap-en, mindenkit megérint aki ott van, kijelölve őt a halálra. Ekkor elmegy a Stack-en álló változókhoz, és megnézi, hogy ki hova mutat. Ha primitív érték, akkor sehova, és csak áll egy helyben, velük nem foglalkozik, de ha egy mutatóval van dolga, megnézi hogy hova mutat. Akire mutat, az túlélte, és nem fogja feltakarítani. Sőt, kedves, akire az épp túlélő Heap-en álló mutat az is túl éli, és így tovább amíg senkire nem mutat valaki. Aztán megy a következő Stack-en álló változóra, és így tovább. Amikor végig ért, azok a Heap-en tartozkodó értékek akikre senki sem mutatott, fel lesznek szabadítva, aztán mehet minden tovább, lehet újra mozogni.

Huh, oké, még egyszer akkor:

1. Ha túl sok a felhasznált memória, a GC megállít mindent.
2. Végig megy a Heap-en, megjelölve mindent.
3. Végig megy a Stack-en.
4. Ha a változó egy mutató, akire mutat arról leveszi a jelölést.
5. Ha mutat valakire az, akiről levette a jelölést, arról is leveszi a jelölést, és megismétli ezt a lépést.
6. Egyébként megy a következő Stack-en lévő változóra, és megismétli a 4. lépést.
7. Ha végig ért, minden memória ami még meg van jelölve, fel lesz szabadítva.

Ezt az algoritmust "*mark-and-sweep*"-nek nevezik. Jó, mert elkerüli a hibákat: Nem szabadítunk fel olyan memóriát ami nem a miénk, mert azt nem jelöltük meg az elején. Ugyanezért nem szabadítunk fel kétszer memóriát. Rossz, mert lassú, de ezért csak amikor szükséges futtatjuk le.

### És most egy cicás kép

```java
/*
       ,
       \`-._           __
        \\  `-..____,.'  `.
         :`.         /    \`.
         :  )       :      : \
          ;'        '   ;  |  :
          )..      .. .:.`.;  :
         /::...  .:::...   ` ;
         ; _ '    __        /:\
         `:o>   /\o_>      ;:. `.
        `-`.__ ;   __..--- /:.   \
        === \_/   ;=====_.':.     ;
         ,/'`--'...`--....        ;
              ;                    ;
            .'                      ;
          .'                        ;
        .'     ..     ,      .       ;
       :       ::..  /      ;::.     |
      /      `.;::.  |       ;:..    ;
     :         |:.   :       ;:.    ;
     :         ::     ;:..   |.    ;
      :       :;      :::....|     |
      /\     ,/ \      ;:::::;     ;
    .:. \:..|    :     ; '.--|     ;
   ::.  :''  `-.,,;     ;'   ;     ;
.-'. _.'\      / `;      \,__:      \
`---'    `----'   ;      /    \,.,,,/
                   `----`
*/ public class Cicus {
    /*#{Nyau~}#*/
    public static void main(String[] args) {
        System.out.println("Nyau~");
    }
}
```

## Static

Oké, vegyünk vissza egy kicsit a dolgokból. Vegyünk egy egyszerűbb problémát: Hogy érhetek el valamit bárhonnan?

### Probléma

C++ megoldása erre a globális változók, hiszen C-n alapul. Ez magában probléma, de most nem (*csak*) azért jöttünk hogy a C++-t szidjuk. Javában nem léteznek globális változók, mivel minden érték vagy változóhoz van kötve egy metódusban, vagy tulajdonsághoz egy objektumban. Hol tudjuk tárolni az értéket, amit mindenhonnan el akarunk érni?

* Változóban, egy metódusban: Amint véget ér a metódus, az értékünk vagy fel lesz szabadítva, vagy elérhető lesz a GC számára feltakarításra, de a nagyobb gond, hogy metóduson belül lévő változókat nincs módunk elérni.
* Tulajdonságban, egy objektumban: Ezt már eltudjuk érni, ha el tudjuk érni az objektumot. Tehát ha mindenhonnan el akarjuk érni az értéket, akkor mindenhonnan el kell tudni érni az objektumot. Ez csak nekem tűnik egy ördögi körnek?

### Megoldás

A `static` kulcsszó segítségével Javában tulajdonságokat és metódusokat nem az osztály egy-egy objektumjához kötjük, hanem magához az osztályhoz. Osztályokat pedig mindenhonnan el lehet érni, hogy példányosítsuk őket.

Oké, emésszük meg mit is jelent az, hogy "az osztályhoz kötjük"? Vegyük példának ezt a párhuzamot: a Balatont nem birtokolja egyetlen magyar ember se, hanem az egész Magyarország-é. Mindenki eléri és fürödhet benne, mert állami kincs (vagy mi a szösz).

Egy másik példa amivel meglehet érteni, ha úgy képzeljük el, mint ha minden osztályhoz kötnénk egyetlen objektumot, aminek ugyan az a neve mint az osztálynak, és azok a tulajdonságai illetve metódusai amiket `static`-al jelöltünk. Ha például a `Kacsa` osztálynak van egy `static String mindenKacsaKedvencÉtele` tulajdonsága, akkor azt úgy lehet elérni, hogy `Kacsa.mindenKacsaKedvencÉtele`;

A második példával fény derül arra is, hogy `static` metódusból az osztály más nem-`static` dolgait nem lehet elérni, hiszen ahhoz kell az osztály egy példánya, amink nincsen, ezért használunk `static`-ot.

Megjegyzés: A Kotlin programozási nyelvben, ami a Javán alapul, ez nem szimpla képzelgés: `static` kulcsszó helyett tényleg `companion object`-eket hozunk létre, ugyan azzal a névvel.

### Feladat

Hozz létre egy metódust, amit bárhonnan el lehet érni, és bárki elérheti (`public`)! Futtatásakor írja ki a "Everbody can reach it!" feliratot!

```java
/*#{Everbody can reach it!}#*/
public class Main {
    /*={public void global() {
    System.out.println(Main.theGround);
}}=*/@Dummy
    public static String theGround = "Everbody can reach it!";
}
```

Ezért van a `static` a `main` metódus előtt, hogy mindenhonnan elérhessük: Még a programon kívülről is, amikor elindítjuk.

## Final

A `static` segítségével bárhonnan el lehet érni, és lehet módosítani a tulajdonságok értékeit. Így bárki, bármikor módosíthatja azokat az értékeket ha nem ügyelünk rá. De mi van, ha nem ezt akarjuk, mivan ha pont az ellentétét szeretnénk: Senki ne módosíthassa az értékét valaminek?

### Probléma

Hogyan hozunk létre konstansokat Javában?

### Megoldás

A `final` kulcsszó segítségével egy változó, vagy tulajdonságot meg lehet jelölni, hogy ne lehessen változtatni értékadása után. Nem kötelező azonnal értéket adni neki, de mivel nem lehet változtatni az értékét, ezért az első értékadás lesz az utolsó is. Szóval konstansokat `final` segítségével hozunk létre, ugye?

Nem ez a helyzet. Konstansokat nem lehet létrehozni Javában. Wait, wait, hagy magyarázzam el. A konstans, definició alapján egy olyan érték, amit a program fordítása után már nem lehet módosítani. Itt nem ez a helyzet: `final` kulcsszót megkaphat egy változó, ami mondjuk a program indulásakor a pontos időt tárolja magában. Ezt nem lehet fordításkor még tudni: Mindig amikor elindítjuk, más lesz az értéke. *DE*, miután megkapta azt az értéket, nem fog változni már.

A gyönyörűség az egészben, hogy pontosan ugyan ez a helyzet a C-ben és a C++-ban is, csak ott más a kulcsszó: A `const`-al jelölt változók nem konstansak, csak értékük nem változhat megadásuk után. Jó nagy hülyeség, nem? De, ezért lett `final` a kulcsszó, nem pedig `const` a Javában.

### Feladat

Igazából nagyon nem lehet feladatot adni erre, de most nem lesz állatka, helyette lehet kísérletezni, és átgondolni hogy miért adnak bizonyos sorok hibát, míg mások nem?

```java
class Constitution {
    final int amendmentCount = 27;
    final String country;

    public Constitution(int amendmentCount, String country) {
        /*${/*}$*/;this.amendmentCount = amendmentCount;/*${* /}$*/;
        /*${/*}$*/;this.country = country;/*${* /}$*/;
        /*${/*}$*/;this.country = country;/*${* /}$*/;
    }

    public void renameTheCountry(String newName) {
        /*${/*}$*/;this.country = newName;/*${* /}$*/;
    }
}
/*#{}#*/
public class MahRights {
    public static void main(String[] args) {
        final int aC = 0;
        final String c;
        c = "'MERICA";
        /*Az "USA" változót úgy nevezzük jelen esetben,
        hogy "effectively final". Az mit jelent?*/
        Constitution USA = new Constitution(aC, c);
     }
}
```

## Final 2.0: Electric Boogaloo

A `final` kulcsszó viszont nem csak erre használható!

### Probléma

Jelenleg minden osztályból tudunk csinálni alosztályt. Ha van egy `Bútor` osztályunk, akkor tudunk az alapján készíteni egy `Asztal` alosztályt. De a Java osztályok közül ha megnézzük, nem mindnél lehetséges ez. Például, a `String` osztálynak nem lehet alosztályt csinálni, mert errort kapunk érte. Ez hasznos lehet, mert így sikerül elérni hogy tényleg pontosan úgy működjön az osztály tagjai a polimorfizmus esetén is. Képzeljük el, ha amikor várunk egy `String` paramétert egy metódusban, és miután meghívtuk egy metódusát, a `String` tartalma törlődik! Hogy lehet az ilyen eseteket úgy elkerülni, hogy nem engedjük hogy legyen egy osztálynak alosztálya?

### Megoldás

Suprise, suprise: a `final` kulcsszó. Osztályokat megjelölhetünk vele, és nem lehet származtatni belőle alosztályt. Lehet metódusokat is `final`-nak jelölni, ha szeretnénk lazább kikötést: Ilyenkor lehet alosztályt csinálni, de azok nem írhatják felül a `final`-nak jelölt metódusokat.

### Feladat

Hát, ez se egy nagyon feladatos lecke volt, hiszen megint az a célunk, hogy valami hibát adjon, ha nem megengedett módon használjuk. De tesztelgetni lehet:

```java
/*${final}$*/
final class Gyalogkakukk {
    double sebességKmPerHban;
    public Gyalogkakukk(double sebességKmPerHban) {
        this.sebességKmPerHban = sebességKmPerHban;
    }
    /*${final}$*/
    final double getSebességMPerSben() {
        return sebességKmPerHban / 3.6;
    }
}

class Kengyelfutó extends Gyalogkakukk {
    public Kengyelfutó() {
        super(120.0);
    }

    //Mit jelent itt a final kulcsszó?
    final double getSebességMPerSben() {
        return 33.3333;
    }
}

/*#{33.3333
33.333333333333336}#*/
public class PreriFarkas {
    public static void main(String[] args) {
        Kengyelfutó gyalogkakukk = new Kengyelfutó();
        //Tudtad hogy Vili a neve a prérifarkasnak?
        Gyalogkakukk viliEllensége = new Gyalogkakukk(120.0);
        System.out.println(gyalogkakukk.getSebességMPerSben());
        System.out.println(viliEllensége.getSebességMPerSben());
    }
}
```

## Abstract

Rendben, nem akarunk gyerekosztályt, akkor `final` kulcsszó. De mivan ha a családi támogatás a célunk, és mindenképpen akarunk gyereket?

### Probléma

Emlékezzünk egy kicsit vissza az `Állat`-os példánkra: Volt egy szülő osztályunk, aminek aztán csináltunk egy `Macska` alosztályát. De gondoljunk bele: Van "macska", van "kutya", van "csiga" és "kutya", de nincs olyan ami *csak* "állat". Az "állat" egy gyűtőszó, és nem pedig egy adott faj. Hogyan tudjuk elérni, hogy valami legyen szülőosztálya másoknak, de ne lehessen objektumokat létrehozni, csak az alosztályaiból?

### Megoldás

Újabb kulcsszó, valaki számolja? Az `abstract` fog nekünk segíteni. Úgy lehet használni, mint az előbb a `final`-t, osztályoknál és metódusoknál, de amíg a `final`-nak nincs mind a két helyen értelme egyszerre, addig itt épp hogy kézen-közön járnak az `abstract`-ok.

Egy `abstract` osztályt nem lehet példányosítani, illetve nem lehet `final`, amit belátni az olvasó feladata mert nem ismétlem magam. Az `abstract` osztályoknak lehet `abstract` metódusai is, ami pont a `final` metódusok ellentéte: az alosztályoknak kötelező felülírniuk, vagy szenvedjék az `abstract` átkát! Nem átok, de na.

### Feladat

Jó szórakozást, csinálj egy osztályt ami az `Állat` alosztálya, és van egy `getLábakSzáma()` ami 6-ot ad vissza!

```java
/*#{6}#*/
abstract class Állat {
    String becenév;
    public String getBecenév() {
        return becenév;
    }
    //Minek a testét deklarálni, ha úgyis felül lesz írva?
    public abstract int getLábakSzáma();
}

/*={}=*/
@Dummy
public class Zoo {
    public static void main(String[] args) {
        /*${MutánsKecske attila = new MutánsKecske();}$*/;
        System.out.println(allat.getLábakSzáma());
    }
}
```

## Interface

Elgondolkoztál már azon, hogy miért van az hogy miért kell minden gyerekosztálynak automatikusan félárvának lennie?

### Probléma

Oké, az állatos példa megint brutál jó lenne, de próbáljunk ki valami mást. Mondjuk a konyha ipart akarjuk lemodellezni valami weird okból. Van egy `Ételkészítő` osztályunk, ami nagyon menő, minden ami ebbe az osztályba tartozik, az tud adni egy listát arról, amilyen ételt tud csinálni. Persze semmisem csak egy "ételkészítő", így legyen az `abstract`! Okés, ez még gyenge a konyha világának leírására, legyen egy `Tűzhely` osztályunk, ami alosztálya az `Ételkészítő`-nek, és főzhetünk vele. Legyen teljes a list, legyen egy `Sütő`-nk is, ami mivel `Ételkészítő`, visszaadja a süthető ételek listáját. Na most, mi lenne egy `Gáztűzhely` szülőosztálya? `Tűzhely`, vagy `Sütő`? Jó lenne ha mind a kettő lehetne, de sajnos ez nem lehet: Az `Ételkészítő` miatt mind a `Tűzhely`-nek, mind a `Sütő`-nek van egy `getKészíthetőÉtelek()` metódusa. Ha mind a kettő szülő osztálya lenne, akkor mit adna vissza ez a metódus a `Gáztűzhely`-nél? A süthető, vagy a főzhető ételek listáját? Mind a kettőt? Egyiket se? 

Ezt a helyzetet drámaian **Deadly Diamond of Death**-nek nevezik, de a barátainak csak "Diamond Problem". Ez azért van, mert egy rombuszt alkot az osztályfa amikor ez a helyzet áll fent:

```java
/*#{  A
 / \
B   C
 \ /
  D}#*/
public class DeadlyDiamondOfDeath {
    public static void main(String[] args) {
        System.out.println("  A");
        System.out.println(" / \\");
        System.out.println("B   C");
        System.out.println(" \\ /");
        System.out.println("  D");
    }
}
```

Nem, nem tudok máshogy ilyen diagrammokat kiírni, deal with it. Ez a probléma azért áll fent, mert az osztályok meg tudják mondani, hogy egy metódus mit csinál, ami öröklődik az alosztályoknak. Habár az `abstract` osztályoknak van `abstract` metódusa, ami nem mondja meg hogy mit csinál, csak hogy "létezik", sajnos ezeknek az osztályoknak lehetnek nem-`abstract` metódusai is. Kéne egy olyan `abstract` osztály, aminek csak `abstract` metódusai lehetnek. Mégjobb lenne, ha nem lehetne neki tulajdonsága sem, hiszen mivan ha ugyan azzal a névvel van két ilyen osztályban tulajdonság, csak más típussal? Nem tudnánk kiválasztani, melyiket értjük amikor megnevezzük őket. 

### Megoldás

Szóval tulajdonságok nélküli, csak abstract metódusokkal rendelkező osztály? Hát, out of luck, erre nincs kulcsszó.

...Mert ez egy teljesen új koncepció, az interfész. Ugyanúgy kell deklarálni, mint a egy osztályt, csak `class` helyett `interface` amit használunk. Az interfészek `extends` kulcsszóval származhatnak más interfészekből, amíg nem ütköznek azok metódusai: Ha két metódusnak ugyanaz a neve, és a paramétereik típusai, akkor a visszatérésüknek is meg kell egyezni. Az interfészek metódusai mind alapból `abstract`-ak, és `public` láthatóságúak.

Az osztályok "megvalósíthatnak" (*fúj magyar*) interfészeket az `implements` kulcsszóval. Itt is, lehet több interfészt megvalósítani, ha azok nem ütköznek. Az interfészek nem zavarnak bele az osztályok hierarhiájába: ugyanúgy lehet egy szülőosztálya egy osztálynak függetlenül attól, hogy mennyi interfészt valósít meg.

### Feladat

Fejezzük be a `Kecske` osztályt, a valós világ adatainak megfelelően.

```java
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

class Kecske extends /*${PatásEmlős}$*/A implements /*${Emlős, Rovar}$*/B {
    /*={}=*/
    @Dummy
    public Kecske() {
        /*={//Érdekesség: Egy kecskének két patája van egy lábán.}=*/;
    }
}

/*#{4 végtagja van Mekinek.
És 2 patája van egy lábán.
Illetve 2 szarva.}#*/
public class MEEEEE {
    public static void main(String[] args) {
        Kecske meki = new Kecske();
        /*
        Az instanceof megnézi, hogy egy adott objektum egy osztály leszármazottja,
        vagy egy interfészt implementál-e
        */
        if (!(meki instanceof Kecske)) {
            System.out.println("Az Univerzum haldoklik.");
        }
        System.out.println(meki.getVégtagokSzáma() + " végtagja van Mekinek.");
        if (meki instanceof PatásEmlős) {
            System.out.println("És " + ((PatásEmlős)meki).getPatákSzáma() + " patája van egy lábán.");
        }
        if (meki instanceof SzarvasEmlős) {
            System.out.println("Illetve " + ((SzarvasEmlős)meki).getSzarvakSzáma() + " szarva.");
        }
        if (meki instanceof RepülőEmlős) {
            System.out.println("Na meg " + ((RepülőEmlős)meki).getSzárnyakSzáma() + " szárnya.");
        }
    }
}
```

## Classes everywhere

Most hogy megismertük az osztályok unokatestvéreit, ideje jobban megismerni az osztályokat is.

### Probléma Uno

Oké, megint kezdek spanyolul számolni, de ez most rövid lesz. Első felvetés: A `static`-nál felvetettük, hogy az osztályok mindenhol elérhetőek, hogy lehessen objektumokat létrehozni belőlük. Na, mennyire hazugság volt :D

Mi van, ha azt akarjuk, hogy csak egy metóduson belül legyen elérhető az osztály?

### Megoldás Uno

Ott definiáljuk az osztályt. Nem vicc, tényleg ennyi. Mondtam rövid lesz.

### Példa Uno

```java
/*#{Nem látja a 'Fa'-tól az 'Erdő'-t.}#*/
public class Fa {
    int ágakSzáma = 4;
    public static void main(String[] args) {
        class Erdő {
            //main metódusa bárminek lehet, egy normálisan használt osztálynak is
            Fa[] fák;
            public Erdő() {
                fák = new Fa[1];
                fák[0] = new Fa();
            }
        }
        System.out.println("Nem látja a 'Fa'-tól az 'Erdő'-t.");
    }
}
```

### Probléma Dos

Mi van, ha azt akarjuk hogy csak egy osztály láthasson egy osztályt?

### Megoldás Dos

Az osztályon belül definiáljuk az osztályt. Ez már mondjuk egy kicsit viccesebb sztori:

Javában az ilyen belső osztályok csak a külső osztály segítségével jöhetnek létre. Oké oké, megmutatom.

### Példa Dos

```java
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

/*#{4
5}#*/
public class Konyvtar {
    public static void main(String[] args) {
        Könyv abc = new Könyv("A\nB\n\nC\nD\n\nE\nF\n\nG\n...");
        //Szólj, hogy soha ne írjak könyvet, mert senki nem fogja elolvasni
        Könyv zyx = new Könyv("Z\n\nY\n\nX\n\nW\n\n...");
        System.out.println(abc.oldalakSzáma);
        System.out.println(zyx.oldalakSzáma);
    }
}
```

### Probléma Tres

Mivan ha nem akarjuk hozzá kötni a belső osztály példányait egy külső osztály példányához? Mivan, ha csak azt akarjuk, hogy ne egy példányhoz, hanem magához az osztályhoz legyenek kötve?

### Megoldás Tres

Nagyon remélem nem volt meglepetés a `static` kulcsszó.

### Példa Tres

```java
class Busz {
    static class Bérlet {
        //Nem final, mert a DKV folyton változtatja...
        static int ár = 5000;
        public final int diákigazolványSzám;
        public Bérlet(int diákigazolványSzám) {
            this.diákigazolványSzám = diákigazolványSzám;
        }
    }
    //Ezt soha nem tudjuk elérni a Bérlet-en belül
    int átlagosKésésÓrában;
}

/*#{5000
420}#*/
public class DKV {
    public static void main(String[] args) {
        System.out.println(Busz.Bérlet.ár);
        Busz.Bérlet snoopBérlete = new Busz.Bérlet(420);
        System.out.println(snoopBérlete.diákigazolványSzám);
    }
}
```

### Probléma Cuatro

Wait, nem úgy volt hogy a belső osztályok lényege az, hogy ne legyenek elérhetők kívülről?

### Megoldás Cuatro

Ha ezt szeretnéd, használd a belső osztály deklarálásánál a `private` kulcsszót. Ha azt akarod hogy mindenhonnan elérd, meg a `public`. Na vajon mi a kulcsszó, ha csak a külső osztály, és az alosztályai érhetik el a belső osztályt? Ez egyszerű dolog, nincs is példa (*kezdek kifogyni kreatívitásból*).

### Probléma Cuatro++

Mivan, ha azt akarjuk, hogy néhány osztály elérhesse az adott osztályt, míg mások nem?

### Megoldás Cuatro++

Ugye milyen cuki kis kérdés? Na a válasz már nem annyira. Hajrá csomagok!

A csomagok segítségével képesek vagyunk a különböző, egybe tartozó osztályokat egymáshoz tartozónak tekinteni. A legtöbb esetben csak egy csomagon belül fogunk dolgozni: Mivel egy mappában létrehozzuk a `.java` fájlokat, és kész vagyunk. A csomagok egyébként nem különböznek a mappáktól, mivel gondolhatunk rájuk úgy.

*Miközben írom ezt a részt, a rendőrség szírénájából ítélve, szerintem a most gyanúsan gyorsan leparkoló autót keresik. A könyv hátralevő részében remélem lesz még róla szó.*

Válasszunk egy mappát: Legyen az a csomagok alapja. Ha ide rakunk `.java` fájlokat, akkor azok az "" csomagban lesznek, amit meg se kell említeni.

Ha viszont abban létrehozunk egy másik mappát, mondjuk `org`, akkor abba lehet tenni `.java` fájlokat. Ezt a legelső sorban meg kell említeni a fájlban: `package org;` Így fogja tudni a package, hogy ő nem a csomagok alapjául szolgáló mappában csücsül. De ha mondjuk azt írnánk, hogy `package com;`, azt hibát adna vissza, mert nem `com` mappában csücsül, mi a franc?

És így tovább: `org`-on belül lehet egy `apache` mappa, amiben a fájlokhoz oda kell írni, hogy `package org.apache;`. Ha azon a mappán belül van egy `commons`, akkor az ott lévő fájlok `package org.apache.commons;` sorral kezdődnek. Jó, de mit kezdünk ezzel?

*Miért nem száll ki az autójából? Ha a könyv a továbbiakban siettetettnek érződik, az azért van mert féltem az életem.*

Azt kezdjük ezekkel, hogy van még egy láthatósági kulcsszó:

Nem viccelek, tényleg ez az: "". Ez az alapértelmezett láthatósága mindennek, aminek nem írunk oda mást a másik háromból. Mivel beszélgetések során nehéz kiejteni azt a szót hogy "", ezért inkább "`package-private`" néven szólítják, ami mintha utal valamire.

A `package-private` osztályok, metódusok, tulajdonságok mind `private`-ként kezelhetők, ha nem ugyanabban a csomagban, azaz nem pontosan ugyan abban a mappában vannak a fájlok. Ha viszont egy csomagban vannak, ugyan abban a mappában, akkor olyan, mintha `public` lenne, mindenkinek elérhető abban a csomagban.

A `package-private`-nál engedékenyebb a `protected`, ami már más csomagban lévőknek is engedi, ha azok az alosztályai neki. De ahhoz látni kell az osztályt, amit a `public` kulcsszó enged, így már más csomagból is elérhető egy külső osztály. Ezt jelenti a `public`, amit minden egyes alkalommal oda van írva minden példában és feladatban. **NEM ÖSSZEKEVERENDŐ** a belső osztályoknál használt láthatósági szabályokkal. Egy külső osztálynak a láthatósága csak `public`, vagy `package-private` lehet. Mi az értelme egy `private` külső osztálynak? Nem lehet példányosítani, és még a `static` részei sem érhetők el.

*Mit csináltál? Már a mentő is megy erre, remélem nem egy maffiózó parkol mellettem. Azt említettem, hogy a tilosban parkol?*

### Példa Cuatro++

Nincs példa, mert ez a "könyv" (*mondtam így fogom hívni*) nem képes csomagokat kezelni. Hídd el nekem, és teszteld le magadnak egy normális IDE-n belül.

## Anonymous Class

Nos egy eléggé érdekes koncepciót fogok most bemutatni: Névtelen osztályok... Csak engem kezd megőríteni? De komolyan, csak a Javában van ilyen dolog, mi a franc?

### Probléma

Mivan, ha minket egy metódus futtatásakor nem érdekel minket, csak egy függvény? Mármint, nem érdekel minket az objektum, sem a tulajdonságai, semmi, csak egy függvény. Az a függvény lehet bármi, mi csak lefuttatjuk valamikor, és megyünk a dolgunkra. Meglehet oldani ezt interfészek segítségével: Csinálunk egy interfészt, aminek van egy metódusa, aminek olyan paraméterei vannak mint amilyeneket mi akarunk neki adni, és az a visszatérési értéke, amit mi akarunk visszakapni. Aztán a mi metódusunk, ami erre kíváncsi, bekér egy paramétert, aminek a típusa ez az interfész.

*Oh mond hogy ez jégeső, és nem a kocsis lövöldöz.*

Ezzel az a gond, hogy minden egyes meghíváskor, minden egyes alkalommal amikor ezt a metódust meg akarjuk hívni, mindig csinálni kell egy új osztályt, ami implementálja ezt az interfészt, hiszen interfészeket nem lehet példányosítani... Ugye?

### Megoldás

Példányosítsuk az interfészt! Hiba. Hmm.

Gondoljunk vissza, miért nem lehet példányosítani interfészt? Azért, mert a metódusai valójában abstractok, azaz nincs megmondva hogy működjenek. De mivan, ha megmondanánk, most az egyszer, hogy hogy működjenek?

### Feladat

Írasd ki egymás után soronként hogy, "Maffiózó", "parkol", "mellettem"!

```java
//Ez amúgy alapból van Javában, de a példa kedvéért létrehozom
interface Runnable {
    void run();
}

/*#{Maffiózó
parkol
mellettem}#*/
public class HelpMe {
    public static void main(String[] args) {
        Runnable one = new Runnable() {
            /*Ez nem kötelező, de egy jó IDE ideteszi, mert hibát jelez, ha egy olyan metódust akarunk
            létrehozni, amilyen nevű és paraméterű metódus nincs a szülő osztályban/megvalósítandó
            interfészben.*/
            @Override
            public void run() {
                System.out.println("Maffiózó");
            }
        };
        Runnable two = new Runnable() {
            @Override
            public void run() {
                System.out.println("mellettem");
            }
        };
        Runnable buckleMyShoes = new Runnable() {
            //Nem? Senki? Lejárt már? Okés, bocsi.
            @Override
            public void run() {
                System.out.println("parkol");
            }
        };
        /*${two}$*/one.run();
        /*${buckleMyShoes}$*/one.run();
        /*${one}$*/one.run();
    }
}
```

Minden alkalommal, amikor kapcsos zárójelet használunk egy `new` utasításnál, olyankor egy névtelen osztályt hozunk létre, aminek a szülő osztálya, vagy a megvalósítandó interfésze a `new` után említett típus. Aztán azonnal ott helyben létrehozunk a `new` helyén egy új objektumot ebből a névtelen típusból, és minden megy tovább. Ki mit gondol, hogy működik ez `final` osztályoknál?

## Lambda

A Java egy objektum orientált programozási nyelv, de az utálói "boilerplate-driven"-nek is nevezik. Itt a programozásban a "boilerplate" azt a szöveget jelenti, amit le kell írni, de nem befolyásolja azt amit akarunk csinálni, csak le kell írni, mert le kell írni. A `public static void main(String[] args)` szerintem egy árulkodó dolog, hogy nem mondanak olyan nagy butaságot.

### Probléma

Ezek a névtelen osztályok elég menő dolgok, de hát, van körülöttük boilerplate **bőven**. Így senki nem használja őket, mert több idő leírni mint kezdeni vele valamit.

### Megoldás

Hát, le kell rövidíteni. És a Java készítői csodák csodájára, elérték! Ha egy névtelen osztályt akarunk létrehozni, ami egy interfészt valósít meg, és annak az interfésznek csak egy metódusa van, akkor lehet egy rövidebb syntax-ot használni. Egyébként meg a régi módszer, de ez magába foglalja a névtelen osztályok használatának a 90%-át, szóval ez egy jó alku.

### Feladat

Írd ki egymás után a "nyerőLottóSzámok" tömb elemeit növekvő sorba rendezve! Majd csökkenő sorrendbe!

```java
//Ismerős?
interface IntComparator {
    //Egyik nagyobb-e, mint a másik?
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
        int n = számok.length;
        int temp = 0;
        for(int i=0; i < n; i++){
            for(int j=1; j < (n-i); j++){
                if(összehasonlító.compare(számok[j-1], számok[j])){
                    //swap elements
                    temp = számok[j-1];
                    számok[j-1] = számok[j];
                    számok[j] = temp;
                }
            }
        }
    }

    public void csináldMinddel(IntConsumer ezt) {
        for(int elem: számok) {
            ezt.consume(elem);
        }
    }
}

/*#{2
6
14
75
80

80
75
14
6
2}#*/
public class HetiHetes {
    //Nem, erre még nincs megoldás...
    public static void main(String[] args) {
        Számok nyerőLottóSzámok = new Számok(new int[] { 80, 75, 14, 6, 2 });
        nyerőLottóSzámok.sorba((a, b) -> a > b);
        IntConsumer kiírató = x -> System.out.println(x);
        nyerőLottóSzámok.csináldMinddel(kiírató);
        System.out.println();
        nyerőLottóSzámok.sorba(/*${}$*/a);
        nyerőLottóSzámok.csináldMinddel(kiírató);
    }
}
```

Ahhoz képest, hogy ez lerövidíti azt amit nekünk kell írni, jó rohadt hosszú feladatot raktam ide, nem? Sajnos a lamdák csak ekkor mutatják meg igazi erejüket, de ilyenkor nagyon is hasznosak.

Okés, lehet kicsit elszaladtam ezzel az új syntax-al. A nyíl előtt vannak a paraméterek, aminek mint láttuk, nem kötelező adni típust, a Java kitalálja, ha tudja. Nyíl után pedig az az egy kifejezés, aminek az értékét visszaadja a lambda. Ha nem csak egy kifejezést akarunk használni, akkor használni kell kapcsos zárójeleket (`{}`), de ekkor muszály a `return` kulcsszót is használni, mint például: `() -> { System.out.println(5); return 5; }`

## Generics

Az előző példa nagyon fasza, de csak `int`-ekkel működik. Oh wait, de a nyerő számok csak 90-ig mehetnek! Akkor ideje mindent is átírni `byte`-ra...

### Probléma

Jelenleg ha azt akarjuk, hogy egy metódus más típusokkal is működjön, akkor azokat újra kell írni azokra a típusokra. Használhatjuk az `Object` típust, amiből minden öszetett típus származik, de akkor nem tudjuk őket szabályozni. Például, ha két ugyan olyan számot akarunk összeadni, akkor ha `Object`-ként kérjük be mind a kettőt, akkor semmi nem akadályoz meg minket abban, hogy az egyik egy `String` legyen, a másik pedig egy `Kecske`. Hogy lehetne ezt megoldani?

### Megoldás

Generikus programozás. Ahogy a változók felvehetnek értéknek számokat, szöveget, stb., addig a generikusok felvehetnek értéknek **típusokat**. Így meg lehet oldani, hogy két típus ugyan az legyen: mind a két helyen ugyan azt a generikust használjuk. A generikusokat könnyű felismerni, mert `<>` "zárójelek" között vannak. Ha Java rá tud jönni arra, hogy mi a generikus értéke, akkor nem kötelező kiírni.

### Feladat

Érd el, hogy a program lefusson!

```java
//Általában a generikusok nevei egyetlen betűből állnak, T a típus rövidítése
class ÖtHosszúTömb<T> {
    //Használható bárhol, ahol típus szükséges
    T[] tömb;
    public ÖtHosszúTömb() {
        //Generikus tömböt csinálni nem lehet, de most nincs jobb ötletem
        tömb = (T[])new Object[5];
    }

    public void setElem(int i, /*${int}$*/int elem) {
        if (i < 0 || i > 4) return;
        tömb[i] = elem;
    }

    public /*${int}$*/int getElem(int i) {
        return tömb[i];
    }
}

/*#{null null null 9 null 
null null null null null 
null null null null null 
null null null null null 
null null null null null}#*/
public class Generikus {
    /*Lehet metódusban is deklarálni generikust
      extends segítségével szűkíthetjük az elfogadott típusok értékeit olyanokra,
      amiknek egy adott típus leszármazottjai
    */
    public static <U extends Number> ÖtHosszúTömb<U> getÖtHosszúSzámTömb() {
        /*Bárhol ahol egy generikus osztály megvan említve,
        a <>-ban ki kell írni a generikusok értékeit.
        Jelenleg ki tudja számolni a Java, szóval nem szükséges,
        csak a zárójel*/
        return new ÖtHosszúTömb<>();
    }
    public static void main(String[] args) {
        //Generikusok értékei csak öszetett típusok lehetnek, int helyett Integer
        ÖtHosszúTömb<ÖtHosszúTömb<Integer>> mátrix = new ÖtHosszúTömb<>();
        //Nagyon ronda a generikus metódusok meghívása, ha nem tudja a Java kitalálni az értéküket
        mátrix.setElem(0, Generikus.<Integer>getÖtHosszúSzámTömb());
        //Nem kötelező a típusokat megadni, ha tudja mi az
        mátrix.setElem(1, getÖtHosszúSzámTömb());
        mátrix.setElem(2, getÖtHosszúSzámTömb());
        mátrix.setElem(3, getÖtHosszúSzámTömb());
        mátrix.setElem(4, getÖtHosszúSzámTömb());
        mátrix.getElem(0).setElem(3, 9);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(mátrix.getElem(i).getElem(j) + " ");
            }
            System.out.println();
        }
    }
}
```

Hát igen, az öszetett típusok alapértelmezett értéke még mindig `null`, még akkor is, ha `Integer`-ről van szó.

## Exceptions

Mi van, ha az előző feladatban azt írjuk, hogy `mátrix.getElem(6)`?

### Probléma

Nagyon ritka, de sajnos fel kell készülni arra a *kivételes* alkalmakra, amikor hiba fordul elő. Mit tegyünk ilyenkor? A C és a C++ az elején azt csinálta, hogy valamit kiválasztott rossz értéknek, és ezt adta vissza a függvény, hogyha hiba történt: például ha mutatót ad vissza a függvény, akkor egy `NULL` mutató jelzi a hibát. Javában erre a kivételek (**Exception**) szolgálnak, amiket különleges visszatérési értékek, amik másik úton kerülni vissza a hívóhoz, aki így tudja hogy hiba történt.

### Megoldás

Ha azt érzékeljük, hogy rosszul lett meghívva egy metódus, `throw` segítségével visszadobhatunk egy hibát a hívónak, hogy erre figyelmeztessük.

Ha tudjuk, hogy lehet hogy visszadob egy hibát egy kód részlet, `try` blokkokal körül lehet venni, ami után `catch` segítségével el lehet kapni azokat. Ha nem kapjuk el, akkor amikor a hívott metódus hibát ad, akkor a hívó metódus is tovább dobja azt, az őt meghívó metódusnak... Hogy mivan?

Képzeljük el ezt a kivételt, mint egy labda, a meghívott metódusok pedig a gyerekek akik játszanak. `getNevekAFájlban` nevű gyerek meghívta `getFájl` gyereket játszani, aki eléállt, aki meghívta `openFile` gyereket játszani, és így tovább. Egyszer az egyik metódus hibát érzékel, és eldob hátra egy labdát, aminek a színe a hibát jelzi (*labda színe == kivétel típusa*). Ez a labda repül a gyerekek, azaz a meghívott metódusok feje felett. Ha egy gyerek úgy hívott valakit játszani, hogy tudja hogy majd repülhet miatta felé egy adott színű labda (`try` blokkban hívta meg, `catch`-ben a kivétel típusa), akkor elkapja, és csinál vele valamit. Ha nem számít labdára, vagy nem arra a színű labdára számít, akkor nem nyújtja ki a kezét, és nem kapja el, és repül szépen tovább. A gond ott van, hogy a gyerekeket a `main` gyerek hívta játszani, aki felett ha elrepül a labda, akkor a tanárbácsit eltalálja, és vége a játéknak (*és a programnak*).

### Feladat

Találjuk ki, milyen színű a `main` által eldobott labda!

```java
/*
Csak Throwable típusokat lehet eldobni és elkapni,
viszont azokról fixen tudja a Java hogy dobhatják.
A RuntimeException viszont nem biztos hogy eldobják, így nem kötelező elkapni
*/
class Labda extends RuntimeException {

}

class PirosLabda extends Labda {

}

class KékLabda extends Labda {

}

class SárgaLabda extends Labda {

}

class ZöldLabda extends Labda {

}

/*#{true}#*/
public class Gyerekek {
    public static void geri() {
        throw new KékLabda();
    }
    public static void pisti() {
        try {
            geri();
        } catch (Labda e) {
            throw new SárgaLabda();
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
        Labda labda = new Labda();
        //Julcsi felől repülhet labda
        try {
            julcsi();
        } catch (Labda l) {
            //Ha a dobott dolog egy Labda, elkapja
            labda = l;
        }/* catch (KékLabda l) {
            Hiba, mert az előző már elkapta a KékLabdát
        }*/
        String válasz = "";
        if (labda instanceof PirosLabda) {
            válasz = "Piros";
        } else if (labda instanceof KékLabda) {
            válasz = "Kék";
        } else if (labda instanceof SárgaLabda) {
            válasz = "Sárga";
        } else if (labda instanceof ZöldLabda) {
            válasz = "Zöld";
        }
        System.out.println(válasz.equals(/*${"Labda"}$*/""));
    }
}
```

# Itt a vége, fuss el véle!

Sok dolgot nem tartalmazott a könyv, de a Java fő funckióin átmentünk, és ezzel már tudunk Javát írni bátran. Még a hétvégén csinálok egy ehhez hasonlót, de az sokkkal rövidebb lesz, és csak Java gyakorlati programok leírása lesz benne, megmagyarázva inkább hogy hogyan építsük fel a Java programunkat a jelenlegi tudásainknak köszönhetően. Bőven tartalmazni fog beépített Java típusokat is, szóval HashMap és hasonlók.