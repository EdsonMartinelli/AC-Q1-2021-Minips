package structures.auxiliaries;

public final class Auxiliary {
    
    private static String[][] functions = new String[8][8];
    private static String[][] functionsRtype = new String[8][8];
    private static String[][] fltpFunction= new String[4][8];
    private static String[][] regimmFunction= new String[4][8];
    private static String[][] coFunctions = new String[8][8]; 

    public static String[][] getOpTable(){
        functions[0][0] = "rTypeFunctions";
        functions[0][1] = "regimmFunctions";
        functions[0][2] = "jump"; //<---- Branch
        functions[0][3] = "jal"; //<---- Branch
        functions[0][4] = "beq"; //<---- Branch
        functions[0][5] = "bne"; //<---- Branch
        functions[0][6] = "blez"; //<---- Branch
        functions[1][0] = "addi";
        functions[1][1] = "addiu";
        functions[1][2] = "slti";
        functions[1][4] = "andi";
        functions[1][5] = "ori";
        functions[1][7] = "lui";
        functions[2][1] = "FLTP";
        functions[4][0] = "lb";
        functions[4][3] = "lw";
        functions[5][3] = "sw";
        functions[6][1] = "lwc1";
        functions[6][5] = "ldc1";
        functions[7][1] = "swc1";
        return functions;
    }

    public static String[][] getRTable(){
        functionsRtype[0][0] = "sll";
        functionsRtype[0][2] = "srl";
        functionsRtype[0][3] = "sra";
        functionsRtype[1][0] = "jr"; //<---- Branch
        functionsRtype[1][1] = "jalr"; //<---- Branch
        functionsRtype[1][4] = "syscall";
        functionsRtype[2][2] = "mflo";
        functionsRtype[2][0] = "mfhi";
        functionsRtype[3][0] = "mult";
        functionsRtype[3][2] = "div";
        functionsRtype[4][1] = "addu";
        functionsRtype[4][0] = "add";
        functionsRtype[4][2] = "sub";
        functionsRtype[4][3] = "subu";
        functionsRtype[4][4] = "and";
        functionsRtype[4][5] = "or";
        functionsRtype[4][6] = "xor";
        functionsRtype[5][2] = "slt";
        functionsRtype[5][3] = "sltu";
        return functionsRtype;
    }

    public static String[][] getFLTPTable(){
        fltpFunction[0][0] = "mfc1";
        fltpFunction[0][4] = "mtc1";
        fltpFunction[2][0] = "s_definition";
        fltpFunction[2][1] = "d_definition";
        fltpFunction[2][4] = "w_definition";
        return fltpFunction;
    }

    public static String[][] getRegimmTable(){
        regimmFunction[0][1] = "bgez";
        return regimmFunction;
    }

    public static String[][] getCoTable(){
        coFunctions[0][0] = "add";
        coFunctions[0][2] = "mul";
        coFunctions[0][3] = "div";
        coFunctions[0][6] = "mov";
        coFunctions[4][0] = "cvt_s";
        coFunctions[4][1] = "cvt_d";
        return coFunctions;
    }

    public static String intToBinaryString(int number) {
        String binarySeq =Integer.toBinaryString(number);
        while(binarySeq.length() < 32 ){
            binarySeq = "0" + binarySeq;
        }
        return binarySeq;
    }

    public static int binaryStringToInt(String binary) {
        String binarySeq = bitExtender(binary);
        return ((int) Long.parseLong(binarySeq, 2));
    }

    private static String bitExtender(String binary){
        String binarySeq = binary;
        char msb = binarySeq.charAt(0);
        while(binarySeq.length() < 32 ){
            binarySeq = msb + binarySeq;
        }
        return binarySeq;
    }

}
