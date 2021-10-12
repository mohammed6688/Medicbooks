package com.mocomp.developer.medicbooks.models.content;

public class FriendlyMessage {

    private String doctorid;
    private String userid;
    private String text;
    private String name;
    private String photoUrl;
    private String recordUrl;
    private boolean checked;
    private boolean consultationEnd;
    private String date;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String doctorid,String userid,String text, String name, String photoUrl , String recordUrl, boolean checked, boolean consultationEnd,String date) {
        this.doctorid = doctorid;
        this.userid = userid;
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.recordUrl = recordUrl;
        this.checked=checked;
        this.consultationEnd=consultationEnd;
        this.date=date;
    }

    public String getDoctorid() {
        return doctorid;
    }

    public void setDoctorid(String doctorid) {
        this.doctorid = doctorid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRecordUrl() {
        return recordUrl;
    }

    public void setRecordUrl(String recordUrl) {
        this.recordUrl = recordUrl;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean getConsultationEnd() {
        return consultationEnd;
    }

    public void setConsultationEnd(boolean consultationEnd) {
        this.consultationEnd = consultationEnd;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}