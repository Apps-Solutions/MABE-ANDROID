package util;

/**
 * Created by raymundo.piedra on 28/01/15.
 */
public class Constants {
    public enum FRAGMENTS_OPTIONS {
        INSIDE_PDV ("inside_pdv"),
        OUTSIDE_PDV ("outside_pdv")
        ;

        private final String name;

        private FRAGMENTS_OPTIONS(String s) {
            name = s;
        }

        public boolean equalsName(String otherName){
            return (otherName == null)? false:name.equals(otherName);
        }

        public String toString(){
            return name;
        }

    }
}
