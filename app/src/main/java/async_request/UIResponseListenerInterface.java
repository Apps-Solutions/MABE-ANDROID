package async_request;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

import database.models.Session;
import database.models.User;

public interface UIResponseListenerInterface {
    public void prepareRequest(METHOD method, Map<String, String> params);
    public void decodeResponse(String stringResponse);
}
