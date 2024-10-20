package setup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthModel {
    private String client_id;
    private String client_secret;
    private String refresh_token;
    private String grant_type;

    public OAuthModel(String client_id, String client_secret, String refresh_token, String grant_type){
        this.client_id=client_id;
        this.client_secret=client_secret;
        this.refresh_token=refresh_token;
        this.grant_type=grant_type;
    }
    public OAuthModel(){

    }
}
