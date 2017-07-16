import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class cachesim {

	static String filename;
	static int cacheSize;							//cache size
	static int asc;									//cache associativity
	static String type;								//wb or wt
	static int blockSize;							//block size
	static int numBlocks; 	//amount of blocks in cache
	static int numSets;
	static Memory memory;
	static Scanner sc;
	static Cache cache;
	static int blockOffset;				//num. of block offset bits
	static int setIndex;					//num of setIndex bits
	static int tag;								//num of tag bits


	//simple log2 fn
	public static int log(int n) {
		return (int) ( Math.log(n) / Math.log(2));
	}
	//simple binary number -> decimal number for block Offset, set index
	public static int offset(String bin, int blockOffset, int setIndex) {
		if(setIndex == 0) {
			String bOffset = bin.substring(bin.length() - blockOffset);
			int converted = Integer.parseInt(bOffset, 2);
			return converted;
		} else {
			String sIndex = bin.substring(bin.length() - blockOffset - setIndex, bin.length() - blockOffset);
			int converted = Integer.parseInt(sIndex, 2);
			return converted;
		}
	}

	public static String buffer(String address) {
		int buffer = 24- address.length();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < buffer; i++) {
			sb.append("0");
		}
		sb.append(address);
		return sb.toString();
	}

	//executes actual program
	public static void newCmd(ArrayList<String> commands) {
		for(int i = 0; i < commands.size(); i++) {
			String cmd = commands.get(i);
			String[] temp = cmd.split(" ");				//splits cmd into cmd, address, access, val
			//String type = temp[0];						//'load' or 'store'
			String hexAd = temp[1];
			String address = hexAd.substring(2);      //'0x000000'
			int decAddress = Integer.parseInt(address, 16);
			address = Integer.toBinaryString(decAddress);
			address = buffer(address);
			int offsetByte = offset(address, blockOffset, 0);				//BLOCK BYTE OFFSET
			int index = offset(address, blockOffset, setIndex);
			String tagBits = address.substring(0, address.length() - blockOffset - setIndex);
			int tag = Integer.parseInt(tagBits, 2);
			int access = Integer.parseInt(temp[2]);
			String val;
			if(temp.length > 3) {		//if it is a STORE
			//	System.out.println("STORE BEGUN");
				val = temp[3];
			//	System.out.println(val);
				// int decimal = Integer.parseInt(val, 16);	//hex -> dec
				// String binData = Integer.toBinaryString(decimal); //dec --> bin string
				//System.out.println(binData);
				cache.cacheWrite(val, offsetByte, index, access, tag, memory, decAddress, type, hexAd);
			} else {
				//System.out.println("LOAD BEGUN");
				cache.cacheLoad(offsetByte, index, access, tag, memory, decAddress, hexAd);
			}

 		}
	}

	public static void main(String[] args) throws IOException {
		filename = args[0];
	//	System.out.println(filename);
		cacheSize = Integer.parseInt(args[1]);
		//System.out.println(cacheSize);
		asc = Integer.parseInt(args[2]);
		//System.out.println(asc);
		type = args[3];
	//	System.out.println(type);
		blockSize = Integer.parseInt(args[4]);
	//	System.out.println(blockSize);
		numBlocks = (cacheSize*1024)/blockSize;
		numSets = (numBlocks / asc);
		blockOffset = log(blockSize);
		setIndex = log(numSets);
		tag = 24 - blockOffset - setIndex;
		BufferedReader sc = new BufferedReader(new FileReader(filename));
		String line = null;
		ArrayList<String> commands = new ArrayList<String>();
		while((line = sc.readLine()) != null) {
		//	System.out.println(line);
			commands.add(line);
		}
		sc.close();
		memory = new Memory();
		cache = new Cache(cacheSize, asc, type, blockSize);
		//System.out.println("Begin tests:");
		newCmd(commands);
	}
}
