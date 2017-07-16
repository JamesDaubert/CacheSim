public class Cache {

	Set[] sets;										//where Cache data will be held

	//cache initialization

	public Cache(int cacheSize, int asc, String type, int blockSize) {
		int numBlocks = (cacheSize*1024)/blockSize; 	//amount of blocks in cache --
		int numSets = numBlocks;
		numSets =	(numBlocks / asc);

		//initalize sets
		sets = new Set[numSets];
		for(int i = 0; i < numSets; i++) {
			sets[i] = new Set(asc, blockSize, i);
		}
	}

	//LOAD

	public void cacheLoad(int offset, int index, int access, int tag, Memory mem, int address, String hexAd) {
		for(Set set : sets) {
			if(set.index == index) {
				//System.out.println(set.index);
				set.load(set, tag, offset, access, mem, address, hexAd);
				break;
			}
		}
	}

	//STORE

	public void cacheWrite(String val, int offset, int index, int access, int tag, Memory mem, int address, String type, String hexAd) {
		for(Set set: sets) {
			if(set.index == index) {
			//	System.out.println("SET FOUND");
				//System.out.println(val);
				//System.out.println(access);
				set.write(set, val, offset, access, tag, mem, address, type, hexAd);
				break;
			}
		}
	}
}
