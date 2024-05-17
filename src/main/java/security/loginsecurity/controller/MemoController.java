package security.loginsecurity.controller;


import security.loginsecurity.memo.dto.MealDto;
import security.loginsecurity.memo.dto.MemoDto;
import security.loginsecurity.memo.dto.SleepDto;
import security.loginsecurity.memo.service.MealService;
import security.loginsecurity.memo.service.MemoService;
import security.loginsecurity.memo.service.SleepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/memo/memo")
public class MemoController {
    private final MemoService memoService;
    private final SleepService sleepService;
    private final MealService mealService;

    @Autowired
    public MemoController(MemoService memoService, SleepService sleepService, MealService mealService) {
        this.memoService = memoService;
        this.sleepService = sleepService;
        this.mealService = mealService;
    }

    @GetMapping
    public String showMemo(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model) {
        List<MemoDto> memos = memoService.getMemosByDate(date);
        List<SleepDto> sleeps = sleepService.getAllSleeps();
        String mealType = null;
        List<MealDto> meals = mealService.getMealsByTypeAndDate(mealType,date);
        model.addAttribute("date", date);
        model.addAttribute("memos", memos);
        model.addAttribute("sleeps", sleeps);
        model.addAttribute("meals", meals);
        return "memo";
    }

    @PostMapping("/save")
    public String saveOrUpdateMemo(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam("content") String content) {
        memoService.appendMemoContent(date, content);
        return "redirect:/memo/memo?date=" + date;
    }


    @GetMapping("/edit/{id}")
    public String editMemo(@PathVariable("id") Long id, Model model) {
        MemoDto memo = memoService.getMemoById(id);
        List<SleepDto> sleeps = sleepService.getAllSleeps(); // 슬립 정보 가져오기
        String mealType = null;
        List<MealDto> meals = mealService.getMealsByTypeAndDate(mealType,LocalDate.now()); // 밀 정보 가져오기 (임시로 오늘 날짜를 사용)
        if (memo != null) {
            model.addAttribute("memo", memo);
            model.addAttribute("sleeps", sleeps); // 슬립 정보 전달
            model.addAttribute("meals", meals); // 밀 정보 전달
            return "editMemo";
        }
        return "redirect:/memo";
    }



    @PostMapping("/update")
    public String updateMemo(@RequestParam("id") Long id, @RequestParam("content") String content) {
        LocalDate updatedDate = memoService.updateMemo(id, content);
        return "redirect:/memo/memo?date=" + updatedDate.toString();
    }

    @PostMapping("/sleep/save")
    public String saveSleepTime(@RequestParam("start") String start, @RequestParam("end") String end) {
        LocalTime startTime = LocalTime.parse(start);
        LocalTime endTime = LocalTime.parse(end);
        SleepDto sleepDto = new SleepDto(null, startTime, endTime);
        sleepService.saveSleep(sleepDto);
        return "redirect:/memo";
    }

    @PostMapping("/meals/save")
    public String saveMeal(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("mealType") String mealType,
            @RequestParam("mealTime") String mealTime,
            @RequestParam("mealMenus") String mealMenus,
            @RequestParam("mealRating") int mealRating) {
        LocalTime time = LocalTime.parse(mealTime);
        List<String> menus = Arrays.asList(mealMenus.split(","));
        MealDto mealDto = new MealDto(null, mealType, date, time, menus, mealRating);
        mealService.saveMeal(mealDto);
        return "redirect:/memo/memo?date=" + date;
    }

    @PostMapping("/meals/update")
    public String updateMeal(@ModelAttribute("meal") MealDto mealDto) {
        mealService.saveMeal(mealDto);
        return "redirect:/memo/memo?date=" + mealDto.getDate();
    }

    @PostMapping("/sleep/update")
    public String updateSleep(@ModelAttribute("sleep") SleepDto sleepDto) {
        sleepService.saveSleep(sleepDto);
        return "redirect:/memo";
    }

}
