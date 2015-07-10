
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import java.awt.Dimension;

public class Chess extends  JFrame 
{

	// count turns.  Even means white's turn, odd means black's turn
	private int turncounter = 0;
	int kingx=0,kingy=0;
	boolean incheck=false;
	boolean checkmate=false;
	boolean catsle=false;
	boolean blackcheckers = false;
	boolean whitecheckers = false;
	int cdirection=0;
	int whites=0,blacks=0;
	char winner=' ';
	boolean bkingmove=false, wkingmove=false;
	private boolean orientation = true;  // true means black on top, white on bottom
	String lastmoved;
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JToolBar tlbMain = null;
	private JLabel lblCells[][] = new JLabel[8][8];
	private piece[][] board = new piece[8][8];
	private JButton btnNewGame = null;
	String color;
	private JButton getBtnCheckers = null;
	private JButton chesscheckers = null;
	private JButton checkerschess = null;
	private JButton cheat = null;
	private JLabel lblStatus = null;
	private int heldX, heldY, upX, upY = -1;
	boolean legalMove=true;
	int direction=0;
	// Map the full names of the pieces to their codenames (White Rook, White Queen, etc.)
	private JLabel lblCurrPlayer = null;
	// Stores the current player's move - we can easily match it against
	// the first character of the pieces array
	private piece blank = new piece(' ');

	// This is the default constructor

	class piece
	{
		char type;	 // p,r,b,h,k,q  ('h' is knight).  Uppercase is black, lowercase is white
		char color;  // 'w', 'b', or ' ' (for blank cell)
		String pieceName;
		ImageIcon imgIcon;
		boolean checkers;	// true if checkers piece
		// Each piece can have an array of possible legal moves
		// naturally this needs to be refreshed on every turn
		// 28 is the maximum number of legal moves for any piece (actually queen with 27)
		// The other dimensino is a row, column pair, + 0 for ordinary move, or 1 for "take"
		int[][] legalmoves = new int[28][5];
		public int legalmovecount = 0;
		// Initialize a piece

		public piece(char type)
		{
			this.type = type;
			this.color = ' ';
			checkers = false;
			switch (type)
			{
			case 'p':
				loadicon("white-pawn");
				break;
			case 'P':
				loadicon("black-pawn");
				break;
			case 'r':
				loadicon("white-rook");
				break;
			case 'R':
				loadicon("black-rook");
				break;
			case 'b':
				loadicon("white-bishop");
				break;
			case 'B':
				loadicon("black-bishop");
				break;
			case 'h':
				loadicon("white-knight");
				break;
			case 'H':
				loadicon("black-knight");
				break;
			case 'q':
				loadicon("white-queen");
				break;
			case 'Q':
				loadicon("black-queen");
				break;
			case 'k':
				loadicon("white-king");
				break;
			case 'K':
				loadicon("black-king");
				break;
			case 'C':
				checkers=true;
				loadicon("blackPiece");
				break;
			case 'c':
				checkers=true;
				loadicon("redPiece");
				break;
			case 'G':
				checkers=true;
				loadicon("blackKing");
				break;
			case 'g':
				checkers=true;
				loadicon("redKing");
				break;

			}
		}

		void loadicon(String name)
		{
			this.pieceName = name;
			// String wd = System.getProperty("user.dir");
			imgIcon = new ImageIcon("set/" + name + ".png");
			color = (this.type>='A' && type<='Z') ? 'b' : 'w';
		}

		private void addmove(int row, int col, int take)
		{
			legalmoves[legalmovecount][0] = row;
			legalmoves[legalmovecount][1] = col;
			legalmoves[legalmovecount][2] = take;
			legalmovecount++;
		}

