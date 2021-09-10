package types;

public class MainType {

    private byte[] instruction;

    public MainType(byte[] instruction){
        this.instruction = instruction;
    }

    public String getCode(){
        String code = "";
        String byteString = "";
        for(int index = 0; index < 4; index ++){
            for(int x = 7; x >= 0; x--){
                byteString = byteString +
                        String.valueOf((int) (instruction[index] >> x & 0x0001));   
            }
            code += byteString;
            byteString = "";
        }
        return code;
    }
}
