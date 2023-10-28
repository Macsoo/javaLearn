class Constitution {
    final int amendmentCount = 27;
    final String country;
    public Constitution(int amendmentCount, String country) {
        
    }
    public renameTheCountry(String newName) {
        
    }
}
public class MahRights {
    public static void main(String[] args) {
        final int aC = 0;
        final String c;
        c = "'MERICA";
        /*Az "USA" változót úgy nevezzük jelen esetben,
        hogy "effectively final". Az mit jelent?*/ Constitution USA = new Constitution(aC, c);
    }
}