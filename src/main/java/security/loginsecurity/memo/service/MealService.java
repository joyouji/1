package security.loginsecurity.memo.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import security.loginsecurity.memo.domain.entity.Meal;
import security.loginsecurity.member.Member;
import security.loginsecurity.memo.domain.repository.MealRepository;
import security.loginsecurity.memo.dto.MealDto;
import security.loginsecurity.service.MemberService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MealService {
    private final MealRepository mealRepository;
    private final MemberService memberService;

    @Autowired
    public MealService(MealRepository mealRepository, MemberService memberService) {
        this.mealRepository = mealRepository;
        this.memberService = memberService;
    }

    private Member getCurrentMember() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return memberService.findByEmail(userDetails.getUsername());
    }

    public List<MealDto> getMealsByTypeAndDate(String mealType, LocalDate date, String username) {
        Member member = getCurrentMember();
        List<Meal> meals = mealRepository.findByMealTypeAndDateAndMemberId(mealType, date, member.getId());
        return meals.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<MealDto> getMealsByDate(LocalDate date, String username) {
        Member member = getCurrentMember();
        List<Meal> meals = mealRepository.findByDateAndMemberId(date, member.getId());
        return meals.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public void saveMeal(MealDto mealDto, String username) {
        Meal meal = convertToEntity(mealDto);
        Member currentMember = getCurrentMember();
        meal.setMember(currentMember);
        mealRepository.save(meal);
    }

    public void updateMeal(MealDto mealDto) {
        Optional<Meal> optionalMeal = mealRepository.findById(mealDto.getId());
        if (optionalMeal.isPresent()) {
            Meal meal = optionalMeal.get();
            meal.setMealType(mealDto.getMealType());
            meal.setDate(mealDto.getDate());
            meal.setTime(mealDto.getTime());
            meal.setMenus(String.join(", ", mealDto.getMenus()));
            meal.setRating(mealDto.getRating());
            mealRepository.save(meal);
        } else {
            throw new RuntimeException("Meal not found with id: " + mealDto.getId());
        }
    }

    private MealDto convertToDto(Meal meal) {
        MealDto dto = new MealDto();
        dto.setId(meal.getId());
        dto.setMealType(meal.getMealType());
        dto.setDate(meal.getDate());
        dto.setTime(meal.getTime());
        dto.setMenus(Arrays.asList(meal.getMenus().split(", ")));
        dto.setRating(meal.getRating());
        dto.setMemberId(meal.getMember().getId());
        return dto;
    }

    private Meal convertToEntity(MealDto mealDto) {
        Meal meal = new Meal();
        meal.setId(mealDto.getId());
        meal.setMealType(mealDto.getMealType());
        meal.setDate(mealDto.getDate());
        meal.setTime(mealDto.getTime());
        meal.setMenus(String.join(", ", mealDto.getMenus()));
        meal.setRating(mealDto.getRating());
        if (mealDto.getMemberId() != null) {
            Member member = memberService.findById(mealDto.getMemberId());
            meal.setMember(member);
        }
        return meal;
    }

    @Transactional
    public void deleteMealsByTypeAndDate(String mealType, LocalDate date, String username) {
        //Member member = memberService.findByEmail(username);
        Member member = getCurrentMember();
        mealRepository.deleteByMealTypeAndDateAndMemberId(mealType, date, member.getId());
    }

}
