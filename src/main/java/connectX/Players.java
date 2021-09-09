package connectX;

public class Players
{
    private int id;
    String player1File;
    String player2File;
    int player1AllowedTime;
    int player2AllowedTime;
    int sleepTime;
    boolean player1Ready;
    boolean player2Ready;
    private static Players players;

    private Players() {
        this.player1AllowedTime = 1;
        this.player2AllowedTime = 1;
        this.sleepTime = 1;
        this.player1Ready = false;
        this.player2Ready = false;
    }

    public static Players getPlayers() {
        if (Players.players == null) {
            Players.players = new Players();
        }
        return Players.players;
    }

    static {
        Players.players = null;
    }
}
