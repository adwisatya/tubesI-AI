import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class TXTtoCLP {
	//attributes
	public static Character puzzle[][];
	public static int size = 6;
	public static void start(String input) {
		// TODO Auto-generated method stub
		puzzle = new Character[size][size];
		readTXT(input);
		writeCLP("tccoba.clp");
	}
	//methods
	public static void readTXT(String filename){
		try{
			BufferedReader file = new BufferedReader(new FileReader(filename));
			String line;
			int i=0;
			do{
				line = file.readLine();
				if(line!=null){
					addToMatrix(line,i);
					i++;
				}
			}while(line!=null && i<size);
			file.close();
		}
		catch(FileNotFoundException e){
			System.out.println("not found");
		}
		catch(IOException e){
			System.out.println("IOException");
		}
	}
	
	public static void writeCLP(String filename){
		try{
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			writer.println("(defrule grid-values\n?f <- (phase grid-values)");
			writer.println("=>\n");
			writer.println("(retract ?f)\n");
			writer.println("(assert (phase expand-any))\n");
			writer.println("(assert (size 3))\n");
			
			int id=1;
			
			for(int i=0 ; i<size ; i++)
				id = writeGroup(writer,i,id);
			
			writer.println(")");
			writer.close();
		}
		catch(UnsupportedEncodingException e){
			System.out.println("unsupported Encoding");
		}
		catch(FileNotFoundException e){
			System.out.println("external file not found");
		}
	}
	
	public static void addToMatrix(String line, int row){
		for(int i=0 ; i<line.length() ; i++){
			System.out.println(i+": " + line);
			puzzle[row][i] = line.charAt(i);
		}
	}
	
	public static int writeGroup(PrintWriter writer, int group, int id){
		int col0 = 0;
		if((group+1) % 2 == 0){
			col0 = 3;
		}
		int row0=0;
		switch ((int)group/2){
		case 0 : row0 = 0; break; 
		case 1 : row0 = 2; break; 
		case 2 : row0 = 4; break; 
		}
		for(int row=row0 ; row<row0+2 ; row++){
			for(int col=col0 ; col<col0+3 ; col++){
				int diagonal = 3;
				if (row == col) diagonal = 1;
				else if(row + col == 5) diagonal = 2;
				String value;
				if (puzzle[row][col] != '*') value = Character.toString(puzzle[row][col]);
				else value = "any";
				writer.println("(assert (possible (row " + (row+1) + ") (column " + (col+1) + ") (value " + value + ") (group " + (group+1) + ") (id " + id + ") (diagonal " + diagonal + ")))");
				id++;
			}
		}
		writer.println();
		return id;
	}
}
