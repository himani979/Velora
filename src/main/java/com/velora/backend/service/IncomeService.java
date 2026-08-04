package com.velora.backend.service;

import com.velora.backend.dto.IncomeRequest;
import com.velora.backend.entity.Income;
import com.velora.backend.entity.User;
import com.velora.backend.repository.IncomeRepository;
import com.velora.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    // Add Income
    public Income saveIncome(IncomeRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        Income income = new Income();

        income.setSource(request.getSource());
        income.setAmount(request.getAmount());
        income.setIncomeDate(request.getIncomeDate());
        income.setNote(request.getNote());

        income.setUser(user);

        return incomeRepository.save(income);
    }

    // Get Logged-in User Income
    public List<Income> getAllIncome() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        return incomeRepository.findByUser(user);
    }
    public Income updateIncome(Integer id, IncomeRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return null;
        }

        Income income = incomeRepository.findById(id).orElse(null);

        if (income == null) {
            return null;
        }

        if (!income.getUser().getId().equals(user.getId())) {
            return null;
        }

        income.setSource(request.getSource());
        income.setAmount(request.getAmount());
        income.setIncomeDate(request.getIncomeDate());
        income.setNote(request.getNote());

        return incomeRepository.save(income);
    }
    public String deleteIncome(Integer id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "User not found";
        }

        Income income = incomeRepository.findById(id).orElse(null);

        if (income == null) {
            return "Income not found";
        }

        if (!income.getUser().getId().equals(user.getId())) {
            return "You cannot delete this income";
        }

        incomeRepository.delete(income);

        return "Income deleted successfully";
    }
}