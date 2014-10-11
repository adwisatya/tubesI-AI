import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CLPtoTXT {
	// attributes
	public static Character puzzle[][];
	public static int size = 9;
	// methods
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		initPuzzle();
		readCLP("grid.clp");
		writeTXT("grid.txt");
	}
	
	public static void initPuzzle(){
		puzzle = new Character[size][size];
		for(int i=0 ; i<size ; i++){
			for(int j=0 ; j<size ; j++){
				puzzle[i][j] = '*'; //kosong = *
			}
		}
	}
	
	public static void readCLP(String filename){
		try{
			BufferedReader file = new BufferedReader(new FileReader(filename));
			String line;
			do{
				line = file.readLine();
				if(line!=null) addToMatrix(line);
			}while(line!=null);
			file.close();
		}
		catch(FileNotFoundException e){
			System.out.println("not found");
		}
		catch(IOException e){
			System.out.println("IO Exception");
		}
	}
	
	public static void addToMatrix(String line){
		Pattern value = Pattern.compile("\\(value (.*?)\\)");
		Matcher mValue = value.matcher(line);
		String rValue = "any";
		while(mValue.find()) {
		    rValue = mValue.group(1);
		}
		if (!rValue.equalsIgnoreCase("any")){
			// memasukkan nilai row
			Pattern row = Pattern.compile("\\(row (.*?)\\)");
			Matcher mRow = row.matcher(line);
			String rRow = "0";
			while(mRow.find()) {
			    rRow = mRow.group(1);
			}
			int iRow = Integer.parseInt(rRow);
			iRow--;
			
			// memasukkan nilai kolom
			Pattern column = Pattern.compile("\\(column (.*?)\\)");
			Matcher mColumn = column.matcher(line);
			String rColumn = "0";
			while(mColumn.find()) {
			    rColumn = mColumn.group(1);
			}
			int iColumn = Integer.parseInt(rColumn);
			iColumn--;
			
			// masukkan ke matriks
			puzzle[iRow][iColumn] = rValue.charAt(0);
		}
	}
	
	public static void writeTXT(String filename){
		try{
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			for(int i=0 ; i<size ; i++){
				for(int j=0 ; j<size ; j++){
					writer.print(puzzle[i][j]);
				}
				writer.println("");
			}
			writer.close();
		}
		catch(UnsupportedEncodingException e){
			System.out.println("unsupported Encoding");
		}
		catch(FileNotFoundException e){
			System.out.println("external file not found");
		}
	}
}
