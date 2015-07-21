package eu.wilkolek.diary.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import eu.wilkolek.diary.exception.OutOfDateException;
import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.DayView;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.DayRepository;

public class DayHelper {
    public static  ArrayList<DayView> fillDates(ArrayList<Day> days, Date dayStart, Date dayFinish, String errMessage) {
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
                d = new DayView(h.getSentence(), h.getWords(), h.getCreationDate());
                count++;
            } else {
                d = new DayView(null, null, nowProcessed);
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
        if (checkIfExists){
            Optional<Day> day = dayRepository.findByCreationDateAndUser(date, user);
            dayExists = day.isPresent();
        }else {
            dayExists = true;
        }
        Long diffInDays = DateTimeUtils.diffInDays(date, DateTimeUtils.getCurrentUTCTime());
        
        if (diffInDays < 3L && diffInDays > -2L) {
            if (checkIfExists && !dayExists || !checkIfExists){
                return date;
            }
        }
            
        
        throw new OutOfDateException(errMessage + " " + DateTimeUtils.format(date));
        

    }
}
