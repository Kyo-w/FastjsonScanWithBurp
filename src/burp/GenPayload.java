package burp;

public class GenPayload {
    private final static GenPayload instance = new GenPayload();
    private GenPayload(){}
    private IBurpCollaboratorClientContext context;

    public GenPayload getInstace(IBurpCollaboratorClientContext burpCollaboratorClientContext){
        if(context == null){
            this.context = burpCollaboratorClientContext;
        }
        return instance;
    }
}
