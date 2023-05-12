package com.fpt.edu.security.services;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.BanDat;
import com.fpt.edu.repository.BanDatRepository;
import com.fpt.edu.repository.BanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BanDatService {
    @Autowired
    BanDatRepository banDatRepository;

    public List<BanDat> findAll() {
        return banDatRepository.findAll();
    }

    public Page<BanDat> findPage(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber -1, 5, Sort.by("id").descending());
        return banDatRepository.findAll(pageable);
    }

    public Page<BanDat> findAllWithSort(String field, String direction, int pageNumber){
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(field).ascending(): Sort.by(field).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1,5, sort);
        return banDatRepository.findAll(pageable);
    }

    public Page<BanDat> findByKeyword(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber -1, 5, Sort.by("id").descending());
        return banDatRepository.findByKeyword(keyword, pageable);
    }
}
