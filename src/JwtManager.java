import java.util.Date;

public class JwtManager {
    private String jwt;
    private String refreshToken;
    private Date refreshTokenExpiresAt;
    private String userId;
    private String username;
    private Date jwtExpiresAt;

    public void logout(){
        jwt = "";
        refreshToken = "";
        refreshTokenExpiresAt = null;
        userId = "";
        username = "";
        jwtExpiresAt = null;
    }
}
