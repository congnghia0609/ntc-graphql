/*
 * Copyright 2015 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntc.graphql.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nghiatc
 * @since May 10, 2015
 */
public class FormatHelper {

    public enum TimeUnit {

        HOUR,
        DAY,
        WEEK,
        MONTH
    }

    public static Integer[] getMonthBoundaries(Calendar c) {
        Integer[] boundaries = new Integer[4];
        int dow = c.get(Calendar.DAY_OF_WEEK);
        switch (dow) {
            case Calendar.SUNDAY:
                boundaries[0] = 5;
                break;
            default:
                boundaries[0] = dow - 3;
                break;
        }
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        if (month < 7) {
            if (month % 2 == 0) {
                boundaries[2] = 31;
                if (month == 2) {
                    if (year % 4 == 0) {
                        boundaries[1] = 29;
                    } else {
                        boundaries[1] = 28;
                    }
                } else {
                    boundaries[1] = 30;
                }
            } else {
                boundaries[1] = 31;
                if (month == 1) {
                    if (year % 4 == 0) {
                        boundaries[2] = 29;
                    } else {
                        boundaries[2] = 28;
                    }
                } else {
                    boundaries[2] = 30;
                }
            }

        } else if (month == 7) {
            boundaries[1] = 31;
            boundaries[2] = 31;
        } else {
            if (month % 2 == 0) {
                boundaries[1] = 31;
                boundaries[2] = 30;
            } else {
                boundaries[1] = 30;
                boundaries[2] = 31;
            }
        }
        boundaries[3] = 40 - boundaries[0] - boundaries[2];
        //boundaries[3] %= 7;
        boundaries[0] = boundaries[1] - boundaries[0];
        return boundaries;
    }

    public static String getShortVNTime(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);

