package com.vikki.Ops;

import java.io.FileInputStream;

import com.vikki.Utils.BITStructure.BIT_STRUCT_REQ;
import com.vikki.Utils.BITStructure.BIT_STRUCT_REQ.BIT_PERFv1;
import com.vikki.Utils.BITStructure.BIT_STRUCT_REQ.BIT_PERFv2;
import com.vikki.Utils.TokenData;
import com.vikki.Utils.tools;

public class BITParsing {
    public static TokenData BITParsingSpec(FileInputStream file, BIT_Types_Enum type, int offset, int version){
        TokenData parsedData = new TokenData();

        switch (type) {
            case BIT_TOKEN_PERF_PTRS -> {
                
                try {
                    file.getChannel().position(offset);
                    if(version == 1){
                        BIT_STRUCT_REQ.BIT_PERFv1 table = new BIT_PERFv1();

                        StringBuilder ptr = new StringBuilder();

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.PERFtbPtr = tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.MemTwkTbPtr = tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);
                        
                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.drSslTbPtr = tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.BTCptr = tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.GPIOvSelectptr = tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        ptr.append(tools.conv((byte)file.read()));
                        table.AGPclkFreq = tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.NVCLKPERFtbPtr = tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);


                        parsedData.BIT_Type = table;
                    } else {
                        BIT_STRUCT_REQ.BIT_PERFv2 table = new BIT_PERFv2();
                        StringBuilder ptr = new StringBuilder();


                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.PERFtbPtr = tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.MemClkTbPtr = tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.MemTwkTbPtr= tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.PwrCtrlTbPtr= tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.TrmlCtrlTbPtr= tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.TrmlDevTbPtr= tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 16; j++){
                            file.read();
                        }

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.PwrSensTbPtr= tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        
                        for(int j = 0; j < 8; j++){
                            file.read();
                        }

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.VFreqTbPtr= tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        

                        for(int j = 0; j < 8; j++){
                            file.read();
                        }

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.PwrLkTbPtr= tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 4; j++){
                            file.read();
                        }

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.TrmlChTbPtr= tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 12; j++){
                            file.read();
                        }

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.FanCoolTbPtr= tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        
                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.FanPlcyTbPtr= tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        
                        for(int j = 0; j < 8; j++){
                            file.read();
                        }

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.VrailTbPtr= tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 4; j++){
                            file.read();
                        }

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.VPlcyTbPtr= tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);

                        for(int j = 0; j < 36; j++){
                            file.read();
                        }

                        for(int j = 0; j < 4; j++){
                            ptr.append(tools.conv((byte)file.read()));
                        }
                        table.OcTbPtr= tools.endiannessTrans(Long.valueOf(ptr.toString(),16));
                        tools.clearStringBuilder(ptr);
                        

                        parsedData.BIT_Type = table;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
            }
        
            default -> {
            }
        }

        

        return parsedData;
    }
}