		// returns false if this move is blocked
		private boolean checkmove(int row, int col)
		{
			int take = 0;
			// if not inbounds stop here
			if (!(inbounds(row,col))) return false;

			if (' '!=board[row][col].type)
			{
				// If the square we can move to already has a piece, is
				// it a piece we can take? (opposite color)
				if (this.color==board[row][col].color) return false;
				take = 1;
			}
			addmove(row, col, take);
			// if this isn't a take move, keep on looking
			return (0==take);
		}

		// return all the legal moves for the piece at x, y 
		public int[][] moves(int row, int col)
		{
			// start anew each time
			legalmovecount = 0;
			switch (type)
			{
			case 'R':
			case 'r':
				rook(row, col);
				break;
			case 'C':
			case 'c':
				checker(row, col);
				break;
			case 'G':
			case 'g':
				checkersKing(row, col);
				break;
			case 'P':
			case 'p':
				pawn(row, col);
				break;
			case 'B':
			case 'b':
				bishop(row, col);
				break;
			case 'Q':
			case 'q':
				queen(row, col);
				break;
			case 'K':
			case 'k':
				king(row, col);
				break;
			case 'H':
			case 'h':
				horsey(row, col);
				break;

			}
			return legalmoves;
		}
		public boolean inbounds(int row, int col)
		{
			return (row>=0 && row<=7&&col>= 0 && col<=7);
		}
		private void queen(int row, int col)
		{
			bishop(row,col);
			rook(row,col);
		}
		private void king(int row, int col)
		{
			checkmove(1+row,1+col);
			checkmove(1+row,-1+col);
			checkmove(-1+row,1+col);
			checkmove(-1+row,-1+col);
			checkmove(1+row,col);
			checkmove(-1+row,col);
			checkmove(row,1+col);
			checkmove(row,-1+col);
			if(	board[row][col].color=='w' && row==7 && col==4 ||board[row][col].color=='b' && row==0 && col==4 )castle(row,col);
		}
		private void horsey(int row, int col)
		{
			checkmove(2+row,1+col);
			checkmove(2+row,-1+col);
			checkmove(-2+row,1+col);
			checkmove(-2+row,-1+col);
			checkmove(1+row,2+col);
			checkmove(1+row,-2+col);
			checkmove(-1+row,2+col);
			checkmove(-1+row,-2+col);
		}
		private void bishop(int row, int col)
		{
			int x=row;
			int y=col;
			x=row;
			y=col;
			while(checkmove(--x,--y));
			x = row;
			y = col;
			while(checkmove(--x,++y));
			x = row;
			y = col;
			while(checkmove(++x,--y));
			x = row;
			y = col;
			while(checkmove(++x,++y));
		}
		// return the legal moves for a rook
		private void rook(int row, int col)
		{
			// horizontal moves to the right
			for (int i=col+1; i<8; i++)
			{
				if (!checkmove(row, i)) break;
			}
			// horizontal moves to the left
			for (int i=col-1; i>=0; i--)
			{
				if (!checkmove(row, i)) break;
			}
			// vertical moves up
			for (int i=row-1; i>=0; i--)
			{
				if (!checkmove(i, col)) break;
			}
			for (int i=row+1; i<8; i++)
			{
				if (!checkmove(i, col)) break;
			}
		}

