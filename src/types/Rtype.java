package types;

public class Rtype extends MainType{
    private byte[] instruction;

    public Rtype(byte[] instruction){
        super(instruction);
        this.instruction = instruction;
    }

    public int getRS(){
        int value  = (int) (((instruction[0] & 0b00000011) << 3) | 
                            ((instruction[1] & 0b11100000) >> 5));
        return value;
    }

    public int getRT(){
        int value  = (int) (instruction[1] & 0b00011111);
        return value;
    }

    public int getRD(){
        int value  = (int) ((instruction[2] & 0b11111000) >> 3);
        return value;
    }

    public int getShamt(){
        int value  = (int) (((instruction[2] & 0b00000111) << 2) | 
                            ((instruction[3] & 0b11000000) >> 6));
        return value;
    }

}