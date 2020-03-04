/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.service;

import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.utility.BLEConverter;
import com.freescale.bletoolbox.utility.CRC_16CITT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class OtapController {
    private static final String TAG = "otap";
    private static final String TAG_DATA = "data";
    private static final String RECORD_TYPE_HEADER = "S0"; // 16 bit address
    private static final String RECORD_TYPE_DATA_1 = "S1"; // 16 bit address
    private static final String RECORD_TYPE_DATA_2 = "S2"; // 24 bit address
    private static final String RECORD_TYPE_DATA_3 = "S3"; // 32 bit address
    private static final String RECORD_TYPE_TERMINATION = "S8"; //
    //
    public static final int COMPANY_IDENTIFIER_FOR_FREESCALE = 0x01FF;
    // command identify
    public static final byte CMD_ID_NEW_IMAGE_NOTIFICATION = 0x01;
    public static final byte CMD_ID_NEW_IMAGE_INFO_REQUEST = 0x02;
    public static final byte CMD_ID_NEW_IMAGE_INFO_RESPONSE = 0x03;
    public static final byte CMD_ID_IMAGE_BLOCK_REQUEST = 0x04;
    public static final byte CMD_ID_IMAGE_CHUNK = 0x05;
    public static final byte CMD_ID_IMAGE_TRANSFER_COMPLETE = 0x06;
    public static final byte CMD_ID_ERROR = 0x07;
    public static final byte CMD_ID_STOP_IMAGE_TRANSFER = 0x08;
    // sub element type
    public static final short SUB_ELEMENT_UPGRADE_IMAGE = 0x0000;
    public static final short SUB_ELEMENT_SECTOR_BITMAP = (short) 0xF000;
    public static final short SUB_ELEMENT_IMAGE_FILE_CRC = (short) 0xFF00;
    // MTU
    public static final int MTU = 247;

    public enum ChecksumStatus {NotMatchLineLength, InvalidChecksumLine, UnknowError, Valid}

    private static OtapController ourInstance = new OtapController();
    private ImageInfo m_clientImageInfo, m_newImageInfo;
    private int m_startPosition = -1;
    private int m_blockSize;
    private short m_chunkSize;
    private boolean m_sendingInProgress = false;
    private boolean m_isSendingBlock = false;
    private boolean m_hasAnError = false;
    private boolean m_completed = false;
    private boolean m_requestStop = false;
    private boolean interruptSending = false;
    private SendChunkCallback sendChunkCallback;

    public static OtapController getInstance() {
        return ourInstance;
    }

    private OtapController() {
    }

    public static void initController() {
        if (null != ourInstance) {
            ourInstance.interruptSending();
        }
        ourInstance = new OtapController();
    }

    public void setSendChunkCallback(SendChunkCallback sendChunkCallback) {
        this.sendChunkCallback = sendChunkCallback;
    }

    public void setClientImageInfo(ImageInfo clientImageInfo) {
        this.m_clientImageInfo = clientImageInfo;
    }

    public void setNewImageInfo(ImageInfo newImageInfo) {
        this.m_newImageInfo = newImageInfo;
        this.m_startPosition = -1;
    }

    public boolean hasAnError() {
        return this.m_hasAnError;
    }

    public boolean isSendingInProgress() {
        return this.m_sendingInProgress;
    }

    public void interruptSending() {
        this.interruptSending = true;
    }

    public void resetInterrupt() {
        this.interruptSending = false;
    }

    public ImageInfo getNewImageInfo() {
        return m_newImageInfo;
    }

    //==================================
    // support srec file
    //==================================
    public String getHeader(Context context, Uri fileUri) throws IOException {
        InputStream srecis = context.getContentResolver().openInputStream(fileUri);
        InputStreamReader isr = new InputStreamReader(srecis, "ASCII");
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith(RECORD_TYPE_HEADER)) break;
        }
        br.close();
        isr.close();
        srecis.close();
        //
        String hexHeaderData = line.substring(8, line.length() - 2);
        //
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hexHeaderData.length(); i += 2) {
            String str = hexHeaderData.substring(i, i + 2);
            result.append((char) Integer.parseInt(str, 16));
        }
        Log.d(TAG, "header data = " + result.toString());
        return result.toString();
    }

    public ChecksumStatus computeChecksumFile(Context context, Uri fileUri) throws IOException {
        InputStream srecis = context.getContentResolver().openInputStream(fileUri);
        InputStreamReader isr = new InputStreamReader(srecis, "ASCII");
        BufferedReader br = new BufferedReader(isr);
        String line;
        int currentLine = 1;
        while (((line = br.readLine()) != null)&&(line.length()>4)) {
//            Log.d(OtapController.class.getSimpleName(), line);
            ChecksumStatus checksumStatus = computeChecksumLine(line);
            if (ChecksumStatus.Valid != checksumStatus) {
                Log.e(OtapController.class.getSimpleName(), checksumStatus + " at line " + currentLine);
                return checksumStatus;
            }
            currentLine++;
        }
        br.close();
        isr.close();
        srecis.close();
        return ChecksumStatus.Valid;
    }

    public ChecksumStatus computeChecksumLine(String srecLine) {
        final String recordType = srecLine.substring(0, 2);
        int addLength = 4;
        if (RECORD_TYPE_HEADER.equals(recordType)) {
            addLength = 4;
        } else if (RECORD_TYPE_DATA_1.equals(recordType)) {
            addLength = 4;
        } else if (RECORD_TYPE_DATA_2.equals(recordType)) {
            addLength = 6;
        } else if (RECORD_TYPE_DATA_3.equals(recordType)) {
            addLength = 8;
        } else if (RECORD_TYPE_TERMINATION.equals(recordType)) {
            addLength = 6;
        }
        final String srecHexLine = srecLine.substring(2, srecLine.length());
        final String count = srecHexLine.substring(0, 2);
        final String address = srecHexLine.substring(2, 2 + addLength);
        final String data = srecHexLine.substring(2 + addLength, srecHexLine.length() - 2);
        final String checksum = srecLine.substring(srecLine.length() - 2);
        int numberOfByte = Integer.parseInt(count, 16);
        int lengthOfHexDigits = address.length() + data.length() + checksum.length();
        if (numberOfByte != (lengthOfHexDigits / 2)) {
            return ChecksumStatus.NotMatchLineLength;
        }
        int sumOfData = 0;
        for (int i = 0; i < srecHexLine.length() - 2; i += 2) {
            String eachByte = srecHexLine.substring(i, i + 2);
            sumOfData += Integer.parseInt(eachByte, 16);
        }
        String strSumOfData = Integer.toHexString(sumOfData);
        String strLeastSignificantByteOfTheTotal = strSumOfData.substring(strSumOfData.length() - 2, strSumOfData.length());
        int leastSignificantByteOfTheTotal = Integer.parseInt(strLeastSignificantByteOfTheTotal, 16);
        int checksumValue = Integer.parseInt(checksum, 16);
        if (0 == (leastSignificantByteOfTheTotal & checksumValue)) {
            return ChecksumStatus.Valid;
        } else {
            return ChecksumStatus.InvalidChecksumLine;
        }
    }

    private String createBleOtapHeader(File newImgFile) throws IOException {
        // read total image size
        FileInputStream fileInputStream = new FileInputStream(newImgFile);
        byte[] curData;
        long fileSize = newImgFile.length();
        /*long totalImageSize = 0;
        int block = 0;
        while (true) {
            if (fileSize > ((block + 1) * 1024)) {
                curData = new byte[1024];
            } else {
                curData = new byte[(int) (fileSize - (block * 1024))];
            }
            totalImageSize += curData.length;
            if (-1 == fileInputStream.read(curData)) {
                break;
            }
        }
        totalImageSize += 58;
        totalImageSize += 8;*/
        fileInputStream.close();
        fileSize += 58; // header length
        fileSize += 8; // CRC sub-element
        /*Log.d(TAG, " totalImageSize " + totalImageSize + " fileSize " + fileSize);
        Log.d(TAG, " totalImageSize " + Long.toHexString(totalImageSize) + " fileSize " + Long.toHexString(fileSize));*/
        //
        Log.d(TAG, " totalImageSize " + BLEConverter.longTo16HexStrLitleEndial(fileSize).substring(0, 8));
//        Log.d(TAG, " totalImageSize " + BLEConverter.longTo16HexStrLitleEndial(Long.MAX_VALUE));
        return "1EF11E0B" //Upgrade File Identifier 4byte
                + "0001" //Header Version
                + "3A00" //Header Length
                + "0000" //Header Field Control
                + "FF01" //Company Identifier
                + "0100" //image ID
                + "0500004111111101" //Image Version 8byte
                + "46534C20424C45204F5441502044656D6F20496D6167652046696C6500000000" //Header String 32byte
                + BLEConverter.longTo16HexStrLitleEndial(fileSize).substring(0, 8); //Total Image File Size 4byte
    }

    public File readSrecToCreateImg(Context context, Uri uri) throws IOException {
        File newImgFile = getTempImgFile(context);
        // Sub-element upgrade
        InputStream is = context.getContentResolver().openInputStream(uri);
        InputStreamReader isr = new InputStreamReader(is, "ASCII");
        BufferedReader br = new BufferedReader(isr);
        writeSubElementUpgrade(br, newImgFile);
        br.close();
        isr.close();
        is.close();
        // Sub-element sector bitmap
        appendSectorBitmap(newImgFile);
        // insert header
        byte[] dataToWrite;
        String otapHeader = createBleOtapHeader(newImgFile);
        if (null != otapHeader) {
            dataToWrite = new byte[otapHeader.length() / 2];
            BLEConverter.hexStrToByteArr(otapHeader, dataToWrite, 0);
            insert(newImgFile, 0, dataToWrite);
        }
        // Sub-element image file CRC
        appendImageFileCRC(newImgFile);
        return newImgFile;
    }

    private void writeSubElementUpgrade(BufferedReader bufferedReader, File newImgFile) throws IOException {
        //Sub-element
        //   Type[2B]: 0x0000 = Upgrade Image
        //   Length[4B]: Length of the binary image copied form the SREC file
        //   Value[Length B]: The actual binary image from the SREC file
        //---
        FileOutputStream fileOutputStream = new FileOutputStream(newImgFile);
        StringBuilder upgradeImage = new StringBuilder();
        // read srec file
        // write content Value[Length B]: The actual binary image from the SREC file
        int currentLine = 1;
        SrecLineData prevSrecLineData = null;
        SrecLineData curSrecLineData = null;
        String line;
        short CrcCompute = (short) 0xFFFF;
        int srecLength = 0;
        while ((line = bufferedReader.readLine()) != null) {
            curSrecLineData = getSrecDataInLine(line);
            if (null != curSrecLineData) {
                byte[] curData = new byte[line.length() / 2];
                BLEConverter.hexStrToByteArr(curSrecLineData.data, curData, 0);
                CrcCompute = CRC_16CITT.OTA_CrcCompute(curData, CrcCompute);
            }
            String lineDataWithGap = getLineDataWithGap(prevSrecLineData, curSrecLineData);
            if (null != lineDataWithGap) {
                srecLength += lineDataWithGap.length();
                upgradeImage.append(lineDataWithGap);
            }
            //
            prevSrecLineData = curSrecLineData;
            curSrecLineData = null;
            currentLine++;
        }
        Log.d(TAG, "crc srec CrcCompute = " + Integer.toHexString(CrcCompute & 0xFFFF));
        // Length[4B]: Length of the binary image copied form the SREC file
        // Ex: "007C0200"
        Log.d(TAG, BLEConverter.intTo8HexStrLitleEndial(srecLength / 2));
        upgradeImage.insert(0, BLEConverter.intTo8HexStrLitleEndial(srecLength / 2));
        upgradeImage.insert(0, "0000"); // Type[2B]: 0x0000 = Upgrade Image
        byte[] dataToWrite;
        while (0 < upgradeImage.length()) {
            int dataLength = (1024 < (upgradeImage.length() / 2)) ? 1024 : (upgradeImage.length() / 2);
            dataToWrite = new byte[dataLength];
            BLEConverter.hexStrToByteArr(upgradeImage.substring(0, dataLength * 2), dataToWrite, 0);
            fileOutputStream.write(dataToWrite);
            upgradeImage.delete(0, dataLength * 2);
        }
    }

    private String getLineDataWithGap(SrecLineData prevSrecLineData, SrecLineData curSrecLineData) {
        if (null == curSrecLineData) return null;
        if (null == prevSrecLineData) return curSrecLineData.data;
        long curAdd = Long.parseLong(curSrecLineData.address, 16);
        long prevAdd = Long.parseLong(prevSrecLineData.address, 16);
        long gapDatasLength = curAdd - prevAdd - prevSrecLineData.data.length() / 2;
        byte[] gapDatas = new byte[(int) gapDatasLength];
        for (int i = 0; i < gapDatasLength; i++) {
            gapDatas[i] = (byte) 0xFF;
        }
        StringBuilder result = new StringBuilder();
        result.append(BLEConverter.bytesToHex(gapDatas));
        result.append(curSrecLineData.data);
        return result.toString();
    }

    private SrecLineData getSrecDataInLine(String line) {
        if (line.startsWith(RECORD_TYPE_DATA_1)) {
            final int TYPE_LENGTH = 2;
            final int COUNT_LENGTH = 2;
            final int ADDRESS_LENGTH = 4;
            final int CHECKSUM_LENGTH = 2;
            String address = line.substring(TYPE_LENGTH + COUNT_LENGTH,
                    TYPE_LENGTH + COUNT_LENGTH + ADDRESS_LENGTH);
            String data = line.substring(TYPE_LENGTH + COUNT_LENGTH + ADDRESS_LENGTH,
                    line.length() - CHECKSUM_LENGTH);
            return new SrecLineData(address, data);
        } else if (line.startsWith(RECORD_TYPE_DATA_2)) {
            final int TYPE_LENGTH = 2;
            final int COUNT_LENGTH = 2;
            final int ADDRESS_LENGTH = 6;
            final int CHECKSUM_LENGTH = 2;
            String address = line.substring(TYPE_LENGTH + COUNT_LENGTH,
                    TYPE_LENGTH + COUNT_LENGTH + ADDRESS_LENGTH);
            String data = line.substring(TYPE_LENGTH + COUNT_LENGTH + ADDRESS_LENGTH,
                    line.length() - CHECKSUM_LENGTH);
            return new SrecLineData(address, data);
        } else if (line.startsWith(RECORD_TYPE_DATA_3)) {
            final int TYPE_LENGTH = 2;
            final int COUNT_LENGTH = 2;
            final int ADDRESS_LENGTH = 8;
            final int CHECKSUM_LENGTH = 2;
            String address = line.substring(TYPE_LENGTH + COUNT_LENGTH,
                    TYPE_LENGTH + COUNT_LENGTH + ADDRESS_LENGTH);
            String data = line.substring(TYPE_LENGTH + COUNT_LENGTH + ADDRESS_LENGTH,
                    line.length() - CHECKSUM_LENGTH);
            return new SrecLineData(address, data);
        } else {
            return null;
        }
    }

    /**
     * write sector bitmap
     * @param newImgFile
     * @throws IOException
     */
    private void appendSectorBitmap(File newImgFile) throws IOException {
        // Sub-element
        //   Type[2B]: 0xF000 = Sector Bitmap
        //   Length[4B]: 32  bytes for KW40Z
        //   Value[32]: All bytes 0xFF
        //   (A valid Sector Bitmap image file sub-element can be found in the imgconfig.json file)
        FileOutputStream fileOutputStream = new FileOutputStream(newImgFile, true);
        StringBuilder sectorBitmap = new StringBuilder();
        sectorBitmap.append("00F0"); // Type[2B]: 0xF000 = Sector Bitmap
        sectorBitmap.append("20000000"); // Length[4B]: read in imgconfig.json (=32)
        sectorBitmap.append("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"); //found in the imgconfig.json
        byte[] dataToWrite = new byte[sectorBitmap.length() / 2];
        BLEConverter.hexStrToByteArr(sectorBitmap.toString(), dataToWrite, 0);
        fileOutputStream.write(dataToWrite);
        fileOutputStream.close();
    }

    /**
     * write Image File CRC
     * @param newImgFile
     * @throws IOException
     */
    private void appendImageFileCRC(File newImgFile) throws IOException {
        //Sub-element
        //   Type[2B]: 0xF100 = Image File CRC
        //   Length[4B]: 2 bytes – 16 bit CRC
        //   Value[2B]: Calculated CRC value
        //   (This is a 16 bit CCITT type CRC which is calculated over all elements of the image file
        //    with the exception of the Image File CRC sub-element itself.
        //    This must be the last sub-element in an image file.)
        //
        FileInputStream fileInputStream = new FileInputStream(newImgFile);
        short crcValue = (short) 0x0000;
        short OTA_CrcCompute = crcValue, OTA_CrcCompute_Java = crcValue;
        int crc16 = crcValue;
        byte[] curData;
        long fileSize = newImgFile.length();
        int block = 0;
        while (true) {
            /*if (fileSize > ((block + 1) * 1024)) {
                curData = new byte[1024];
            } else {
                curData = new byte[(int) (fileSize - (block * 1024))];
            }*/
            curData = new byte[(int) (fileSize)];
            if (-1 == fileInputStream.read(curData)) {
                break;
            }
            OTA_CrcCompute = CRC_16CITT.OTA_CrcCompute(curData, OTA_CrcCompute);
            OTA_CrcCompute_Java = CRC_16CITT.OTA_CrcCompute_Java(curData, (short) curData.length, OTA_CrcCompute_Java);
            crc16 = CRC_16CITT.crc16(curData, crc16);
        }
        fileInputStream.close();
        Log.d(TAG, "OTA_CrcCompute = " + Integer.toHexString(OTA_CrcCompute & 0xFFFF).toUpperCase());
        Log.d(TAG, "OTA_CrcCompute_Java = " + Integer.toHexString(OTA_CrcCompute_Java & 0xFFFF).toUpperCase());
        Log.d(TAG, "crc16 = " + Integer.toHexString(crc16 & 0xFFFF).toUpperCase());
        crcValue = CRC_16CITT.OTA_CrcCompute(curData, crcValue);
        Log.d(TAG, "crc = " + Integer.toHexString(crcValue) + " " + Integer.toHexString(crcValue & 0xFFFF));
        //
        StringBuilder imageFileCRC = new StringBuilder();
        imageFileCRC.append("00F1"); // Type[2B]: 0xF100 = Image File CRC
        imageFileCRC.append("02000000"); // Length[4B]: read from imgconfig.json (=0x2)
        //
        byte[] crcValueInByte = BLEConverter.shortToByteArray(OTA_CrcCompute);
        crcValueInByte = BLEConverter.convertLittleEndian(crcValueInByte);
        Log.d(TAG, "crcValueInByte = " + BLEConverter.bytesToHex(crcValueInByte));
        imageFileCRC.append(BLEConverter.bytesToHex(crcValueInByte)); // Value[2B]: Calculated CRC value
        byte[] dataToWrite = new byte[imageFileCRC.length() / 2];
        FileOutputStream fileOutputStream = new FileOutputStream(newImgFile, true);
        BLEConverter.hexStrToByteArr(imageFileCRC.toString(), dataToWrite, 0);
        fileOutputStream.write(dataToWrite);
        fileOutputStream.close();
        //============
        /*fileInputStream = new FileInputStream(newImgFile);
        OTA_CrcCompute = 0x0000;
        curData = new byte[(int) (1024)];
        fileInputStream.read(curData);
        OTA_CrcCompute = CRC_16CITT.OTA_CrcCompute(curData, OTA_CrcCompute);
        fileInputStream.close();
        Log.d(TAG, "OTA_CrcCompute 1024 = " + Integer.toHexString(OTA_CrcCompute & 0xFFFF).toUpperCase());
        //10240
        fileInputStream = new FileInputStream(newImgFile);
        OTA_CrcCompute = 0x0000;
        curData = new byte[(int) (10240)];
        fileInputStream.read(curData);
        OTA_CrcCompute = CRC_16CITT.OTA_CrcCompute(curData, OTA_CrcCompute);
        fileInputStream.close();
        Log.d(TAG, "OTA_CrcCompute 10240 = " + Integer.toHexString(OTA_CrcCompute & 0xFFFF).toUpperCase());
        //102400
        fileInputStream = new FileInputStream(newImgFile);
        OTA_CrcCompute = 0x0000;
        curData = new byte[(int) (102400)];
        fileInputStream.read(curData);
        OTA_CrcCompute = CRC_16CITT.OTA_CrcCompute(curData, OTA_CrcCompute);
        fileInputStream.close();
        Log.d(TAG, "OTA_CrcCompute 102400 = " + Integer.toHexString(OTA_CrcCompute & 0xFFFF).toUpperCase());*/
        // ==============
        // Test code
        /*short value = CRC_16CITT.OTA_CrcCompute("123456789".getBytes(), (short)0x0000);
        Log.d(TAG, "123456789 = " + Integer.toHexString(value & 0xFFFF));
        //
        value = CRC_16CITT.OTA_CrcCompute("12345".getBytes(), (short)0x0000);
        value = CRC_16CITT.OTA_CrcCompute("6789".getBytes(), value);
        Log.d(TAG, "123456789 = " + Integer.toHexString(value & 0xFFFF));
        //
        byte[] data = {0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39};
        value = CRC_16CITT.OTA_CrcCompute(data, (short)0x0000);
        Log.d(TAG, "123456789 byte = " + Integer.toHexString(value & 0xFFFF));
        //
        newImgFile = new File(newImgFile.getParentFile(), "textfile");
        fileOutputStream = new FileOutputStream(newImgFile, false);
        fileOutputStream.write(data);
        fileOutputStream.close();
        Log.d(TAG, "123456789 newImgFile = " + newImgFile.getAbsolutePath());
        fileInputStream = new FileInputStream(newImgFile);
        crcValue = (short) 0x0000;
        OTA_CrcCompute = crcValue;
        curData = null;
        fileSize = newImgFile.length();
        block = 0;
        while (true) {
            if (fileSize > ((block + 1) * 2)) {
                curData = new byte[2];
            } else {
                curData = new byte[(int) (fileSize - (block * 2))];
            }
            if (-1 == fileInputStream.read(curData)) {
                break;
            }
            OTA_CrcCompute = CRC_16CITT.OTA_CrcCompute(curData, OTA_CrcCompute);
        }
        fileInputStream.close();
        Log.d(TAG, "123456789 file = " + Integer.toHexString(OTA_CrcCompute & 0xFFFF).toUpperCase());*/
    }

    public void insert(File fileToWirte, long offset, byte[] content) throws IOException{
        RandomAccessFile r = new RandomAccessFile(fileToWirte, "rw");
        File tempFile = new File(fileToWirte.getAbsoluteFile() + "~");
        RandomAccessFile rtemp = new RandomAccessFile(tempFile, "rw");
        long fileSize = r.length();
        FileChannel sourceChannel = r.getChannel();
        FileChannel targetChannel = rtemp.getChannel();
        sourceChannel.transferTo(offset, (fileSize - offset), targetChannel);
        sourceChannel.truncate(offset);
        r.seek(offset);
        r.write(content);
        long newOffset = r.getFilePointer();
        targetChannel.position(0L);
        sourceChannel.transferFrom(targetChannel, newOffset, (fileSize - offset));
        sourceChannel.close();
        targetChannel.close();
        tempFile.delete();
    }

    private File getTempImgFile(Context context) throws IOException{
        File dir = context.getCacheDir();
        /*File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "FSL_MCU_APK");
        if (!dir.exists()) {
            dir.mkdir();
        } else if (dir.isFile()) {
            dir.delete();
            dir.mkdir();
        }*/
        File newImgFile = new File(dir, "OTAP_GEN" + ".img");
        if (newImgFile.exists()) newImgFile.delete();
        newImgFile.createNewFile();
        return newImgFile;
    }

    //==================================
    // SUPPORT IMG FILE
    //==================================
    public ImageInfo getNewImgImageInfo(Context context, Uri imageUri) throws IOException {
        Log.d(TAG, "================================== ");
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        return getNewImgImageInfo(inputStream, imageUri);
    }

    public ImageInfo getNewImgImageInfo(InputStream inputStream, Uri imageUri) throws IOException {
        ImageInfo newImageInfo = new ImageInfo();
        newImageInfo.imageUri = imageUri;
        newImageInfo.realFileSize = inputStream.available();
        byte[] header = new byte[58];
        inputStream.read(header);
        inputStream.close();
        //
        byte[] imageId = new byte[2];
        System.arraycopy(header, 12, imageId, 0, 2);
        newImageInfo.imageIdHex = BLEConverter.bytesToHex(imageId);
        byte[] imageVersion = new byte[8];
        System.arraycopy(header, 14, imageVersion, 0, 8);
        newImageInfo.imageVersionHex = BLEConverter.bytesToHex(imageVersion);
        byte[] imageSize = new byte[4];
        System.arraycopy(header, 54, imageSize, 0, 4);
        newImageInfo.imageSizeHex = BLEConverter.bytesToHex(imageSize);
        // image name
        byte[] imageName = new byte[32];
        System.arraycopy(header, 22, imageName, 0, 32);
        newImageInfo.imageName = BLEConverter.readStringFromByte(imageName);
        Log.d(TAG, "=== HEADER === ");
        Log.d(TAG, "imageUri = " + newImageInfo.imageUri);
        Log.d(TAG, "imageId = " + newImageInfo.imageIdHex);
        Log.d(TAG, "imageVersionHex = " + newImageInfo.imageVersionHex);
        Log.d(TAG, "imageSizeHex = " + newImageInfo.imageSizeHex);
        Log.d(TAG, "realFileSize = " + newImageInfo.realFileSize);
        Log.d(TAG, "imageName = " + newImageInfo.imageName);
        Log.d(TAG, "=== HEADER === ");
        //
        byte[] data;
        data = new byte[4];
        System.arraycopy(header, 0, data, 0, 4);
        Log.d(TAG, "Upgrade File Identifier = " + BLEConverter.bytesToHex(data));
        data = new byte[2];
        System.arraycopy(header, 10, data, 0, 2);
        Log.d(TAG, "Company Identifier = " + BLEConverter.bytesToHex(data));
        Log.d(TAG, "=== HEADER END === ");
        Log.d(TAG, "================================== ");
        System.gc();
        return newImageInfo;
    }

    public void sendDummyImageChunk() {
        // TODO: 12/17/2015  dummy data
        // email subject: [FSL_Toolbox] Questions for OTAP
        // 2. If the Kinetis BLE Toolbox application does not receive the Image Block Request
        // (indication) at in capture frame 2005
        //      a. Send a Data Chunk with Sequence Number 0xFF, Length 1 and some dummy payload
        //        (after the image is opened in the application) – this will trigger an error
        //        in the OTAP Client state machine and it will restart the image transfer from where
        //        it was left off
//        byte[] chunk = new byte[m_chunkSize];
        byte[] chunk = new byte[1];
        short seqNumber = 0xFF;
        sendImageChunkByte(chunk, (byte) seqNumber);
    }

    public void  sendImgImageChunk(Context context, long timeDelay) throws IOException {
        if (null == m_newImageInfo || null == m_newImageInfo.imageUri) {
            throw new IllegalStateException("have no new image");
        }
        //
        if (m_isSendingBlock) {
            Log.e(TAG, "a block is sending");
            return;
        }
        //
        m_isSendingBlock = true;
        m_sendingInProgress = true;
        InputStream is = context.getContentResolver().openInputStream(m_newImageInfo.imageUri);
        /*
        //The Data parameter is an array containing the actual image part being transferred starting from the
        //BlockStartPosition + SeqNumber * ChunkSize position in the image file and containing ChunkSize or
        //less bytes depending on the position in the block. Only the last chunk in a block can have less than
        //ChunkSize bytes in the Image Chunk Command data payload.
        */
        final int byteOffset = m_startPosition;
        // move to offset
        if (0 < byteOffset) {
            byte[] chunk = new byte[byteOffset];
            is.read(chunk);
        }
        //
//        SystemClock.sleep(1000);
        Log.d(TAG, "start sending block = " + m_startPosition);
//        SystemClock.sleep(3000);
        // send a block
        final int fileSize = is.available();
        short seqNumber = 0x00;
        int nunberBytesInChuck;
        int totalSentInBlock = 0;
        int totalDataInBlock = 0;
        while (true) {
            SystemClock.sleep(timeDelay);
            if (m_hasAnError || m_requestStop || m_completed || interruptSending) {
                break;
            }
            byte[] chunk;
            int dataAvaiable = fileSize - seqNumber * m_chunkSize;
            if (m_chunkSize <= dataAvaiable) {
                chunk = new byte[m_chunkSize];
            } else if (dataAvaiable > 0) {
                chunk = new byte[dataAvaiable];
            } else {
                break;
            }
            nunberBytesInChuck = is.read(chunk);
            // end of file
            if (-1 == nunberBytesInChuck) {
                break;
            } else if (m_blockSize <= totalDataInBlock) {
                // end of block
                break;
            } else if (((byte) 0xff) == (byte) seqNumber) {
                // end of seqNumber limit
                // send the latest chunk
                int frameNo = seqNumber + 1;
                String offset = Integer.toHexString(seqNumber * m_chunkSize).toUpperCase();
                Log.d(TAG_DATA, "Send Frame " + frameNo + " offset " + offset + " of " + m_newImageInfo.imageSizeHex);
                int sentBytes = sendImageChunkByte(chunk, (byte) seqNumber);
                if (null != sendChunkCallback) {
                    long startPositionChunk = byteOffset + (seqNumber - 1) * m_chunkSize;
                    long endPositionChunk = byteOffset + seqNumber * m_chunkSize;
                    sendChunkCallback.onSentTrunk(startPositionChunk, endPositionChunk, frameNo, m_blockSize);
                }
                totalDataInBlock += chunk.length;
                totalSentInBlock += sentBytes;
                break;
            } else {
                // continue
                int frameNo = seqNumber + 1;
                String offset = Integer.toHexString(seqNumber * m_chunkSize).toUpperCase();
                Log.d(TAG_DATA, "Send Frame " + frameNo + " offset " + offset + " of " + m_newImageInfo.imageSizeHex);
                int sentBytes = sendImageChunkByte(chunk, (byte) seqNumber);
                if (null != sendChunkCallback) {
                    long startPositionChunk = byteOffset + (seqNumber - 1) * m_chunkSize;
                    long endPositionChunk = byteOffset + seqNumber * m_chunkSize;
                    sendChunkCallback.onSentTrunk(startPositionChunk, endPositionChunk, frameNo, m_blockSize);
                }
                totalDataInBlock += chunk.length;
                totalSentInBlock += sentBytes;
                seqNumber += 0x01;
            }
        }
        Log.d(TAG_DATA, "Sent in Block = " + totalSentInBlock + " data of block = " + totalDataInBlock);
        //
        Log.d(TAG, "latest SeqNumber = " + seqNumber);
        Log.d(TAG, "Sent in Block = " + totalSentInBlock + " data of block = " + totalDataInBlock);
        Log.d(TAG, "end sending block = " + m_startPosition);
        is.close();
        m_isSendingBlock = false;
        System.gc();
    }

    /**
     * @param actualDataToSend
     * @param seqNumber
     * @return size of chunkData include cmdId and seqNumber
     */
    private int sendImageChunkByte(byte[] actualDataToSend, byte seqNumber) {
        byte[] chunkData = new byte[actualDataToSend.length + 2];
        chunkData[0] = CMD_ID_IMAGE_CHUNK;
        chunkData[1] = seqNumber;
        System.arraycopy(actualDataToSend, 0, chunkData, 2, actualDataToSend.length);
        BLEService.INSTANCE.writeData(BLEAttributes.OTAP, BLEAttributes.OTAP_DATA,
                BLEService.Request.WRITE_NO_RESPONSE, chunkData);
        Log.d(TAG_DATA, "SEND ImageChunkByte " + BLEConverter.bytesToHexWithSpace(chunkData));
        return chunkData.length;
    }

    //====================================
    // COMMAND VIA OTAP CONTROL POINT
    //====================================

    /**
     *
     */
    public void sendNewImageNotification() {
        byte[] data = new byte[15];
        data[0] = CMD_ID_NEW_IMAGE_NOTIFICATION;
        // ImageId: The ImageId parameter should not be 0x0000
        // which is the reserved value for the current running image
        // or 0xFFFF which is the reserved value for “no image available”.
        BLEConverter.hexStrToByteArr(m_newImageInfo.imageIdHex, data, 1);
        // version
        BLEConverter.hexStrToByteArr(m_newImageInfo.imageVersionHex, data, 3);
        // file size 56792
        BLEConverter.hexStrToByteArr(m_newImageInfo.imageSizeHex, data, 11);
        BLEService.INSTANCE.writeData(BLEAttributes.OTAP, BLEAttributes.OTAP_CONTROL,
                BLEService.Request.WRITE, data);
        Log.d(TAG_DATA, "SEND NewImageNotification " + BLEConverter.bytesToHexWithSpace(data));
    }

    /**
     *
     */
    public void sendNewImageInfoResponse() {
        byte[] data = new byte[15];
        //
        data[0] = CMD_ID_NEW_IMAGE_INFO_RESPONSE;
        // ImageId: The ImageId parameter should not be 0x0000
        // which is the reserved value for the current running image
        // or 0xFFFF which is the reserved value for “no image available”.
        BLEConverter.hexStrToByteArr(m_newImageInfo.imageIdHex, data, 1);
        // version
        BLEConverter.hexStrToByteArr(m_newImageInfo.imageVersionHex, data, 3);
        // file size 56792
        BLEConverter.hexStrToByteArr(m_newImageInfo.imageSizeHex, data, 11);
        BLEService.INSTANCE.writeData(BLEAttributes.OTAP, BLEAttributes.OTAP_CONTROL,
                BLEService.Request.WRITE, data);
        Log.d(TAG_DATA, "SEND NewImageInfoResponse " + BLEConverter.bytesToHexWithSpace(data));
    }

    public void handleNewImageInfoRequest(byte[] clientData) {
        Log.d(TAG_DATA, "NewImageInfoRequest " + BLEConverter.bytesToHexWithSpace(clientData));
        final byte LENGTH = 11;
        if (CMD_ID_NEW_IMAGE_INFO_REQUEST != clientData[0] || LENGTH != clientData.length) {
            throw new IllegalArgumentException("clientData format is not correct");
        }
        // 02 00 00 01 00 00 41 11 11 11 01
        m_clientImageInfo = new ImageInfo();
        StringBuffer buildVersion = new StringBuffer();
        buildVersion.append(String.format("%02x ", clientData[3]));
        buildVersion.append(String.format("%02x ", clientData[4]));
        buildVersion.append(String.format("%02x ", clientData[5]));
        Log.d(TAG, "build version " + buildVersion);
        StringBuffer appVersion = new StringBuffer();
        appVersion.append(String.format("%02x ", clientData[6]));
        Log.d(TAG, "app Version " + appVersion);
        StringBuffer hardwareId = new StringBuffer();
        hardwareId.append(String.format("%02x ", clientData[7]));
        hardwareId.append(String.format("%02x ", clientData[8]));
        hardwareId.append(String.format("%02x ", clientData[9]));
        Log.d(TAG, "hardware Id " + hardwareId);
        StringBuffer manufacturer = new StringBuffer();
        manufacturer.append(String.format("%02x ", clientData[10]));
        Log.d(TAG, "manufacturer " + manufacturer);
        byte[] clientVersion = new byte[8];
        System.arraycopy(clientData, 3, clientVersion, 0, 8);
        m_clientImageInfo.setImageVersionHex(clientVersion);

        // request mtu
        BLEService.INSTANCE.requestMTU(MTU);
    }

    public boolean handleImageBlockRequest(byte[] clientData) {
        final byte CMD_ID = 0x04;
        final byte LENGTH = 16;
        if (CMD_ID != clientData[0] || LENGTH != clientData.length) {
            throw new IllegalArgumentException("clientData format is not correct");
        }
        Log.d(TAG_DATA, "Image Block Request received " + BLEConverter.bytesToHexWithSpace(clientData));
        boolean result = false;
        // 04 12 43 00 00 00 00 00 12 00 00 12 00 00 04 00
        // start position
        byte[] startPositionByte = new byte[4];
        System.arraycopy(clientData, 3, startPositionByte, 0, 4);
        startPositionByte = BLEConverter.convertLittleEndian(startPositionByte);
        int newStartPosition = ByteBuffer.wrap(startPositionByte).getInt();
        Log.d(TAG, "Start Position request Ox" + BLEConverter.bytesToHexWithSpace(startPositionByte));

        // handle error 0x0B : A chunk with an unexpected sequence number has been received.
        // -> restart sequence position
        if (m_hasAnError) {
            Log.e(TAG, "m_hasAnError = true");
            m_startPosition = -1;
            m_hasAnError = false;
        }

        if (m_startPosition == newStartPosition) {
            result = false;
            Log.e(TAG, "DUPLICATE request from the CLIENT BOARD");
            return result;
        }
        m_startPosition = newStartPosition;
        Log.d(TAG, "SETUP start position" + m_startPosition);
        // block size
        byte[] blockSize = new byte[4];
        System.arraycopy(clientData, 7, blockSize, 0, 4);
        blockSize = BLEConverter.convertLittleEndian(blockSize);
        m_blockSize = ByteBuffer.wrap(blockSize).getInt();
        Log.d(TAG, "Block Size request 0x" + BLEConverter.bytesToHexWithSpace(blockSize));
        Log.d(TAG, "SETUP block Size " + m_blockSize);
        // chunk size
        byte[] chunkSize = new byte[2];
        System.arraycopy(clientData, 11, chunkSize, 0, 2);
        chunkSize = BLEConverter.convertLittleEndian(chunkSize);
        m_chunkSize = ByteBuffer.wrap(chunkSize).getShort();
        Log.d(TAG, "Chunk Size 0x" + BLEConverter.bytesToHexWithSpace(chunkSize));
        Log.d(TAG, "SETUP chunk Size  " + m_chunkSize);
        //
        StringBuffer transferMethod = new StringBuffer();
        transferMethod.append(String.format("%02x ", clientData[13]));
        Log.d(TAG, "transferMethod " + transferMethod);
        byte[] L2capChannelOrPsm = new byte[2];
        System.arraycopy(clientData, 14, L2capChannelOrPsm, 0, 2);
        L2capChannelOrPsm = BLEConverter.convertLittleEndian(L2capChannelOrPsm);
        Log.d(TAG, "L2capChannelOrPsm " + BLEConverter.bytesToHexWithSpace(L2capChannelOrPsm));
        result = true;
        return result;
    }

    public void handleImageTransferComplete(byte[] clientData) {
        final byte CMD_ID = 0x06;
        final byte LENGTH = 4;
        if (CMD_ID != clientData[0] || LENGTH != clientData.length) {
            throw new IllegalArgumentException("clientData format is not correct");
        }
        // TODO
        Log.d(TAG_DATA, "COMPLETE TRANS " + BLEConverter.bytesToHexWithSpace(clientData));
        m_completed = true;
        if (null != sendChunkCallback) {
            sendChunkCallback.onStopSending(true);
        }
    }

    public void handleStopImageTransfer(byte[] clientData) {
        final byte CMD_ID = 0x08;
        final byte LENGTH = 3;
        if (CMD_ID != clientData[0] || LENGTH != clientData.length) {
            throw new IllegalArgumentException("clientData format is not correct");
        }
        // TODO
        Log.d(TAG_DATA, "STOP TRANS " + BLEConverter.bytesToHexWithSpace(clientData));
        m_requestStop = true;
        if (null != sendChunkCallback) {
            sendChunkCallback.onStopSending(false);
        }
    }

    public void handleErrorNotification(byte[] clientData) {
        final byte CMD_ID = 0x07;
        final byte LENGTH = 3;
        if (CMD_ID != clientData[0] || clientData.length != LENGTH) {
            throw new IllegalArgumentException("clientData format is not correct");
        }
        // TODO
        Log.e(TAG_DATA, "ERROR " + BLEConverter.bytesToHexWithSpace(clientData));
        m_hasAnError = true;
        if (null != sendChunkCallback) {
            sendChunkCallback.onStopSending(false);
        }
    }

    //==================================
    // IMAGE INFO
    //==================================
    public static class ImageInfo {
        private Uri imageUri;
        private String imageIdHex; // 2 bytes
        private String imageVersionHex; // 8 bytes
        private String imageSizeHex; // 4 bytes
        private String imageName;
        private long realFileSize;

        /**
         * @param imageIdHex size = 2 bytes
         */
        private void setImageIdHex(byte[] imageIdHex) {
            if (null != imageIdHex && 2 != imageIdHex.length) {
                throw new IllegalArgumentException("imageIdHex incorrect");
            } else if (null == imageIdHex) {
                this.imageIdHex = null;
            } else {
                StringBuffer sb = new StringBuffer();
                sb.append(String.format("%02x", imageIdHex[0]));
                sb.append(String.format("%02x", imageIdHex[1]));
                this.imageIdHex = sb.toString();
            }
        }

        /**
         * @param imageVersionHex size = 8 bytes
         */
        public void setImageVersionHex(byte[] imageVersionHex) {
            if (null != imageVersionHex && 8 != imageVersionHex.length) {
                throw new IllegalArgumentException("imageIdHex incorrect");
            } else if (null == imageVersionHex) {
                this.imageVersionHex = null;
            } else {
                StringBuffer sb = new StringBuffer();
                sb.append(String.format("%02x", imageVersionHex[0]));
                sb.append(String.format("%02x", imageVersionHex[1]));
                sb.append(String.format("%02x", imageVersionHex[2]));
                sb.append(String.format("%02x", imageVersionHex[3]));
                sb.append(String.format("%02x", imageVersionHex[4]));
                sb.append(String.format("%02x", imageVersionHex[5]));
                sb.append(String.format("%02x", imageVersionHex[6]));
                sb.append(String.format("%02x", imageVersionHex[7]));
                this.imageVersionHex = sb.toString();
            }
        }

        /**
         * @param imageSizeHex size = 4 bytes
         */
        private void setImageSizeHex(byte[] imageSizeHex) {
            if (null != imageSizeHex && 4 != imageSizeHex.length) {
                throw new IllegalArgumentException("imageIdHex incorrect");
            } else if (null == imageSizeHex) {
                this.imageSizeHex = null;
            } else {
                StringBuffer sb = new StringBuffer();
                sb.append(String.format("%02x", imageSizeHex[0]));
                sb.append(String.format("%02x", imageSizeHex[1]));
                sb.append(String.format("%02x", imageSizeHex[2]));
                sb.append(String.format("%02x", imageSizeHex[3]));
                this.imageSizeHex = sb.toString();
            }
        }

        private void setRealFileSize(long realFileSize) {
            this.realFileSize = realFileSize;
        }

        public double getFileSizeInKb() {
            byte[] tempByte = new byte[this.imageSizeHex.length() / 2];
            BLEConverter.hexStrToByteArr(this.imageSizeHex, tempByte, 0);
            tempByte = BLEConverter.convertLittleEndian(tempByte);
            String tempStr = BLEConverter.bytesToHex(tempByte);
            long imageSize = Long.parseLong(tempStr, 16);
            return (double) imageSize / 1024;
        }

        public String getImageName() {
            return imageName;
        }

        public String getImageVersionHex() {
            byte[] tempByte = new byte[this.imageVersionHex.length() / 2];
            BLEConverter.hexStrToByteArr(this.imageVersionHex, tempByte, 0);
            tempByte = BLEConverter.convertLittleEndian(tempByte);
            String tempStr = BLEConverter.bytesToHex(tempByte);
            return tempStr;
        }
    }

    private class SrecLineData {
        final String address;
        final String data;
        final int dataLength;

        SrecLineData(String address, String data) {
            this.address = address;
            this.data = data;
            dataLength = data.length();
        }
    }

    public interface SendChunkCallback {
        public void onSentTrunk(long startPosition, long endPosition, int frameNoInBlock, int blockSize);

        public void onStopSending(boolean sentDone);
    }
}
