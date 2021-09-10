package structures.cachememory;
import java.util.Hashtable;
import structures.memory.Memory;

public class CacheSplitWayLRU implements CacheControll{
    public class Model{
        boolean valid;
        boolean isDirty;
        byte[] bytes;
        int tag;
        public Model(int sizeBlock){
            this.tag = 0;
            this.isDirty = false;
            this.valid = false;
            this.bytes = new byte[sizeBlock];
        }
    }

    public class Set{
        Model[] model;
        int LRUindex = 1;
        int [] LRU;
        public Set(int ways, int sizeBlock){
            LRU = new int[ways];
            model = new Model[ways];
            for(int i = 0; i < ways; i++){
                LRU[i] = 0;
                model[i] = new Model(sizeBlock);
            }
        }

        public void update(int index){
            LRU[index] = LRUindex;
            LRUindex++;
        }
    }
    
    private Hashtable<Integer, Set> cacheMemoryI = new Hashtable<>();
    private Hashtable<Integer, Set> cacheMemoryD = new Hashtable<>();
    private Memory memory;
    private CacheControll newLevel;
    private int sizeBlock;
    private int bitsIndex;
    private int bitsOffset;
    private int ways;
    private int positionSetMask;
    private int positionOffsetMask;

    public CacheSplitWayLRU(Memory memory,
                            int size,
                            int sizeBlock,
                            int ways,
                            CacheControll newLevel){
        this.memory = memory;
        this.ways = ways;
        this.sizeBlock = sizeBlock;
        this.bitsIndex = (int) (Math.log(size / ways) / Math.log(2));
        this.bitsOffset = (int) (Math.log(sizeBlock) / Math.log(2));
        this.positionSetMask = ((int) (size / ways) - 1) << bitsOffset;
        this.positionOffsetMask = sizeBlock - 1;
        for(int i = 0; i < size; i++){
            Set set = new Set(ways, sizeBlock);
            cacheMemoryI.put(i, set);
        }
        for(int i = 0; i < size ; i++){
            Set set = new Set(ways, sizeBlock);
            cacheMemoryD.put(i, set);
        }
    }

    public byte[] load(int position){
        int tag = position >>> (bitsIndex + bitsOffset);
        int positionSet    = (position & positionSetMask) >>> bitsOffset;
        int positionOffset = (position & positionOffsetMask);
        Set set;
        if(position < 0x00800000){
            set = this.cacheMemoryI.get(positionSet);
        }else{
            set = this.cacheMemoryD.get(positionSet);
        }
        Byte[] bytes = getWord(set, positionOffset, tag);
        byte[] bytesReturn = new byte[4];
        if(bytes == null){
            setWord(position, positionSet, set, tag);
            bytes = getWord(set, positionOffset, tag);
        }
        for(int i = 0; i < ways; i++){
            bytesReturn[i] = bytes[i].byteValue();
        }
        return bytesReturn;
    }

    public void store(int position, byte[] values){
        int tag = position >>> (bitsIndex + bitsOffset);
        int positionSet    = (position & positionSetMask) >>> bitsOffset;
        int positionOffset = (position & positionOffsetMask);
        Set set;
        if(position < 0x00800000){
            set = this.cacheMemoryI.get(positionSet);
        }else{
            set = this.cacheMemoryD.get(positionSet);
        }
        int find = findModel(set, tag);
        if(find == -1){
            setWord(position, positionSet, set, tag);
            find = findModel(set, tag);
        }
        for(int i = 0; i < 4; i++){
            set.model[find].bytes[positionOffset + i] = values[i];
        }
        set.model[find].isDirty = true;
        set.update(find);
    }
    
    public Byte[] getWord(Set set, int positionOffset, int tag){
        int find = findModel(set, tag);
        if(find == -1){
            return null;
        }else{
            Byte[] bytes = new Byte[4];
            for(int i = 0; i < 4; i++){
                bytes[i] = Byte.valueOf(set.model[find].bytes[positionOffset + i]);
            }
            return bytes;
        }
    }

    public int findModel(Set set, int tag){
        for(int i = 0; i < ways; i++){
            if(set.model[i].valid == true && set.model[i].tag == tag){
                set.update(i);
                return i;
            }
        }
        return -1;
    }

    public void setWord(int position, int positionSet, Set set, int tag){
        int[] LRU = set.LRU;
        int minIndex = 0;
        for(int i = 0; i < LRU.length; i++){
            if(LRU[i] < LRU[minIndex]){
                minIndex = i;
            }   
        }
        if(set.model[minIndex].isDirty == true){
            writeBack(set.model[minIndex], positionSet);
        }
        set.model[minIndex].tag = tag;
        int initPosition = position - (position % sizeBlock);
        for(int x = 0; x < sizeBlock; x = x + 4){
            byte[] bytesM;
            if(newLevel == null){
                bytesM = memory.loadWordByPosition(initPosition + x);
            }else{
                bytesM = newLevel.load(initPosition + x);
            }
            set.model[minIndex].bytes[x] = bytesM[0];
            set.model[minIndex].bytes[x + 1] = bytesM[1];
            set.model[minIndex].bytes[x + 2] = bytesM[2];
            set.model[minIndex].bytes[x + 3] = bytesM[3];
        }
        set.model[minIndex].valid = true;
    }

    public void writeBack(Model model, int positionSet){
        int position = model.tag << (bitsIndex +  bitsOffset);
        position = position | (positionSet << bitsOffset);
        for(int x = 0; x < sizeBlock; x = x + 4){
            byte[] bytesS = new byte[4];
            bytesS[0] = model.bytes[x];
            bytesS[1] = model.bytes[x + 1];
            bytesS[2] = model.bytes[x + 2];
            bytesS[3] = model.bytes[x + 3];
            if(newLevel == null){
                memory.saveWordByPosition((position + x), bytesS);
            }else{
                newLevel.store((position + x), bytesS);
            }
        }
        model.isDirty = false;
    }
}
