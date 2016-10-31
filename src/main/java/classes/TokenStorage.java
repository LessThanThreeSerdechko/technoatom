package classes;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Никита on 25.10.2016.
 */
public class TokenStorage {
    private static String login;
    private static Token token;

    private ConcurrentHashMap<Token, String> tokensReversed;
    private ConcurrentHashMap<String, Token> tokens;
    private ConcurrentHashMap<String, String> stringtokens;

    public Token getToken(String login) {
        return this.tokens.get(login);
    }

    public String getUser(Token token) {
        return tokensReversed.get(token);
    }
    public String getUser(String token) {
        return stringtokens.get(token);
    }
    public String getUserByStringToken (String token) { return stringtokens.get(token); }

    public TokenStorage() {
        tokens = new ConcurrentHashMap<>();
        tokensReversed = new ConcurrentHashMap<>();
        stringtokens = new ConcurrentHashMap<>();
    }

    public void addToken(String login, Token token) {
        tokens.put(login, token);
        tokensReversed.put(token, login);
        stringtokens.put(token.toString(), login);
    }

    public void removeToken(Token token) {
        String login = this.getUser(token);
        tokensReversed.remove(token);
        tokens.remove(login);
        stringtokens.remove(token.toString());
    }
    public void removeToken(String login) {
        Token token = this.getToken(login);
        tokensReversed.remove(token);
        tokens.remove(login);
        stringtokens.remove(login);
    }

    public boolean containsToken(String token) {
        if (this.stringtokens.containsKey(token))
            return true;
        else return false;
    }

    public ConcurrentHashMap<String, Token> getTokenMap() {
        return this.tokens;
    }

}
