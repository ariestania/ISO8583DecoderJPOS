/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ariestania.isodecoder.main;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

/**
 *
 * @author ariestania.winda
 */
public class ISOProcess {

    private static String findBitMaps(String msg) {
        String firstBitmap = msg.substring(4, 20);
        String binFirstBitMap = hexToBinary(firstBitmap);
        boolean hasSecondary = (binFirstBitMap.charAt(0)) == '1';
        if (!hasSecondary) {
            return firstBitmap;
        }
        return msg.substring(4,36);
    }

    public static String processIsoMsg(String msg) {
        String isoMsgResult = "";
        String error = "";
        try {
            GenericPackager packager = new GenericPackager("ISOMSG.xml");
            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.unpack(msg.getBytes());

            isoMsgResult = logISOMsg(isoMsg, findBitMaps(msg));
        } catch (ISOException ex) {
            error = ex.getMessage();
        }
        return isoMsgResult + "\n" + error;
    }

    private static String logISOMsg(ISOMsg msg, String bitmaps) {
        StringBuilder sb = new StringBuilder();
        StringBuilder activeBit = new StringBuilder();

        String error = "";
        sb.append("----ISO MESSAGE-----\n");
        activeBit.append("Active Bit: ");
        try {
            sb.append("  MTI : ").append(msg.getMTI()).append("\n");
            sb.append("  Bitmaps : ").append(bitmaps).append("\n\n");
            for (int i = 1; i <= msg.getMaxField(); i++) {
                if (msg.hasField(i)) {
                    activeBit.append(i);
                    sb.append("    Bit-").append(i).append(" : ").append(msg.getString(i)).append("\n");
                    if (i < msg.getMaxField()) {
                        activeBit.append(", ");
                    } else {
                        activeBit.append("\n\n");
                    }
                }
            }
        } catch (ISOException e) {
            error = e.getMessage();
        } finally {
            sb.append("--------------------\n");
        }
        return activeBit.toString() + sb.toString() + error;
    }

    private static String HexToBinary(char Hex) {
        String[] staticLookup = new String[]{"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};
        return staticLookup[Integer.parseInt(Character.toString(Hex), 16)];
    }

    private static String hexToBinary(String s) {
        String result = "";

        for (int i = 0; i < s.length(); i++) {
            result = result + HexToBinary(s.charAt(i));
        }

        return result;
    }

}