		private void pawn(int row, int col)
		{
			// normal orientation is black moving down (positive increment)
			int direction = ('b'==color ? 1 : -1);
			if (!orientation) direction = direction * -1;
			boolean startrow = (direction==1 ? 1==row : 6==row);
			// if the board is flipped, we flip orientation

			// move forward one
			if (row+direction>=0 && row+direction<8 && board[row+direction][col].type==' ')
			{
				addmove(row+direction, col, 0);
			}

			// special case move by two from the start row
			if (startrow)
			{
				// legal move only if there if the space we're skipping is empty
				// and the destination square is empty
				if (board[row+direction][col].type==' ' && board[row+direction+direction][col].type==' ')
				{
					addmove(row+2*direction, col, 0);
				}
			}// check on taking pieces

			if (col<7 && board[row+direction][col+1].type!=' ' && this.color!=board[row+direction][col+1].color)
			{
				// take piece
				addmove(row+direction, col+1, 1);
			}

			if (col>0 && board[row+direction][col-1].type!=' ' && this.color!=board[row+direction][col-1].color)
			{
				addmove(row+direction, col-1, 1);
			}
		}
		private boolean check(int row, int col)
		{
			int take = 0;
			// if not inbounds stop here
			if (!(inbounds(row,col))) return false;

			if (' '!=board[row][col].type)
			{
				// If the square we can move to already has a piece, is
				// it a piece we can take? (opposite color)
				if (this.color==board[row][col].color) return false;
				take = 1;
			}
			// if this isn't a take move, keep on looking
			return (0==take);
		}
		private void castle(int row, int col)
		{
			if(check(row, col-2)&& check(row, col-1)&& check(row, col-3)&& board[row][col-4].type=='r'|| check(row, col-2)&& check(row, col-1)&& check(row, col-3)&& board[row][col-4].type=='R')
			{
				addmove(row,col-3,0);
				cdirection=-1;
				catsle=true;
			}
			else if(check(row, col+2)&& check(row, col+1)&& board[row][col+3].type=='r'||check(row, col+2)&& check(row,col+1)&&  board[row][col+3].type=='R')
			{
				addmove(row,col+2,0);
				cdirection=1;
				catsle=true;
			}
			
		}
		private void checkersKing( int row,int col)
		{
			checkerMove(row,col,1,1);
			checkerMove(row,col,-1,-1);
			checkerMove(row,col,1,-1);
			checkerMove(row,col,-1,1);
		}
		private void checker(int row, int col)
		{
			// normal orientation is black moving down (positive increment)
			int direction = ('b'==color ? 1 : -1);
			if (!orientation) direction = direction * -1;
			// if the board is flipped, we flip orientation
			checkerMove(row,col,direction,1);
			checkerMove(row,col,direction,-1);
		}

		// move class is used to record moves for undo.
		// Also used to disallow moves that would result in check
		private void checkerMove(int row, int col, int vdirection, int hdirection )
		{
			if (inbounds(row+vdirection,col+hdirection)&&board[row+vdirection][col+hdirection].type==' ')
			{
				addmove(row+vdirection, col+hdirection, 0);
			}
			//
			else if (inbounds(row+2*vdirection,col+2*hdirection)&&this.color!=board[row+vdirection][col+hdirection].color)
			{
				if(board[row+(2*vdirection)][col+(2*hdirection)].type==' ')
					jump(row+(2*vdirection),col+(2*hdirection),row+vdirection,col+hdirection);
			}
		}
		private void jump(int row, int col, int takerow, int takecol)
		{
			legalmoves[legalmovecount][0] = row;
			legalmoves[legalmovecount][1] = col;
			legalmoves[legalmovecount][2] = 1;
			legalmoves[legalmovecount][3] = takerow;
			legalmoves[legalmovecount][4] = takecol;
			legalmovecount++;

		}

		// paint the piece at coordinates x, y
		void paint(int row, int col)
		{
			// Draw white or black square in order to restore background
			// and erase any piece that may have been there.
			lblCells[row][col].setIcon(null);
			if(((row%2)^(col%2)) == 0) lblCells[row][col].setBackground(Color.WHITE);
			else lblCells[row][col].setBackground(Color.GRAY);
			// If there's a piece, draw it
			if (' '!=type) lblCells[row][col].setIcon(imgIcon);
		}
	}

	// check to see if this color is in check
	public boolean incheck(char color)
	{
		// First find where the other color's king is
		for( int i=0;i!=8;i++)
		{
			for (int j=0;j!=8;j++)
			{
				// Then find the king
				if (board[i][j].color==color && (board[i][j].type=='k' ||  board[i][j].type=='K'))
				{
					kingx=i;
					kingy=j;
					System.out.println("kingx: " + kingx+" kingy: " + kingy);
				}
			}
		}

		// Then see if any piece of the other color has a move on the king
		return checkcheck(color, kingx, kingy);
	}

