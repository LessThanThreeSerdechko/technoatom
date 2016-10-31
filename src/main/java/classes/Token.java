package classes;

/**
 * Created by Никита on 25.10.2016.
 */
public class Token {
    private static Long token;

    public void setToken(Long token) {
        this.token = token;
    }

    public long getToken() {
        return token;
    }

    public Token(Long token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return this.token.toString();
    }
    @Override
    public int hashCode(){
        return this.token.hashCode();
    }
    @Override
    public boolean equals(Object o){
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Token))return false;
        Token token = (Token) o;
        return this.token.equals(token.getToken());
    }
}
