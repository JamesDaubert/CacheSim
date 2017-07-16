public class Memory {
	int memorySize = 16777216;
	String[] data;

	public Memory() {
		data = new String[memorySize];
	}
	//returns a full block of data from the read
	public String readBlock(int address, int blockSize) {
		StringBuilder sb = new StringBuilder();
		int mod = address % blockSize;
		int blockStart = address - mod; 	//makes sure we are starting at correct block
		for(int i = blockStart; i < blockStart + blockSize; i ++) {		//memory hasn't been updated either
			if(data[i] != null){
				sb.append(data[i]);
				}
				else {
				sb.append("00000000");
			}
		}
		//System.out.println(sb.toString());
		return sb.toString();
	}

	//TODO update read function, think about access size
	public String read(int address, int access, int blockSize) {
		StringBuilder sb = new StringBuilder();
		int mod = address % blockSize;
		int blockStart = address - mod; 	//makes sure we are starting at correct block
		for(int i = blockStart; i < blockStart + blockSize; i ++) {		//memory hasn't been updated either
			if(data[i] == null){
				sb.append("00000000");
			} sb.append(data[i]);
		}
		return sb.toString();
	}
	// use for smaller writes to data where there is a write through hit or miss

	public void write(int address, String value, int access) {
		//System.out.println(value);
	//	System.out.println(address);
	//	System.out.println(access);
		for(int i = 0; i < access; i++) {
			String sub = value.substring(i*8, (i+1)*8);
			data[address + i] = sub;
		}
	}

	// used for evictions when whole block must be written to memory

	public void blockWrite(int address, String[] val, int blockSize) {
		int mod = address % blockSize;
		int blockStart = address - mod; 	//makes sure we are starting at correct block
		// System.out.println("BLOCK BEING WRITTEN IN MEMORY");
		// System.out.println(blockStart);
		for(int i = blockStart; i < blockSize + blockStart; i++) {
			data[i] = val[i-blockStart];
 		}
	}
}
