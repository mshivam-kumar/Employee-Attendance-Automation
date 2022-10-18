package com.attendance.ai.smart;

import java.util.ArrayList;

public class AttendanceSettings {
    private String minEntryTime,minExitTime,totalNumOfMonthlyGraces,graceTime,minHalfDayExitTime;
    private String totalNumOfMonthlyOneHrLeaves,graceForOneHrLeaves;

    public AttendanceSettings(String minEntryTime, String minExitTime, String totalNumOfMonthlyGraces, String graceTime, String minHalfDayExitTime, String totalNumOfMonthlyOneHrLeaves) {
        this.minEntryTime = minEntryTime;
        this.minExitTime = minExitTime;
        this.totalNumOfMonthlyGraces = totalNumOfMonthlyGraces;
        this.graceTime = graceTime;
        this.minHalfDayExitTime = minHalfDayExitTime;
        this.totalNumOfMonthlyOneHrLeaves = totalNumOfMonthlyOneHrLeaves;
    }

    public void setMinEntryTime(String minEntryTime) {
        this.minEntryTime = minEntryTime;
    }

    public void setMinExitTime(String minExitTime) {
        this.minExitTime = minExitTime;
    }

    public void setTotalNumOfMonthlyGraces(String totalNumOfMonthlyGraces) {
        this.totalNumOfMonthlyGraces = totalNumOfMonthlyGraces;
    }

    public void setGraceTime(String graceTime) {
        this.graceTime = graceTime;
    }

    public void setMinHalfDayExitTime(String minHalfDayExitTime) {
        this.minHalfDayExitTime = minHalfDayExitTime;
    }


    public void setTotalNumOfMonthlyOneHrLeaves(String totalNumOfMonthlyOneHrLeaves) {
        this.totalNumOfMonthlyOneHrLeaves = totalNumOfMonthlyOneHrLeaves;
    }

    public void setGraceForOneHrLeaves(String graceForOneHrLeaves) {
        this.graceForOneHrLeaves = graceForOneHrLeaves;
    }

    public String getMinEntryTime() {
        return minEntryTime;
    }

    public String getMinExitTime() {
        return minExitTime;
    }

    public String getTotalNumOfMonthlyGraces() {
        return totalNumOfMonthlyGraces;
    }

    public String getGraceTime() {
        return graceTime;
    }

    public String getMinHalfDayExitTime() {
        return minHalfDayExitTime;
    }


    public String getTotalNumOfMonthlyOneHrLeaves() {
        return totalNumOfMonthlyOneHrLeaves;
    }

    public String getGraceForOneHrLeaves() {
        return graceForOneHrLeaves;
    }
}
