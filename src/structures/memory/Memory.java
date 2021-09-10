package structures.memory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;

public class Memory {

    private Hashtable<Integer, Byte> HashMemory = new Hashtable<>();
    private int TextSection = 0x00400000;
    private int DataSection = 0x10010000;
    private int RodataSection = 0x00800000;
    private int numberOfInstructions = 0;
    private byte[] bytesProgramText;
    private byte[] bytesProgramData;
    private byte[] bytesProgramRodata;

    public Memory(Path pathText, Path pathData, Path pathRodata){
        try {
            this.bytesProgramText = Files.readAllBytes(pathText);  
            this.bytesProgramData = Files.readAllBytes(pathData);
            if(Files.exists(pathRodata)){
                this.bytesProgramRodata = Files.readAllBytes(pathRodata); 
            }else{
                this.bytesProgramRodata = new byte[0];
            }
        } catch (Exception e) {
            System.out.println("Invalid path to data file or text file.");
        }

        Byte[] wordsText = convertLittleEndian(bytesProgramText);
        Byte[] wordsData = convertLittleEndian(bytesProgramData);
        Byte[] wordsRodata = convertLittleEndian(bytesProgramRodata);
        addToHashMemory(TextSection, wordsText);
        addToHashMemory(DataSection, wordsData);
        addToHashMemory(RodataSection, wordsRodata);
    }

    public int getNumberOfInstruction(){
        return numberOfInstructions;
    }

    private byte getPositionValue(int position){
        return this.HashMemory.get(position) == null ?
                Byte.valueOf("00000000").byteValue() : this.HashMemory.get(position).byteValue();
    }

    public byte[] loadWordByPosition(int position){
        byte[] loadBytes = new byte[4];
        loadBytes[0] = getPositionValue(position);
        loadBytes[1] = getPositionValue(position + 1);
        loadBytes[2] = getPositionValue(position + 2);
        loadBytes[3] = getPositionValue(position + 3);
        return loadBytes;
    }

    public void saveWordByPosition(int position, byte[] values){
        for(int count = 0; count < values.length; count++){
            this.HashMemory.put(position, values[count]);
            position++;
        }
    }

    public byte[] loadByteByPosition(int position){
        byte[] loadBytes = new byte[4];
        loadBytes[0] = 0;
        loadBytes[1] = 0;
        loadBytes[2] = 0;
        loadBytes[3] = getPositionValue(position);
        return loadBytes;
    }

    private void addToHashMemory(int Section, Byte[] values){       
        for(int count = 0; count < values.length; count++){
            this.HashMemory.put(Section, values[count]);
            if(Section < 0x00800000){
                numberOfInstructions++;
            }
            Section++;
        }
    }

    private Byte[] convertLittleEndian (byte[] code){
        Byte[] vectorWord = new Byte[code.length];
        Byte[] word = new Byte[4];
        int countWord = 3;
        int countVectorWord = 0;
        for(int valueByte = 0; valueByte < code.length; valueByte++){
            Byte newByte = Byte.valueOf(code[valueByte]);
            word[countWord] = newByte;
            countWord--;
            if(countWord == -1){
                countWord = 3;
                for(int countByte = 0; countByte <= countWord; countByte++){
                    vectorWord[countVectorWord] = word[countByte];
                    countVectorWord++;
                }
            }
        }
        return vectorWord;
    }
}
