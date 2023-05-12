package com.fpt.edu.security.services;

import com.fpt.edu.models.Ban;
import com.fpt.edu.repository.BanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BanService {
    @Autowired
    BanRepository banRepository;

    public List<Ban> findAll() {
        return banRepository.findAll();
    }

    public Page<Ban> findPage(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber -1, 5);
        return banRepository.findAll(pageable);
    }

    public Page<Ban> findAllWithSort(String field, String direction, int pageNumber){
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(field).ascending(): Sort.by(field).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1,5, sort);
        return banRepository.findAll(pageable);
    }

    public Page<Ban> findByKeyword(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber -1, 5);
        return banRepository.findByKeyword(keyword, pageable);
    }
}
