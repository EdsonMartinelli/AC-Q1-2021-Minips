package structures.cachememory;

import structures.memory.Memory;

public class CacheNull implements CacheControll{

    private Memory memory;

    public CacheNull(Memory memory){
        this.memory = memory;
    }

    public byte[] load(int position) {
        return memory.loadWordByPosition(position);
    }

    public void store(int position, byte[] values) {
        memory.saveWordByPosition(position, values);
        
    }
    
    
}