        return String.format("%tl:%tM %s ", cl, cl, (cl.get(Calendar.HOUR_OF_DAY) > 12) ? "chiều" : "sáng");
    }

    public static String getVNTime(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        return String.format("%tl:%tM %s %td/%tm", cl, cl, (cl.get(Calendar.HOUR_OF_DAY) > 12) ? "chiều" : "sáng", cl, cl);
    }

    public static String getFullVNTime(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        return String.format("%tl:%tM %s %td/%tm/%tY", cl, cl, (cl.get(Calendar.HOUR_OF_DAY) > 12) ? "chiều" : "sáng", cl, cl, cl);
    }

    public static String getPeriod(Date d_s, Date d_e) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int y_c = c.get(Calendar.YEAR);
        c.setTime(d_s);
        int y_s = c.get(Calendar.YEAR);
        int m_s = c.get(Calendar.MONTH);
        c.setTime(d_e);
        int y_e = c.get(Calendar.YEAR);
        int m_e = c.get(Calendar.MONTH);
        if (y_s == y_c && y_e == y_c) {
            if (m_s == m_e) {
                return getShortVNTime(d_s) + " - " + getVNTime(d_e);
            }
            return getVNTime(d_s) + " - " + getVNTime(d_e);
        }
        return getFullVNTime(d_s) + " - " + getFullVNTime(d_e);
    }

    public static String getAge(Date dob) {
        return dateDiff(dob, new Date(System.currentTimeMillis()));
    }

    public static String dateDiff(Date d0, Date d1) {
        String unit;
        int rs;
        long duration_in_secs = Math.abs(d1.getTime() - d0.getTime()) / 1000;
        if (duration_in_secs < 60) {
            rs = (int) duration_in_secs;
            unit = " giây";
        } else {
            // < 60 phút
            if (duration_in_secs < 3600) {
                rs = (int) (duration_in_secs / 60);
                unit = " phút";
            } else {
                //< 24 giờ
                if (duration_in_secs < 86400) {
                    rs = (int) (duration_in_secs / 3600);
                    unit = " giờ";
                } else {
                    //< 7 ngày
                    if (duration_in_secs < 604800) {
                        rs = (int) (duration_in_secs / 86400);
                        unit = " ngày";
                    } else {
                        //< 30 ngày
                        if (duration_in_secs < 2592000) {
                            rs = (int) duration_in_secs / 604800;
                            unit = " tuần";
                        } else {

                            Calendar cl = Calendar.getInstance();
                            cl.setTime(d0);
                            int m_0 = cl.get(Calendar.MONTH);
                            int y_0 = cl.get(Calendar.YEAR);
                            int d_0 = cl.get(Calendar.DAY_OF_MONTH);

                            cl.setTime(d1);
                            int m_1 = cl.get(Calendar.MONTH);
                            int y_1 = cl.get(Calendar.YEAR);
                            int d_1 = cl.get(Calendar.DAY_OF_MONTH);

                            int duration_in_months = (y_1 - y_0) * 12 + m_1 - m_0;
                            int plus = 0;
                            if (d1.getTime() < d0.getTime()) {
                                duration_in_months = -duration_in_months;
                                if (d_1 < d_0) {
                                    plus = 1;
                                }
                            } else {
                                if (d_1 > d_0) {
                                    plus = 1;
                                }
                            }
                            duration_in_months += plus;
                            if (duration_in_months < 12) {
                                rs = duration_in_months;
                                if (rs <= 0) {
                                    rs = 1;
                                }
                                unit = " tháng";
                            } else {
                                rs = (int) Math.floor(duration_in_months / 12d);
                                unit = " năm";
                            }
                        }
                    }
                }
            }
        }
        return String.valueOf(rs) + unit;
    }

    public static int getTimeFromStringWithFormat(String time, String formatString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(formatString);
        return (int) ((dateFormat.parse(time).getTime()) / 1000);
    }

    static DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static int getTimeFromVNString(String time) throws ParseException {
        return (int) ((df.parse(time).getTime()) / 1000);
    }

    static DateFormat hf = new SimpleDateFormat("HH:mm");

    public static int getTimeFromHourString(String time) throws ParseException {
        return (int) ((hf.parse(time).getTime()) / 1000);
    }

    public static String getVnTimeFromInt(int time) {
        return df.format(new Date((long) time * 1000));
    }

    public static String getVnTimeFromInt(long time) {
        return df.format(new Date(time * 1000));
    }

    public static String getHourTimeFromInt(int time) {
        return hf.format(new Date((long) time * 1000));
    }

    public static Date getDateFromInt(int time) {
        return new Date((long) time * 1000);
    }

    public static int convertDMYToSecond(int day, int month, int year) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.YEAR, year);
        return (int) (cal.getTimeInMillis() / 1000);
    }

    public static long currentTimeLong() {
        Calendar cal = Calendar.getInstance();
        return cal.getTimeInMillis();
    }

    public static long currentTimeLongYMDHT() {
        long res = 0;
        Date date = new Date();
        String temp = df.format(date);
        try {
            res = (long) getTimeFromVNString(temp);
        } catch (ParseException ex) {
            Logger.getLogger(FormatHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    public static long currentTimeWithFormat(String formatString) {
        DateFormat dateFormat = new SimpleDateFormat(formatString);
        long res = 0;
        Date date = new Date();
        String temp = dateFormat.format(date);
        try {
            res = (long) getTimeFromStringWithFormat(temp, formatString);
        } catch (ParseException ex) {
            Logger.getLogger(FormatHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    private static DateFormat MMddyyyyf = new SimpleDateFormat("MM/dd/yyyy");
    
    public static long convertStringDateMMddyyyy2Long(String date) throws ParseException{
        Calendar cal = Calendar.getInstance();
        cal.setTime(MMddyyyyf.parse(date));
        return cal.getTimeInMillis();
    }
    
    public static long convertStringDateMMddyyyy2Long(Calendar cal, String date) throws ParseException{
        cal.setTime(MMddyyyyf.parse(date));
        return cal.getTimeInMillis();
    }
    
    public static Calendar convertStringDateMMddyyyy2Calendar(String date) throws ParseException{
        Calendar cal = Calendar.getInstance();
        cal.setTime(MMddyyyyf.parse(date));
        return cal;
    }
    
    public static String convertCalendar2StringMMddyyyy(Calendar cal){        
        return MMddyyyyf.format(cal.getTime());
    }
    
    public static String convertLong2StringMMddyyyy(long date){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return MMddyyyyf.format(cal.getTime());
    }
    
    public static Calendar currentTimeCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTimeLong());
        return cal;
    }

    public static Calendar convertIntToCalendar(int time) {
        Date date = getDateFromInt(time);
        Calendar ret = Calendar.getInstance();
        ret.setTime(date);
        return ret;
    }

    public static Calendar convertSecondToCalendar(int seconds) {
        Date date = getDateFromInt(seconds * 1000);
        Calendar ret = Calendar.getInstance();
        ret.setTime(date);
        return ret;
    }

    private static final DateFormat yyMMddHHDateFormat = new SimpleDateFormat("yyMMddHH");
    private static final DateFormat yyyyMMddDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat yyMMddDateFormat = new SimpleDateFormat("yyMMdd");
    private static final DateFormat yyyyMMddHHmmSSDateFormat = new SimpleDateFormat("yyyyMMdd:HHmmss");
    private static final DateFormat MMddHHDateFormat = new SimpleDateFormat("MMddHH");
    private static final DateFormat yyMMwwddHHDateFormat = new SimpleDateFormat("yyMMwwddHH");
    private static final DateFormat yyMMwwddDateFormat = new SimpleDateFormat("yyMMwwdd");
    private static final DateFormat yyMMwwDateFormat = new SimpleDateFormat("yyMMww");
    private static final DateFormat yyMMDateFormat = new SimpleDateFormat("yyMM");
    private static final DateFormat MMddDateFormat = new SimpleDateFormat("MMdd");
    private static final DateFormat ddMMyyyyDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat MMyyyyDateFormat = new SimpleDateFormat("MM/yyyy");

    public static String getYMDHTodaySuffix() {
        Date date = new Date();
        return yyMMddHHDateFormat.format(date).substring(1);
    }

    public static String getYMDHYesterdaySuffix() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return yyMMddHHDateFormat.format(cal.getTime()).substring(1);
    }

    public static String getYMDHTodaySuffix(int hour) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        return yyMMddHHDateFormat.format(cal.getTime()).substring(1);
    }

    public static String getYMDHYesterdaySuffix(int hour) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        return yyMMddHHDateFormat.format(cal.getTime()).substring(1);
    }

    public static String getYMD(Date date) {
        return yyyyMMddDateFormat.format(date);
    }

    public static String getYMDYesterdaySuffix() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return yyMMddDateFormat.format(cal.getTime()).substring(1);
    }

    public static String getYMDSuffix(Calendar cal) {
        return yyMMddDateFormat.format(cal.getTime()).substring(1);
    }
    
    public static String getYMD(Calendar cal) {
        return yyMMddDateFormat.format(cal.getTime());
    }
    
    public static String getYYMDHMS(Calendar cal) {
        return yyyyMMddHHmmSSDateFormat.format(cal.getTime());
    }
    
    public static String getYMDHSuffix(Calendar cal) {
        return yyMMddHHDateFormat.format(cal.getTime()).substring(1);
    }

    public static String getMDHSuffix(Calendar cal) {
        return MMddHHDateFormat.format(cal.getTime());
    }


    public static String getYMwDHSuffix(Calendar cal) {
        return yyMMwwddHHDateFormat.format(cal.getTime()).substring(1);
    }

    public static String getYMwDH24Suffix(Calendar cal) {
        int HourTemp = cal.get(Calendar.HOUR_OF_DAY) + 1;
        String rs = "";
        if (1 <= HourTemp && HourTemp <= 9) {
            rs = yyMMwwddDateFormat.format(cal.getTime()).substring(1) + "0" + HourTemp;
        } else {
            rs = yyMMwwddDateFormat.format(cal.getTime()).substring(1) + HourTemp;
        }
        return rs;
    }

    public static String getYMwDSuffix(Calendar cal) {
        return yyMMwwddDateFormat.format(cal.getTime()).substring(1);
    }
    
    public static String getYMwD(Calendar cal) {
        return yyMMwwddDateFormat.format(cal.getTime());
    }

    public static String getYMwSuffix(Calendar cal) {
        return yyMMwwDateFormat.format(cal.getTime()).substring(1);
    }
    
    public static String getYMw(Calendar cal) {
        return yyMMwwDateFormat.format(cal.getTime());
    }

    public static String getYMSuffix(Calendar cal) {
        return yyMMDateFormat.format(cal.getTime()).substring(1);
    }
    
    public static String getYM(Calendar cal) {
        return yyMMDateFormat.format(cal.getTime());
    }

    public static String getYMDMDSuffix(Calendar cal) {
        String ret = "";
        Calendar temp = (Calendar) cal.clone();
        temp.setFirstDayOfWeek(Calendar.MONDAY);
        temp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        ret = yyMMddDateFormat.format(temp.getTime()).substring(1);
        temp.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        ret += MMddDateFormat.format(temp.getTime());
        return ret;
    }

    public static String getDMYTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return ddMMyyyyDateFormat.format(date);
    }

    public static String getDMYTime(Calendar cal) {
        return ddMMyyyyDateFormat.format(cal.getTime());
    }

    public static Date getDateFromDMYString(String date) throws ParseException {
        return ddMMyyyyDateFormat.parse(date);
    }

    public static long getLongFromDate(Date date) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTimeInMillis();
    }

    public static int dateDiff(TimeUnit unit, Calendar calA, Calendar calB) {
        int ret = 0;
        long diff = calB.getTimeInMillis() - calA.getTimeInMillis();
        switch (unit) {
            case HOUR:
                ret = (int) (diff / (1000 * 60 * 60));
                break;
            case DAY:
                ret = (int) (diff / (1000 * 60 * 60 * 24));
                break;
            case WEEK: {//assume that calA and calB have diff < 6 months
                calA.setFirstDayOfWeek(Calendar.MONDAY);
                calB.setFirstDayOfWeek(Calendar.MONDAY);
                int first = calA.get(Calendar.WEEK_OF_YEAR);
                int second = calB.get(Calendar.WEEK_OF_YEAR);
                if (first > second) {
                    ret = calA.getActualMaximum(Calendar.WEEK_OF_YEAR) - first + second + 1;
                } else if (first < second) {
                    ret = second - first + 1;
                }
                break;
            }
            case MONTH: {
                Calendar temp = (Calendar) calA.clone();
                for (ret = 0; temp.before(calB); ret++) {
                    temp.add(Calendar.MONTH, 1);
                }
                if (calA.get(Calendar.DAY_OF_MONTH) > calB.get(Calendar.DAY_OF_MONTH)) {
                    ret += 1;
                }

                break;
            }

        }
        return ret;
    }

    public static String getMonth(Calendar cal) {
        return MMyyyyDateFormat.format(cal.getTime());
    }

    public static long convertIntToLong(int intDate) {
        Calendar cal = convertIntToCalendar(intDate);
        return cal.getTimeInMillis();
    }

    public static Date getDateFromLong(long time) {
        return new Date(time * 1000);
    }

    public static Calendar convertLongToCalendar(long time) {
        Date date = getDateFromLong(time);
        Calendar ret = Calendar.getInstance();
        ret.setTime(date);
        return ret;
    }

    public static String convertLongTimeToString(long time) {
        String strDate = "";
        Date date = getDateFromLong(time);
//		Calendar ret = Calendar.getInstance();
//		ret.setTime(date);

        strDate = ddMMyyyyDateFormat.format(date);
        return strDate;
    }
}
