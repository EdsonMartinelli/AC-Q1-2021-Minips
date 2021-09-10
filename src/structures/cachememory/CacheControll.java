package structures.cachememory;
//import structures.memory.*;

public interface CacheControll{

    public byte[] load(int position);

    public void store(int position, byte[] values);

}
