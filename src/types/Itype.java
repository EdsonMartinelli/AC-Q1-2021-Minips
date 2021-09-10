package types;

public class Itype extends MainType{
    private byte[] instruction;

    public Itype(byte[] instruction) {
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

    public byte[] getAddressImmediate(boolean signed){
        byte[] bytes = new byte[4];
        if(((instruction[2] >> 7 & 0x0001) == 1) && (signed == true)){
            bytes[0] = (byte) 0xFF;
            bytes[1] = (byte) 0xFF;
        }else{
            bytes[0] = 0;
            bytes[1] = 0;
        }
        bytes[2] = instruction[2];
        bytes[3] = instruction[3];
        return bytes;
    }

    public String getAddressImmediateString(){
        String returnValue1 = "";
        String returnValue2 = "";
        for(int x = 7; x >= 0; x--){
            returnValue1 = returnValue1 +
                    String.valueOf((int) (instruction[2] >> x & 0x0001));   
        }
        for(int x = 7; x >= 0; x--){
            returnValue2 = returnValue2 +
                    String.valueOf((int) (instruction[3] >> x & 0x0001));   
        }
        return returnValue1 + returnValue2;
    }


}
