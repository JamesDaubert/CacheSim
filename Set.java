import java.util.Queue;
import java.util.LinkedList;

public class Set {

	int index;
	int asc;
	int blockSize;
	Queue<Block> LRU;

	public Set(int asc, int blockSize, int index) {
		this.asc = asc;
		this.blockSize = blockSize;
		this.index =index;
		LRU = new LinkedList<Block>();
		for(int i = 0; i < asc; i++) {
			LRU.add(new Block(blockSize));
		}
	}

	public void load(Set set, int tag, int offset, int access, Memory mem, int address, String hexAd){
		boolean check = false;
		//System.out.println(tag);
		for(Block block : set.LRU) {
			//System.out.println(block.tag);
			if(tag == block.tag) {	//HIT
				//System.out.println("LOAD HIT");
				check = true;
				LRU.remove(block);	//puts this block as most recently used in this set's LRU QUEUE
				block.valid = true;
				LRU.add(block);
				String hitdata = block.load(offset, access);
				System.out.println(printLoad(hexAd, hitdata, "load ", " hit "));	//prints 'load 0x000000 hit 4bac'
				break;
			}
		}
		if(check == false) {		//MISS
			//System.out.println("LOAD MISS");
			// System.out.println("load missed data from mem address:");
			// System.out.println(address);
			String data = mem.readBlock(address, this.blockSize);	//returns binary string
			//System.out.println(data);
			Block memBlock = new Block(blockSize);
			memBlock.valid = true;
			for(int x = 0; x < this.blockSize; x++) {			//   adds byte length data into the proper part byte by byte of the block
				String sub = data.substring(x*8, (x+1)*8);
				memBlock.data[x] = sub;
			}

			memBlock.tag = tag;
			memBlock.address = address;
			//System.out.println(memBlock.tag);
			String missdata = memBlock.load(offset, access);
			//System.out.println(missdata);
			Block oldBlock = LRU.peek();
			int oldAddress = oldBlock.address;
			if(oldBlock.dirty == true) {
				//System.out.println("DIRTY EVICTION");
				Block ev = LRU.peek();
				String[] d = ev.data;
				mem.blockWrite(oldAddress, d, this.blockSize);
			}
			LRU.remove();
			LRU.add(memBlock);
			System.out.println(printLoad(hexAd, missdata, "load ", " miss"));	//prints 'load 0x000000 hit 4bac'
		}
	}

	public void write(Set set, String val, int offset, int access, int tag, Memory mem, int address, String type, String hexAd) {
		//System.out.println("WRITING");
		boolean check = false;
		String padded = fix(access, val);
		//System.out.println(padded);
		//System.out.println(tag);
		for(Block block : set.LRU) {
			//System.out.println(block.tag);
			if(tag == block.tag) {		//HIT
				//System.out.println("HIT");
				check = true;
				for(int i = offset; i < offset+access; i++){
					String sub = val.substring((i-offset), i-offset+1);
					block.data[i] = sub;
				}
				block.valid = true;				//assure block is valid
				if(type.equals("wt")) {		//write through updates memory
					mem.write(address, padded, access);
					block.dirty = false;	//memory has same value

				} else {			//write back dirty bit is updated
					block.dirty = true;
				}
				System.out.println(printLoad(hexAd, "", "store ", " hit"));
				LRU.remove(block);	//puts this block as most recently used in this set's LRU QUEUE
				LRU.add(block);
				break;
			}
		}
		//System.out.print(check);
		if(check == false) {	//MISS
			String memData = mem.readBlock(address, this.blockSize);
			//System.out.println(memData);
			if(type.equals("wb")) {	//ADD A NEW BLOCK TO CACHE, DO NOT WRITE TO MEMORY
				//System.out.println("WB BEING TESTED");
				Block newBlock = new Block(this.blockSize);
				for(int i = 0; i < this.blockSize; i++) {		//add old data from memory, some of which will be written over in next loop
					String sub = memData.substring(i*8, (i+1)*8);
					newBlock.data[i] = sub;
				}
				for(int x = offset; x < offset + access; x++) {		//now WRITE with new data
					String sub = val.substring((x-offset), (x-offset+1));	//weird math to get it to be [0,8]
					newBlock.data[x] = sub;
				}
				newBlock.dirty = true;
				newBlock.valid = true;
				newBlock.tag = tag;
				newBlock.address = address;
				Block oldBlock = LRU.peek();
				int oldAddress = oldBlock.address;

				//System.out.println(oldAddress);
				if(LRU.peek().dirty == true) {
					//System.out.println("DIRTY EVICTION");
					Block ev = LRU.peek();
					String[] d = ev.data;
					// for(int i = 0; i < d.length; i++) {
					// 	System.out.println(d[i]);
					// }
					// System.out.println("evicted block write to old address:");
					// System.out.println(oldAddress);
					mem.blockWrite(oldAddress, d, this.blockSize);	//write data to address
				}
				LRU.remove();
				LRU.add(newBlock);
			}
			if(type.equals("wt")) {	//WRITE TO MEMORY BUT DO NOT UPDATE A BLOCK
				mem.write(address, val, access);
			}
			System.out.println(printLoad(hexAd, "", "store ", " miss"));
		}
	}

	public String printLoad(String hexAd, String data, String type, String status) {
			StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(hexAd);
		sb.append(status);
		if(data.length() > 0) {
				sb.append(" ");
				sb.append(data);
		}

		return sb.toString();

	}


	//simple function to correct values to be correct length: 10100 -> 00010100 for access size 1
	public String fix(int access, String val) {
	//	System.out.println("FIXING");
		int length = access*8;
		//System.out.println(length);
		//System.out.println(val.length());
		StringBuilder sb = new StringBuilder();
		sb.append(val);
		if(length == val.length()) {
			return sb.toString();
		} else{
			int pad = length - sb.length();
			for(int i = 0; i < pad; i++) {
				sb.insert(0, "0"); 	//adds to front
			}
		}
		return sb.toString();
	}

	//simple add to Queue fn if it comes from a load where all the data has been updated already
	//TODO update this, whole Set must be cleaned then re-added
	public void add(Block block) {
		popLRU();		//creates space for new block

		LRU.add(block);
	}

	//pop the least recently used

	public void popLRU() {
		LRU.remove();
	}

}
