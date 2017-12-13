package com.bluetoothlib.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据格式实体类
 *
 * @author john_chen
 */
public class DateUtil {
    private static Pattern pattern;
    private static Matcher matcher;
    private static SimpleDateFormat dateFormat = null;
    private static SimpleDateFormat dateFormat2 = null;
    public static final String PATTERN_STANDARD08W = "yyyyMMdd";
    public static final String PATTERN_STANDARD12W = "yyyyMMddHHmm";
    public static final String PATTERN_STANDARD14W = "yyyyMMddHHmmss";
    public static final String PATTERN_STANDARD17W = "yyyyMMddHHmmssSSS";

    public static final String PATTERN_STANDARD10H = "yyyy-MM-dd";
    public static final String PATTERN_STANDARD16H = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_STANDARD19H = "yyyy-MM-dd HH:mm:ss";

    public static final String PATTERN_STANDARD10X = "yyyy/MM/dd";
    public static final String PATTERN_STANDARD16X = "yyyy/MM/dd HH:mm";
    public static final String PATTERN_STANDARD19X = "yyyy/MM/dd HH:mm:ss";


    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        dateFormat2 = new SimpleDateFormat("yyyyMMdd");
        dateFormat2.setLenient(false);
    }

    public static String getSystemDateTime(String datepattern)
            throws Exception {
        SimpleDateFormat df = new SimpleDateFormat(datepattern);
        return df.format(Calendar.getInstance().getTime());
    }

    public static boolean isValidDate(String s) {
        try {
            dateFormat.parse(s);
            return true;
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return false;
        }
    }

    /**
     * utf-8 转unicode
     *
     * @param inStr
     * @return String
     */
    public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(myBuffer[i]);
            if (ub == Character.UnicodeBlock.BASIC_LATIN) {
                sb.append(myBuffer[i]);
            } else if (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * @param theString
     * @return String
     */
    public static String unicodeToUtf8(String theString) {
        char aChar;
        if (theString == null) {
            return "";
        }
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed  \\uxxxx  encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = 't';
                    else if (aChar == 'r')
                        aChar = 'r';
                    else if (aChar == 'n')
                        aChar = 'n';
                    else if (aChar == 'f')
                        aChar = 'f';
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

    /**
     * @param date
     * @param pattern
     * @return
     * @Title: date2String
     * @Description: 日期格式的时间转化成字符串格式的时间
     * @author YFB
     */
    public static String date2String(Date date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("timestamp null illegal");
        }
        pattern = (pattern == null || pattern.equals("")) ? PATTERN_STANDARD19H : pattern;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * @param strDate
     * @param pattern
     * @return
     * @Title: string2Date
     * @Description: 字符串格式的时间转化成日期格式的时间
     * @author YFB
     */
    public static Date string2Date(String strDate, String pattern) {
        if (strDate == null || strDate.equals("")) {
            throw new RuntimeException("strDate is null");
        }
        pattern = (pattern == null || pattern.equals("")) ? PATTERN_STANDARD19H : pattern;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    /**
     * @param format 格式 17位(yyyyMMddHHmmssSSS) (14位:yyyyMMddHHmmss) (12位:yyyyMMddHHmm) (8位:yyyyMMdd)
     * @return
     * @Title: getCurrentTime
     * @Description: 取得当前系统时间
     * @author YFB
     */
    public static String getCurrentTime(String format) {
        SimpleDateFormat formatDate = new SimpleDateFormat(format);
        String date = formatDate.format(new Date());
        return date;
    }

    /**
     * @param dateStr
     * @param wantFormat
     * @return
     * @Title: getWantDate
     * @Description: 获取想要的时间格式
     * @author YFB
     */
    public static String getWantDate(String dateStr, String wantFormat) {
        if (!"".equals(dateStr) && dateStr != null) {
            String pattern = PATTERN_STANDARD14W;
            int len = dateStr.length();
            switch (len) {
                case 8:
                    pattern = PATTERN_STANDARD08W;
                    break;
                case 12:
                    pattern = PATTERN_STANDARD12W;
                    break;
                case 14:
                    pattern = PATTERN_STANDARD14W;
                    break;
                case 17:
                    pattern = PATTERN_STANDARD17W;
                    break;
                case 10:
                    pattern = (dateStr.contains("-")) ? PATTERN_STANDARD10H : PATTERN_STANDARD10X;
                    break;
                case 16:
                    pattern = (dateStr.contains("-")) ? PATTERN_STANDARD16H : PATTERN_STANDARD16X;
                    break;
                case 19:
                    pattern = (dateStr.contains("-")) ? PATTERN_STANDARD19H : PATTERN_STANDARD19X;
                    break;
                default:
                    pattern = PATTERN_STANDARD14W;
                    break;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(wantFormat);
            try {
                SimpleDateFormat sdfStr = new SimpleDateFormat(pattern);
                Date date = sdfStr.parse(dateStr);
                dateStr = sdf.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dateStr;
    }

    /**
     * @param dateStr
     * @param minute
     * @return
     * @Title: getAfterTime
     * @Description: 获取该时间的几分钟之后的时间
     * @author YFB
     */
    public static String getAfterTime(String dateStr, int minute) {
        String returnStr = "";
        try {
            String pattern = PATTERN_STANDARD14W;
            int len = dateStr.length();
            switch (len) {
                case 8:
                    pattern = PATTERN_STANDARD08W;
                    break;
                case 10:
                    pattern = PATTERN_STANDARD10H;
                    break;
                case 12:
                    pattern = PATTERN_STANDARD12W;
                    break;
                case 14:
                    pattern = PATTERN_STANDARD14W;
                    break;
                case 16:
                    pattern = PATTERN_STANDARD16H;
                    break;
                case 17:
                    pattern = PATTERN_STANDARD17W;
                    break;
                case 19:
                    pattern = PATTERN_STANDARD19H;
                    break;
                default:
                    pattern = PATTERN_STANDARD14W;
                    break;
            }
            SimpleDateFormat formatDate = new SimpleDateFormat(pattern);
            Date date = null;
            date = formatDate.parse(dateStr);
            Date afterDate = new Date(date.getTime() + (60000 * minute));
            returnStr = formatDate.format(afterDate);
        } catch (Exception e) {
            returnStr = dateStr;
            e.printStackTrace();
        }
        return returnStr;
    }

    /**
     * @param dateStr
     * @param minute
     * @return
     * @Title: getBeforeTime
     * @Description: 获取该时间的几分钟之前的时间
     * @author YFB
     */
    public static String getBeforeTime(String dateStr, int minute) {
        String returnStr = "";
        try {
            String pattern = PATTERN_STANDARD14W;
            int len = dateStr.length();
            switch (len) {
                case 8:
                    pattern = PATTERN_STANDARD08W;
                    break;
                case 10:
                    pattern = PATTERN_STANDARD10H;
                    break;
                case 12:
                    pattern = PATTERN_STANDARD12W;
                    break;
                case 14:
                    pattern = PATTERN_STANDARD14W;
                    break;
                case 16:
                    pattern = PATTERN_STANDARD16H;
                    break;
                case 17:
                    pattern = PATTERN_STANDARD17W;
                    break;
                case 19:
                    pattern = PATTERN_STANDARD19H;
                    break;
                default:
                    pattern = PATTERN_STANDARD14W;
                    break;
            }
            SimpleDateFormat formatDate = new SimpleDateFormat(pattern);
            Date date = null;
            date = formatDate.parse(dateStr);
            Date afterDate = new Date(date.getTime() - (60000 * minute));
            returnStr = formatDate.format(afterDate);
        } catch (Exception e) {
            returnStr = dateStr;
            e.printStackTrace();
        }
        return returnStr;
    }

    private static final String DATE_PATTERN =
            "(0?[1-9]|1[012]) [/.-] (0?[1-9]|[12][0-9]|3[01]) [/.-] ((19|20)\\d\\d)";


    /**
     * Validate date format with regular expression
     *
     * @param matcher
     * @return true valid date format, false invalid date format
     */
    public static boolean validate(Matcher matcher) {

        if (matcher.matches()) {
            matcher.reset();

            if (matcher.find()) {
                String day = matcher.group(1);
                String month = matcher.group(2);
                int year = Integer.parseInt(matcher.group(3));
                LogUtil.e("test", "get year: " + year + " ,month: " + month + " ,day: " + day);
                if (day.equals("31") &&
                        (month.equals("4") || month.equals("6") || month.equals("9") ||
                                month.equals("11") || month.equals("04") || month.equals("06") ||
                                month.equals("09"))) {
                    return false; // only 1,3,5,7,8,10,12 has 31 days
                } else if (month.equals("2") || month.equals("02")) {
                    //leap year
                    if (year % 4 == 0) {
                        if (day.equals("30") || day.equals("31")) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        if (day.equals("29") || day.equals("30") || day.equals("31")) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.getWantDate("2011-01-01 23:59:23", "yyyyMMdd"));
    }

    /**
     * 获取想要的时间
     *
     * @param dateType
     * @return
     */
    public static String getLongTime(char dateType) {
        String returnData = "";
        Calendar c = Calendar.getInstance(); // 当时的日期和时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        int hour; // 需要更改的小时
        int day; // 需要更改的天数
        switch (dateType) {
            case '0': // 1小时前
                hour = c.get(Calendar.HOUR_OF_DAY) - 1;
                c.set(Calendar.HOUR_OF_DAY, hour);
                returnData = df.format(c.getTime());
                break;
            case '1': // 2小时前
                hour = c.get(Calendar.HOUR_OF_DAY) - 2;
                c.set(Calendar.HOUR_OF_DAY, hour);
                returnData = df.format(c.getTime());
                break;
            case '2': // 3小时前
                hour = c.get(Calendar.HOUR_OF_DAY) - 3;
                c.set(Calendar.HOUR_OF_DAY, hour);
                returnData = df.format(c.getTime());
                break;
            case '3': // 6小时前
                hour = c.get(Calendar.HOUR_OF_DAY) - 6;
                c.set(Calendar.HOUR_OF_DAY, hour);
                returnData = df.format(c.getTime());
                break;
            case '4': // 12小时前
                hour = c.get(Calendar.HOUR_OF_DAY) - 12;
                c.set(Calendar.HOUR_OF_DAY, hour);
                returnData = df.format(c.getTime());
                break;
            case '5': // 一天前
                day = c.get(Calendar.DAY_OF_MONTH) - 1;
                c.set(Calendar.DAY_OF_MONTH, day);
                returnData = df.format(c.getTime());
                break;
            case '6': // 一星期前
                day = c.get(Calendar.DAY_OF_MONTH) - 7;
                c.set(Calendar.DAY_OF_MONTH, day);
                returnData = df.format(c.getTime());
                break;
            case '7': // 一个月前
                day = c.get(Calendar.DAY_OF_MONTH) - 30;
                c.set(Calendar.DAY_OF_MONTH, day);
                returnData = df.format(c.getTime());
                break;
            case '8': //当前时间然后延的一星期的第一天
                day = c.get(Calendar.DAY_OF_MONTH) - 6;
                c.set(Calendar.DAY_OF_MONTH, day);
                returnData = df.format(c.getTime());
                break;
        }
        return returnData;
    }

    public static boolean isValidDate2(String s) {
        try {
            dateFormat2.parse(s);
            return true;
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return false;
        }
    }
}