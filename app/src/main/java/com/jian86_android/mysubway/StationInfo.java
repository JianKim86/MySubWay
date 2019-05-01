package com.jian86_android.mysubway;

public class StationInfo {
    private String line_num; //호선
    private String station_CD; //역코드
    private String station_NM; //역명
    private String arrive_TIME; //도착시간
    private String left_TIME;//출발시간
    private String subwayE_NM; //도착역
    private String week_TAG;//요일
    private String inout_RAG; //상.하행선
    private String express_YN; //급행선
    private String train_N0; //열차번호

    public String getTrain_N0() {
        return train_N0;
    }

    public void setTrain_N0(String train_N0) {
        this.train_N0 = train_N0;
    }

    public String getLine_num() {
        return line_num;
    }

    public void setLine_num(String line_num) {
        this.line_num = line_num;
    }

    public String getStation_CD() {
        return station_CD;
    }

    public void setStation_CD(String station_CD) {
        this.station_CD = station_CD;
    }

    public String getStation_NM() {
        return station_NM;
    }

    public void setStation_NM(String station_NM) {
        this.station_NM = station_NM;
    }

    public String getArrive_TIME() {
        return arrive_TIME;
    }

    public void setArrive_TIME(String arrive_TIME) {
        this.arrive_TIME = arrive_TIME;
    }

    public String getLeft_TIME() {
        return left_TIME;
    }

    public void setLeft_TIME(String left_TIME) {
        this.left_TIME = left_TIME;
    }

    public String getSubwayE_NM() {
        return subwayE_NM;
    }

    public void setSubwayE_NM(String subwayE_NM) {
        this.subwayE_NM = subwayE_NM;
    }

    public String getWeek_TAG() {
        return week_TAG;
    }

    public void setWeek_TAG(String week_TAG) {
        this.week_TAG = week_TAG;
    }

    public String getInout_RAG() {
        return inout_RAG;
    }

    public void setInout_RAG(String inout_RAG) {
        this.inout_RAG = inout_RAG;
    }

    public String getExpress_YN() {
        return express_YN;
    }

    public void setExpress_YN(String express_YN) {
        this.express_YN = express_YN;
    }
}//stationInfo
