package com.vikki.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class tools {
    public static String prefixCalc(int value){
        StringBuilder build = new StringBuilder();
        if(value < Math.pow(1024, 2)){
            build.append(value/1024);
            build.append("KiB");
        } else if(value >= Math.pow(1024, 2)){
            build.append(value/Math.pow(1024,2));
            build.append("MiB");
        } else if(value >= Math.pow(1024, 3)){
            build.append(value/Math.pow(1024,3));
            build.append("GiB");
        } 
        return build.toString();
    }

    public static String vendorLookup(String id){
        /*
         * 4310: ASUSTeK Computer Inc.
         * 6212: Micro-Star International Co., Ltd.
         * 5814: Gigabyte Technology Co., Ltd
         * db13: Biostar Microtech International Corp.
         * 1910: Elitegroup Computer Systems Co., Ltd.
         * cc55: ELSA Optronics Technology (ShenZhen) Co., Ltd.
         * b010: Gainward Technology Int'l Limited
         * 8411: Galaxy Microsystems Ltd.
         * 7773: Shenzhen Colorful Yugong Technology and Development Co.
         * 7d10: LeadTek Research Inc.
         * 6915: Palit Microsystems Inc.
         * 6e19: PNY Technologies, Inc.
         * 8216: XFX Pine Group Inc.
         * 4238: EVGA Corporation
         * 10de: Nvidia Corporation
         */

        Map<String, String> SVendors = new HashMap<>();
        String[] SvendorIDs = {"4310", "6212", "5814", "db13", "1910", "cc55", "b010", "8411", "7773", "7d10", "6915", "6e19", "8216", "4238", "de10"};
        String[] SvendorNames = {
            "ASUSTeK Computer Inc.", 
            "Micro-Star International Co., Ltd.", 
            "Gigabyte Technology Co., Ltd", 
            "Biostar Microtech International Corp.", 
            "Elitegroup Computer Systems Co., Ltd.",
            "Gainward Technology Int'l Limited",
            "ELSA Optronics Technology (ShenZhen) Co., Ltd.", 
            "Galaxy Microsystems Ltd.", 
            "Shenzhen Colorful Yugong Technology and Development Co.", 
            "LeadTek Research Inc.",
            "Palit Microsystems Inc.", 
            "PNY Technologies, Inc.", 
            "XFX Pine Group Inc.",
            "EVGA Corporation",
            "Nvidia Corporation"
        };

        for(int i = 0; i < Math.max(SvendorIDs.length, SvendorNames.length); i++){
            SVendors.put(SvendorIDs[i], SvendorNames[i]);
        }

        return SVendors.get(id);
    }

    public static String fTwo(String str){
        return str.length() > 2 ? str.substring(str.length() - 2) : str;
    }

    public static String conv(byte str){
        return fTwo(String.format("%02x", (int)str));
    }

    public static void closeFile(FileInputStream file){
        try {
            file.close();
        } catch (IOException e) {
            // TODO: handle exception
        }
    }


    public static int endiannessTrans(int pLow, int pHigh){
        return (pHigh << 8) | pLow;
    }

    public static int endiannessTrans(int data){
        return ((data << 8) | (data >> 8)) & 0xFFFF;
    }

    public static int endiannessTrans(Long data){
        return (int) (((data << 8) | (data >> 8)) & 0xFFFF);
    }

    public static void clearStringBuilder(StringBuilder sb){
        sb.delete(0, sb.length());
    }
}