	// returns true if the king is in check by any piece of the *opposite* color
	public boolean checkcheck(char color, int kingx, int kingy)
	{
		for( int i=0;i!=8;i++)
		{
			for (int j=0;j!=8;j++)
			{
				if (board[i][j].color!=color)
				{
					// While we're here, update all the legalmoves arrays.  We'll need this later
					board[i][j].moves(i,j);
					for(int k=0; k!=board[i][j].legalmovecount; k++)
					{
						if(board[i][j].legalmoves[k][0]==kingx && board[i][j].legalmoves[k][1]==kingy) return true;
					}
				}
			}
		}
		return false;
	}
	// Assume this is called right after incheck() and all the legalmoves arrays
	// have been updated.
	public boolean checkmate(char color)
	{
		boolean mate = true;
		for( int i=0;i!=8;i++)
		{
			for (int j=0;j!=8;j++)
			{
				if (board[i][j].color==color)
				{
					// Need to copy the legal moves for the current piece 
					// so they don't change out from under us
					int legalmovecount = board[i][j].legalmovecount;
					int[][] legalmoves = new int[28][3];
					for (int m=0; m!=legalmovecount; m++) legalmoves[m] = board[i][j].legalmoves[m].clone();
					for(int k=0; k!=legalmovecount; k++)
					{
						// we have to make the move and see what the result would be
						int tempx = legalmoves[k][0];
						int tempy = legalmoves[k][1];
						piece savepiece = board[tempx][tempy];
						// Make the move to test
						board[tempx][tempy] = board[i][j];
						board[i][j] = blank;
						if (i==kingx && j==kingy)
						{
							if (!checkcheck(color, tempx,tempy)) mate = false;
						}
						else if (!checkcheck(color, kingx,kingy)) mate = false;	// yes, this move gets us out of check
						// Undo the move
						board[i][j] = board[tempx][tempy];
						board[tempx][tempy] = savepiece;
						if (!mate) break;
					}
				}
			}
		}
		return mate;
	}
	//	if (i==kingx&&j==kingy ) mate = false;

	public Chess() 
	{
		super();
		initialize();
		//	buildBoard();
	}

