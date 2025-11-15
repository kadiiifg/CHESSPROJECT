import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChessGUI {
    private JFrame frame;
    private JButton[][] squares = new JButton[8][8];
    private Piece[][] board = new Piece[8][8];
    private int selectedRow = -1, selectedCol = -1;
    private boolean whiteTurn = true;

    enum PieceType { KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN }
    enum Color { WHITE, BLACK }

    class Piece {
        PieceType type;
        Color color;
        Piece(PieceType t, Color c){ type=t; color=c; }
        public String toString(){
            String s = switch(type){
                case KING -> "K";
                case QUEEN -> "Q";
                case ROOK -> "R";
                case BISHOP -> "B";
                case KNIGHT -> "N";
                case PAWN -> "P";
            };
            return color==Color.WHITE ? s : s.toLowerCase();
        }
    }

    public ChessGUI() {
        frame = new JFrame("Chess GUI");
        frame.setSize(600,600);
        frame.setLayout(new GridLayout(8,8));

        // Create buttons
        for(int r=0;r<8;r++){
            for(int c=0;c<8;c++){
                JButton btn = new JButton();
                btn.setFont(new Font("Arial", Font.BOLD, 24));
                btn.setMargin(new Insets(0,0,0,0));
                btn.setOpaque(true);
                btn.setBackground((r+c)%2==0 ? Color.WHITE : Color.GRAY);

                final int row = r;
                final int col = c;
                btn.addActionListener(e -> clickSquare(row,col));

                squares[r][c] = btn;
                frame.add(btn);
            }
        }

        initBoard();
        updateBoard();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void initBoard() {
        // Pawns
        for(int c=0;c<8;c++){
            board[1][c] = new Piece(PieceType.PAWN, Color.BLACK);
            board[6][c] = new Piece(PieceType.PAWN, Color.WHITE);
        }
        // Other pieces
        PieceType[] order = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};
        for(int c=0;c<8;c++){
            board[0][c] = new Piece(order[c], Color.BLACK);
            board[7][c] = new Piece(order[c], Color.WHITE);
        }
    }

    private void clickSquare(int row, int col) {
        Piece p = board[row][col];

        // First click: select piece
        if(selectedRow==-1){
            if(p!=null && ((whiteTurn && p.color==Color.WHITE) || (!whiteTurn && p.color==Color.BLACK))){
                selectedRow=row;
                selectedCol=col;
                squares[row][col].setBackground(Color.YELLOW);
            }
        } else { // Second click: attempt move
            Piece selected = board[selectedRow][selectedCol];
            if(isValidMove(selectedRow, selectedCol, row, col, selected)){
                board[row][col] = selected;
                board[selectedRow][selectedCol] = null;
                whiteTurn = !whiteTurn;
            }
            // Reset selection
            squares[selectedRow][selectedCol].setBackground((selectedRow+selectedCol)%2==0 ? Color.WHITE : Color.GRAY);
            selectedRow=-1; selectedCol=-1;
            updateBoard();
        }
    }

    private boolean isValidMove(int sr,int sc,int tr,int tc, Piece p){
        if(p==null) return false;
        Piece dest = board[tr][tc];
        if(dest!=null && dest.color==p.color) return false;

        int dr = tr-sr;
        int dc = tc-sc;

        switch(p.type){
            case PAWN:
                int dir = p.color==Color.WHITE?-1:1;
                if(dc==0 && dr==dir && dest==null) return true;
                if(dc==0 && dr==2*dir && ((p.color==Color.WHITE && sr==6) || (p.color==Color.BLACK && sr==1)) && board[sr+dir][sc]==null && dest==null) return true;
                if(Math.abs(dc)==1 && dr==dir && dest!=null) return true;
                return false;
            case ROOK:
                if(sr==tr){
                    for(int i=Math.min(sc,tc)+1;i<Math.max(sc,tc);i++)
                        if(board[sr][i]!=null) return false;
                    return true;
                }
                if(sc==tc){
                    for(int i=Math.min(sr,tr)+1;i<Math.max(sr,tr);i++)
                        if(board[i][sc]!=null) return false;
                    return true;
                }
                return false;
            case BISHOP:
                if(Math.abs(dr)==Math.abs(dc)){
                    int rStep = dr>0?1:-1;
                    int cStep = dc>0?1:-1;
                    int r=sr+rStep, c=sc+cStep;
                    while(r!=tr){
                        if(board[r][c]!=null) return false;
                        r+=rStep; c+=cStep;
                    }
                    return true;
                }
                return false;
            case QUEEN:
                return isValidMove(sr,sc,tr,tc,new Piece(PieceType.ROOK,p.color)) || isValidMove(sr,sc,tr,tc,new Piece(PieceType.BISHOP,p.color));
            case KING:
                return Math.abs(dr)<=1 && Math.abs(dc)<=1;
            case KNIGHT:
                return (Math.abs(dr)==2 && Math.abs(dc)==1) || (Math.abs(dr)==1 && Math.abs(dc)==2);
        }
        return false;
    }

    private void updateBoard() {
        for(int r=0;r<8;r++){
            for(int c=0;c<8;c++){
                squares[r][c].setText(board[r][c]==null?"":board[r][c].toString());
            }
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(ChessGUI::new);
    }
}
