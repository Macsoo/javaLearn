class Person {
    public int age;
    public Person(int age) {
        this.age = age;
    }
    public void setAge(int value) {
        if (value <= age)
    return;
        age = value;
    }
    public int getAge() {
        return age;
    }
}
public class JohnAndTheWizard {
    public static void main(String[] args) {
        //Megszületett!
        Person john = new Person(0);
        //Iskolába megy. Szegény.
        john.setAge(7);
        System.out.println(john.getAge());
        //Olyan hamar felnőnek!
        john.setAge(40);
        System.out.println(john.getAge());
        //Elment egy varázslóhoz, hogy újra átélje gyerekkorát.
        john.setAge(10);
        //...Megtalálta a varázslót?
        System.out.println(john.getAge());
    }
}