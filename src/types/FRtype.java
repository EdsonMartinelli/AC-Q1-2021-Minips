package types;

public class FRtype extends MainType{
    private byte[] instruction;
    
    public FRtype(byte[] instruction){
        super(instruction);
        this.instruction = instruction;
    }
    
    public int getFT(){
        int value  = (int) (instruction[1] & 0b00011111);
        return value;
    }

    public int getFS(){
        int value  = (int) ((instruction[2] & 0b11111000) >> 3);
        return value;
    }

    public int getFD(){
        int value  = (int) (((instruction[2] & 0b00000111) << 2) | 
                            ((instruction[3] & 0b11000000) >> 6));
        return value;
    }
    
}
