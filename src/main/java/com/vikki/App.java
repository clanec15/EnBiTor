package com.vikki;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vikki.Utils.BITTokenData;
import com.vikki.Utils.indexes;

public class App 
{
    public static void main( String[] args )
    {
        FileInputStream fileT;
        List<String> deviceIDs = new ArrayList<>();
        List<String> deviceNames = new ArrayList<>();
        ClassLoader load = Thread.currentThread().getContextClassLoader();
        URL db = load.getResource("deviceIDdatabase.csv");
        boolean preFermi = false;
        boolean postFermi = false;



        try {
            deviceIDs = Files.readAllLines(Paths.get(db.toURI())).stream()
                .skip(1)
                .filter(l -> l.split(",").length > 3)
                .map(l -> l.split(",")[3])
                .collect(Collectors.toList());

            deviceNames = Files.readAllLines(Paths.get(db.toURI())).stream()
                .skip(1)
                .filter(l -> l.split(",").length > 0)
                .map(l -> l.split(",")[0])
                .collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            System.out.println(e.getMessage());
        }

        try {
            fileT = new FileInputStream("NVIDIA.Quadro2000.1024.110216.rom");
            //fileT = new FileInputStream("EVGA.GTX470.1280.100406.rom");
            //fileT = new FileInputStream("Gigabyte.GTX460.1024.101029_1.rom");
            //fileT = new FileInputStream("NVIDIA.8800GTX.768.070328.rom");
            //fileT = new FileInputStream("NVIDIA.GTX760.2048.130506.rom");
            //fileT = new FileInputStream("NVIDIA.GTX970.4096.140826.rom");
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            return;
        }

        byte[] read = null;

        try {
            byte[] expected = {(byte)85, (byte)170};
            byte[] start = fileT.readNBytes(2);

            if(!Arrays.equals(start, expected)){
                System.out.println("Kepler or Newer arquitecture detected!, skipping NVGI");
                fileT.getChannel().position(20);

                int mainBiosOffset;

                StringBuilder offset = new StringBuilder();
                for(int i = 0; i < 2; i++){
                    offset.append(conv((byte)fileT.read()));
                }
                mainBiosOffset = Integer.parseInt(offset.toString());
                mainBiosOffset = endiannessTrans(mainBiosOffset);
                fileT.getChannel().position(mainBiosOffset);
                read = fileT.readAllBytes();
                postFermi = true;
            } else {
                fileT.getChannel().position(0);
                read = fileT.readAllBytes();
            }

            
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeFile(fileT);
            return;
        }

        if(read == null){
            closeFile(fileT);
            System.out.println("File parsing failed!");
            return;
        }

        
        
        if(conv(read[0]).equals("55") && conv(read[1]).equals("aa")){
            System.out.println("BIOS Sig GOOD!");
        } else {
            System.out.println("BIOS Sig Incorrect or Invalid!");
            closeFile(fileT);
            return;
        }

        byte BIOSSize = read[2];

        System.out.printf("Full BIOS Size: %s (%d)\n", prefixCalc((int)BIOSSize*512), (int)BIOSSize*512);

        
        int PCIRHeaderOffset = Integer.parseInt(String.valueOf(conv(read[25])) + String.valueOf(conv(read[24])), 16);
        
        byte[] PCIRHeader = new byte[24];
        for(int i = 0; i < 24; i++){
            PCIRHeader[i] = read[PCIRHeaderOffset+i];
        }

        int BITTableOffset = PCIRHeaderOffset + 28;

        Map<String, BITTokenData> BITTableValues = BITContents(fileT, BITTableOffset);

        StringBuilder sig = new StringBuilder();
        for(int i = 0; i < 4; i++){
            sig.append((char)PCIRHeader[i]);
        }

        if(sig.toString().equals("PCIR")){
            System.out.println("PCIR Signature detected!");
        } else {
            System.out.println("PCIR Signature malformed, cannot lock in to PCIR Header!: " + sig.toString());
            closeFile(fileT);
            return;
        }

        StringBuilder vendorID = new StringBuilder();
        for(int i = 4; i < 6; i++){
            vendorID.append(conv(PCIRHeader[i]));
        }

        if(vendorID.toString().equals("de10")){
            System.out.println("NVIDIA Vendor ID Detected!, NVIDIA BIOS Confirmed!");
        } else {
            System.out.println("NVIDIA Vendor ID not Detected!, this editor might not work as expected!: " + vendorID.toString());
        }

        StringBuilder SubVendorS = new StringBuilder();
        for(int i = 0; i < 2; i++){
            SubVendorS.append(conv(read[indexes.SVENDOR_OF+i]));
        }

        System.out.println("Subsystem Vendor: " + (vendorLookup(SubVendorS.toString()) == null ? "Unknown" : vendorLookup(SubVendorS.toString())));

        StringBuilder deviceID = new StringBuilder();
        deviceID.append("0x");
        for(int i = 7; i > 5; i--){
            deviceID.append(conv(PCIRHeader[i]).toUpperCase());
        }


        int device = deviceIDs.indexOf(deviceID.toString());
        if(device == -1){
            System.out.println("Device not recognized!, DeviceID: " + deviceID);
            closeFile(fileT);
            return;
        } else {
            System.out.println("Graphics Card: " + deviceNames.get(device));
        }

        StringBuilder biosVer = new StringBuilder();
        for(int i = 0; i < 14; i++){
            biosVer.append((char)read[indexes.BIOSVER_OF+i]);
        }

        if(!biosVer.toString().contains(".")){
            biosVer.delete(0, biosVer.length());
            for(int i = 0; i < 14; i++){
                biosVer.append((char)read[(indexes.BIOSVER_OF+38)+i]);
            }
            preFermi = true;
        }

        System.out.println("BIOS Version: " + biosVer.toString());

        StringBuilder productDt = new StringBuilder();
        for(int i = 0; i < 22; i++){
            productDt.append((char)read[(preFermi == true ? indexes.PRODUCT_OF+34 : indexes.PRODUCT_OF)+i]);
        }

        System.out.println("Product: " + productDt.toString());

        StringBuilder BuildDate = new StringBuilder();
        for(int i = 0; i < 8; i++){
            BuildDate.append((char)read[indexes.BDATE_OF+i]);
        }

        System.out.println("Build Date: " + BuildDate.toString());

        closeFile(fileT);
    }

