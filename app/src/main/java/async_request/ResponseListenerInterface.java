package async_request;

import org.json.JSONObject;

import java.util.Map;

public interface ResponseListenerInterface {
    public void responseServiceToManager(JSONObject jsonResponse);
}
