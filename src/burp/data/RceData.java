package burp.data;

import burp.IHttpRequestResponse;

import java.net.URL;

public class RceData {
    private String url;
    private String payload;
    private IHttpRequestResponse requestResponse;

    public RceData(String url, String payload, IHttpRequestResponse requestResponse) {
        this.url = url;
        this.payload = payload;
        this.requestResponse = requestResponse;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public IHttpRequestResponse getRequestResponse() {
        return requestResponse;
    }

    public void setRequestResponse(IHttpRequestResponse requestResponse) {
        this.requestResponse = requestResponse;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
