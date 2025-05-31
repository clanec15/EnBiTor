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

import com.vikki.Ops.BITParsing;
import com.vikki.Ops.BIT_Types_Enum;
import com.vikki.Utils.BITTokenData;
import com.vikki.Utils.TokenData;
import com.vikki.Utils.indexes;
import com.vikki.Utils.tools;

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
            //fileT = new FileInputStream("NVIDIA.Quadro2000.1024.110216.rom");
            fileT = new FileInputStream("EVGA.GTX470.1280.100406.rom");
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
                    offset.append(tools.conv((byte)fileT.read()));
                }
                mainBiosOffset = Integer.parseInt(offset.toString());
                
                mainBiosOffset = tools.endiannessTrans(mainBiosOffset);
                fileT.getChannel().position(mainBiosOffset);
                read = fileT.readAllBytes();
                postFermi = true;
            } else {
                fileT.getChannel().position(0);
                read = fileT.readAllBytes();
            }

            
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
            tools.closeFile(fileT);
            return;
        }

        if(read == null){
            tools.closeFile(fileT);
            System.out.println("File parsing failed!");
            return;
        }

        
        
        if(tools.conv(read[0]).equals("55") && tools.conv(read[1]).equals("aa")){
            System.out.println("BIOS Sig GOOD!");
        } else {
            System.out.println("BIOS Sig Incorrect or Invalid!");
            tools.closeFile(fileT);
            return;
        }

        byte BIOSSize = read[2];

        System.out.printf("Full BIOS Size: %s (%d)\n", tools.prefixCalc((int)BIOSSize*512), (int)BIOSSize*512);

        
        int PCIRHeaderOffset = Integer.parseInt(String.valueOf(tools.conv(read[25])) + String.valueOf(tools.conv(read[24])), 16);
        
        byte[] PCIRHeader = new byte[24];
        for(int i = 0; i < 24; i++){
            PCIRHeader[i] = read[PCIRHeaderOffset+i];
        }

        int BITTableOffset = PCIRHeaderOffset + 28;

        Map<String, BITTokenData> BITTableValues = BITContents(fileT, BITTableOffset);



        TokenData perfTable = null;

        if(BITTableValues.get("PERF Table Pointers") == null){
            System.out.println("PERF TABLE NOT FOUND!!!");
        } else {
            System.out.println("Parsing PERF Table...");
            perfTable = BITParsing.BITParsingSpec(fileT, BIT_Types_Enum.BIT_TOKEN_PERF_PTRS, BITTableValues.get("PERF Table Pointers").pointer, BITTableValues.get("PERF Table Pointers").version);
        }

        StringBuilder sig = new StringBuilder();
        for(int i = 0; i < 4; i++){
            sig.append((char)PCIRHeader[i]);
        }

        if(sig.toString().equals("PCIR")){
            System.out.println("PCIR Signature detected!");
        } else {
            System.out.println("PCIR Signature malformed, cannot lock in to PCIR Header!: " + sig.toString());
            tools.closeFile(fileT);
            return;
        }

        StringBuilder vendorID = new StringBuilder();
        for(int i = 4; i < 6; i++){
            vendorID.append(tools.conv(PCIRHeader[i]));
        }

        if(vendorID.toString().equals("de10")){
            System.out.println("NVIDIA Vendor ID Detected!, NVIDIA BIOS Confirmed!");
        } else {
            System.out.println("NVIDIA Vendor ID not Detected!, this editor might not work as expected!: " + vendorID.toString());
        }

        StringBuilder SubVendorS = new StringBuilder();
        for(int i = 0; i < 2; i++){
            SubVendorS.append(tools.conv(read[indexes.SVENDOR_OF+i]));
        }

        System.out.println("Subsystem Vendor: " + (tools.vendorLookup(SubVendorS.toString()) == null ? "Unknown" : tools.vendorLookup(SubVendorS.toString())));

        StringBuilder deviceID = new StringBuilder();
        deviceID.append("0x");
        for(int i = 7; i > 5; i--){
            deviceID.append(tools.conv(PCIRHeader[i]).toUpperCase());
        }


        int device = deviceIDs.indexOf(deviceID.toString());
        if(device == -1){
            System.out.println("Device not recognized!, DeviceID: " + deviceID);
            tools.closeFile(fileT);
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

        tools.closeFile(fileT);
    }

    

    private static Map<String, BITTokenData> BITContents(FileInputStream bios, int BITsig){
        Map<String, BITTokenData> BITTokens = new HashMap<>();
        Map<Byte, String> BIT_Designations = new HashMap<>();

        final byte[] BITTOkensIDs = {(byte)0x32, (byte)0x41, (byte)0x42, (byte)0x43, (byte)0x44, (byte)0x49, (byte)0x4C, (byte)0x4D, (byte)0x4E, (byte)0x50, (byte)0x53, (byte)0x54, (byte)0x55, (byte)0x56, (byte)0x63, (byte)0x64, (byte)0x70, (byte)0x75, (byte)0x78, (byte)0x52, };
        final String[] BITokensNames = {"I2C Script Pointers", "DAC Data Pointers", "BIOS Data", "Clock Scripts Pointers", "DFP/Pane Data Pointers", "Init Table Pointers", "LVDS Table Pointers", "Memory Pointers", "NOP", "PERF Table Pointers", "String Pointers", "TMDS Table Pointers", "Display Pointers", "Virtual Field Pointers", "32-Bit Pointer Data", "DP Tables Pointers", "Falcode U-Code Data", "UEFI Driver Data", "MXM Config. Data", "BIT_TOKEN_BRIDGE_FW_DATA"};
        


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
                bitVersion.append(tools.conv((byte)bios.read()));
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        System.out.println("BIT Version: " + tools.endiannessTrans(Integer.parseInt(bitVersion.toString(), 16)) + " (" + bitVersion.toString() + ")");

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


        for(int i = 0; i < BITEntries-1; i++){
            BITTokenData data = new BITTokenData();

            try{
                data.ID = (byte)bios.read();
                data.version = (byte)bios.read();

                StringBuilder size = new StringBuilder();
                for(int j = 0; j < 2; j++){
                    size.append(tools.conv((byte)bios.read()));
                }
                data.size = tools.endiannessTrans(Integer.parseInt(size.toString(),16));
                

                StringBuilder ptr = new StringBuilder();
                for(int j = 0; j < 2; j++){
                    ptr.append(tools.conv((byte)bios.read()));
                }
                data.pointer = tools.endiannessTrans(Integer.parseInt(ptr.toString(),16));
                
            } catch (Exception e){

            }

            if(data.pointer == 0){
                data = null;
            } else {
                System.out.println("Found BIT Token Table: " + BIT_Designations.get(data.ID));
                BITTokens.put(BIT_Designations.get(data.ID), data);
            }

            
        }
        
        return BITTokens;
    }
    
}
