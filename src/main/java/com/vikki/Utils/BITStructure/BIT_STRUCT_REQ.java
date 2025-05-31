package com.vikki.Utils.BITStructure;

public class BIT_STRUCT_REQ {
    public static class BIT_PERFv1{
        public int PERFtbPtr;
        public int MemTwkTbPtr;
        public int drSslTbPtr;
        public int BTCptr;
        public int GPIOvSelectptr;
        public int AGPclkFreq;
        public int NVCLKPERFtbPtr;
    }


    
    public static class BIT_PERFv2{
        /*Common Mods */   
        public int PERFtbPtr;
        public int MemClkTbPtr;
        public int PwrCtrlTbPtr;
        public int VFreqTbPtr;
        public int FanCoolTbPtr;
        public int FanPlcyTbPtr;
        public int TrmlCtrlTbPtr;
        public int OcTbPtr;

        /*Adv. Data */
        public int MemTwkTbPtr;
        public int TrmlDevTbPtr;
        public int PerfConfigScPtr;
        public int VrailTbPtr;
        public int TrmlChTbPtr;
        public int VPlcyTbPtr;
        public int PwrLkTbPtr;
        public int PwrSensTbPtr;
    }

    public static class BIT_STRINGv1{
        public int SignOnMsgPtr;
        public int SignOnMsgMLPtr;
        public int OEMstr;
        public int OEMstrML;
        public int OEMvendor;
        public int OEMvendorML;
        public int OEMpd;
        public int OEMpdML;
        public int OEMpdRev;
        public int OEMpdRevML;
    }

    public static class BIT_STRINGv2{
        public int SignOnMsgPtr;
        public int SignOnMsgMLPtr;
        public int VerStrPtr;
        public int VerStrML;
        public int CpyRghtPtr;
        public int CpyRghtML;
        public int OEMstr;
        public int OEMstrML;
        public int OEMvendor;
        public int OEMvendorML;
        public int OEMpd;
        public int OEMpdML;
        public int OEMpdRev;
        public int OEMpdRevML;
    }


}