    private static String prefixCalc(int value){
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

    private static String vendorLookup(String id){
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

    private static String fTwo(String str){
        return str.length() > 2 ? str.substring(str.length() - 2) : str;
    }

    private static String conv(byte str){
        return fTwo(String.format("%02x", (int)str));
    }

    private static void closeFile(FileInputStream file){
        try {
            file.close();
        } catch (IOException e) {
            // TODO: handle exception
        }
    }


    private static int endiannessTrans(int pLow, int pHigh){
        return (pHigh << 8) | pLow;
    }

    private static int endiannessTrans(int data){
        return ((data << 8) | (data >> 8)) & 0xFFFF;
    }

    private static Map<String, BITTokenData> BITContents(FileInputStream bios, int BITsig){
        Map<String, BITTokenData> BITTokens = new HashMap<>();
        Map<Byte, String> BIT_Designations = new HashMap<>();

        final byte[] BITTOkensIDs = {(byte)0x32, (byte)0x41, (byte)0x42, (byte)0x43, (byte)0x44, (byte)0x45, (byte)0x49, (byte)0x4C, (byte)0x4D, (byte)0x4E, (byte)0x50, (byte)0x53, (byte)0x54, (byte)0x55, (byte)0x56, (byte)0x63, (byte)0x64, (byte)0x70, (byte)0x75, (byte)0x78, (byte)0x52, };
        final String[] BITokensNames = {"I2C Script Pointers", "DAC Data Pointers", "BIOS Data", "Clock Scripts Pointers", "DFP/Pane Data Pointers", "Init Table Pointers", "LVDS Table Pointers", "Memory Pointers", "NOP", "PERF Table Pointers", "String Pointers", "TMDS Table Pointers", "Display Pointers", "Virtual Field Pointers", "32-Bit Pointer Data", "DP Tables Pointers", "Falcode U-Code Data", "UEFI Driver Data", "MXM Config. Data", "", };
        
        for(int i = 0; i < 20; i++){
            BIT_Designations.put(BITTOkensIDs[i], BITokensNames[i]);
        }
    
        try {
            bios.getChannel().position(BITsig);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        final byte[] BITsigEx = {(byte)0xFF, (byte)0xB8, (byte)0x42, (byte)0x49, (byte)0x54, (byte)0x00};
        byte[] BITsigRead = new byte[6];
        for(int i = 0; i < 6; i++){
            try {
                BITsigRead[i] = (byte)bios.read();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if(!Arrays.equals(BITsigRead, BITsigEx)){
            System.out.println("Could not lock in to BIT Index Table, Bailing Out!");
            return null;
        }

        StringBuilder bitVersion = new StringBuilder();
        for(int i = 0; i < 2; i++){
            try {
                bitVersion.append(conv((byte)bios.read()));
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        System.out.println("BIT Version: " + endiannessTrans(Integer.parseInt(bitVersion.toString(), 16)) + " (" + bitVersion.toString() + ")");

        int BITEntries = 0;
        try {
            System.out.println("Size of BIT Header: " + bios.read());
            System.out.println("Size of BIT Tokens: " + bios.read());
            BITEntries = bios.read();
            System.out.println("BIT Entries to be parsed: " + BITEntries);
            bios.read();
        } catch (Exception e) {
            // TODO: handle exception
        }


        for(int i = 0; i < BITEntries; i++){
            BITTokenData data = new BITTokenData();

            try{
                data.ID = (byte)bios.read();
                data.version = (byte)bios.read();

                StringBuilder size = new StringBuilder();
                for(int j = 0; j < 2; j++){
                    size.append(conv((byte)bios.read()));
                }
                data.size = endiannessTrans(Integer.parseInt(size.toString(),16));
                

                StringBuilder ptr = new StringBuilder();
                for(int j = 0; j < 2; j++){
                    ptr.append(conv((byte)bios.read()));
                }
                data.pointer = endiannessTrans(Integer.parseInt(ptr.toString(),16));
                
            } catch (Exception e){

            }

            if(data.pointer == 0){
                data = null;
            } else {
                BITTokens.put(BIT_Designations.get(data.ID), data);
            }

            
        }
        
        return BITTokens;
    }
    
}