	public static void main( String args[] ) 
	{
		new Chess().setVisible(true);
	}
	//This method initializes this
	//@return void
	private void initialize() 
	{
		this.setSize(671, 555);
		this.setContentPane(getJContentPane());
		this.setTitle("Basic Chess");
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		buildBoard();
		resetChessPieces();
		RepaintPieces();
	}
	//This method initializes jContentPanel
	//  @return javax.swing.JPanel
	private JPanel getJContentPane() 
	{
		if (jContentPane == null) 
		{
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), BorderLayout.CENTER);
			jContentPane.add(getTlbMain(), BorderLayout.NORTH);
		}
		return jContentPane;
	}
	//This method initializes jPanel       
	//@return javax.swing.JPanel   
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(8);
			gridLayout.setHgap(5);
			gridLayout.setVgap(5);
			gridLayout.setColumns(8);
			jPanel = new JPanel();
			jPanel.setLayout(gridLayout);
			//buildBoard();
		}
		return jPanel;
	}
	private void newChess()
	{
		turncounter=0;
		checkmate = false;
		resetChessPieces();
		RepaintPieces();
	}
	private void newCheckers()
	{
		turncounter=0;
		checkmate = false;
		// start out with a chess board for the half case
		resetChessPieces();
		resetCheckersPieces();
		RepaintPieces();
	}

	private void resetCheckersPieces()
	{
		if (blackcheckers)
		{
			// clear out chess pieces
			for (int i=0; i!=2; i++) for (int j=0; j!=8; j++) board[i][j] = blank;
			board[0][1] = new piece('C');
			board[0][3] = new piece('C');
			board[0][5] = new piece('C');
			board[0][7] = new piece('C');
			board[1][0] = new piece('C');
			board[1][2] = new piece('C');
			board[1][4] = new piece('C');
			board[1][6] = new piece('C');
			board[2][1] = new piece('C');
			board[2][3] = new piece('C');
			board[2][5] = new piece('C');
			board[2][7] = new piece('C');
		}

		if (whitecheckers)
		{
			// clear out chess pieces
			for (int i=6; i!=8; i++) for (int j=0; j!=8; j++) board[i][j] = blank;
			board[5][0] = new piece('c');
			board[5][2] = new piece('c');
			board[5][4] = new piece('c');
			board[5][6] = new piece('c');
			board[6][1] = new piece('c');
			board[6][3] = new piece('c');
			board[6][5] = new piece('c');
			board[6][7] = new piece('c');
			board[7][0] = new piece('c');
			board[7][2] = new piece('c');
			board[7][4] = new piece('c');
			board[7][6] = new piece('c');
		}
	}
	private void resetChessPieces()
	{
		// Initalize all non-piece squares
		for (int i=2; i!=6; i++)
		{
			for (int j=0; j!=8; j++)
			{
				board[i][j] = new piece(' ');
			}
		}
		// initialize piece squares
		board[0][0] = new piece('R');
		board[0][1] = new piece('H');
		board[0][2] = new piece('B');
		board[0][4] = new piece('K');
		board[0][3] = new piece('Q');
		board[0][5] = new piece('B');
		board[0][6] = new piece('H');
		board[0][7] = new piece('R');
		board[1][0] = new piece('P');
		board[1][1] = new piece('P');
		board[1][2] = new piece('P');
		board[1][3] = new piece('P');
		board[1][4] = new piece('P');
		board[1][5] = new piece('P');
		board[1][6] = new piece('P');
		board[1][7] = new piece('P');
		board[6][0] = new piece('p');
		board[6][1] = new piece('p');
		board[6][2] = new piece('p');
		board[6][3] = new piece('p');
		board[6][4] = new piece('p');
		board[6][5] = new piece('p');
		board[6][6] = new piece('p');
		board[6][7] = new piece('p');
		board[7][0] = new piece('r');
		board[7][1] = new piece('h');
		board[7][2] = new piece('b');
		board[7][4] = new piece('k');
		board[7][3] = new piece('q');
		board[7][5] = new piece('b');
		board[7][6] = new piece('h');
		board[7][7] = new piece('r');
	}

	private void RepaintPieces()
	{
		for(int x = 0; x < 8; x++)
		{
			for(int y = 0; y < 8; y++)
			{
				board[x][y].paint(x, y);
			}
		}
	}
	private void ClearHlight(int x, int y)
	{
		if(((x%2)^(y%2)) == 0)
		{
			lblCells[x][y].setBackground(Color.WHITE);
		}
		else
		{
			lblCells[x][y].setBackground(Color.GRAY);
		}
	}

	private void buildBoard()
	{

		jContentPane.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e)
			{
				//	System.out.println("Mouse pressed at " + e.getX() + ", " + e.getY());
			}
		});

		int rowColor = 0;
		int i = 0;

		for(int x = 0; x <= 7; x++)
		{
			rowColor++;
			for(int y = 0; y <= 7; y++)
			{
				lblCells[x][y] = new JLabel("", JLabel.CENTER);
				lblCells[x][y].setOpaque(true);
				if(((x%2)^(y%2)) == 0)
				{
					lblCells[x][y].setBackground(Color.WHITE);
				}
				else
				{
					lblCells[x][y].setBackground(Color.GRAY);
				}

				final int passX = x;
				final int passY = y;

				lblCells[x][y].addMouseListener(new java.awt.event.MouseAdapter() {
					public void mousePressed(java.awt.event.MouseEvent e)
					{
						char colortomove = board[passX][passY].color; 
						if ('w'==colortomove & 0==turncounter%2 || 'b'==colortomove && 1==turncounter%2)
						{
							heldX = passX;
							heldY = passY;
							upX = -1;
							upY = -1;

							piece p = board[heldX][heldY];
							if (p.type!=' ')
							{
								int legalmoves[][] = p.moves(heldX, heldY);
								for (int i=0; i!=p.legalmovecount; i++)
								{

									//	System.out.println(legalmoves[i][0] + " " + 
									//		legalmoves[i][1] + " " +
									//legalmoves[i][2]); 

									if (0==legalmoves[i][2])
									{
										// if it's just a move, highlight yellow
										lblCells[legalmoves[i][0]][legalmoves[i][1]].setBackground(Color.YELLOW);
									}
									// if we can take a piece, paint it red
									else lblCells[legalmoves[i][0]][legalmoves[i][1]].setBackground(Color.RED);
								}
							}
						}
					}

				});

				lblCells[x][y].addMouseListener(new java.awt.event.MouseAdapter() {
					public void mouseReleased(java.awt.event.MouseEvent e)
					{
						if (-1!=heldX && -1!=heldY)
						{
							//	System.out.println("Move from " + heldX + "," + heldY + " to " + passX + "," + passY);

							// first unpaint the highlighted legal moves
							piece p = board[heldX][heldY];
							int legalmoves[][] = p.moves(heldX, heldY);
							for (int i=0; i!=p.legalmovecount; i++)
							{
								// repaint the squares as is
								board[legalmoves[i][0]][legalmoves[i][1]].paint(legalmoves[i][0], legalmoves[i][1]);
							}

							// Then see if we have a legitimate target move
							if (upX != -1)
							{
								boolean move = false;
								int i;
								// Is this target a legal move for our piece?
								for (i=0; i!=p.legalmovecount; i++)
								{
									move = (upX == legalmoves[i][0]) && (upY == legalmoves[i][1]);
									if (move) break;
								}

								if (move)
								{
									piece takenpiece = board[upX][upY];
									board[upX][upY] = board[heldX][heldY];
									if(board[upX][upY].type=='p'&&upX==0)board[upX][upY]=new piece('q');
									if (board[upX][upY].type=='P' &&upX==7)board[upX][upY]=new piece('Q');
									// replace with a blank piece
									board[heldX][heldY] = new piece(' ');
									if(board[upX][upY].type=='k' && catsle==true && cdirection==1&& (!checkcheck('w',upX,upY))){// 7,4 k
									if(upX==7 && upY==6){
										board[upX][upY+1]= new piece('r');
										board[upX][upY+1].paint(upX,upY-1);
										board[upX][upY+1]= new piece(' ');
										board[upX][upY+1].paint(upX,upY+1);
										catsle=false;
									}
									}
									if(board[upX][upY].type=='K'&& catsle==true&& cdirection==1&&(!checkcheck('b',upX,upY))){
										if(upX==0 && upY==6){
										board[upX][upY+1]= new piece('R');
										board[upX][upY+1].paint(upX,upY-1);
										board[upX][upY+1]= new piece(' ');
										board[upX][upY+1].paint(upX,upY+1);
										catsle=false;
										}
									}
									if(board[upX][upY].type=='k' && catsle==true && cdirection==-1&&(!checkcheck('w',upX,upY))){
										if(upX==7 && upY==1){
										board[upX][upY+1]= new piece('r');
										board[upX][upY+1].paint(upX,upY+1);
										board[upX][upY-1]= new piece(' ');
										board[upX][upY-1].paint(upX,upY-1);
										catsle=false;
										}
									}
									if(board[upX][upY].type=='K'&& catsle==true&& cdirection==-1&&(!checkcheck('b',upX,upY))){
										if(upX==0 && upY==1){
										board[upX][upY+1]= new piece('R');
										board[upX][upY+1].paint(upX,upY+1);
										board[upX][upY-1]= new piece(' ');
										board[upX][upY-1].paint(upX,upY-1);
										catsle=false;
										}
									}

									if(board[upX][upY].checkers)
									{
										if(0==upX || 7==upX)
										{
											board[upX][upY]=new piece('w'==board[upX][upY].color ? 'g' : 'G');	
											board[upX][upY].paint(upX, upY);
										}
										if( legalmoves[i][2]==1)
										{
											// deletes piece you are jumping
											board[legalmoves[i][3]][legalmoves[i][4]]=new piece(' ');
											board[legalmoves[i][3]][legalmoves[i][4]].paint(legalmoves[i][3], legalmoves[i][4]);
										}
										checkerswinner();
										Component frame = null;
										if(winner=='w')  JOptionPane.showMessageDialog(frame, "red wins!");
										else if (winner=='b')  JOptionPane.showMessageDialog(frame, "black wins");
									}

									if (incheck(0==turncounter%2? 'w' : 'b'))
									{
										// Oops, gotta take it back
										board[heldX][heldY] = board[upX][upY];
										board[upX][upY] = takenpiece;
										board[heldX][heldY].paint(heldX, heldY);
										System.out.println("Can't move -- would put you in check");
										java.awt.Toolkit.getDefaultToolkit().beep();
									}
									else
									{
										board[upX][upY].paint(upX, upY);
										board[heldX][heldY].paint(heldX, heldY);

										// reset everything
										heldX = heldY = upX = upY = -1;
										turncounter++;
										// 
										if (incheck)
										{
											// repaint the king not in check
											board[kingx][kingy].paint(kingx,kingy);
										}
										incheck = incheck(0==turncounter%2? 'w' : 'b');
										if (incheck)
										{
											lblCells[kingx][kingy].setBackground(Color.MAGENTA);
											if (checkmate=checkmate(0==turncounter%2? 'w' : 'b'))
											{
												JOptionPane.showMessageDialog(null, "Checkmate!");
											}
										}
										//System.out.println(checkmate(0==turncounter%2? 'w' : 'b'));
									}
								}
							}

						}
					}
				});
				lblCells[x][y].addMouseListener(new java.awt.event.MouseAdapter() {
					public void mouseEntered(java.awt.event.MouseEvent e)
					{
						// Keep track of where we've been
						if (heldX!=-1)
						{
							upX = passX;
							upY = passY;
							//	System.out.println("Entered from " + heldX + "," + heldY + " to " + passX + "," + passY);
						}
					}	
				});

				lblCells[x][y].addMouseListener(new java.awt.event.MouseAdapter() {
					public void mouseExit(java.awt.event.MouseEvent e)
					{
						//System.out.println("Exited from " + heldX + "," + heldY + " to " + passX + "," + passY);
					}	
				});

				jPanel.add(lblCells[x][y]);
				rowColor++;
				i++;
			}
		}
	}
	public void checkerswinner()
	{ 
		for ( int i=0;i!=8;i++)
		{
			for ( int j=0;j!=8;j++)
			{
				if (board[i][j].color=='w')whites++;
				if (board[i][j].color=='b')blacks++;
			}
		}

		if (blacks==0) winner='w';
		else if (whites==0) winner='b';
		if(blacks!=0 && whites!=0) winner=' ';
		if (winner!=' ') checkmate = true;
		whites=0;
		blacks=0;
	}
	// This method initializes tlbMain         
	// @return javax.swing.JToolBar 
	private JToolBar getTlbMain() {
		if (tlbMain == null) {
			lblCurrPlayer = new JLabel();
			lblCurrPlayer.setText("");
			lblCurrPlayer.setOpaque(true);
			lblStatus = new JLabel();
			lblStatus.setText("");
			lblStatus.setPreferredSize(new Dimension(200, 16));
			lblStatus.setSize(new Dimension(200, 16));
			tlbMain = new JToolBar();
			tlbMain.setOrientation(JToolBar.HORIZONTAL);
			tlbMain.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			tlbMain.setFloatable(false);
			tlbMain.add(getBtnCheckers());
			tlbMain.add(new JToolBar.Separator());
			tlbMain.add(getBtnNewGame());
			tlbMain.add(new JToolBar.Separator());
			tlbMain.add(chesscheckers());
			tlbMain.add(new JToolBar.Separator());
			tlbMain.add(checkerschess());
			tlbMain.add(new JToolBar.Separator());
			tlbMain.add(cheat());
			//tlbMain.add(getundo());
			tlbMain.add(new JToolBar.Separator());
			tlbMain.add(lblCurrPlayer);
			tlbMain.add(new JToolBar.Separator());
			tlbMain.add(lblStatus);
		}
		return tlbMain;
	}
	//This method initializes btnNewGame       
	//  @return javax.swing.JButton 
	private JButton cheat()
	{
		if (cheat == null) 
		{
			cheat = new JButton();
			cheat.setText("");
			cheat.addMouseListener(new java.awt.event.MouseAdapter()
			{
				public void mouseReleased(java.awt.event.MouseEvent e)
				{		
					for( int i=0;i!=8;i++)
					{
						for (int j=0;j!=8;j++)
						{
							char c=(0==turncounter%2? 'p' : 'P');
							if(board[i][j].type== c)
							{
								char x= 0==turncounter%2? 'q' : 'Q';
								board[i][j]=new piece (x);
								board[i][j].paint(i,j);
							}

						}

					}
				}
			});
		}
		return cheat;
	} 
	private JButton getBtnNewGame() {
		if (btnNewGame == null) {
			btnNewGame = new JButton();
			btnNewGame.setText("Chess");
			btnNewGame.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseReleased(java.awt.event.MouseEvent e) {
					if (turncounter==0 ||
							JOptionPane.YES_OPTION == 
								JOptionPane.showConfirmDialog(null, "Are you sure you wish start a new chess game?"))
					{
						blackcheckers=whitecheckers=false;
						newChess();
					}
				}
			});
		}
		return btnNewGame;
	} 

	private JButton checkerschess() {
		if (checkerschess == null) {
			checkerschess = new JButton();
			checkerschess.setText("CheckersChess");
			checkerschess.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseReleased(java.awt.event.MouseEvent e) {

					if (turncounter==0 || checkmate == true ||
							JOptionPane.YES_OPTION == 
								JOptionPane.showConfirmDialog(null, "Are you sure you wish start a new chess game?"))
					{
						blackcheckers = false;
						whitecheckers = true;
						newCheckers();
					}
				}
			});
		}
		return checkerschess;
	} 

	private JButton chesscheckers() {
		if (chesscheckers == null) {
			chesscheckers = new JButton();
			chesscheckers.setText("ChessCheckers");
			chesscheckers.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseReleased(java.awt.event.MouseEvent e) {

					if (turncounter==0 || checkmate == true ||
							JOptionPane.YES_OPTION == 
								JOptionPane.showConfirmDialog(null, "Are you sure you wish start a new chess game?"))
					{
						blackcheckers = true;
						whitecheckers = false;
						newCheckers();
					}
				}
			});
		}
		return chesscheckers;
	} 


	private JButton getBtnCheckers() {
		if (getBtnCheckers == null) {
			getBtnCheckers = new JButton();
			getBtnCheckers.setText("Checkers");
			getBtnCheckers.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseReleased(java.awt.event.MouseEvent e) {
					if (turncounter==0 || checkmate == true ||
							JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "Are you sure you want to play checkers?"))
					{
						blackcheckers=whitecheckers=true;
						newCheckers();
					}
				}
			});
		}
		return getBtnCheckers;
	}
}

