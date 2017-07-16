public class Block {

	//variable declarations

	Boolean valid;
	Boolean dirty;	//DIRTY BIT
	int tag;
	int size;
	String[] data;
	int address;

	//initializing vars

	public Block(int size) {
		valid = false;
		dirty = false;
		tag = -1;
		this.size = size;
		data = new String[size];
	}

	//will return tag no. when called in Cache class

	public int returnTag() {
		return tag;
	}

	//will return if Block is valid when called in Cache class

	public Boolean returnValid(){
		return valid;
	}

	//will return if Block is dirty when called in Cache class

	public Boolean returnDirty(){
		return dirty;
	}

	//takes byte blockoffset and byte access, returns string with necessary data
	public String load(int offset, int access) {
		//System.out.println(access);
		StringBuilder sb = new StringBuilder();
		for(int i = offset; i < offset + access; i++) {
			//System.out.println(data[i]);
			sb.append(data[i]);
		}
		String dataS = sb.toString();	//will be a binary num, length of 8,16,24,32..etc
		int dec = Integer.parseInt(dataS, 2);	//decimal number
		String hex = Integer.toString(dec, 16);	//hex answer we want
		//System.out.println(hex.length());
		//System.out.println(access*2);
		StringBuilder n = new StringBuilder();
		if(hex.length() != access*2) {
			int dif = access*2 - hex.length();
			for(int i = 0; i < dif; i++) {
				n.append("0");
			}
	} n.append(hex);
	return n.toString();
}
}
