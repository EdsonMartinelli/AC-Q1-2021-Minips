package structures.cachememory;
import java.util.Hashtable;
import structures.memory.Memory;

public class CacheSplitDir implements CacheControll{

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
    
    private Hashtable<Integer, Model> cacheMemoryI = new Hashtable<>();
    private Hashtable<Integer, Model> cacheMemoryD = new Hashtable<>();
    private Memory memory;
    private CacheControll newLevel;
    private int bitsIndex;
    private int bitsOffset;
    private int positionBlockMask;
    private int positionOffsetMask;

    public CacheSplitDir(Memory memory,
                         int size,
                         int sizeBlock,
                         CacheControll newLevel){
        this.memory = memory;
        this.bitsIndex = (int) (Math.log(size) / Math.log(2));
        this.bitsOffset = (int) (Math.log(sizeBlock) / Math.log(2));
        this.positionBlockMask = (size - 1) << bitsOffset;
        this.positionOffsetMask = sizeBlock - 1;
        for(int i = 0; i < size; i++){
            Model model = new Model(sizeBlock);
            cacheMemoryI.put(i, model);
        }
        for(int i = 0; i < size; i++){
            Model model = new Model(sizeBlock);
            cacheMemoryD.put(i, model);
        }
    }

    public byte[] load(int position){
        int tag = position >>> (bitsIndex + bitsOffset);
        int positionBlock = (position & positionBlockMask) >>> bitsOffset;
        int positionOffset = (position & positionOffsetMask);

        Model model;
        if(position < 0x00800000){
            model = this.cacheMemoryI.get(positionBlock);
        }else{
            model = this.cacheMemoryD.get(positionBlock);
        }
        if(!(model.valid == true && model.tag == tag)){
            setWord(position, positionBlock, tag, model);
        }
        return getWord(positionOffset, model);
    }

    public void store(int position, byte[] values){
        int tag = position >>> (bitsIndex + bitsOffset);
        int positionBlock = (position & positionBlockMask) >>> bitsOffset;
        int positionOffset = (position & positionOffsetMask);

        Model model;
        if(position < 0x00800000){
            model = this.cacheMemoryI.get(positionBlock);
        }else{
            model = this.cacheMemoryD.get(positionBlock);
        }

        if(!(model.valid == true && model.tag == tag)){
            setWord(position, positionBlock, tag, model);
        }

        for(int i = 0; i < 4; i++){
            model.bytes[positionOffset + i] = values[i];
        }
        model.isDirty = true;
    }
    
    public byte[] getWord(int position, Model model){
        byte[] bytes = new byte[4];
        for(int i = 0; i < 4; i++){
            bytes[i] = model.bytes[position + i];
        }
        return bytes;
    }

    public void setWord(int position, int positionBlock, int tag, Model model){
        if(model.isDirty == true){
            writeBack(model, positionBlock);
        }
        model.tag = tag;
        int initPosition = position - (position % 32);
        for(int x = 0; x < 32; x = x + 4){
            byte[] bytesM;
            if(newLevel == null){
                bytesM = memory.loadWordByPosition(initPosition + x);
            }else{
                bytesM = newLevel.load(initPosition + x);
            }
            model.bytes[x] = bytesM[0];
            model.bytes[x + 1] = bytesM[1];
            model.bytes[x + 2] = bytesM[2];
            model.bytes[x + 3] = bytesM[3];
        }
        model.valid = true;
    }

    public void writeBack(Model model, int positionBlock){
        int position = model.tag << (bitsIndex +  bitsOffset);
        position = position | (positionBlock << bitsOffset);
        for(int x = 0; x < 32; x = x + 4){
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
