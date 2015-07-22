package eu.wilkolek.diary.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.web.servlet.ModelAndView;

import eu.wilkolek.diary.exception.OutOfDateException;
import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.DayView;
import eu.wilkolek.diary.model.DayViewData;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.DayRepository;

public class DayHelper {
    public static ArrayList<DayView> fillDates(ArrayList<Day> days, Date dayStart, Date dayFinish, String errMessage) {
        Integer diff = (int) TimeUnit.MILLISECONDS.toDays(dayFinish.getTime() - dayStart.getTime());
        diff = Math.abs(diff) + 1;
        Integer count = 0;
        Integer dayProcessed = 0;
        ArrayList<DayView> resultDays = new ArrayList<DayView>(diff);

        while (diff != resultDays.size()) {
            Date nowProcessed = new Date(dayFinish.getTime() - TimeUnit.DAYS.toMillis(dayProcessed));
            DayView d;
            if (days.size() > 0 && days.size() > count && days.get(count) != null && nowProcessed.equals(days.get(count).getCreationDate())) {
                Day h = days.get(count);
                d = new DayView(h.getSentence(), h.getWords(), h.getCreationDate(), h.getNote());
                count++;
            } else {
                d = new DayView(null, null, nowProcessed, null);
                d.setEmpty(true);
            }

            try {

                d.setCanAdd(true);
                DayHelper.checkDate(null, d.getCreationDate(), false, null, errMessage);
                d.setCanEdit(true);
            } catch (OutOfDateException e) {
                d.setCanEdit(false);

            }

            resultDays.add(d);
            dayProcessed++;
        }

        return resultDays;

    }

    public static Date checkDate(DayRepository dayRepository, Date date, Boolean checkIfExists, User user, String errMessage) throws OutOfDateException {
        Boolean dayExists;
        if (checkIfExists) {
            Optional<Day> day = dayRepository.findByCreationDateAndUser(date, user);
            dayExists = day.isPresent();
        } else {
            dayExists = true;
        }
        Long diffInDays = DateTimeUtils.diffInDays(date, DateTimeUtils.getCurrentUTCTime());

        if (diffInDays < 3L && diffInDays > -2L) {
            if (checkIfExists && !dayExists || !checkIfExists) {
                return date;
            }
        }

        throw new OutOfDateException(errMessage + " " + DateTimeUtils.format(date));

    }

    public static DayViewData createDataForView(DayRepository dayRepository, int pageSelected, User user, int recordsPerPage) {
        DayViewData data = new DayViewData();
        Integer page = pageSelected;
        Date now = DateTimeUtils.getCurrentUTCTime();
        if (!(page != null && page > 0)) {
            page = 1;
        }

        page--;
        Date chosenThen = new Date(now.getTime() - TimeUnit.DAYS.toMillis(recordsPerPage * page));
        Date chosenEarlier = new Date(chosenThen.getTime() - TimeUnit.DAYS.toMillis(recordsPerPage - 1));

        
        Date current = DateTimeUtils.getCurrentUTCTime();
        Date created = user.getCreated();
        long mCurrent = current.getTime();
        long mCreated = created.getTime();
        int allDays = (int) TimeUnit.MILLISECONDS.toDays(mCurrent - mCreated);

        int mPages = (int) Math.ceil(allDays / recordsPerPage);
        
        if (mPages*recordsPerPage < allDays){
            mPages++;
        }
        int sPages = page - 4;
        int tPages = page + 5;

        if (sPages < 1) {
            tPages += Math.abs(1 - sPages);
            sPages = 1;
        }

        if (tPages > mPages) {
            sPages -= (tPages - mPages);
            tPages = mPages;
        }

        // int tPages = mPages > 8 ? sPage+8 : mPages;

        long milisDay = TimeUnit.DAYS.toMillis(1);

        long milisEarlier = milisDay * (recordsPerPage) * (page + 1) - milisDay;
        long milisThen = milisDay * (recordsPerPage) * page;

        chosenEarlier = new Date(mCurrent - milisEarlier);
        chosenThen = new Date(mCurrent - milisThen);

        if (chosenEarlier.getTime() < created.getTime()){
            chosenEarlier = created;
        }
       // System.out.println(chosenEarlier + " , "+chosenThen + " > " + milisThen);
        
//        if (milisEarlier < mCreated) {
//            chosenEarlier = new Date(mCreated);
//        }

        ArrayList<Day> days = dayRepository.getDaysFromDateToDate(user, chosenEarlier, chosenThen);
        ArrayList<DayView> daysView = DayHelper.fillDates(days, chosenEarlier, chosenThen, "Error while getting list");

        data.setcPage(page + 1);
        data.setDays(daysView);
        data.setPages(mPages);
        data.settPage(tPages);
        data.setsPage(sPages);

        return data;
    }
}
