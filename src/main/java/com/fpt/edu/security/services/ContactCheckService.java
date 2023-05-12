package com.fpt.edu.security.services;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.Contact_check;
import com.fpt.edu.repository.BanRepository;
import com.fpt.edu.repository.Contact_checkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactCheckService {
    @Autowired
    Contact_checkRepository contactCheckRepository;

    public List<Contact_check> findAll() {
        return contactCheckRepository.findAll();
    }

    public Page<Contact_check> findPage(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber -1, 5);
        return contactCheckRepository.findAll(pageable);
    }

    public Page<Contact_check> findAllWithSort(String field, String direction, int pageNumber){
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(field).ascending(): Sort.by(field).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1,5, sort);
        return contactCheckRepository.findAll(pageable);
    }

    public Page<Contact_check> findByKeyword(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber -1, 5);
        return contactCheckRepository.findByKeyword(keyword, pageable);
    }
}
