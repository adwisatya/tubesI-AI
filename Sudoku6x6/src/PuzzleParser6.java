import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class PuzzleParser6 {
	public char[][] Puzzle; //puzzle
	public PuzzleParser6 ()
	{
		Puzzle = new char[7][7];
	}
	public void FillPuzzleFromTxt () throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("input6.txt"));
	    try {
	    	
	        String inputtxt = br.readLine();		        
        	int row=1; //row
	        while (inputtxt != null) 
	        {
	        	if (inputtxt.length()>1)
		        {
		        	System.out.println(inputtxt);
	        		int i=0; //indeksinputtxt
		        	int column=1;
			        while (i<=inputtxt.length()-1)
			        {
		        		System.out.println("inputtxt.charAt("+i+") = "+inputtxt.charAt(i));
			        	if (!(inputtxt.charAt(i) ==' '))
			        	{
				        	Puzzle[row][column] = inputtxt.charAt(i);	        		
			        		column++;
			        	}
			        	i++;
			        }
			        row++;	        	
		        }
	            inputtxt = br.readLine();
	        }
	    } finally {
	        br.close();
	    }
	    
	    
	}
	public void PrintPuzzle()
	{
		for (int i=1;i<=6;i++)
		{
			for (int j=1;j<=6;j++)
			{
				System.out.print(Puzzle[i][j]+" ");
			}
			System.out.print("\n");
		}
	}
}
