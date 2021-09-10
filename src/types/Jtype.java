package types;

public class Jtype extends MainType{
    private byte[] instruction;
    
    public Jtype(byte[] instruction) {
        super(instruction);
        this.instruction = instruction;
    }

    public String getTargetAddress(){
        String returnValue0 = "";
        String returnValue1 = "";
        String returnValue2 = "";
        String returnValue3 = "";
        for(int x = 7; x >= 6; x--){
            returnValue0 = returnValue0 +
                    String.valueOf((int) (instruction[0] >> x & 0x0001));   
        }
        for(int x = 7; x >= 0; x--){
            returnValue1 = returnValue1 +
                    String.valueOf((int) (instruction[1] >> x & 0x0001));   
        }
        for(int x = 7; x >= 0; x--){
            returnValue2 = returnValue2 +
                    String.valueOf((int) (instruction[2] >> x & 0x0001));   
        }

        for(int x = 7; x >= 0; x--){
            returnValue3 = returnValue3 +
                    String.valueOf((int) (instruction[3] >> x & 0x0001));   
        }
        return returnValue0 + returnValue1 + returnValue2 + returnValue3;
    }
    
}
