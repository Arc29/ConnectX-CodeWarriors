package connectX;

import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Game implements Runnable{
    private Constants.State[][] board;
    private int rows,cols;
    private Constants.Player prevPlayer;
    private int preMoveX,preMoveY;
    private Constants.Player start;
    private final Arena controller;

    public Game(final Arena controller,Constants.Player start){
        this.board=new Constants.State[9][9];
        this.rows=9;
        this.cols=9;
        this.start=start;
        this.controller=controller;
        this.prevPlayer=Constants.Player.P2;
        this.preMoveX=this.preMoveY=-1;
    }
    private String filename(final Constants.Player player) {
        if (player == Constants.Player.P1) {
            return Players.getPlayers().player1File;
        }
        if (player == Constants.Player.P2) {
            return Players.getPlayers().player2File;
        }
        this.controller.log("Impossible. Admin ko contact kar lo plzzz :(");
        throw new RuntimeException("Impossible");
    }
    public static boolean inRange(final int x, final int y) {
        return x >= 0 && x < 9 && y >= 0 && y < 9;
    }
    private Pair<Boolean, String> checkValidMove(final Constants.Player player, final int xi, final int yi,final int xj,final int yj) {

        if (xj < 0 || xj >= this.rows || yj < 0 || yj >= this.cols ) {
            return new Pair<>(false, (xj + " " + yj + ". Out of bound move"));
        }
        int validBlock=5;
        if(xi!=-1 || yi!=-1)
        validBlock=Constants.projectFrom[xi][yi];

            if (player == Constants.Player.P1) {
                if(this.board[xj][yj]!= Constants.State.EMPTY)
                    return new Pair<>(false, "P1 trying to occupy already occupied cell");

                if(Constants.projectTo[xj][yj]!=validBlock)
                    return new Pair<>(false, "P1 trying to occupy invalid block");
                return new Pair<>(true, "P1 valid move");
            }
            else if (player == Constants.Player.P2) {
                if(this.board[xj][yj]!= Constants.State.EMPTY)
                    return new Pair<>(false, "P2 trying to occupy already occupied cell");

                if(Constants.projectTo[xj][yj]!=validBlock)
                    return new Pair<>(false, "P2 trying to occupy invalid block");
                return new Pair<>(true, "P2 valid move");

            }

        return new Pair<>(false, "Error 1:Invalid Move. Contact Admin");
    }
    private Pair<Boolean, String> checkTerminalState(final Constants.Player player,int x,int y){
        //Check if X connected in horizontal

            int cnt=1,a=y,b=y;
            for(int i=y+1;i<9;i++) {
                if ((player == Constants.Player.P1 && this.board[x][i] == Constants.State.P1) || (player == Constants.Player.P2 && this.board[x][i] == Constants.State.P2)) {

                        cnt++;
                        b++;

                }
                else
                    break;
            }
        for(int i=y-1;i>=0;i--) {
            if ((player == Constants.Player.P1 && this.board[x][i] == Constants.State.P1) || (player == Constants.Player.P2 && this.board[x][i] == Constants.State.P2)) {

                    cnt++;
                    a--;

            }
            else
                break;
        }
                if(cnt==Players.getPlayers().xValue)
                    return new Pair<>(true, player.name()+" wins with match from ("+x+","+a+") to ("+x+","+b+")!");


        //Check if X connected in vertical
        cnt=1;a=x;b=x;
        for(int i=x+1;i<9;i++) {
            if ((player == Constants.Player.P1 && this.board[i][y] == Constants.State.P1) || (player == Constants.Player.P2 && this.board[i][y] == Constants.State.P2)) {

                    cnt++;
                    b++;

            }
            else
                break;
        }
        for(int i=x-1;i>=0;i--) {
            if ((player == Constants.Player.P1 && this.board[i][y] == Constants.State.P1) || (player == Constants.Player.P2 && this.board[i][y] == Constants.State.P2)) {

                    cnt++;
                    a--;

            }
            else
                break;
        }
        if(cnt==Players.getPlayers().xValue)
            return new Pair<>(true, player.name()+" wins with match from ("+x+","+a+") to ("+x+","+b+")!");

        //Check X connected in increasing diagonal
        cnt=1;a=x;b=x;
        int c=y,d=y;
        for(int i=x+1,j=y+1;i<9&&j<9;i++,j++) {
            if ((player == Constants.Player.P1 && this.board[i][j] == Constants.State.P1) || (player == Constants.Player.P2 && this.board[i][j] == Constants.State.P2)) {

                    cnt++;
                    b++;
                    d++;

            }
            else
                break;
        }
        for(int i=x-1,j=y-1;i>=0&&j>=0;i--,j--) {
            if ((player == Constants.Player.P1 && this.board[i][j] == Constants.State.P1) || (player == Constants.Player.P2 && this.board[i][j] == Constants.State.P2)) {

                    cnt++;
                    a--;
                    c--;

            }
            else
                break;
        }
        if(cnt==Players.getPlayers().xValue)
            return new Pair<>(true, player.name()+" wins with match from ("+a+","+c+") to ("+b+","+d+")!");
        //Check X connected in decreasing diagonal
        cnt=1;a=x;b=x;c=y;d=y;
        for(int i=x-1,j=y+1;i>=0&&j<9;i--,j++) {
            if ((player == Constants.Player.P1 && this.board[i][j] == Constants.State.P1) || (player == Constants.Player.P2 && this.board[i][j] == Constants.State.P2)) {

                cnt++;
                b--;
                d++;

            }
            else
                break;
        }
        for(int i=x+1,j=y-1;i<9&&j>=0;i++,j--) {
            if ((player == Constants.Player.P1 && this.board[i][j] == Constants.State.P1) || (player == Constants.Player.P2 && this.board[i][j] == Constants.State.P2)) {

                cnt++;
                a++;
                c--;

            }
            else
                break;
        }
        if(cnt==Players.getPlayers().xValue)
            return new Pair<>(true, player.name()+" wins with match from ("+a+","+c+") to ("+b+","+d+")!");
        if(checkBoardFull())
            return new Pair<>(true, "Draw!");
        return new Pair<>(false, "Not terminal state");
    }
    private Pair<Integer,Integer> getSubXScores(int x){
        int p1_score=0,p2_score=0;
        //Horizontal
        int p1c,p2c;
        for(int i=0;i<9;i++){
            p1c=0;p2c=0;
            for(int j=0;j<9;j++){
                if(this.board[i][j]==Constants.State.P1 ) {
                    p1c++;
                    p2c=0;
                }
                else if(this.board[i][j]==Constants.State.P2 ) {
                    p2c++;
                    p1c=0;
                }
                else if(this.board[i][j]== Constants.State.EMPTY)
                {
                    p1c++;
                    p2c++;
                }
                if(p1c==x){
                    p1_score++;
                    p1c=0;
                }
                if(p2c==x){
                    p2_score++;
                    p2c=0;
                }
            }
        }
        //Vertical
        for(int i=0;i<9;i++){
            p1c=0;p2c=0;
            for(int j=0;j<9;j++){
                if(this.board[j][i]==Constants.State.P1 ) {
                    p1c++;
                    p2c=0;
                }
                else if(this.board[j][i]==Constants.State.P2 ) {
                    p2c++;
                    p1c=0;
                }
                else if(this.board[j][i]== Constants.State.EMPTY)
                {
                    p1c++;
                    p2c++;
                }
                if(p1c==x){
                    p1_score++;
                    p1c=0;
                }
                if(p2c==x){
                    p2_score++;
                    p2c=0;
                }
            }
        }
        //Increasing diagonal
        for(int i=0;i<=9-x;i++){
            p1c=0;p2c=0;
            for(int j=i,k=0;j<9&&k<9;j++,k++){
                if(this.board[j][k]==Constants.State.P1 ) {
                    p1c++;
                    p2c=0;
                }
                else if(this.board[j][k]==Constants.State.P2 ) {
                    p2c++;
                    p1c=0;
                }
                else if(this.board[j][k]== Constants.State.EMPTY)
                {
                    p1c++;
                    p2c++;
                }
                if(p1c==x){
                    p1_score++;
                    p1c=0;
                }
                if(p2c==x){
                    p2_score++;
                    p2c=0;
                }
            }
            if(i>0){
                p1c = 0;
                p2c = 0;
                for (int j = 0, k = i; j < 9 && k < 9; j++, k++) {
                    if (this.board[j][k] == Constants.State.P1) {
                        p1c++;
                        p2c = 0;
                    } else if (this.board[j][k] == Constants.State.P2) {
                        p2c++;
                        p1c = 0;
                    } else if (this.board[j][k] == Constants.State.EMPTY) {
                        p1c++;
                        p2c++;
                    }
                    if (p1c == x) {
                        p1_score++;
                        p1c = 0;
                    }
                    if (p2c == x) {
                        p2_score++;
                        p2c = 0;
                    }
                }
            }
        }
        //Decreasing diagonal
        for(int i=8;i>=x-1;i--){
            p1c=0;p2c=0;
            for(int j=0,k=i;j<9&&k>=0;j++,k--){
                if(this.board[j][k]==Constants.State.P1 ) {
                    p1c++;
                    p2c=0;
                }
                else if(this.board[j][k]==Constants.State.P2 ) {
                    p2c++;
                    p1c=0;
                }
                else if(this.board[j][k]== Constants.State.EMPTY)
                {
                    p1c++;
                    p2c++;
                }
                if(p1c==x){
                    p1_score++;
                    p1c=0;
                }
                if(p2c==x){
                    p2_score++;
                    p2c=0;
                }
            }
            if(i<8){
                p1c = 0;
                p2c = 0;
                for (int j = 8-i, k = 8; j < 9 && k >= 0; j++, k--) {
                    if (this.board[j][k] == Constants.State.P1) {
                        p1c++;
                        p2c = 0;
                    } else if (this.board[j][k] == Constants.State.P2) {
                        p2c++;
                        p1c = 0;
                    } else if (this.board[j][k] == Constants.State.EMPTY) {
                        p1c++;
                        p2c++;
                    }
                    if (p1c == x) {
                        p1_score++;
                        p1c = 0;
                    }
                    if (p2c == x) {
                        p2_score++;
                        p2c = 0;
                    }
                }
            }
        }
        return new Pair<>(p1_score, p2_score);
    }
    private boolean checkBoardFull(){
        boolean flag=true;
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                if(this.board[i][j]== Constants.State.EMPTY){
                    flag=false;
                    break;
                }
            }
        }
        return flag;
    }
    @Override
    public void run() {
        if (!Players.getPlayers().player1Ready || !Players.getPlayers().player2Ready) {
            this.controller.log("Teams not ready");
            return;
        }
        Constants.Player currentPlayer = this.start;
        this.prepareBoard();
        while (Main.running) {
            while (Main.paused) {}
            final File file = new File(this.filename(currentPlayer));
            String command = this.filename(currentPlayer);
            if (Constants.classFileFilter.accept(file)) {
                command = "java " + file.getName().substring(0, file.getName().indexOf(".class"));
            }
            if (Constants.pythonFileFilter.accept(file)) {
                command = "python " + file.getName();
                System.out.println(command);
            }
            try {
                final Runtime runtime = Runtime.getRuntime();
                final Process proc = runtime.exec(command, null, file.getParentFile());
                final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
                writer.write(Players.getPlayers().xValue+"\n");
                writer.write(this.rows+" "+this.cols+"\n");
                for (int i = 0; i < this.rows; ++i) {
                    for (int j = 0; j < this.cols; ++j) {
                        System.out.print(this.board[i][j].getValue() + " ");
                        writer.write(this.board[i][j].getValue() + " ");

                    }
                    System.out.println();
                    writer.write("\n");
                }
                writer.write(this.prevPlayer.getValue()+" "+this.preMoveX+" "+this.preMoveY+"\n");
                writer.flush();
                System.out.println(((long)Players.getPlayers().player1AllowedTime*10));
                System.out.println(((long)Players.getPlayers().player2AllowedTime*10));
                if (proc.waitFor((currentPlayer == Constants.Player.P1) ? ((long)Players.getPlayers().player1AllowedTime*10) : ((long)Players.getPlayers().player2AllowedTime*10), TimeUnit.SECONDS)) {
                    System.out.println(proc.exitValue());
                    final Scanner output = new Scanner(proc.getInputStream());
                    final Scanner erroutput = new Scanner(proc.getErrorStream());
                    while(erroutput.hasNext())
                    System.out.println(erroutput.nextLine());
                    int xi = -10;
                    int yi = -10;
                    try {
                        if (proc.getInputStream().available() != 0) {
                            xi = output.nextInt();
                            yi = output.nextInt();

                        }
                    }
                    catch (InputMismatchException ex2) {
                        if (currentPlayer == Constants.Player.P1) {
                            this.controller.log("Illegal Move by P1. (" + xi + "," + yi + "). Maybe wrong output format");
                            this.controller.log("P2 won the game !!!"); //Win
                        }
                        if (currentPlayer == Constants.Player.P2) {
                            this.controller.log("Illegal Move by P2, (" + xi + "," + yi + "). Maybe wrong output format");
                            this.controller.log("P1 won the game !!!"); //Win
                        }
                        break;
                    }
                    final Pair<Boolean, String> response = this.checkValidMove(currentPlayer, this.preMoveX, this.preMoveY, xi, yi);
                    if (!(boolean)response.getKey()) {
                        this.controller.log((String)response.getValue());
                        if (currentPlayer == Constants.Player.P1) {
                            if (xi == -10 && yi == -10 ) {
                                this.controller.log("Illegal Move by P1.");
                            }
                            else {
                                this.controller.log("Illegal Move by P1. (" + xi + "," + yi + ")");
                            }
                            this.controller.log("P2 won the game !!!"); //Win
                        }
                        if (currentPlayer == Constants.Player.P2) {
                            if (xi == -10 && yi == -10) {
                                this.controller.log("Illegal Move by P2.");
                            }
                            else {
                                this.controller.log("Illegal Move by P2. (" + xi + "," + yi + ")");
                            }
                            this.controller.log("P1 won the game !!!"); //Win
                        }
                    }
                    else {
                        this.board[xi][yi] = (currentPlayer== Constants.Player.P1)? Constants.State.P1: Constants.State.P2;
                            this.controller.updateBoard(xi, yi, this.board[xi][yi]);
                            this.controller.log(currentPlayer.name()+" at ("+xi+","+yi+")");


                        final Pair<Boolean,String> p=this.checkTerminalState(currentPlayer,xi,yi);
                        if (currentPlayer == Constants.Player.P1) {
                            currentPlayer = Constants.Player.P2;
                        }
                        else if (currentPlayer == Constants.Player.P2) {
                            currentPlayer = Constants.Player.P1;
                        }

                        if (!p.getKey()) {
                            Thread.sleep(Players.getPlayers().sleepTime * 1000L);
                            this.preMoveX=xi;
                            this.preMoveY=yi;
                            this.prevPlayer=currentPlayer;
                            continue;
                        }
                        else{
                            this.controller.log(p.getValue());  //Win
                            break;
                        }
                    }
                }
                else {
                    if (currentPlayer == Constants.Player.P1) {
                        this.controller.log("Time Limit Exceeded for P1. P2 won the game !!!"); //Win
                    }
                    if (currentPlayer == Constants.Player.P2) {
                        this.controller.log("Time Limit Exceeded for P2. P1 won the game !!!"); //Win
                    }
                }
            }
            catch (IOException | InterruptedException ex4) {
                ex4.printStackTrace();
                continue;
            }
            break;
        }
    }
    public void prepareBoard(){
        for(int i=0;i<this.rows;i++){
            for(int j=0;j<this.cols;j++)
                this.board[i][j]= Constants.State.EMPTY;
        }
    }
}
