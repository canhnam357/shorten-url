package canhnam357.shortenurlproject.service.utility;

import canhnam357.shortenurlproject.exception.TooManyRequestException;

public class GenId {
    private static final int BIT_LENGTH_TIMESTAMP = 36;
    private  static final int BIT_LENGTH_ID = 14;
    private  static final long MAX_ID = (1L << BIT_LENGTH_ID) - 1;

    public static long lastEpochSecond = -1L;
    private static long sequence = 0L;

    public static synchronized long getNextId(long currentEpochSecond) {
        if (currentEpochSecond < lastEpochSecond) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        if (currentEpochSecond == lastEpochSecond) {
            sequence++;
            if (sequence > MAX_ID) {
                throw new TooManyRequestException("Too many requests for the same timestamp");
            }
        } else {
            sequence = 0L;
            lastEpochSecond = currentEpochSecond;
        }
        return sequence;
    }

    public static long calculateCode(long epochSecond, long id) {
        return (1L << BIT_LENGTH_TIMESTAMP | epochSecond) << BIT_LENGTH_ID | id;
    }

    public static String getCodeString(long code) {
        StringBuilder codeString = new StringBuilder();
        while (code > 0) {
            long k = code % 62;
            code /= 62;
            if (k < 26) {
                codeString.insert(0, (char) ('A' + k));
            }
            else if (k < 52) {
                codeString.insert(0, (char) ('a' + k - 26));
            }
            else {
                codeString.insert(0, (char) ('0' + k - 52));
            }
        }
        return codeString.toString();
    }

    public static long getCode(String codeString) {
        long code = 0;
        for (int i = 0; i < codeString.length(); i++) {
            code *= 62;
            char c = codeString.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                code += (c - 'A');
            }
            else if (c >= 'a' && c <= 'z') {
                code += (c - 'a' + 26);
            }
            else if (c >= '0' && c <= '9') {
                code += (c - '0' + 52);
            }
            else {
                code = -1;
                break;
            }
            if (code > (1L << (BIT_LENGTH_TIMESTAMP + BIT_LENGTH_ID + 1))) {
                code = -1;
                break;
            }
        }
        return code;
    }
}
