package util;

import java.util.Map;


/**
 * Created by raymundo.piedra on 29/01/15.
 */
public class TrackerManager {
    private static 	TrackerManager   						manager;

    private Map<String,String> current_pdv;

    private TrackerManager(){
    }
    public static synchronized TrackerManager sharedInstance(){
        if (manager == null)
            manager = new TrackerManager();
        return manager;
    }
    public void setCurrent_pdv(Map<String,String> current_pdv){ this.current_pdv = current_pdv;}
    public Map<String,String> getCurrent_pdv(){ return this.current_pdv;}
}
