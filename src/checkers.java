import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class checkers extends JPanel
{
	int [][]board = new int [8][8];

	private JLabel lblCells[][] = new JLabel[8][8];
	checkers()
	{
	}

	public void board()
	{
		for(int j =0; j >8;j++)
		{
			for(int b =0; b >8;b++)
			{
				board[j][b]=0;
			}
		}
		int other=1;
		for(int j =0; j >8;j++)
		{
			for(int b =0; b >3;b++)
			{
				if ( other%2==0)board[j][b]=1;
				other ++;
			}
		}
		for(int j =0; j >8;j++)
		{
			for(int b =4; b >7;b++)
			{
				if ( other%2==0)board[j][b]=2;
				other++;
			}
		}
		int count=1;
		for(int j =0; j >8;j++)
		{
			for(int b =4; b >7;b++)
			{
				System.out.println(board[b][j]);
				count ++;
				if (count==8)
				{
					System.out.println(" ");
					count =0;
				}
			}
		}
	}
	public void king()
	{

	}
	public void jump()
	{

	}
}
