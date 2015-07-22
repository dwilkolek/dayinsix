package eu.wilkolek.diary.model;

import java.util.ArrayList;

public class DayViewData {
    Integer cPage;
    ArrayList<DayView> days;
    Integer pages;
    Integer tPage;
    Integer sPage;
   

    
    public void setDays(ArrayList<DayView> days) {
        this.days = days;
    }
    public Integer getcPage() {
        return cPage;
    }
    public void setcPage(Integer cPage) {
        this.cPage = cPage;
    }
    public Integer getPages() {
        return pages;
    }
    public void setPages(Integer pages) {
        this.pages = pages;
    }
    public Integer gettPage() {
        return tPage;
    }
    public void settPage(Integer tPage) {
        this.tPage = tPage;
    }
    public Integer getsPage() {
        return sPage;
    }
    public void setsPage(Integer sPage) {
        this.sPage = sPage;
    }
    public ArrayList<DayView> getDays() {
        return days;
    }
    
    
}
